package com.garudahacks.falconnect.model

import com.google.firebase.Timestamp

data class Course(
    var id: String = "",
    var title: String = "",
    var imageCourse: String = "",
    var instructor: String = "",
    var job: String = "",
    var price: Int = 0,
    var rating: Int = 0,
)