package com.example.practice.activities

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.practice.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.coroutines.delay
import okhttp3.internal.wait


class ProductActivity : BaseActivity() {
    override fun getLayoutResourceID(): Int {
        return R.layout.activity_product
    }

    lateinit var title : String
    lateinit var description : String
    lateinit var artist : String
    lateinit var year : String
    lateinit var country : String
    private lateinit var price : String
    private var image = ""

    private var imageReference = FirebaseStorage.getInstance().reference.child("images")
    private var fileRef: StorageReference? = null

    var filepath : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(R.string.info_del_producto)
        super.onCreate(savedInstanceState)

        findViewById<Button>(R.id.postAlbum).setOnClickListener {

            title = findViewById<EditText>(R.id.albumTitle).text.toString()
            description = findViewById<EditText>(R.id.albumDescription).text.toString()
            artist = findViewById<EditText>(R.id.albumArtist).text.toString()
            year = findViewById<EditText>(R.id.albumReleaseYear).text.toString()
            country = findViewById<EditText>(R.id.albumReleaseCountry).text.toString()
            price = findViewById<EditText>(R.id.albumPrice).text.toString()

            val extras = Bundle();

            extras.putString("artist", artist)
            extras.putString("description", description)
            extras.putString("year", year)
            extras.putString("country", country)
            extras.putString("price", price)
            extras.putString("title", title)
            extras.putString("image", image)

            launchActivity(MarketActivity::class.java, extras)

            finish()
        }

        btUploadImage.setOnClickListener {
            startFileChooser()
        }

    }

    private fun startFileChooser(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Chose Picture"), 111)
    }

    private fun uploadFile() {

        var fileName: String

        if (albumTitle.text.toString() != ""){
            fileName = albumTitle.text.toString()
        }else{
            fileName = "generic"
        }

        if (!validateInputFileName(fileName)) {
            return
        }

        fileRef = imageReference!!.child(fileName + "." + filepath?.let { getFileExtension(it) })
        filepath?.let {
            fileRef!!.putFile(it)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri?> {
                        if (it != null) {
                            val uri = it

                            Log.e("TAG", "Url: $uri")
                            Log.e("TAG", "Name: ${taskSnapshot.metadata!!.name}")
                            val meta = "${taskSnapshot.metadata!!.path} - ${taskSnapshot.metadata!!.sizeBytes / 1024} KBs"
                            tvFilename.text = meta

                            image = uri.toString()

                            Toast.makeText(this, "File Uploaded ", Toast.LENGTH_LONG).show()

                        }else{
                            showError()
                        }
                    })
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                } //progress bar
                .addOnProgressListener { taskSnapshot ->
                    // progress percentage
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount

                    // percentage in progress
                    val intProgress = progress.toInt()
                    val prog = "Uploaded $intProgress%..."
                    tvFilename.text = prog
                }
                .addOnPausedListener { System.out.println("Upload is paused!") }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 111 && resultCode == Activity.RESULT_OK && data != null){
            filepath = data.data!!
            try {
                filepath.let{
                    if(Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, filepath)
                        ivPrev.setImageBitmap(bitmap)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, filepath!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        ivPrev.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        uploadFile()
    }

    private fun showError(){
        Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun validateInputFileName(fileName: String): Boolean {
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(this, "Enter file name!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}