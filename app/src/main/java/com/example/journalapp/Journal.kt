package com.example.journalapp

import com.google.firebase.Timestamp

data class Journal(
    val title: String,
    val thoughts: String,
    val imageUrl: Int,

    val userId: String,
    val timeAdded: Timestamp,
    var userName: String
    )
