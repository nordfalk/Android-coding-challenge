package dk.nordfalk.snakomvejr.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent.*
import android.speech.SpeechRecognizer
import java.util.*

class SOVSpeechListener {

    fun interface Callback {
        fun callback(res: String?)
    }

    private var speechRecognizerIntent: Intent
    private var speechRecognizer: SpeechRecognizer
    var speechDataCallback: Callback? = null
    var recognitionEndCallback: Callback? = null


    constructor(ctx: Context) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(ctx)
        speechRecognizerIntent = Intent(ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(EXTRA_LANGUAGE_MODEL,LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(EXTRA_PARTIAL_RESULTS, true)
        speechRecognizerIntent.putExtra(EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {
                println("onReadyForSpeech bundle = [${bundle}]")
            }

            override fun onBeginningOfSpeech() {
                println("onBeginningOfSpeech")
                speechDataCallback?.callback("")
            }

            override fun onRmsChanged(v: Float) {
                //println("onRmsChanged")
            }

            override fun onBufferReceived(bytes: ByteArray) {
                println("onBufferReceived")
            }

            override fun onEndOfSpeech() {
                println("onEndOfSpeech")
            }

            override fun onError(i: Int) {
                println("onError i = [${i}]")
                recognitionEndCallback?.callback("error $i")
            }

            override fun onResults(bundle: Bundle) {
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                println("onResults data = $data")
                if (data!=null) speechDataCallback?.callback("$data")
                recognitionEndCallback?.callback("ok")
            }

            override fun onPartialResults(bundle: Bundle) {
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                println("onPartialResults data = $data")
                if (data!=null) speechDataCallback?.callback("$data")
            }

            override fun onEvent(i: Int, bundle: Bundle) {
                println("onEvent i = [${i}], bundle = [${bundle}]")
            }
        })

    }

    fun start() {
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    fun stop() {
        speechRecognizer.stopListening()
    }

    fun destroy() {
        speechRecognizer.destroy()
    }

}