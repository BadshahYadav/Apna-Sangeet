package com.example.apnasangeet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.RunnableKt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class playSongs extends AppCompatActivity {
    private Runnable myRunnable;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
        myHandler.removeCallbacks(myRunnable);

    }

    TextView textView;
ImageView play, previous, next;
ArrayList<File> songs;
MediaPlayer mediaPlayer;
String textContent;
int position;
SeekBar seekBar;
Thread updateSeek;
Handler handler ;
TextView currentTimer,totalTimer;
    private Handler myHandler;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_songs);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        currentTimer = findViewById(R.id.currentTimer);
        totalTimer = findViewById(R.id.totalTimer);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        textView.setSelected(true);
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        position = intent.getIntExtra("position",0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();

        String totalTime = createTimerLabel(mediaPlayer.getDuration());
        totalTimer.setText(totalTime);

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
              mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTimerLabel(mediaPlayer.getCurrentPosition());
                currentTimer.setText(currentTime);
                handler.postDelayed(this, 1000);
            }
        });






        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                while (currentPosition<mediaPlayer.getDuration()){
                    currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    sleep(1000);
                }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                mediaPlayer.release();
                if (position != 0){
                    position = position-1;
                }
                else {
                    position = songs.size()-1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();

                String totalTime = createTimerLabel(mediaPlayer.getDuration());
                totalTimer.setText(totalTime);

                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                mediaPlayer.release();
                if (position != songs.size()-1){
                    position = position+1;
                }
                else {
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();

                String totalTime = createTimerLabel(mediaPlayer.getDuration());
                totalTimer.setText(totalTime);

                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
            }
        });
    }


     public String createTimerLabel(int  duration){
        String timeLabel = "";
        int min = duration /1000 /60;
        int sec = duration / 1000 % 60;
        timeLabel += min + ":";
         if (sec < 10) timeLabel += "0";
         timeLabel += sec;

        return timeLabel;

     }


}