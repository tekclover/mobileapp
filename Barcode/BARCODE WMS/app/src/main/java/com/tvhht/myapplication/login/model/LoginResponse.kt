package com.tvhht.myapplication.login.model


data class LoginResponse (

    val access_token : String,
    val token_type : String,
    val refresh_token : String,
    val expires_in : Int,
    val scope : String,
    val jti : String
)
