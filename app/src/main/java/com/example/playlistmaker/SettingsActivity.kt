package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val returnImageView = findViewById<ImageView>(R.id.return_image)

        returnImageView.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        val shareFrameLayout = findViewById<FrameLayout>(R.id.shareFrameLayout)

        shareFrameLayout.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_body))
            startActivity(shareIntent)
        }

        val writeToSupportFrameLayout = findViewById<FrameLayout>(R.id.writeToSupportFrameLayout)

        writeToSupportFrameLayout.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.write_to_support_subject))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.write_to_support_body))
            startActivity(shareIntent)
        }

        val userAgreementFrameLayout = findViewById<FrameLayout>(R.id.userAgreementFrameLayout)

        userAgreementFrameLayout.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_VIEW)
            shareIntent.data = Uri.parse(getString(R.string.user_agreement_url))
            startActivity(shareIntent)
        }
    }
}