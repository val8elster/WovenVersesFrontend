package com.example.verseverwebt.valueobjects.permissions

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

//unique PermissionManager object to manage the permission request
object PermissionManager {

    private const val PERMISSION_REQUEST_CODE = 101

    //a variable that specifies whether the authorizations have been permitted
    private var allowedPermission by mutableStateOf(false)

    //the permission that may be required
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_MEDIA_IMAGES
    )

    //this function is responsible for the permission request and its result
    fun requestUserPermissions(activity: Activity, onPermissionsGranted: () -> Unit, onPermissionsDenied: () -> Unit) {
        //checks whether all permissions have been given
        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        //if permission has been granted, the submitted function onPermissionsGranted will be executed
        // if the permissions were rejected, the function onPermissionsDenied passed is executed
        // and allowedPermission would be set true
        if (notGrantedPermissions.isEmpty()) {
            onPermissionsGranted()
        } else {
            showPermissionExplanation(activity) {
                ActivityCompat.requestPermissions(
                    activity,
                    notGrantedPermissions.toTypedArray(),
                    PERMISSION_REQUEST_CODE
                )
            }
            onPermissionsDenied()
            allowedPermission = false
        }
    }

    //This function generates the pop-up with content
    private fun showPermissionExplanation(activity: Activity, onPositiveButtonClicked: () -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle("Allow Permissions")
            .setMessage("This app requires access to your photos, microphone and sensors to ensure full functionality.Please agree to use the app")
            .setPositiveButton("Agree") { _, _ ->
                allowedPermission = true
                onPositiveButtonClicked() }
            .setNegativeButton("Disagree") { _, _ -> allowedPermission = false }
            .create()
            .show()
    }

    //this function returns the value to check in other classes whether the consent has been given
    // and thus the registration can take place
    fun isPermissionAllowed(): Boolean {
        return allowedPermission
    }

}
