package com.example.alliancetekpractical.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alliancetekpractical.R

class LoginViewModel() : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _signupForm = MutableLiveData<SignUpState>()
    val signUpFormState: LiveData<SignUpState> = _signupForm

   // login validation check
    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // Signup validation check
    fun signUpDataChanged(firstName: String, lastName: String, email: String, password: String) {
        if (firstName.isEmpty()) {
            _signupForm.value = SignUpState(userFirstNameError = R.string.enter_firstname)
        }else if (lastName.isEmpty()) {
            _signupForm.value = SignUpState(userLastNameError = R.string.enter_lastname)
        }else if (!isUserNameValid(email)) {
            _signupForm.value = SignUpState(userEmailError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _signupForm.value = SignUpState(passwordError = R.string.invalid_password)
        } else {
            _signupForm.value = SignUpState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.isNotEmpty()) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}