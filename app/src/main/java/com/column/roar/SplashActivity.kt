package com.column.roar

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


class SplashActivity : AppCompatActivity() {

    val sharedPref: SharedPreferences by lazy {
        this.getPreferences(Context.MODE_PRIVATE)
    }

    companion object {
        const val RESULT_WRITE_MEMORY = 123
    }

    private lateinit var dialogLayout: AlertDialog
    private lateinit var iconImage: ImageView

    private val requestPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        iconImage = findViewById(R.id.iconView)

        val animation = AnimationUtils.loadAnimation(this, R.anim.bounce_anim)
        iconImage.startAnimation(animation)

        lifecycleScope.launch {
            val isShowed = sharedPref.getBoolean("isShowed", false)

            delay(2000)
            if (!isShowed) {
                slideInDialog()
            } else {
                lifecycleScope.launch {
                    moveToMainActivity()
                }
            }
        }

        if(!checkPermissionApproved()) {
            requestExternalStoragePermission()
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

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("InflateParams")
    private fun showTermAndCondition(): AlertDialog {
        dialogLayout = this.let {
            AlertDialog.Builder(it).apply {
                setCancelable(false)
                val inflater = LayoutInflater.from(this@SplashActivity)
                val view = inflater.inflate(R.layout.splash_rules_dialog, null)

                val iconSmall = view.findViewById<ImageView>(R.id.iconSmall)
                val okayBtn = view.findViewById<MaterialButton>(R.id.okayBtn)
                val checkBtn = view.findViewById<MaterialCheckBox>(R.id.acceptCheckbox)
                val closeBtn = view.findViewById<ImageView>(R.id.closeBtn)
                var isChecked = false
                val privacy = view.findViewById<TextView>(R.id.privacyTerms)

                privacy.text = Html.fromHtml(getString(R.string.privacyAndTerms),FROM_HTML_MODE_LEGACY)

                val animation =
                    AnimationUtils.loadAnimation(this@SplashActivity, R.anim.shake_rotate)
                iconSmall.startAnimation(animation)

                checkBtn.setOnCheckedChangeListener { _, checked ->

                    if (checked) {
                        okayBtn.alpha = 1F
                        isChecked = checked
                    } else {
                        okayBtn.alpha = 0.2F
                        isChecked = checked
                    }
                }

                closeBtn.setOnClickListener {
                    showCloseDialog()
                }

                okayBtn.setOnClickListener {
                    if (isChecked) {
                        lifecycleScope.launch {
                            storeCondition(isChecked)
                            dialogLayout.dismiss()
                            moveToMainActivity()
                        }
                    }
                }
                setView(view)
            }.show()
        }
        return dialogLayout
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
            setPositiveButton("Okay") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun checkPermissionApproved() = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissionResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                requestPermissionResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), RESULT_WRITE_MEMORY
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RESULT_WRITE_MEMORY -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    Timber.i("user accepted location permission")
                } else {
                    Timber.i("User canceled location request")
                }
                return
            }
            else -> { }
        }
    }
}