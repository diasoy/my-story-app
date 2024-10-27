package com.example.mystoryapp.view.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.data.model.LoginDataAccount
import com.example.mystoryapp.data.model.RegisterDataAccount
import com.example.mystoryapp.databinding.ActivityRegisterBinding
import com.example.mystoryapp.helper.AppPreferences
import com.example.mystoryapp.view.viewmodel.AuthViewModel
import com.example.mystoryapp.view.viewmodel.MainViewModel
import com.example.mystoryapp.view.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this)[AuthViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val pref = AppPreferences.getInstance(dataStore)
        val mainViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
        mainViewModel.getLoginSession().observe(this) { sessionTrue ->
            if (sessionTrue) {
                val intent = Intent(this@RegisterActivity, StoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        authViewModel.messageRegist.observe(this) { messageRegist ->
            responseRegister(
                authViewModel.isErrorRegist,
                messageRegist
            )
        }

        authViewModel.isLoadingRegist.observe(this) {
            showLoading(it)
        }

        authViewModel.messageLogin.observe(this) { messageLogin ->
            responseLogin(
                authViewModel.isErrorLogin,
                messageLogin,
                mainViewModel
            )
        }

        authViewModel.isLoadingLogin.observe(this) {
            showLoading(it)
        }

        binding.cbPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.etConfirmPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.etPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.etConfirmPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }

            binding.buttonRegister.setOnClickListener {
                binding.apply {
                    etName.clearFocus()
                    etEmail.clearFocus()
                    etPassword.clearFocus()
                    etConfirmPassword.clearFocus()
                }

                if (binding.etName.isNameValid && binding.etEmail.isEmailValid && binding.etPassword.isPasswordValid && binding.etConfirmPassword.isPasswordValid) {
                    val dataRegisterAccount = RegisterDataAccount(
                        name = binding.etName.text.toString().trim(),
                        email = binding.etEmail.text.toString().trim(),
                        password = binding.etPassword.text.toString().trim()
                    )
                    authViewModel.getResponseRegister(dataRegisterAccount)
                } else {
                    if (!binding.etName.isNameValid) binding.etName.error =
                        resources.getString(R.string.nama_kosong)
                    if (!binding.etEmail.isEmailValid) binding.etEmail.error =
                        resources.getString(R.string.email_kosong)
                    if (!binding.etPassword.isPasswordValid) binding.etPassword.error =
                        resources.getString(R.string.password_kosong)
                    if (!binding.etConfirmPassword.isPasswordValid) binding.etConfirmPassword.error =
                        resources.getString(R.string.password_tidak_sama)

                    Toast.makeText(this, R.string.login_gagal, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun responseLogin(
        isError: Boolean,
        message: String,
        mainViewModel: MainViewModel
    ) {
        if (!isError) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val user = authViewModel.userLogin.value
            mainViewModel.saveLoginSession(true)
            mainViewModel.saveToken(user?.loginResult!!.token)
            mainViewModel.saveName(user.loginResult.name)
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun responseRegister(
        isError: Boolean,
        message: String,
    ) {
        if (!isError) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val userLogin = LoginDataAccount(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
            authViewModel.getResponseLogin(userLogin)
        } else {
            if (message == "1") {
                Toast.makeText(this, resources.getString(R.string.email_digunakan), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }
}