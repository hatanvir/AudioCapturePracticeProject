package com.example.tanvir.audiocapturepractice;

import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    ImageView recordImBt,playImBt,stopImBt,imageView;
    public static final int REQUESTPERMISSIONCODE = 1;
    String path;
    AnimationDrawable animationDrawable;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    Chronometer chronometer;
    boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recordImBt = findViewById(R.id.recordImBt);
        playImBt = findViewById(R.id.playImBt);
        stopImBt = findViewById(R.id.stopImBt);

        imageView = findViewById(R.id.imageView);
        chronometer = findViewById(R.id.chrnometer);


        playImBt.setEnabled(false);
        stopImBt.setEnabled(false);

        imageView.setBackgroundResource(R.drawable.record_animation);
        animationDrawable = (AnimationDrawable) imageView.getBackground();

        recordImBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()){
                    Calendar calendar = Calendar.getInstance();
                    path =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    calendar.get(Calendar.HOUR) + "AudioCapturePractice.3gp";

                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    mediaRecorder.setOutputFile(path);

                    try{
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                    if (!running){
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                        running = true;
                    }
                    recordImBt.setEnabled(false);
                    stopImBt.setEnabled(true);
                    animationDrawable.start();//animation started
                    Toast.makeText(MainActivity.this, "Recording started",
                            Toast.LENGTH_LONG).show();
                }else {
                    requestPermission();
                }
            }
        });


        stopImBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    mediaRecorder.stop();
                } catch (IllegalStateException e){
                    e.printStackTrace();
                }

                stopImBt.setEnabled(false);
                recordImBt.setEnabled(true);
                playImBt.setEnabled(true);
                animationDrawable.stop();

                Toast.makeText(MainActivity.this, "Recording Completed",
                        Toast.LENGTH_LONG).show();

                if (running){
                    chronometer.stop();
                    running = false;
                }
            }
        });


        playImBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                stopImBt.setEnabled(false);
                //recordImBt.setEnabled(false);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                playImBt.setImageResource(R.drawable.pause);
                Toast.makeText(MainActivity.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();

                if (running){
                    chronometer.stop();
                    running = false;
                }
            }
        });

    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, REQUESTPERMISSIONCODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUESTPERMISSIONCODE:
                if (grantResults.length>0){
                    boolean storagePermission = grantResults[0]==(PackageManager.PERMISSION_GRANTED);
                    boolean audioPermission = grantResults[1]==(PackageManager.PERMISSION_GRANTED);

                    if(storagePermission && audioPermission){
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "Permission denied ", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
        int recordResult = ContextCompat.checkSelfPermission(getApplicationContext(),RECORD_AUDIO);

        //if permission granted then it will send true
        if(result == PackageManager.PERMISSION_GRANTED && recordResult == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
    }

}
