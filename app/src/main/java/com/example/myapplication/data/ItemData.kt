package com.example.myapplication.data

import androidx.compose.ui.text.input.TextFieldValue

data class ItemData(
    val title: String="",
    val amount: String="",
    val inputs: List<String> = List(4) { "" }
)