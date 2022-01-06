package com.stizsoftware.unitecnicos.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.VideoView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.stizsoftware.unitecnicos.R;

public class TelaAbertura extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_abertura);
        getSupportActionBar().hide();

        try {
            VideoView videoHolder = new VideoView(this);
            setContentView(videoHolder);
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.animacao_logo);
            videoHolder.setVideoURI(video);

            videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    jump();
                }
            });
            videoHolder.start();
        } catch (Exception ex) {
            jump();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        jump();
        return true;
    }

    private void jump() {
        if (isFinishing())
            return;
        startActivity(new Intent(this, TelaTecnico.class));
        finish();
    }
    private void abrirTelaInicial()
    {
        Intent i = new Intent(TelaAbertura.this, TelaLogin.class);
        startActivity(i);
        Animatoo.animateFade(TelaAbertura.this);
        finish();
    }
    /*
    new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirTelaInicial();
            }
        },6000);
     */

    /*
    VideoView simpleVideoView = (VideoView) findViewById(R.id.videoView);
        simpleVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.animacao_logo));
        simpleVideoView.start();
     */
}
