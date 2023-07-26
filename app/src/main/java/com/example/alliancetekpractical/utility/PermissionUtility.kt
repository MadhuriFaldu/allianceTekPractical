package com.example.alliancetekpractical.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// Get user permission
class PermissionUtility {

    companion object{
        @JvmStatic
        val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
        val CAMERA = Manifest.permission.CAMERA
        val WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

        fun checkPermission(context: Context, permission: String): Boolean {
            val result = ContextCompat.checkSelfPermission(context.applicationContext, permission)
            return result == PackageManager.PERMISSION_GRANTED
        }
        fun requestPermission(activity: Activity, permissions: Array<String>, requestCode: Int) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }
}