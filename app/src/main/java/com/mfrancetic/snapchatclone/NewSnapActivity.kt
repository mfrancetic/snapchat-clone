package com.mfrancetic.snapchatclone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.android.synthetic.main.activity_new_snap.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.*


class NewSnapActivity : AppCompatActivity() {

    private val IMAGE_NOTE: String = ""

    private val IMAGE_CHOOSE_CODE = 1

    private val PERMISSION_CODE_READ = 1

    private var choosePhotoIntent: Intent? = null

    private var photoUri: Uri? = null

    private var imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_snap)

        checkPermissionForImage()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        choose_image_new_snap_button.setOnClickListener(View.OnClickListener {
            chooseImageFromGallery()
        })

        next_new_snap_button.setOnClickListener(View.OnClickListener {
            val noteText = note_new_snap_edit_text.text.toString()
            if (noteText.isBlank()) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.note_text_blank_error),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                goToNextActivity()
            }
        })
    }

    private fun goToNextActivity() {
        val storage = Firebase.storage
        val storageReference = storage.reference

        // upload from data in memory --> bitmap to JPEG file
        new_snap_image_view.isDrawingCacheEnabled = true
        new_snap_image_view.buildDrawingCache()
        val bitmap = (new_snap_image_view.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // create a child with a unique name (UUID + .jpg)
        val uploadTask = storageReference.child("images").child(imageName)
            .putBytes(data)

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(
                baseContext,
                getString(R.string.photo_upload_failure),
                Toast.LENGTH_SHORT
            )
                .show()
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Toast.makeText(
                baseContext,
                getString(R.string.photo_upload_success),
                Toast.LENGTH_SHORT
            )
                .show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CHOOSE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                photoUri = data?.data
                new_snap_image_view.setImageURI(photoUri)
            } else {
                Toast.makeText(
                    baseContext,
                    getString(R.string.choose_image_unsuccessful),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {
                val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(
                    permission,
                    PERMISSION_CODE_READ
                )
            }
        }
    }

    private fun chooseImageFromGallery() {
        if (isPermissionGranted()) {
            choosePhotoIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            choosePhotoIntent!!.type = "image/*"
            startActivityForResult(choosePhotoIntent, IMAGE_CHOOSE_CODE)
        } else {
            checkPermissionForImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE_READ -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                    Toast.makeText(
                        this,
                        getString(R.string.read_external_storage_permission_granted),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    //  askForPermissions()
                    Toast.makeText(
                        this,
                        getString(R.string.read_external_storage_permission_denied),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                return
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}