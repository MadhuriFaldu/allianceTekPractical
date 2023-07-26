package com.example.alliancetekpractical.ui.users

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.alliancetekpractical.R
import com.example.alliancetekpractical.databinding.ActivityUsersBinding
import com.example.alliancetekpractical.ui.login.LoginActivity
import com.example.alliancetekpractical.ui.photos.PhotosActivity
import com.example.alliancetekpractical.utility.STORE_PATH_NAME
import com.example.alliancetekpractical.utility.SharedPref
import com.example.alliancetekpractical.utility.showDialog
import com.example.alliancetekpractical.utility.showToast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var firestore: FirebaseFirestore
    private var backCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_users)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutUsersHeader.textViewTitle.text = getString(R.string.userlist)
        binding.layoutUsersHeader.ivLogOut.visibility = View.VISIBLE
        binding.layoutUsersHeader.ivImages.visibility = View.VISIBLE

        binding.layoutUsersHeader.ivImages.setOnClickListener {
            startActivity(Intent(this@UsersActivity, PhotosActivity::class.java))
        }
        binding.layoutUsersHeader.ivLogOut.setOnClickListener {

            // Dialog for logout
            showDialog(getString(R.string.log_out_msg),
                getString(R.string.sign_out),
                getString(R.string.sign_out),
                { _, _ ->
                    logout()
                },
                getString(R.string.cancel),
                { _, _ -> })
        }

        firestore = FirebaseFirestore.getInstance()

        //getUsers()
    }

    override fun onResume() {
        super.onResume()
        getUsers()
    }

    // Get all registered user list
    private fun getUsers() {
        firestore.collection(STORE_PATH_NAME)
            .get()
            .addOnSuccessListener {users ->

                val arrayList = ArrayList<UserModel>()

                for (user in users) {
                    val user = user.toObject<UserModel>()
                    //showToast(user.toString())
                    arrayList.add(user)
                }

                binding.rvUsers.adapter = UserAdapter(this, arrayList)
                binding.rvUsers.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
            }
            .addOnFailureListener{
                it.printStackTrace()
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onBackPressed() {
        doubleTapToExit()
    }

    // logout user
    private fun logout() {
        SharedPref.clearAll()
        startActivity(Intent(this@UsersActivity,LoginActivity::class.java))
        this.finishAffinity()
    }

    private fun doubleTapToExit() {
        if (backCheck) {
            finishAffinity()
            return
        }

        backCheck = true
        showToast(getString(R.string.please_click_back_again_to_exit))

        Handler(Looper.getMainLooper()).postDelayed({ backCheck = false; }, 2000)
    }
}