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
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    val sharedPref: SharedPreferences by lazy {
        this.getPreferences(Context.MODE_PRIVATE)
    }

    private lateinit var dialogLayout: AlertDialog
    private lateinit var iconImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        iconImage = findViewById(R.id.iconView)

        val animation = AnimationUtils.loadAnimation(this, R.anim.bounce_anim)
        iconImage.startAnimation(animation)

        lifecycleScope.launch {
            val isShowed = sharedPref.getBoolean("isShowed", false)

            delay(2500)
            if (!isShowed) {
               slideInDialog()
            } else {
                moveToMainActivity()
            }
        }
    }

    private fun slideInDialog(): Animator {
        val translationY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 2000f, 0f) //was  1000f
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)

        val animateDialog = ObjectAnimator.ofPropertyValuesHolder(
            showTermAndCondition(),
            translationY,
            scaleX,
            scaleY,
            alpha
        ).apply {
            interpolator = OvershootInterpolator()
        }

        return AnimatorSet().apply {
            play(animateDialog)
        }
    }

    @SuppressLint("InflateParams")
    private fun showTermAndCondition(): AlertDialog {
        dialogLayout = MaterialAlertDialogBuilder(this).apply {
            val inflater = LayoutInflater.from(this@SplashActivity)
            val view = inflater.inflate(R.layout.splash_rules_dialog, null)

            val iconSmall = view.findViewById<ImageView>(R.id.iconSmall)
            val okayBtn = view.findViewById<MaterialButton>(R.id.okayBtn)
            val checkBtn = view.findViewById<MaterialCheckBox>(R.id.acceptCheckbox)
            val closeBtn = view.findViewById<ImageView>(R.id.closeBtn)
            var isChecked = false

            val animation = AnimationUtils.loadAnimation(this@SplashActivity, R.anim.shake_rotate)
            iconSmall.startAnimation(animation)

            checkBtn.setOnCheckedChangeListener { _,  checked ->

                if(checked) {
                    okayBtn.alpha = 1F
                }else {
                    okayBtn.alpha = 0.2F
                    isChecked = checked
                }
            }

            closeBtn.setOnClickListener {
                showCloseDialog()
            }

            okayBtn.setOnClickListener {
               // if(isChecked) {
                    //storeCondition(isChecked)
                dialogLayout.dismiss()
                hideImageView()
                  //  moveToMainActivity()
              //  }
            }
            setCancelable(false)
            setView(view)
        }.show()
        return dialogLayout
    }

    private fun hideImageView() {
        val hideAlpha =PropertyValuesHolder.ofFloat(View.ALPHA,1f,0f)
        val hideIcon = ObjectAnimator.ofPropertyValuesHolder(iconImage,hideAlpha).apply {
            duration = 500
        }

      AnimatorSet().apply {
            play(hideIcon)
            moveToMainActivity()
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

    private fun showCloseDialog() {
        MaterialAlertDialogBuilder(this).apply {
            setTitle("You're about to exit app")
            setPositiveButton("Okay") { dialog , _ ->
                dialog.dismiss()
                finish()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

}