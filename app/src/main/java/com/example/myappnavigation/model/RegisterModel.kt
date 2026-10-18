package com.example.myappnavigation.model

data class RegisterModel(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null
)
