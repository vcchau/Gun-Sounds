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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager manager;
    private Sensor accelerometer;
    private ImageButton button;
    private SoundPool soundPool;
    private int sound1;
    private int numGuns = 1;
    private TextView xView;
    private TextView yView;
    private TextView zView;
    private float previousY;
    private float previousX;
    private boolean loaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Find manual fire button
        button = findViewById(R.id.imageButton);

        // Initialize SensorManager and accelerometer
        manager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);


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

        xView = findViewById(R.id.textView);
        yView = findViewById(R.id.textView2);
        zView = findViewById(R.id.textView3);

        // Load the gun
        loaded = true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /* X and Z directional changes
        *  -2 < x < 2
        *  Phone placed horizontal with some room for error
        *  ______________
        *
        *  -3 < z < 3
        *  Phone placed vertical up/down with some room for error
        *     |
        *     |
        *     |
        *     |
        *
        *  Fire based on phone's orientation */
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

//        xView.setText("X: " + x);
//        yView.setText("Y: " + y);
//        zView.setText("Z: " + z);

        // Horizontal firing
        if (-2 < x && x < 1.5) {
            // Check phone's previous orientation before firing
            if ((y > previousY) && (y - previousY > 2)) {
                fire();
            }
        }
        // Phone sideways firing
        else if (-3 < z && z < 3) {
            if (previousX > x && previousX - x > 2) {
                fire();
            }
        }

        previousY = y;
        previousX = x;
    }

    // On click firing
    public void fire(View view) {
        soundPool.play(sound1, 1, 1, 0, 0, 1);
    }

    // On tilt firing
    public void fire() {
        if (loaded) {
            soundPool.play(sound1, 1, 1, 0, 0, 1);
            loaded = false;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    loaded = true;
                }
            }, 150);
            // Replace 150 with a variable denoting a certain weapon's firing rate
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
