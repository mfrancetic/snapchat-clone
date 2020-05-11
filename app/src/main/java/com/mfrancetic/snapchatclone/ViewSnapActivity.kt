package com.mfrancetic.snapchatclone

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_view_snap.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        displaySnapDetails()
    }

    private fun displaySnapDetails() {
        if (intent != null) {
            val snapMessage = intent.getStringExtra(Constants.MESSAGE_KEY)
            val imageUrl = intent.getStringExtra(Constants.IMAGE_URL_KEY)
            val imageName = intent.getStringExtra(Constants.IMAGE_NAME_KEY)
            val snapshotId = intent.getStringExtra(Constants.SNAP_KEY)

            snap_detail_message.text = snapMessage
            downloadImage(imageUrl)
        }
    }

    private fun downloadImage(imageUrl: String?) {
        try {
            val bitmap = ImageDownloader().execute(imageUrl).get()
            snap_detail_image.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                getString(R.string.image_download_unsuccessful),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class ImageDownloader : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg urls: String?): Bitmap? {
            return try {
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}