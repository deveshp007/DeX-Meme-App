package alpha.meme.dexmeme

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.button.MaterialButton

class Dashboard : AppCompatActivity() {

    var currentUrl: String? = null
    private var progBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        loadMeme()
        progBar = findViewById<View>(R.id.progressBar) as ProgressBar
        val nextButton = findViewById<MaterialButton>(R.id.nextButton)
        nextButton.setOnClickListener { loadMeme() }
    }

    @SuppressLint("SetTextI18n")
    private fun loadMeme() {
        progBar?.visibility = View.VISIBLE
        val url = "https://meme-api.com/gimme"

        // API call for Title text +++++++++++++++++++
        // Request title from the URL

        val title = findViewById<View>(R.id.textView2) as TextView
        // Instantiate the RequestQueue.
        // API call for Image ++++++++++++++++++++++++++

        val imageView = findViewById<View>(R.id.memeImageView) as ImageView
        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val previews = response.getJSONArray("preview")
                val title1 = response.getString("title")
                title.text = title1
                currentUrl = when (previews.length()) {
                    3 -> previews[2].toString()
                    2 -> previews[1].toString()
                    1 -> previews[0].toString()
                    else -> previews[2].toString()
                }
                Glide.with(this).load(currentUrl).listener(glideListener).into(imageView)
            },
            {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
            })

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun shareMeme(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey, Checkout this meme. I got it from DeX Meme App. $currentUrl"
        )
        val chooser = Intent.createChooser(intent, "Share this meme using...")
        startActivity(chooser)
    }


    private val glideListener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            progBar?.visibility = View.GONE
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?, dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            progBar?.visibility = View.GONE
            return false
        }
    }


    // A function to check the permissions
    private fun hasPermission(): Boolean = ActivityCompat.checkSelfPermission(
        applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED


    fun downloadMeme(view: View) {

        if (hasPermission()) {

            val dlRequest = DownloadManager.Request(Uri.parse(currentUrl))
            dlRequest.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "DeXMeme.png"
            )
            dlRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val dlManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dlManager.enqueue(dlRequest)
            Toast.makeText(
                applicationContext,
                "File Downloading",
                Toast.LENGTH_SHORT
            ).show()
        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1
            )
            Toast.makeText(
                applicationContext,
                "File Not Downloaded",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

