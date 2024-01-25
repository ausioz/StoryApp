package com.example.storyapp.ui.user.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.customview.LoadingDialogFragment
import com.example.storyapp.ui.welcome.WelcomeActivity

class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this,application)
    }
    private lateinit var binding: ActivityRegisterBinding
    private val loadingDialog = LoadingDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        viewModel.registerData.observe(this) {
            if (it.error == false) {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,WelcomeActivity::class.java))
            }
        }

        viewModel.errorMsg.observe(this) {
            showError(it)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.registerButton.setOnClickListener {
            viewModel.register(
                binding.nameEditText.text.toString(),
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )

        }

        binding.emailEditText.addTextChangedListener {
            setButtonEnable()
        }
        binding.passwordEditText.addTextChangedListener {
            setButtonEnable()
        }

    }

    private fun showError(errorMsg: String?) {
        Toast.makeText(this, "Error! \n$errorMsg", Toast.LENGTH_SHORT).show()
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show(supportFragmentManager, "loadingDialog")
        } else {
            if (loadingDialog.isVisible) loadingDialog.dismiss()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val textView = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameET = ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 1f).setDuration(500)
        val nameTL =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val nameTV = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val mailET = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val mailTL =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val mailTV = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val passET =
            ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val passTL =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passTV =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val registerBT =
            ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)

        val nameTogether = AnimatorSet().apply {
            playTogether(nameET, nameTL, nameTV)
        }
        val mailTogether = AnimatorSet().apply {
            playTogether(mailET, mailTL, mailTV)
        }
        val passTogether = AnimatorSet().apply {
            playTogether(textView,passET, passTL, passTV)
        }


        AnimatorSet().apply {
            playSequentially(nameTogether, mailTogether, passTogether, registerBT)
            start()
        }

    }

    private fun setButtonEnable() {
        val email = binding.emailEditText
        val password = binding.passwordEditText
        binding.registerButton.isEnabled = email.text != null && email.text.toString()
            .isNotEmpty() && email.error.isNullOrEmpty() && password.text != null && password.text.toString()
            .isNotEmpty() && password.error.isNullOrEmpty()
    }
}