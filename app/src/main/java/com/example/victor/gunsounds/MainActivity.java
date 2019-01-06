package com.example.victor.gunsounds;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager manager;
    private Sensor accelerometer;
    private Button button;
    private SoundPool soundPool;
    private int sound1;
    private int numGuns = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find manual fire button
        button = findViewById(R.id.button2);

        // Initialize SensorManager and accelerometer
        manager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Create soundpool based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder().setMaxStreams(numGuns).build();
        }
        else {
            soundPool = new SoundPool(numGuns, AudioManager.STREAM_MUSIC, 0);
        }

        // Load sounds into sound pool
        sound1 = soundPool.load(this, R.raw.gunshot_9_mm, 1);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        fire();
    }

    // On click firing
    public void fire(View view) {
        soundPool.play(sound1, 1, 1, 0, 0, 1);
    }

    // On tilt firing
    public void fire() {
        soundPool.play(sound1, 1, 1, 0, 0, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
