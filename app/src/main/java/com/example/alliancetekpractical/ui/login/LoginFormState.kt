package com.example.alliancetekpractical.ui.login

/**
 * Data validation state of the login and signup form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)

data class SignUpState(
    val userFirstNameError: Int? = null,
    val userLastNameError: Int? = null,
    val userEmailError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)