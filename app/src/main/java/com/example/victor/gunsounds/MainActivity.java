package com.example.victor.gunsounds;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager manager;
    private Sensor accelerometer;
    private ToggleButton toggleButton;
    private SoundPool soundPool;

    private float previousY;
    private float previousX;
    private boolean loaded;
    private boolean tiltFireOn;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private ImageButton imageButton;

    // Guns and sounds
    private String[] guns = {
            "9mm Handgun",
            "Finger Gun"
    };

    private int[] images = {
            R.drawable.handgun_9mm,
            R.drawable.finger_gun
    };

    private int[] sounds = new int[guns.length];


//    private TextView xView;
//    private TextView yView;
//    private TextView zView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Keep screen on while the app is open
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Identify toggle button
        toggleButton = findViewById(R.id.toggleButton);

        // Initialize SensorManager and accelerometer
        manager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Verify accelerator exists
        if (accelerometer != null) {
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            tiltFireOn = true;
        }
        else {
            tiltFireOn = false;
            toggleButton.setEnabled(false);
        }

        // Set toggle button to match
        toggleButton.setChecked(tiltFireOn);

        // Create soundpool based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder().setMaxStreams(guns.length).build();
        }
        else {
            soundPool = new SoundPool(guns.length, AudioManager.STREAM_MUSIC, 0);
        }

        // Identify and populate spinner
        spinner = findViewById(R.id.spinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, guns);
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);


        // Load sounds into sound pool
        sounds[0] = soundPool.load(this, R.raw.gunshot_9_mm, 1);

        // Set image button
        imageButton = findViewById(R.id.imageButton);
        imageButton.setImageResource(R.drawable.handgun_9mm);

//        xView = findViewById(R.id.textView);
//        yView = findViewById(R.id.textView2);
//        zView = findViewById(R.id.textView3);

        // Load the gun
        loaded = true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (tiltFireOn) {
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

            // Horizontal firing
            if (-2 < x && x < 1.5) {
                // Check phone's previous orientation before firing
                if ((y > 0) && (y - previousY > 1.5)) {
                    fire();
                }
            }
            // Phone sideways firing
            else if (-3 < z && z < 3) {
                if (x < 9 && previousX - x > 2) {
                    fire();
                }
            }

            previousY = y;
            previousX = x;
        }
    }

    // Toggle tilt fire
    public void toggleTilt(View view) {
        tiltFireOn = !tiltFireOn;
    }

    // On click firing
    public void fire(View view) {
        soundPool.play(sounds[0], 1, 1, 0, 0, 1);
    }

    // On tilt firing
    public void fire() {
        if (loaded) {
            soundPool.play(sounds[0], 1, 1, 0, 0, 1);
            loaded = false;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    loaded = true;
                }
            }, 100);
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
