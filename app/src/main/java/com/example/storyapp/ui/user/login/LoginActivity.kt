package com.example.storyapp.ui.user.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.customview.LoadingDialogFragment
import com.example.storyapp.ui.main.MainActivity


class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private val loadingDialog = LoadingDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()

        viewModel.errorMsg.observe(this) {
            showError(it)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.loginData.observe(this) {
            if (it.error == false) {
                viewModel.saveSession(
                    UserModel(
                        binding.emailEditTextLogin.text.toString(),
                        it.loginResult?.name.toString(),
                        it.loginResult?.token.toString(),
                        true
                    )
                )
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.loginButton.setOnClickListener {
            viewModel.login(
                binding.emailEditTextLogin.text.toString(),
                binding.passwordEditTextLogin.text.toString()
            )
        }

        binding.emailEditTextLogin.addTextChangedListener {
            setButtonEnable()
        }

        binding.passwordEditTextLogin.addTextChangedListener {
            setButtonEnable()
        }


    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showError(errorMsg: String?) {
        Toast.makeText(this, "Error! \n$errorMsg", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        loadingDialog.isCancelable = false
        if (isLoading) {
            loadingDialog.show(supportFragmentManager, "loadingDialog")
        } else {
            if (loadingDialog.isVisible) loadingDialog.dismiss()
        }
    }

    private fun playAnimation() {
            ObjectAnimator.ofFloat(binding.imageViewLogin, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 3000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val mailET =
                ObjectAnimator.ofFloat(binding.emailEditTextLogin, View.ALPHA, 1f).setDuration(500)
            val mailTL = ObjectAnimator.ofFloat(binding.emailEditTextLayoutLogin, View.ALPHA, 1f)
                .setDuration(500)
            val mailTV =
                ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
            val passET = ObjectAnimator.ofFloat(binding.passwordEditTextLogin, View.ALPHA, 1f)
                .setDuration(500)
            val passTL = ObjectAnimator.ofFloat(binding.passwordEditTextLayoutLogin, View.ALPHA, 1f)
                .setDuration(500)
            val passTV =
                ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
            val loginBT =
                ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

            val mailTogether = AnimatorSet().apply {
                playTogether(mailET, mailTL, mailTV)
            }
            val passTogether = AnimatorSet().apply {
                playTogether(passET, passTL, passTV)
            }


            AnimatorSet().apply {
                playSequentially(mailTogether, passTogether, loginBT)
                start()
            }
    }

    private fun setButtonEnable() {
        val email = binding.emailEditTextLogin
        val password = binding.passwordEditTextLogin
        binding.loginButton.isEnabled = email.text != null && email.text.toString()
            .isNotEmpty() && email.error.isNullOrEmpty() && password.text != null && password.text.toString()
            .isNotEmpty() && password.error.isNullOrEmpty()

        with(binding.loginButton){
            background = if (isEnabled) ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
            else ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable

            setTextColor(ContextCompat.getColor(context, android.R.color.background_light))
            textSize = 12f
            gravity = Gravity.CENTER
            text =
                if (isEnabled) context.getString(R.string.kirim) else context.getString(R.string.isi_dulu)
        }
    }


}