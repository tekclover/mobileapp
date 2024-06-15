package com.tvhht.myapplication.login.model

data class LoginModel (

    val apiName : String,
    val clientId : String,
    val clientSecretKey : String,
    val grantType : String,
    val oauthPassword : String,
    val oauthUserName : String
)
