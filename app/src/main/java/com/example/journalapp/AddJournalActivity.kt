package com.example.journalapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.journalapp.databinding.ActivityAddJournalBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date

class AddJournalActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddJournalBinding

    // Credentials
    var currentUserId: String = ""
    var currentUserName: String = ""

    // Firebase
    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser

    // Firebase Firestore
    var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference

    var collectionReference: CollectionReference = db.collection("Journal")
    lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_journal)

        storageReference = FirebaseStorage.getInstance().getReference()
        auth = FirebaseAuth.getInstance()

        binding.apply {
            postProgressBar.visibility = View.INVISIBLE
            if(JournalUser.instance != null){
                currentUserId = JournalUser.instance!!
                    .userId.toString()
                currentUserName = JournalUser.instance!!
                    .username.toString()

                postUsernameTextView.text = currentUserName
            }
            postSaveJournalButton.setOnClickListener {
                saveJournal()
            }
        }
    }

    private fun saveJournal() {
        var title: String = binding.postTitleEt.text.toString().trim()
        var thoughts: String = binding.postDescriptionEt.text.toString().trim()

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null)
        {
            // Saving the path of images in Storage
            // ......./journal_img/our_image.png
            val filePath: StorageReference = storageReference
                .child("journal_images")
                .child("my_image_"+ Timestamp.now().seconds)

            // Uploading the images
            filePath.putFile(imageUri)
                .addOnSuccessListener(){
                    filePath.downloadUrl.addOnSuccessListener {

                        var imageUri: String = it.toString()
                        var timeStamp: Timestamp = Timestamp(Date())
                        // Creating the object of Journal

                        var journal: Journal = Journal(
                            title,
                            thoughts,
                            imageUri,
                            currentUserId,
                            timeStamp,
                            currentUserName
                        )

                        // adding the new journal
                        collectionReference.add(journal)
                            .addOnSuccessListener {
                                binding.postProgressBar.visibility = View.INVISIBLE
                                var i = Intent(this,JournalList::class.java)

                                startActivity(i)
                                finish()
                            }
                    }
                }.addOnFailureListener(){
                    binding.postProgressBar.visibility = View.INVISIBLE
                }
        }
        else{
            binding.postProgressBar.visibility = View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && requestCode == RESULT_OK){
                if (data != null) {
                    imageUri = data.data!! // getting the actual image path
                    binding.postImageView.setImageURI(imageUri) // Showing the image
                }
        }
    }

    override fun onStart() {
        super.onStart()
        user = auth.currentUser!!
    }

    override fun onStop() {
        super.onStop()
        if(auth != null){

        }
    }
}