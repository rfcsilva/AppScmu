package com.agrosmart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class EstufaActivity extends AppCompatActivity {



    public static List<DataGraph> graphs;
    private ItemFragment.OnListFragmentInteractionListener mListener;
    public static MyItemRecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estufa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Tomato's greenhouse");

        mListener =  new ItemFragment.OnListFragmentInteractionListener() {
            @Override
            public void onEditInteraction(DataGraph item) {
                Intent intent;
                switch (item.getTitulo()){
                    case "Luminosidade":
                        intent = new Intent(EstufaActivity.this, ConfigurarLuminosidadeActivity.class);
                        startActivity(intent);
                        break;
                    case "Humidade do ar"  :
                        intent = new Intent(EstufaActivity.this, ConfigurarHumidadeArActivity.class);
                        startActivity(intent);
                        break;
                    case "Humidade do solo"  :
                        intent = new Intent(EstufaActivity.this, ConfigurarHumidadeSoloActivity.class);
                        startActivity(intent);
                        break;
                    case "Nivel de Água":
                        intent = new Intent(EstufaActivity.this, ConfugurarNivelAguaActivity.class);
                        startActivity(intent);
                        break;
                    case "Temperatura":
                        intent = new Intent(EstufaActivity.this, ConfigurarTemperaturaActivity.class);
                        startActivity(intent);
                        break;
                }

            }
        };

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        graphs = new ArrayList<>();

        graphs.add(new DataGraph("Luminosidade", series));
        graphs.add(new DataGraph("Humidade do ar", series));
        graphs.add(new DataGraph("Humidade do solo", series));
        graphs.add(new DataGraph("Nivel de Água", series));
        graphs.add(new DataGraph("Temperatura", series));

        adapter = new MyItemRecyclerViewAdapter(graphs, mListener, EstufaActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();





    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
