package com.example.mystoryapp.view.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.databinding.ActivityAddStoryBinding
import com.example.mystoryapp.helper.AppPreferences
import com.example.mystoryapp.view.viewmodel.AddStoryViewModel
import com.example.mystoryapp.view.viewmodel.MainViewModel
import com.example.mystoryapp.view.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var userToken: String
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.buat_story)

        checkPermissions()
        setupUI()
        setupAnimation()

        val preferences = AppPreferences.getInstance(dataStore)
        val mainViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[MainViewModel::class.java]

        mainViewModel.getToken().observe(this) { token ->
            userToken = token
            setupViewModel()
        }
    }

    private fun setupViewModel() {
        addStoryViewModel = ViewModelProvider(this, ViewModelFactory(AppPreferences.getInstance(dataStore)))[AddStoryViewModel::class.java]

        addStoryViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarAddStory.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        addStoryViewModel.message.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        addStoryViewModel.isUploaded.observe(this) { isUploaded ->
            if (isUploaded) {
                startActivity(Intent(this, StoryActivity::class.java))
                finish()
            }
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = grantResults.indices.filter {
                grantResults[it] != PackageManager.PERMISSION_GRANTED
            }
            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(this, "Izin kamera dan galeri diperlukan untuk melanjutkan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUI() {
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let { uri ->
                    val compressedImage = compressImage(uri)
                    binding.imgUpload.setImageBitmap(compressedImage)
                    binding.imgUpload.alpha = 1f
                } ?: run {
                    Toast.makeText(this, "Gagal mendapatkan gambar dari galeri", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photo: Bitmap? = result.data?.extras?.get("data") as? Bitmap
                photo?.let {
                    val compressedImage = compressImage(saveImageToInternalStorage(it))
                    binding.imgUpload.setImageBitmap(compressedImage)
                    binding.imgUpload.alpha = 1f
                    selectedImageUri = saveImageToInternalStorage(compressedImage)
                } ?: run {
                    Toast.makeText(this, "Gagal mengambil gambar dari kamera", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCamera.setOnClickListener { openCamera() }
        binding.btnGaleri.setOnClickListener { openGallery() }
        binding.btnPostStory.setOnClickListener { uploadStory() }
    }

    private fun openCamera() {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        galleryLauncher.launch(galleryIntent)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val file = File(cacheDir, "story_image.jpg")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
        }
        return Uri.fromFile(file)
    }

    private fun compressImage(imageUri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(imageUri)!!
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        var imageBytes = outputStream.toByteArray()

        if (imageBytes.size > 1_000_000) {
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            imageBytes = outputStream.toByteArray()
        }

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun uploadStory() {
        val description = binding.etDeskripsi.text.toString()
        if (selectedImageUri != null && description.isNotBlank()) {
            val inputStream = contentResolver.openInputStream(selectedImageUri!!)
            val tempFile = File(cacheDir, "temp_image.jpg")
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            if (tempFile.length() > 1_000_000) {
                Toast.makeText(this, "Ukuran file terlalu besar, harus di bawah 1 MB", Toast.LENGTH_SHORT).show()
                return
            }

            val requestImageFile = MultipartBody.Part.createFormData(
                "photo", tempFile.name, tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            val requestDescription = description.toRequestBody("text/plain".toMediaTypeOrNull())

            addStoryViewModel.uploadStory(requestImageFile, requestDescription, userToken)
        } else {
            Toast.makeText(this, "Tolong isi deskripsi dan image dengan benar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAnimation() {
        ObjectAnimator.ofFloat(binding.imgUpload, View.ALPHA, 0f, 1f).apply {
            duration = 1000
            interpolator = AccelerateInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(binding.btnPostStory, View.TRANSLATION_Y, 300f, 0f).apply {
            duration = 800
            start()
        }

        ObjectAnimator.ofFloat(binding.btnCamera, View.TRANSLATION_X, -300f, 0f).apply {
            duration = 800
            start()
        }

        ObjectAnimator.ofFloat(binding.btnGaleri, View.TRANSLATION_X, 300f, 0f).apply {
            duration = 800
            start()
        }
    }
}
