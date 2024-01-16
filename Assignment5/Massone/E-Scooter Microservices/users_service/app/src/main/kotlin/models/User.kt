package models

data class User(val name: String, val email: String, val password: String, val isMaintainer: Boolean)