package com.garudahacks.falconnect.model

import com.google.firebase.Timestamp

data class Jobs(
    var id: String = "",
    var category: String = "",
    var city: String = "",
    var companyImg: String = "",
    var companyName: String = "",
    var description: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var roleJob: String = "",
    var salary: String = "",
    var isDisability: Boolean? = false,
    var isBad: Boolean? = false,
    var createdAt: Timestamp? = null
)