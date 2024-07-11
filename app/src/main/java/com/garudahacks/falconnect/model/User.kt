package com.garudahacks.falconnect.model

import com.google.firebase.Timestamp

data class User(
    var identification: String,
    var fullname: String,
    var date: Timestamp,
    var phone: String,
    var email: String,
    var password: String,
    var created: Timestamp
)