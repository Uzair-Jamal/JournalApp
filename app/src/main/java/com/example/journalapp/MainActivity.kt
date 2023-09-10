package com.example.journalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.journalapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Firebase Auth
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.createAccBTN.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.emailSignInButton.setOnClickListener {
            LoginWithEmailPassword(
                binding.email.text.toString().trim(),
                binding.password.text.toString().trim()
            )
        }

        // Auth Ref
        auth = Firebase.auth
    }

    private fun LoginWithEmailPassword(email: String, password: String) {
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    // Success SignIn
                    if(task.isSuccessful){
                       var journal: JournalUser = JournalUser.instance!!
                        journal.userId = auth.currentUser?.uid
                        journal.username = auth.currentUser?.displayName


                        goToJournalList()
                    }
                    else{
                        Toast.makeText(
                            this,
                            "Authentication Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null){
            goToJournalList()
        }
    }

    private fun goToJournalList() {
        var intent = Intent(this,JournalList::class.java)
        startActivity(intent)
    }
}