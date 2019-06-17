package com.agrosmart;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;


import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


import com.agrosmart.Utils.DecodeBitmapTask;
import com.agrosmart.cards.SliderAdapter;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final int[][] dotCoords = new int[5][2];
    private final int[] pics = {R.drawable.estufacultivomorangos, R.drawable.estufa2, R.drawable.estufa3, R.drawable.estufa4, R.drawable.estufa5};
    private final int[] maps = {R.drawable.map_paris, R.drawable.map_seoul, R.drawable.map_london, R.drawable.map_beijing, R.drawable.map_greece};
    private final int[] descriptions = {R.string.text1, R.string.text2, R.string.text3, R.string.text4, R.string.text5};
    private final String[] countries = {"Amadora", "Cova", "LONDON", "BEIJING", "THIRA"};
    private final String[] places = {"Estufa de morangos", "Estufa de tomates", "Tower Bridge", "Temple of Heaven", "Aegeana Sea"};
    private final String[] temperatures = {"21°C", "19°C", "17°C", "23°C", "20°C"};
    private final String[] humidityAir = {"Humidade do ar: 23%", "Sep 5 - Nov 10    8:00-16:00", "Mar 8 - May 21    7:00-18:00"};
    private final String[] humiditySoil = {"Humidade do solo: 23%", "Sep 5 - Nov 10    8:00-16:00", "Mar 8 - May 21    7:00-18:00"};
    private final String[] luminosity = {"Luminosidade: 23%", "Sep 5 - Nov 10    8:00-16:00", "Mar 8 - May 21    7:00-18:00"};
    private final String[] waterlevel = {"Nível de água: 20%", "Sep 5 - Nov 10    8:00-16:00", "Mar 8 - May 21    7:00-18:00"};


    private final SliderAdapter sliderAdapter = new SliderAdapter(pics, 5, new OnCardClickListener());

    private CardSliderLayoutManager layoutManger;
    private RecyclerView recyclerView;
    private ImageSwitcher mapSwitcher;
    private TextSwitcher temperatureSwitcher;
    private TextSwitcher temperatureSwitcher2;
    private TextSwitcher placeSwitcher;
    private TextSwitcher humidityAirSwitcher;
    private TextSwitcher humiditySoilSwitcher;
    private TextSwitcher luminositySwitcher;
    private TextSwitcher waterlevelSwitcher;
    private View greenDot;

    private TextView country1TextView;
    private TextView country2TextView;
    private int countryOffset1;
    private int countryOffset2;
    private long countryAnimDuration;
    private int currentPosition;

    private DecodeBitmapTask decodeMapBitmapTask;
    private DecodeBitmapTask.Listener mapLoadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();
        initCountryText();
        initSwitchers();
        initGreenDot();
        voleyGetEstufas();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange();
                }
            }
        });

        layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

        new CardSnapHelper().attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && decodeMapBitmapTask != null) {
            decodeMapBitmapTask.cancel(true);
        }
    }

    private void initSwitchers() {
        temperatureSwitcher = (TextSwitcher) findViewById(R.id.ts_temperature);
        temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView, true));
        temperatureSwitcher.setCurrentText(temperatures[0]);

        temperatureSwitcher2 = (TextSwitcher) findViewById(R.id.ts_temperature2);
        temperatureSwitcher2.setFactory(new TextViewFactory(R.style.ClockTextView, true));
        temperatureSwitcher2.setCurrentText(temperatures[0]);

        placeSwitcher = (TextSwitcher) findViewById(R.id.ts_place);
        placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
        placeSwitcher.setCurrentText(places[0]);

        humidityAirSwitcher = (TextSwitcher) findViewById(R.id.ts_humidityair);
        humidityAirSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView, false));
        humidityAirSwitcher.setCurrentText(humidityAir[0]);

        humiditySoilSwitcher = (TextSwitcher) findViewById(R.id.ts_humiditysoil);
        humiditySoilSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView, false));
        humiditySoilSwitcher.setCurrentText(humiditySoil[0]);

        luminositySwitcher = (TextSwitcher) findViewById(R.id.ts_luminosity);
        luminositySwitcher.setFactory(new TextViewFactory(R.style.ClockTextView, false));
        luminositySwitcher.setCurrentText(luminosity[0]);

        waterlevelSwitcher = (TextSwitcher) findViewById(R.id.ts_waterlevel);
        waterlevelSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView, false));
        waterlevelSwitcher.setCurrentText(waterlevel[0]);

        /**
        waterlevelSwitcher = (TextSwitcher) findViewById(R.id.ts_description);
        waterlevelSwitcher.setInAnimation(this, android.R.anim.fade_in);
        waterlevelSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        waterlevelSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
        waterlevelSwitcher.setCurrentText(getString(descriptions[0]));*/

        mapSwitcher = (ImageSwitcher) findViewById(R.id.ts_map);
        mapSwitcher.setInAnimation(this, R.anim.fade_in);
        mapSwitcher.setOutAnimation(this, R.anim.fade_out);
        mapSwitcher.setFactory(new ImageViewFactory());
        mapSwitcher.setImageResource(maps[0]);

        mapLoadListener = new DecodeBitmapTask.Listener() {
            @Override
            public void onPostExecuted(Bitmap bitmap) {
                ((ImageView)mapSwitcher.getNextView()).setImageBitmap(bitmap);
                mapSwitcher.showNext();
            }
        };
    }

    private void initCountryText() {
        countryAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
        countryOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
        countryOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);
        country1TextView = (TextView) findViewById(R.id.tv_country_1);
        country2TextView = (TextView) findViewById(R.id.tv_country_2);

        country1TextView.setX(countryOffset1);
        country2TextView.setX(countryOffset2);
        country1TextView.setText(countries[0]);
        country2TextView.setAlpha(0f);

        //country1TextView.setTypeface(Typeface.createFromAsset(getAssets(), "open-sans-extrabold.ttf"));
        //country2TextView.setTypeface(Typeface.createFromAsset(getAssets(), "open-sans-extrabold.ttf"));
    }

    private void initGreenDot() {
        mapSwitcher.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapSwitcher.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                final int viewLeft = mapSwitcher.getLeft();
                final int viewTop = mapSwitcher.getTop() + mapSwitcher.getHeight() / 3;

                final int border = 100;
                final int xRange = Math.max(1, mapSwitcher.getWidth() - border * 2);
                final int yRange = Math.max(1, (mapSwitcher.getHeight() / 3) * 2 - border * 2);

                final Random rnd = new Random();

                for (int i = 0, cnt = dotCoords.length; i < cnt; i++) {
                    dotCoords[i][0] = viewLeft + border + rnd.nextInt(xRange);
                    dotCoords[i][1] = viewTop + border + rnd.nextInt(yRange);
                }

                greenDot = findViewById(R.id.green_dot);
                greenDot.setX(dotCoords[0][0]);
                greenDot.setY(dotCoords[0][1]);
            }
        });
    }

    private void setCountryText(String text, boolean left2right) {
        final TextView invisibleText;
        final TextView visibleText;
        if (country1TextView.getAlpha() > country2TextView.getAlpha()) {
            visibleText = country1TextView;
            invisibleText = country2TextView;
        } else {
            visibleText = country2TextView;
            invisibleText = country1TextView;
        }

        final int vOffset;
        if (left2right) {
            invisibleText.setX(0);
            vOffset = countryOffset2;
        } else {
            invisibleText.setX(countryOffset2);
            vOffset = 0;
        }

        invisibleText.setText(text);

        final ObjectAnimator iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f);
        final ObjectAnimator vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f);
        final ObjectAnimator iX = ObjectAnimator.ofFloat(invisibleText, "x", countryOffset1);
        final ObjectAnimator vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset);

        final AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(iAlpha, vAlpha, iX, vX);
        animSet.setDuration(countryAnimDuration);
        animSet.start();
    }

    private void onActiveCardChange() {
        final int pos = layoutManger.getActiveCardPosition();
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return;
        }

        onActiveCardChange(pos);
    }

    private void onActiveCardChange(int pos) {
        int animH[] = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
        int animV[] = new int[] {R.anim.slide_in_top, R.anim.slide_out_bottom};

        final boolean left2right = pos < currentPosition;
        if (left2right) {
            animH[0] = R.anim.slide_in_left;
            animH[1] = R.anim.slide_out_right;

            animV[0] = R.anim.slide_in_bottom;
            animV[1] = R.anim.slide_out_top;
        }

        setCountryText(countries[pos % countries.length], left2right);

        temperatureSwitcher.setInAnimation(MainActivity.this, animH[0]);
        temperatureSwitcher.setOutAnimation(MainActivity.this, animH[1]);
        temperatureSwitcher.setText(temperatures[pos % temperatures.length]);

        temperatureSwitcher2.setInAnimation(MainActivity.this, animH[0]);
        temperatureSwitcher2.setOutAnimation(MainActivity.this, animH[1]);
        temperatureSwitcher2.setText(temperatures[pos % temperatures.length]);

        placeSwitcher.setInAnimation(MainActivity.this, animV[0]);
        placeSwitcher.setOutAnimation(MainActivity.this, animV[1]);
        placeSwitcher.setText(places[pos % places.length]);

        humidityAirSwitcher.setInAnimation(MainActivity.this, animV[0]);
        humidityAirSwitcher.setOutAnimation(MainActivity.this, animV[1]);
        humidityAirSwitcher.setText(humidityAir[pos % humidityAir.length]);

        humiditySoilSwitcher.setInAnimation(MainActivity.this, animV[0]);
        humiditySoilSwitcher.setOutAnimation(MainActivity.this, animV[1]);
        humiditySoilSwitcher.setText(humiditySoil[pos % humiditySoil.length]);

        luminositySwitcher.setInAnimation(MainActivity.this, animV[0]);
        luminositySwitcher.setOutAnimation(MainActivity.this, animV[1]);
        luminositySwitcher.setText(luminosity[pos % luminosity.length]);

        waterlevelSwitcher.setInAnimation(MainActivity.this, animV[0]);
        waterlevelSwitcher.setOutAnimation(MainActivity.this, animV[1]);
        waterlevelSwitcher.setText(waterlevel[pos % waterlevel.length]);

        //waterlevelSwitcher.setText(getString(descriptions[pos % descriptions.length]));

        showMap(maps[pos % maps.length]);

        ViewCompat.animate(greenDot)
                .translationX(dotCoords[pos % dotCoords.length][0])
                .translationY(dotCoords[pos % dotCoords.length][1])
                .start();

        currentPosition = pos;
    }

    private void showMap(@DrawableRes int resId) {
        if (decodeMapBitmapTask != null) {
            decodeMapBitmapTask.cancel(true);
        }

        final int w = mapSwitcher.getWidth();
        final int h = mapSwitcher.getHeight();

        decodeMapBitmapTask = new DecodeBitmapTask(getResources(), resId, w, h, mapLoadListener);
        decodeMapBitmapTask.execute();
    }

    private class TextViewFactory implements  ViewSwitcher.ViewFactory {

        @StyleRes
        final int styleId;
        final boolean center;

        TextViewFactory(@StyleRes int styleId, boolean center) {
            this.styleId = styleId;
            this.center = center;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            final TextView textView = new TextView(MainActivity.this);

            if (center) {
                textView.setGravity(Gravity.CENTER);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                textView.setTextAppearance(MainActivity.this, styleId);
            } else {
                textView.setTextAppearance(styleId);
            }

            return textView;
        }

    }

    private class ImageViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            final ImageView imageView = new ImageView(MainActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final LayoutParams lp = new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);

            return imageView;
        }
    }

    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final CardSliderLayoutManager lm =  (CardSliderLayoutManager) recyclerView.getLayoutManager();

            if (lm.isSmoothScrolling()) {
                return;
            }

            final int activeCardPosition = lm.getActiveCardPosition();
            if (activeCardPosition == RecyclerView.NO_POSITION) {
                return;
            }

            final int clickedPosition = recyclerView.getChildAdapterPosition(view);
            if (clickedPosition == activeCardPosition) {
                final Intent intent = new Intent(MainActivity.this, EstufaActivity.class);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent);
                } else {
                    final CardView cardView = (CardView) view;
                    final View sharedView = cardView.getChildAt(cardView.getChildCount() - 1);
                    final ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(MainActivity.this, sharedView, "shared");
                    startActivity(intent, options.toBundle());
                }
            } else if (clickedPosition > activeCardPosition) {
                recyclerView.smoothScrollToPosition(clickedPosition);
                onActiveCardChange(clickedPosition);
            }
        }
    }



    private void voleyGetEstufas() {


        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        String url = "https://jersey-scmu-server.appspot.com/rest/withtoken/greenhouse/";
        final String token = sharedPreferences.getString("tokenID", "erro");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("TokenAreaPessoal", response.toString());
                //Log.i("TokenAreaPessoal", token.toString());
                // TODO: store the token in the SharedPreferences

                SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                try {
                    final JSONArray list = response.getJSONArray("list");
                    Log.d("LISTA ", list.toString());
                    if (!response.isNull("list"))
                        for (int i = 0; i < list.length(); i++) {
                            if (list.getJSONObject(i).getString("center_coordinates")!=null){

                                //TODO: CENTRAR NAS COORDENADAS
                            }

                            if (list.getJSONObject(i).getString("id")!=null){
                                editor.putString("greenhouseId", list.getJSONObject(i).getString("id"));
                            }

                            if (list.getJSONObject(i).getString("creatorUserName")!=null){
                                editor.putString("creatorName", list.getJSONObject(i).getString("creatorUserName"));
                            }

                            editor.commit();
                        }
                    volleyGetData("https://jersey-scmu-server.appspot.com/rest/withtoken/config/GreenHouse@2.000000,2.000000");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erro", "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Por favor verifique a sua ligação", Toast.LENGTH_SHORT).show();
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

    private void volleyGetData(String url) {


        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                Log.i("TokenAreaPessoal", response.toString());
                //Log.i("TokenAreaPessoal", token.toString());
                // TODO: store the token in the SharedPreferences


                try {
                    if(response.has("avgTemperature")) {
                        int temp = (int) response.getDouble("avgTemperature");
                        temperatureSwitcher.setCurrentText(temp + "ºC");
                        temperatureSwitcher2.setCurrentText("Temperatura: " +temp + "ºC");
                        editor.putInt("avgTemperature", temp);
                    }

                    if(response.has("tempDeviation")) {
                        int temp = (int) response.getDouble("tempDeviation");
                        editor.putInt("tempDeviation", temp);
                    }


                    if(response.has("avgAirHumidity")) {
                        int airHum = (int) response.getDouble("avgAirHumidity");
                        humidityAirSwitcher.setCurrentText("Humidade do ar: " + airHum + "%");
                        editor.putInt("avgAirHumidity", airHum);
                    }

                    if(response.has("airHumidityDeviation")) {
                        int temp = (int) response.getDouble("airHumidityDeviation");
                        editor.putInt("airHumidityDeviation", temp);
                    }

                    if(response.has("avgSoilHumidity")) {
                        int soilHum = (int) response.getDouble("avgSoilHumidity");
                        humiditySoilSwitcher.setCurrentText("Humidade do solo: " + soilHum + "%");
                        editor.putInt("avgSoilHumidity", soilHum);
                    }

                    if(response.has("soilHumidityDeviation")) {
                        int temp = (int) response.getDouble("soilHumidityDeviation");
                        editor.putInt("soilHumidityDeviation", temp);
                    }

                    if(response.has("avgLuminosity")) {
                        int lum = (int) response.getDouble("avgLuminosity");
                        luminositySwitcher.setCurrentText("Luminosidade: " + lum + "%");
                        editor.putInt("avgLuminosity", lum);
                    }

                    if(response.has("luminosityDeviation")) {
                        int temp = (int) response.getDouble("luminosityDeviation");
                        editor.putInt("luminosityDeviation", temp);
                    }

                    if(response.has("avgSteam")) {
                        int steam = (int) response.getDouble("avgSteam");
                        waterlevelSwitcher.setCurrentText("Nível da água: " + steam + "%");
                        editor.putInt("avgSteam", steam);
                    }

                    if(response.has("steamDeviation")) {
                        int temp = (int) response.getDouble("steamDeviation");
                        editor.putInt("steamDeviation", temp);
                    }
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erro", "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Por favor verifique a sua ligação", Toast.LENGTH_SHORT).show();
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
