package dk.nordfalk.snakomvejr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import dk.nordfalk.snakomvejr.MainActivity;
import dk.nordfalk.snakomvejr.R;


public class Splash_akt extends AppCompatActivity implements Runnable {

  Handler handler = new Handler(Looper.getMainLooper());

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_splash);

    Log.d("Velkomst_akt", "aktiviteten blev startet!");

    // Hvis savedInstanceState ikke er null er det en aktivitet der er ved at blive genstartet
    if (savedInstanceState == null) {
      handler.postDelayed(this, 3000); // <1> Kør run() om 3 sekunder
    }
  }

  public void run() {
    startActivity(new Intent(this, MainActivity.class));
    // overgang til næste aktivitet
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    finish();  // <2> Luk velkomstaktiviteten
  }

  /**
   * Kaldes hvis brugeren trykker på tilbage-knappen.
   * I så tilfælde skal vi ikke hoppe videre til næste aktivitet
   */
  @Override
  public void finish() {
    super.finish();
    handler.removeCallbacks(this);
  }
}