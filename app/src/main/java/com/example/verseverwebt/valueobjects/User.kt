package com.example.verseverwebt.valueobjects

data class User(
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    var time1: Float,
    var time2: Float,
    var time3: Float,
    var time4: Float,
    var time5: Float,
    var time6: Float,
    var time7: Float,
    var introCompleted: Boolean,
    var rank: Int
)
