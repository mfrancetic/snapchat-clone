package com.mfrancetic.snapchatclone

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_new_snap.*
import java.util.jar.Manifest


class NewSnapActivity : AppCompatActivity() {

    private val IMAGE_CHOOSE_CODE = 1

    private val PERMISSION_CODE_READ = 1

    private var choosePhotoIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_snap)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        choose_image_new_snap_button.setOnClickListener(View.OnClickListener {
            checkPermissionForImage()
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
        Toast.makeText(baseContext, "go to next activity", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CHOOSE_CODE) {
            new_snap_image_view.setImageURI(data?.data)
        } else {
            Toast.makeText(
                baseContext,
                getString(R.string.choose_image_unsuccessful),
                Toast.LENGTH_SHORT
            )
                .show()
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
            } else {
                chooseImageFromGallery()
            }
        }
    }

    private fun chooseImageFromGallery() {
        choosePhotoIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        choosePhotoIntent!!.type = "image/*"
        startActivityForResult(choosePhotoIntent, IMAGE_CHOOSE_CODE)
    }
}