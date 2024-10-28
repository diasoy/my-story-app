package com.example.mystoryapp.view.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.data.model.LoginDataAccount
import com.example.mystoryapp.data.model.RegisterDataAccount
import com.example.mystoryapp.databinding.ActivityRegisterBinding
import com.example.mystoryapp.view.viewmodel.AuthViewModel

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

        setupObservers()

        binding.cbPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.etConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.etConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        binding.buttonRegister.setOnClickListener {
            binding.apply {
                etName.clearFocus()
                etEmail.clearFocus()
                etPassword.clearFocus()
                etConfirmPassword.clearFocus()
            }

            if (isRegisterFormValid()) {
                val dataRegisterAccount = RegisterDataAccount(
                    name = binding.etName.text.toString().trim(),
                    email = binding.etEmail.text.toString().trim(),
                    password = binding.etPassword.text.toString().trim()
                )
                authViewModel.getResponseRegister(dataRegisterAccount)
            } else {
                showRegisterError()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToLogin()
    }

    private fun setupObservers() {
        authViewModel.isLoadingRegist.observe(this) { showLoading(it) }
        authViewModel.messageRegist.observe(this) { response ->
            response?.let { responseRegister() }
        }
    }

    private fun isRegisterFormValid(): Boolean {
        return binding.etName.isNameValid &&
                binding.etEmail.isEmailValid &&
                binding.etPassword.isPasswordValid &&
                binding.etConfirmPassword.isPasswordValid
    }

    private fun showRegisterError() {
        if (!binding.etName.isNameValid) binding.etName.error = getString(R.string.nama_kosong)
        if (!binding.etEmail.isEmailValid) binding.etEmail.error = getString(R.string.email_kosong)
        if (!binding.etPassword.isPasswordValid) binding.etPassword.error = getString(R.string.password_kosong)
        if (!binding.etConfirmPassword.isPasswordValid) binding.etConfirmPassword.error = getString(R.string.password_tidak_sama)
        Toast.makeText(this, R.string.login_gagal, Toast.LENGTH_SHORT).show()
    }

    private fun responseRegister() {
        authViewModel.isErrorRegist.observe(this) { isError ->
            authViewModel.messageRegist.observe(this) { message ->
                if (!isError) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    val userLogin = LoginDataAccount(
                        binding.etEmail.text.toString(),
                        binding.etPassword.text.toString()
                    )
                    authViewModel.getResponseLogin(userLogin)
                    navigateToLogin()
                } else {
                    if (message == "1") {
                        Toast.makeText(this, resources.getString(R.string.email_digunakan), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}