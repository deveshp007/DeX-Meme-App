package alpha.meme.dexmeme

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class Dashboard : AppCompatActivity() {

    var currentUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        loadMeme()
    }


private fun loadMeme() {
    val progBar = findViewById<View>(R.id.progressBar) as ProgressBar
    progBar.visibility = View.VISIBLE
    val url = "https://meme-api.herokuapp.com/gimme"
    val imageView = findViewById<View>(R.id.memeImageView) as ImageView
    // Request a string response from the provided URL.
    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.GET, url,null,
        { response ->
            currentUrl = response.getString("url")
            Glide.with(this).load(currentUrl).listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progBar.visibility = View.GONE
                    return false
                }
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progBar.visibility = View.GONE
                    return false
                }
            }).into(imageView)
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
        intent.putExtra(Intent.EXTRA_TEXT, "Hey, Checkout this meme. I got it from DeX Meme App. $currentUrl")
        val chooser = Intent.createChooser(intent, "Share this meme using...")
        startActivity(chooser)
    }

    fun nextMeme(view: View) {
        loadMeme()
    }
}