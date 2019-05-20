package com.agrosmart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agrosmart.Models.LoginData;
import com.agrosmart.Utils.InformationChecker;
import com.agrosmart.exceptions.InvalidPasswordException;
import com.agrosmart.exceptions.InvalidUsernameException;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    //Shared prefs keys
    public static final String PREFS = "Prefs";
    public static final String KEEP_CREDENTIALS_STATUS = "checked";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private static final String TOKEN = "Token";

    //error messages
    public static final String INVALID_PASSWORD = "Password inválida";
    public static final String INVALID_USERNAME = "Campo não pode estar vazio";
    public static final String LOGIN_ENDPOINT = "https://jersey-scmu-server.appspot.com/rest/login";
    public static final String USER_ENDPOINT = "https://jersey-scmu-server.appspot.com/rest/withtoken/user/";

    //static variables

    public static final String AUTHORIZATION = "Authorization";
    public static final String POR_FAVOR_VERIFIQUE_A_SUA_LIGAÇÃO = "Por favor verifique a sua ligação";
    private static final String WRONGPASSWORD = "Password errada.";
    public static final int INITIAL_TIMEOUT_MS = 10000;


    // UI references.
    private AutoCompleteTextView usernameTextView;
    private EditText passwordTextView;
    private Context mContext;
    private CheckBox keepCredentialCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Load UI views
        usernameTextView = (AutoCompleteTextView) findViewById(R.id.username);
        passwordTextView = (EditText) findViewById(R.id.password);
        keepCredentialCheckBox = (CheckBox) findViewById(R.id.checkBox);

        //Setup UI Views
        if (getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(KEEP_CREDENTIALS_STATUS, false)) {
            keepCredentialCheckBox.setChecked(true);
            usernameTextView.setText(getSharedPreferences(PREFS, MODE_PRIVATE).getString(USERNAME, ""));
            passwordTextView.setText(getSharedPreferences(PREFS, MODE_PRIVATE).getString(PASSWORD, ""));
        }

        Button loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        TextView registerTextView = (TextView) findViewById(R.id.registar_textView);
        registerTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        mContext = this;
    }

    private void register() {
        Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }


    private void login() {

        resetLoginErrors();

        // Store values at the time of the login attempt.
        String username = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        boolean cancel = false;
        View focusedView = null;

        try {

            // Check for a valid password, if the user entered one.
            if (TextUtils.isEmpty(password) || !InformationChecker.validPassword(password)) {
                passwordTextView.setError(INVALID_PASSWORD);
                focusedView = passwordTextView;
                throw new InvalidPasswordException();
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(username)) {
                usernameTextView.setError(INVALID_USERNAME);
                focusedView = usernameTextView;
                throw new InvalidUsernameException();
            }

            loginVolley(new LoginData(username, password));

        }catch (InvalidPasswordException | InvalidUsernameException invalidationException) {
            focusedView.requestFocus();
        }
    }

    private void loginVolley(final LoginData loginData) {

        final SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();

        final ProgressDialog pDialog = showProgressDialog();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", loginData.getUsername());
            jsonObject.put("password", loginData.getPassword());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    LOGIN_ENDPOINT,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            editor.putString("username", loginData.getUsername());
                            editor.putString("password", loginData.getPassword());

                            if (keepCredentialCheckBox.isChecked())
                                editor.putBoolean("checked", true);
                            else
                                editor.putBoolean("checked", false);

                            editor.commit();
                            pDialog.hide();
                            voleyGetInfo();
                            // TODO: call the main activity (to be implemented) with data in the intent
                          //  Intent myIntent = new Intent(LoginActivity.this, FeedActivity.class);

                            //LoginActivity.this.startActivity(myIntent);
                            finish();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("erroLOGIN", "Error: " + error.getMessage());
                    Toast.makeText(mContext, "Por favor verifique a sua ligação", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    try {

//                        pDialog.hide();
                        String jsonString = new String(
                                response.data,
                                HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                        JSONObject jsonResponse = new JSONObject(response.headers);
                        //jsonResponse.put("headers", new JSONObject(response.headers));
                        Log.d("YA BINA", jsonResponse.getString("Authorization"));
                        editor.putString("tokenID", jsonResponse.getString("Authorization"));
                        editor.commit();
                        return Response.success(jsonResponse,
                                HttpHeaderParser.parseCacheHeaders(response));
                    } catch (UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    } catch (JSONException je) {
                        return Response.error(new ParseError(je));
                    }
                }
            };

            jsonObjectRequest.setRetryPolicy(
                    new DefaultRetryPolicy(INITIAL_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ProgressDialog showProgressDialog() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("A Carregar...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
        return pDialog;
    }

    private void voleyGetInfo() {


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
                Toast.makeText(mContext, "Por favor verifique a sua ligação", Toast.LENGTH_SHORT).show();
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



/**
    private void requestLogin(String username, String password) {

        LoginData loginData = new LoginData(username, password);
        RequestBody body = RequestBody.create(JSON, gson.toJson(loginData));
        Request request = new Request.Builder()
                .url(LOGIN_ENDPOINT)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if(response.isSuccessful()){ //if login is successful store username and token

                List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(2);
                list.add(new AbstractMap.SimpleEntry<String, String>(USERNAME, loginData.getUsername()));
                list.add(new AbstractMap.SimpleEntry<String, String>(TOKEN, response.header(AUTHORIZATION)));
                storeString(list);

                requestUserInfo(response.header(AUTHORIZATION), loginData.getUsername());
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(myIntent);

            }else if(response.code() == HttpURLConnection.HTTP_UNAUTHORIZED)
                Toast.makeText(mContext, WRONGPASSWORD, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext, POR_FAVOR_VERIFIQUE_A_SUA_LIGAÇÃO, Toast.LENGTH_SHORT).show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void requestUserInfo(String header, String username) {
        Request request = new Request.Builder()
                .url(USER_ENDPOINT.concat(username))
                .addHeader(AUTHORIZATION, header)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if(response.isSuccessful()) {
                User user = gson.fromJson(response.body().string(), User.class);
                Log.i("USER", user.toString());
                List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(4);
                list.add(new AbstractMap.SimpleEntry<String, String>(USERNAME, username));
                list.add(new AbstractMap.SimpleEntry<String, String>(User.NAME, user.name));
                list.add(new AbstractMap.SimpleEntry<String, String>(User.EMAIL, user.email));
                list.add(new AbstractMap.SimpleEntry<String, String>(User.ROLE, user.role));
                storeString(list);

            }else{
                Toast.makeText(mContext, POR_FAVOR_VERIFIQUE_A_SUA_LIGAÇÃO, Toast.LENGTH_SHORT).show();
            }


        }catch (IOException e){
            e.printStackTrace();
        }

    }

**/
    private void storeString(List<Map.Entry<String, String>> values){

        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();

        for(Map.Entry<String, String> entry : values){
            editor.putString(entry.getKey(), entry.getValue());
        }

        editor.commit();


    }


    private void resetLoginErrors() {
        // Reset errors.
        usernameTextView.setError(null);
        passwordTextView.setError(null);
    }

    private void clearSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
}