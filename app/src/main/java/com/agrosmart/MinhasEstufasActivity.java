package com.agrosmart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

public class MinhasEstufasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_estufas);

        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setLayoutManager(new CardSliderLayoutManager(this));

        new CardSnapHelper().attachToRecyclerView(recyclerView);
    }
}
