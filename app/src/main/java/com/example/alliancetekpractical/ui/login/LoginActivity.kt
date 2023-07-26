package com.example.alliancetekpractical.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.alliancetekpractical.R
import com.example.alliancetekpractical.databinding.ActivityLoginBinding
import com.example.alliancetekpractical.ui.users.UsersActivity
import com.example.alliancetekpractical.ui.signup.SignUpActivity
import com.example.alliancetekpractical.utility.IS_USER_LOGIN
import com.example.alliancetekpractical.utility.SharedPref
import com.example.alliancetekpractical.utility.USER_EMAIL
import com.example.alliancetekpractical.utility.afterTextChanged
import com.example.alliancetekpractical.utility.showToast
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.layoutLoginHeader.textViewTitle.text = getString(R.string.login)

        // handle error state
        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            binding.tvLogin.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                binding.edRegisterUserEmail.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                binding.edRegisterPassword.error = getString(loginState.passwordError)
            }
        })


        binding.edRegisterUserEmail.afterTextChanged {
            loginViewModel.loginDataChanged(
                binding.edRegisterUserEmail.text.toString(),
                binding.edRegisterPassword.text.toString()
            )
        }

        binding.edRegisterPassword.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    binding.edRegisterUserEmail.text.toString(),
                    binding.edRegisterPassword.text.toString()
                )
            }

            // Login user
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginUser(
                            binding.edRegisterUserEmail.text.toString().trim(),
                            binding.edRegisterPassword.text.toString()
                        )
                }
                false
            }

            // login user
            binding.tvLogin.setOnClickListener {

                binding.loading.visibility = View.VISIBLE
                loginUser(
                    binding.edRegisterUserEmail.text.toString().trim(),
                    binding.edRegisterPassword.text.toString()
                )
            }

            binding.tvDontHaveAccount.setOnClickListener {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            }
        }
    }

    //Login success
    private fun updateUiWithUser(name:String) {
        val welcome = getString(R.string.welcome)
        val displayName = name
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }


    private fun loginUser(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.loading.visibility = View.GONE
                if (task.isSuccessful) {
                    // Login success, proceed to the main activity
                    val user = auth.currentUser
                    Log.d("MMM",user.toString())
                    updateUiWithUser(user?.email.toString())

                    // store user login data
                    SharedPref.save(USER_EMAIL, user?.email.toString())
                    SharedPref.save(IS_USER_LOGIN,true)

                    startActivity(Intent(this@LoginActivity, UsersActivity::class.java))
                    finish()
                } else {
                    // Login failed
                    val exception = task.exception
                    showToast(getString(R.string.login_failed))
                }
            }
    }
}