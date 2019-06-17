package com.agrosmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);

        LineGraphSeries<DataPoint> luminosity = new LineGraphSeries<DataPoint>(new DataPoint[]{

                new DataPoint(0, sharedPreferences.getInt("avgLuminosity", 15)+3),
                new DataPoint(1, sharedPreferences.getInt("avgLuminosity", 15)-2),
                new DataPoint(2, sharedPreferences.getInt("avgLuminosity", 15)-6),
                new DataPoint(3, sharedPreferences.getInt("avgLuminosity", 15) - 5),
                new DataPoint(4, sharedPreferences.getInt("avgLuminosity", 15))
        });

        LineGraphSeries<DataPoint> airHumidity = new LineGraphSeries<DataPoint>(new DataPoint[]{

                new DataPoint(0, sharedPreferences.getInt("avgAirHumidity", 15)-10),
                new DataPoint(1, sharedPreferences.getInt("avgAirHumidity", 15)+1),
                new DataPoint(2, sharedPreferences.getInt("avgAirHumidity", 15)-2),
                new DataPoint(3, sharedPreferences.getInt("avgAirHumidity", 15) + 5),
                new DataPoint(4, sharedPreferences.getInt("avgAirHumidity", 15))
        });

        LineGraphSeries<DataPoint> soilHumidity = new LineGraphSeries<DataPoint>(new DataPoint[]{

                new DataPoint(0, sharedPreferences.getInt("avgSoilHumidity", 15)-1),
                new DataPoint(1, sharedPreferences.getInt("avgSoilHumidity", 15)- 2),
                new DataPoint(2, sharedPreferences.getInt("avgSoilHumidity", 15)+7),
                new DataPoint(3, sharedPreferences.getInt("avgSoilHumidity", 15) - 2),
                new DataPoint(4, sharedPreferences.getInt("avgSoilHumidity", 15))
        });

        LineGraphSeries<DataPoint> waterLevel = new LineGraphSeries<DataPoint>(new DataPoint[]{

                new DataPoint(0, sharedPreferences.getInt("avgSteam", 15)+2),
                new DataPoint(1, sharedPreferences.getInt("avgSteam", 15)+ 4),
                new DataPoint(2, sharedPreferences.getInt("avgSteam", 15)+7),
                new DataPoint(3, sharedPreferences.getInt("avgSteam", 15) + 2),
                new DataPoint(4, sharedPreferences.getInt("avgSteam", 15))
        });

        LineGraphSeries<DataPoint> temp = new LineGraphSeries<DataPoint>(new DataPoint[]{

                new DataPoint(0, sharedPreferences.getInt("textTemp", 15)-1),
                new DataPoint(1, sharedPreferences.getInt("textTemp", 15) -2),
                new DataPoint(2, sharedPreferences.getInt("textTemp", 15) +7),
                new DataPoint(3, sharedPreferences.getInt("textTemp", 15) + 2),
                new DataPoint(4, sharedPreferences.getInt("textTemp", 15))
        });

        graphs = new ArrayList<>();

        graphs.add(new DataGraph("Luminosidade", luminosity));
        graphs.add(new DataGraph("Humidade do ar", airHumidity));
        graphs.add(new DataGraph("Humidade do solo", soilHumidity));
        graphs.add(new DataGraph("Nivel de Água", waterLevel));
        graphs.add(new DataGraph("Temperatura", temp));

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


    private void voleyGetEstufas() {


        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/users/profileinfo?user=" +
                sharedPreferences.getString("username", "erro");
        final String token = sharedPreferences.getString("tokenID", "erro");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("TokenAreaPessoal", response.toString());
                //Log.i("TokenAreaPessoal", token.toString());
                // TODO: store the token in the SharedPreferences

                SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                try {

                    if (response.has("email"))
                        editor.putString("email", response.getString("email"));
                    if (response.has("role"))
                        editor.putString("role", response.getString("role"));
                    if (response.has("numb_reports"))
                        editor.putString("numb_reports", response.getString("numb_reports"));
                    if (response.has("approval_rate"))
                        editor.putString("approval_rate", response.getString("approval_rate"));
                    if (response.has("name"))
                        editor.putString("name", response.getString("name"));
                    if (response.has("locality"))
                        editor.putString("locality", response.getString("locality"));
                    if (response.has("firstaddress"))
                        editor.putString("firstaddress", response.getString("firstaddress"));
                    if (response.has("complementaryaddress"))
                        editor.putString("complementaryaddress", response.getString("complementaryaddress"));
                    if (response.has("mobile_phone"))
                        editor.putString("mobile_phone", response.getString("mobile_phone"));
                    if (response.has("name"))
                        editor.putString("name", response.getString("name"));
                    if (response.has("image_uri"))
                        editor.putString("image_user", response.getJSONObject("image_uri").getString("value"));
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erro", "Error: " + error.getMessage());
                Toast.makeText(EstufaActivity.this, "Por favor verifique a sua ligação", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "UserInfo");

    }


}
