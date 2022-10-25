package edu.ib.visionhelper.manager
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import edu.ib.visionhelper.R
import java.util.*

class SpeechManager(var context: Context) : TextToSpeech.OnInitListener {
    private var textToSpeech: TextToSpeech
    private lateinit var engLocaleTranslator: Translator
    private lateinit var translationOptions: TranslatorOptions
    private var TRANSLATION = 0
    private var preferences: PreferencesManager? = null
    private var isOptionsFinished = false

    init {
        getDownloadedOptions()
        preferences = PreferencesManager(context)
        textToSpeech = TextToSpeech(context, this)
    }

    private fun getDownloadedOptions() {
        val primaryLocale = context.resources.configuration.locales[0]
        //translator options
        translationOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(primaryLocale.language)
            .build()
        engLocaleTranslator = Translation.getClient(translationOptions)
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        // downloads local language model if needed
        engLocaleTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener(
                OnSuccessListener<Any?> {
                    TRANSLATION = 1
                    println("Success")
                })
            .addOnFailureListener(
                OnFailureListener {
                    TRANSLATION = 0
                    println("Failed")
                })
            .addOnCompleteListener(
                OnCompleteListener {
                    isOptionsFinished = true
                }
            )
    }

    fun stopSpeaking() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    /**
     * Function that reads given text
     * @param text - text to be read
     */
    fun speakOut(text: String) {

            textToSpeech.setSpeechRate(1.1f)
            if (TRANSLATION == 1) {
                engLocaleTranslator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        textToSpeech.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                    .addOnFailureListener(OnFailureListener {
                        println("Failed to translate")
                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
                    })
            } else {
                Log.d("TTSFailed", "Failed to translate")
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.getDefault())
            println(Locale.getDefault())

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language not supported!")
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                context.startActivity(installIntent)
            }
            if (preferences!!.firstTimeLaunched == 0) {
                speakOut(context.getString(R.string.main_helper_text))
                preferences!!.firstTimeLaunched = 1
            }

        }

    }


}