package com.column.roar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
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

    companion object {
        const val RESULT_WRITE_MEMORY = 123
    }

    private lateinit var dialogLayout: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            val isShowed = sharedPref.getBoolean("isShowed", false)

            delay(1000)
            if (!isShowed) {
                showTermAndCondition()
            } else {
                lifecycleScope.launch {
                    moveToMainActivity()
                }
            }
        }
    }

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    privacy.text = Html.fromHtml(getString(R.string.privacyAndTerms),FROM_HTML_MODE_LEGACY).toString()
                }else {
                    privacy.text = Html.fromHtml(getString(R.string.privacyAndTerms)).toString()
                }

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

}