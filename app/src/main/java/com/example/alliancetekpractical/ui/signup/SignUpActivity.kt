package com.example.alliancetekpractical.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.alliancetekpractical.R
import com.example.alliancetekpractical.databinding.ActivitySignUpBinding
import com.example.alliancetekpractical.ui.users.UsersActivity
import com.example.alliancetekpractical.ui.login.LoginViewModel
import com.example.alliancetekpractical.utility.IS_USER_LOGIN
import com.example.alliancetekpractical.utility.STORE_PATH_NAME
import com.example.alliancetekpractical.utility.SharedPref
import com.example.alliancetekpractical.utility.USER_EMAIL
import com.example.alliancetekpractical.utility.afterTextChanged
import com.example.alliancetekpractical.utility.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.layoutSignUpHeader.textViewTitle.text = getString(R.string.signup)
        binding.layoutSignUpHeader.imageViewBack.visibility = View.VISIBLE
        binding.layoutSignUpHeader.imageViewBack.setOnClickListener {
            this@SignUpActivity.finish()
        }

        // handle error state
        loginViewModel.signUpFormState.observe(this@SignUpActivity, Observer {
            val signUpState = it ?: return@Observer

            // disable login button unless both username / password is valid
            binding.tvSignUp.isEnabled = signUpState.isDataValid

            if (signUpState.userFirstNameError != null) {
                binding.edRegisterUserFirstName.error = getString(signUpState.userFirstNameError)
            }
            if (signUpState.userLastNameError != null) {
                binding.edRegisterUserLastName.error = getString(signUpState.userLastNameError)
            }
            if (signUpState.userEmailError != null) {
                binding.edRegisterUserEmail.error = getString(signUpState.userEmailError)
            }
            if (signUpState.passwordError != null) {
                binding.edRegisterPassword.error = getString(signUpState.passwordError)
            }
        })


        binding.edRegisterUserFirstName.afterTextChanged {
            loginViewModel.signUpDataChanged(
                binding.edRegisterUserFirstName.text.toString().trim(),
                binding.edRegisterUserLastName.text.toString().trim(),
                binding.edRegisterUserEmail.text.toString(),
                binding.edRegisterPassword.text.toString()
            )
        }


        binding.edRegisterUserLastName.afterTextChanged {
            loginViewModel.signUpDataChanged(
                binding.edRegisterUserFirstName.text.toString().trim(),
                binding.edRegisterUserLastName.text.toString().trim(),
                binding.edRegisterUserEmail.text.toString(),
                binding.edRegisterPassword.text.toString()
            )
        }


        binding.edRegisterUserEmail.afterTextChanged {
            loginViewModel.signUpDataChanged(
                binding.edRegisterUserFirstName.text.toString().trim(),
                binding.edRegisterUserLastName.text.toString().trim(),
                binding.edRegisterUserEmail.text.toString(),
                binding.edRegisterPassword.text.toString()
            )
        }

        binding.edRegisterPassword.apply {
            afterTextChanged {
                loginViewModel.signUpDataChanged(
                    binding.edRegisterUserFirstName.text.toString().trim(),
                    binding.edRegisterUserLastName.text.toString().trim(),
                    binding.edRegisterUserEmail.text.toString(),
                    binding.edRegisterPassword.text.toString()
                )
            }

            // Register user
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        registerUser(
                            binding.edRegisterUserFirstName.text.toString().trim(),
                            binding.edRegisterUserLastName.text.toString().trim(),
                            binding.edRegisterUserEmail.text.toString().trim(),
                            binding.edRegisterPassword.text.toString()
                        )
                }
                false
            }

            // Register user
            binding.tvSignUp.setOnClickListener {

                binding.loading.visibility = View.VISIBLE
                registerUser(
                    binding.edRegisterUserFirstName.text.toString().trim(),
                    binding.edRegisterUserLastName.text.toString().trim(),
                    binding.edRegisterUserEmail.text.toString().trim(),
                    binding.edRegisterPassword.text.toString()
                )
            }

        }

    }

    // Register user
    private fun registerUser(firstName:String, lastName:String, email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration success to Firestore
                    saveUserDataToFireStore(firstName, lastName, email,password)
                } else {
                    // Registration failed, handle the error
                    val exception = task.exception
                    // ...
                }
            }
    }

    //Save UserData
    private fun saveUserDataToFireStore(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val add = HashMap<String,Any>()

        add["firstName"] = firstName
        add["lastName"] = lastName
        add["email"] = email
        add["password"] = password

        db.collection(STORE_PATH_NAME)
            .add(add)
            .addOnSuccessListener {
                showToast(getString(R.string.data_added))
                SharedPref.save(USER_EMAIL, email.toString())
                SharedPref.save(IS_USER_LOGIN,true)

                startActivity(Intent(this@SignUpActivity, UsersActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                showToast(getString(R.string.data_not_added))
            }

        binding.loading.visibility = View.GONE

    }
}