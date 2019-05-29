package com.agrosmart;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
        graphView = (GraphView) findViewById(R.id.graphNivelAgua);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        graphView.addSeries(series);

        rangeSeekBar = (RangeSeekBar) findViewById(R.id.seekbarNivelAgua);
        rangeSeekBar.setValue(20,30);
        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                int min = (int) leftValue;
                int max = (int) rightValue;
                Toast.makeText(ConfugurarNivelAguaActivity.this, "min = " + min + " right = " + max, Toast.LENGTH_SHORT).show();
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
            volleyConfigurar();
        }
        super.onBackPressed();
    }


    private void volleyConfigurar() {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/comment?group_id=";

        JSONObject conf = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");
        try {

            //conf.put("id", com.getAuthor());
            conf.put("author", "TODO");
            conf.put("message", "TODO");
            conf.put("image", "TODO");
            conf.put("creation_date", "TODO");
            //conf.put("id", com.getId());
            //conf.put("image", sharedPreferences.getString("image_user", null));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, conf,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                conf.put("image", "TODO");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //comentarios.get(comentarios.indexOf(com)).setId();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ConfugurarNivelAguaActivity.this, "Erro de ligação", Toast.LENGTH_SHORT).show();
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
