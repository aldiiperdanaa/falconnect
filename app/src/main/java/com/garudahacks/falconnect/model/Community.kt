package com.garudahacks.falconnect.model

import com.google.firebase.Timestamp

data class Community(
    var id: String = "",
    var profileImageUrl: String = "",
    var fullname: String = "",
    var username: String = "",
    var photoStatusUrl: String = "",
    var status: String = "",
    var totalLike: Int = 0,
    var totalComment: Int = 0,
    var totalBookmark: Int = 0,
    var createdAt: Timestamp? = null
)
