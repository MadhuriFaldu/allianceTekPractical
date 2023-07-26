package com.example.alliancetekpractical.ui.photos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.alliancetekpractical.R
import com.example.alliancetekpractical.databinding.ActivityPhotosBinding
import com.example.alliancetekpractical.databinding.ActivityUsersBinding
import com.example.alliancetekpractical.ui.users.UserAdapter
import com.example.alliancetekpractical.ui.users.UsersActivity
import com.example.alliancetekpractical.utility.IMAGE_STORE_PATH
import com.example.alliancetekpractical.utility.showToast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PhotosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotosBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutPhotoHeader.textViewTitle.text = getString(R.string.photos)
        binding.layoutPhotoHeader.imageViewBack.visibility = View.VISIBLE
        binding.layoutPhotoHeader.imageViewBack.setOnClickListener {
            this@PhotosActivity.finish()
        }
        binding.layoutPhotoHeader.ivAddImages.visibility = View.VISIBLE
        binding.layoutPhotoHeader.ivAddImages.setOnClickListener {
            startActivity(Intent(this@PhotosActivity, AddPhotoActivity::class.java))
        }

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
        // Reload images
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchDataFromFireStore()
        }

        firestore = FirebaseFirestore.getInstance()

    }

    override fun onResume() {
        super.onResume()
        fetchDataFromFireStore()
    }

    // Fetch Images from Storage
    private fun fetchDataFromFireStore() {

        try {
            val storageRef = FirebaseStorage.getInstance().reference.child(IMAGE_STORE_PATH)

            storageRef.listAll().addOnSuccessListener { result ->
                val dataList = mutableListOf<ImageModel>()
                for (imageRef in result.items) {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val imageName = imageRef.name

                        val image = ImageModel(imageUrl, imageName)
                        dataList.add(image)

                        //Log.d("MMM",imageUrl +" "+imageName)
                        // Update your RecyclerView with the data
                        binding.rvPhotos.adapter = PhotoAdapter(this, dataList)
                    }.addOnFailureListener { exception -> }
                }
            }.addOnFailureListener { exception -> }

        }catch (e:Exception){
            showToast(e.toString())
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }
}