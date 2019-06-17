package com.agrosmart;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConfugurarNivelAguaActivity extends AppCompatActivity {

    GraphView graphView;
    RangeSeekBar rangeSeekBar;
    boolean changed;
    float initleftValue;
    float initRightValue;
    TextView textSteam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confugurar_nivel_agua);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        changed = false;
        textSteam = (TextView) findViewById(R.id.textNivelAgua);
        graphView = (GraphView) findViewById(R.id.graphNivelAgua);
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, sharedPreferences.getInt("avgSteam", 15)+2),
                new DataPoint(1, sharedPreferences.getInt("avgSteam", 15)+ 4),
                new DataPoint(2, sharedPreferences.getInt("avgSteam", 15)+7),
                new DataPoint(3, sharedPreferences.getInt("avgSteam", 15) + 2),
                new DataPoint(4, sharedPreferences.getInt("avgSteam", 15))
        });

        graphView.addSeries(series);

        rangeSeekBar = (RangeSeekBar) findViewById(R.id.seekbarNivelAgua);


        textSteam.setText("Nível de água atual: " + sharedPreferences.getInt("avgSteam", 15) + "%");


        rangeSeekBar.setValue(sharedPreferences.getInt("avgSteam", 15) - sharedPreferences.getInt("steamDeviation", 15),
                sharedPreferences.getInt("avgSteam", 15) + sharedPreferences.getInt("steamDeviation", 15));
        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                initleftValue = leftValue;
                initRightValue = rightValue;
                changed = true;
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        if (changed){
            float center = (initleftValue + initRightValue) / 2;
            float deviation = center - initleftValue;
            volleyConfigurar(center, deviation);
        }
        super.onBackPressed();
    }


    private void volleyConfigurar(float center, float deviation) {

        final SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();


        String tag_json_obj = "json_obj_req";
        String url = "https://jersey-scmu-server.appspot.com/rest/withtoken/config/new/" +
                getSharedPreferences("Prefs", MODE_PRIVATE).getString("greenhouseId", "erro");

        JSONObject conf = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");
        try {

            //conf.put("id", com.getAuthor());
            conf.put("avgLuminosity", sharedPreferences.getInt("avgLuminosity", 15));
            conf.put("luminosityDeviation", sharedPreferences.getInt("luminosityDeviation", 15));

            conf.put("avgAirHumidity", sharedPreferences.getInt("avgAirHumidity", 15));
            conf.put("airHumidityDeviation", sharedPreferences.getInt("airHumidityDeviation", 15));

            conf.put("avgSoilHumidity", sharedPreferences.getInt("avgSoilHumidity", 15));
            conf.put("soilHumidityDeviation", sharedPreferences.getInt("soilHumidityDeviation", 15));

            conf.put("avgTemperature", sharedPreferences.getInt("avgTemperature", 15));
            conf.put("tempDeviation", sharedPreferences.getInt("tempDeviation", 15));

            conf.put("avgSteam", center);
            conf.put("steamDeviation", deviation);
            editor.putInt("avgSteam", (int) center);
            editor.putInt("steamDeviation", (int) deviation);
            editor.commit();
            //conf.put("id", com.getId());
            //conf.put("image", sharedPreferences.getString("image_user", null));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, conf,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {}
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(ConfugurarNivelAguaActivity.this, "Erro de ligação", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", token);
                    return headers;
                }

            };
            AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
