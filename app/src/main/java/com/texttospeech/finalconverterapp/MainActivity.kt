package com.texttospeech.finalconverterapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var etText: EditText

    private lateinit var tvStatus: TextView
    private lateinit var btnSpeak: Button
    private lateinit var btnStop: Button
    private lateinit var seekPitch: SeekBar
    private lateinit var seekSpeed: SeekBar
    private lateinit var spinnerLanguage: Spinner
    private lateinit var tvCharCount: TextView

    private lateinit var btnClear: Button



    private val languages = listOf(
        Locale.ENGLISH,
        Locale.FRENCH,
        Locale.GERMAN,
        Locale.ITALIAN,
        Locale.JAPANESE,
        Locale.KOREAN,
        Locale.CHINESE,
        Locale("es", "ES"),
        Locale("ar"),
        Locale("hi", "IN"),
        Locale("ur", "PK"),
        Locale("pt", "PT"),
        Locale("ru", "RU"),
        Locale("tr", "TR"),
        Locale("bn", "BD")
    )

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etText = findViewById(R.id.etText)
        btnSpeak = findViewById(R.id.btnSpeak)
        btnStop = findViewById(R.id.btnStop)
        seekPitch = findViewById(R.id.seekPitch)
        seekSpeed = findViewById(R.id.seekSpeed)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)
        tvStatus = findViewById(R.id.tvStatus)
        tvCharCount = findViewById(R.id.tvCharCount)

        etText.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                tvCharCount.text = "Characters: ${s?.length ?: 0}"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnClear = findViewById(R.id.btnClear)

        btnClear.setOnClickListener {
            etText.text.clear()
            tvStatus.text = "Status: Ready"
            tvStatus.setTextColor(0xFF2E7D32.toInt())
        }




        tts = TextToSpeech(this, this)

        // Set up Spinner with dark text
        val languageNames = languages.map { it.displayLanguage }
        val adapter = ArrayAdapter(
            this,
            R.drawable.spinner_item,
            languageNames
        )
        adapter.setDropDownViewResource(R.drawable.spinner_item)
        spinnerLanguage.adapter = adapter

        btnSpeak.setOnClickListener {
            tvStatus.text = "Status: Speaking"
            tvStatus.setTextColor(0xFF1565C0.toInt())
            speakText()
        }

        btnStop.setOnClickListener {
            tts.stop()
            tvStatus.text = "Status: Stopped"
            tvStatus.setTextColor(0xFFD32F2F.toInt())
        }

    }

    private fun speakText() {
        val text = etText.text.toString()
        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show()
            return
        }

        val pitch = seekPitch.progress / 5.0f
        val speed = seekSpeed.progress / 5.0f

        tts.setPitch(if (pitch < 0.1f) 0.1f else pitch)
        tts.setSpeechRate(if (speed < 0.1f) 0.1f else speed)

        val selectedLanguage = languages[spinnerLanguage.selectedItemPosition]
        tts.language = selectedLanguage

        if (tts.language == null) {
            Toast.makeText(this, "Selected language not supported", Toast.LENGTH_SHORT).show()
            return
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.ENGLISH
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }


}
