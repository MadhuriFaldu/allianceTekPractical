package com.example.alliancetekpractical.ui.photos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.developers.imagezipper.ImageZipper
import com.example.alliancetekpractical.R
import com.example.alliancetekpractical.databinding.ActivityAddPhotoBinding
import com.example.alliancetekpractical.utility.IMAGE_STORE_PATH
import com.example.alliancetekpractical.utility.PERMISSION_CAMERA
import com.example.alliancetekpractical.utility.PermissionUtility
import com.example.alliancetekpractical.utility.checkStringValue
import com.example.alliancetekpractical.utility.generateRoundCornerPlaceholder
import com.example.alliancetekpractical.utility.getPath
import com.example.alliancetekpractical.utility.showDialog
import com.example.alliancetekpractical.utility.showToast
import java.io.File
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPhotoBinding
    private var isSelectCamera = false
    private var imagePath: String = ""
    private lateinit var storage: FirebaseStorage

    companion object {
        const val REQUEST_IMAGE_PICKER = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutAddPhotoHeader.textViewTitle.text = getString(R.string.photos)
        binding.layoutAddPhotoHeader.imageViewBack.visibility = View.VISIBLE
        binding.layoutAddPhotoHeader.imageViewBack.setOnClickListener {
            this@AddPhotoActivity.finish()
        }
        binding.btnSelectAndUpload.setOnClickListener {
            if (imagePath.isNotEmpty()){
                binding.loading.visibility = View.VISIBLE
                uploadFileToFirestore(filePath = imagePath)
            }
            else showToast(getString(R.string.select_image))
        }

        binding.imageView.setOnClickListener {
            // open dialog for image selection
            showDialog(getString(R.string.choose_image_from),
                "",
                getString(R.string.camera),
                { _, _ -> getPermission(true) },
                getString(R.string.gallery),
                { _, _ -> getPermission(false) })
        }
    }

    // get permission for camera and storage
    private fun getPermission(isCamera :Boolean) {
        isSelectCamera = isCamera
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtility.checkPermission(this, PermissionUtility.CAMERA)) {
                PermissionUtility.requestPermission(
                    this,
                    arrayOf(
                        PermissionUtility.CAMERA,
                        PermissionUtility.WRITE_STORAGE,
                        PermissionUtility.READ_EXTERNAL_STORAGE
                    ),
                    PERMISSION_CAMERA
                )
            } else {
                if (isCamera) {
                    openCamera()
                } else openGallery()
            }
        } else {
            if (isCamera) {
                openCamera()
            } else openGallery()
        }
    }

    // open camera
    private fun openCamera() {
        ImagePicker.with(this).cameraOnly().start(REQUEST_IMAGE_PICKER)
    }

    // open Gallery
    private fun openGallery() {
        ImagePicker.with(this).galleryOnly().start(REQUEST_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // retrieve data from another activity
        if (data?.data == null) return

        if (!checkStringValue(getPath(this, data?.data!!))){
            showToast(resources.getString(R.string.image_path_is_not_valid))
            return
        }
        val file = File(getPath(this, data.data!!).toString())

        // compress image
        val imageZipperFile = ImageZipper(this)
            .setQuality(50)
            .setMaxWidth(400)
            .setMaxHeight(400)
            .compressToFile(file)

        if (requestCode == REQUEST_IMAGE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                binding.imageView.setImageURI(data?.data)
                imagePath = imageZipperFile.absolutePath ?: ""
            }
        }
    }

    // Upload image in fire store
    private fun uploadFileToFirestore(filePath: String) {

        storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val file = Uri.fromFile(File(filePath))
        val fileName = file.lastPathSegment ?: "default_filename"
        val imageRef = storageRef.child(IMAGE_STORE_PATH+fileName)

        val uploadTask = imageRef.putFile(file)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            // Continue with the task to get the download URL
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                // Save the downloadUri and other metadata to Firestore
                //saveDownloadedFileUrlToFirestore(downloadUri.toString(), fileName)
                showToast(getString(R.string.image_uploaded_successfully))
                this@AddPhotoActivity.finish()
            } else {
                // Handle upload failure
                val exception = task.exception
                // ...
            }
        }

        binding.loading.visibility = View.GONE
    }

}