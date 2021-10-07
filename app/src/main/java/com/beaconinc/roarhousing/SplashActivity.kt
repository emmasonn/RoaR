package com.beaconinc.roarhousing

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    val sharedPref: SharedPreferences by lazy {
        this.getPreferences(Context.MODE_PRIVATE)
    }

    private lateinit var dialogLayout: MaterialAlertDialogBuilder
    private lateinit var iconImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        dialogLayout = showTermAndCondition()
        iconImage = findViewById(R.id.iconView)

        val animation = AnimationUtils.loadAnimation(this, R.anim.bounce_anim)
        iconImage.startAnimation(animation)

        lifecycleScope.launch {
            val isShowed = sharedPref.getBoolean("isShowed", false)

            delay(2000)
            if (!isShowed) {
               slideInDialog()
            } else {
                moveToMainActivity()
            }
        }
    }

    private fun slideInDialog(): Animator {
        val translationY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 1000f, 0f) //was  1000f
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 1f)
        val hideAlpha =PropertyValuesHolder.ofFloat(View.ALPHA,1f,0f)

        val dialog = ObjectAnimator.ofPropertyValuesHolder(dialogLayout.show(), translationY, alpha)
        val hideIcon = ObjectAnimator.ofPropertyValuesHolder(iconImage,hideAlpha)

        return AnimatorSet().apply {
            play(hideIcon)
            play(dialog)
        }
    }

    @SuppressLint("InflateParams")
    private fun showTermAndCondition(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(this).apply {
            val inflater = LayoutInflater.from(this@SplashActivity)
            val view = inflater.inflate(R.layout.splash_rules_dialog, null)

            val okayBtn = view.findViewById<MaterialButton>(R.id.okayBtn)
            val checkBtn = view.findViewById<MaterialCheckBox>(R.id.acceptCheckbox)
            var isChecked = false

            checkBtn.setOnCheckedChangeListener { _,  checked ->

                if(checked) {
                    okayBtn.alpha = 1F
                    isChecked = checked
                   // storeCondition(checked)
                }
            }

            okayBtn.setOnClickListener {
                if(isChecked) {
                    moveToMainActivity()
                }
            }
            setCancelable(false)
            setView(view)
        }
    }

    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    private fun storeCondition(condition: Boolean) {
        with(sharedPref.edit()) {
            this.putBoolean("isShowed", condition)
            commit()
        }
    }
}