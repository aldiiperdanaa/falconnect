package com.garudahacks.falconnect.model

import com.google.firebase.Timestamp

data class News(
    var id: String = "",
    var title: String = "",
    var photoUrl: String = "",
    var sourceLogo: String = "",
    var source: String = "",
    var author: String = "",
    var description: String = "",
    var createdAt: Timestamp? = null
)