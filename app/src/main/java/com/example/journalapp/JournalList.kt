package com.example.journalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.journalapp.databinding.ActivityJournalListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference

class JournalList : AppCompatActivity() {
    lateinit var binding: ActivityJournalListBinding

    //Firebase Reference
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference

    lateinit var journalList: MutableList<Journal>
    var collectionReference: CollectionReference = db.collection("Journal")

    lateinit var adapter: JournalRecyclerAdapter
    lateinit var noPostTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_journal_list)

        // Firebase Auth
        firebaseAuth = Firebase.auth
        user = firebaseAuth.currentUser!!

        // RecyclerView
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this)

        // Posts ArrayList
        journalList = arrayListOf<Journal>()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add -> if(user != null && firebaseAuth != null){
                val intent = Intent(this,AddJournalActivity::class.java)
                startActivity(intent)
            }
            R.id.action_signout -> {
                if (user != null && firebaseAuth != null){
                    firebaseAuth.signOut()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        collectionReference.whereEqualTo("userId",
            JournalUser.instance?.userId)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    it.forEach {
                        // convert snapshot to journal objects
                        var journal = it.toObject(Journal::class.java)

                        journalList.add(journal)

                    }
                    // RecyclerView
                    adapter = JournalRecyclerAdapter(
                        this,journalList
                    )
                    binding.recyclerView.setAdapter(adapter)
                    adapter.notifyDataSetChanged()
                }
                else{
                    binding.listNoPosts.visibility = View.VISIBLE
                }
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Opps! something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}