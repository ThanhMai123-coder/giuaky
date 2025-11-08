package com.example.midterm_ltdd.data


data class Phone(
    val name: String = "",
    val category: String = "",
    val price: String = "",
    val image: String? = null
)

data class PhoneItem(
    val id: String,
    val name: String,
    val category: String,
    val price: String,
    val image: String?
)

