package com.agrosmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


/**
 * Author: Hugo Mochao
 * Atividade de entrada com a imagem
 */
public class EntradaActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada);



        passActivity();

    }


    /**
     * passa a proxima atividade depois de 4 segundos
     */
    public void passActivity(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(EntradaActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }

        }, 4000L);
    }
}
