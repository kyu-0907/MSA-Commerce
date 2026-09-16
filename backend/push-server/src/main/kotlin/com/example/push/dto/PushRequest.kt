package com.example.push.dto

data class PushRequest(
    val userId: Long,
    val title: String,
    val body: String,
    val type: String
)
