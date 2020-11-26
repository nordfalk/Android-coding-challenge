package dk.nordfalk.snakomvejr.service;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.inputmethod.InputMethodManager;

import java.util.Locale;

public class SOVTextToSpeech implements TextToSpeech.OnInitListener {
    private boolean initialiseret;
    private TextToSpeech tts;
    private String pendingText;

    public SOVTextToSpeech(Context ctx) {
        tts = new TextToSpeech(ctx, this);
        initialiseret = false;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            initialiseret = true;
            int res = tts.setLanguage(new Locale("da", ""));
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                res = tts.setLanguage(Locale.getDefault());
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    res = tts.setLanguage(Locale.US);
                    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                        initialiseret = false;
                        System.out.println("Kunne ikke initialisere TTS");
                    }
                }
            }


            if (initialiseret) {
                Locale sprog = tts.getVoice().getLocale();
                if (pendingText!=null) speak(pendingText);
                else speak("Klar");
                //else speak("Tekst til tale klar for sproget " + sprog.getDisplayLanguage(sprog));
            }
        }
    }

    public void speak(String text) {
        System.out.println("speak text = " + text);
        if (initialiseret) tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        else pendingText = text;
    }

    public void destroy() {
        tts.shutdown();
    }
}
