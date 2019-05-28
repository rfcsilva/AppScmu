package com.agrosmart;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
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
                Intent intent = new Intent(EstufaActivity.this, ConfiguracoesActivity.class);
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
        graphs.add(new DataGraph("Humidade", series));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.edit_prefrences) {
            Intent intent = new Intent(EstufaActivity.this, ConfiguracoesActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }
}
