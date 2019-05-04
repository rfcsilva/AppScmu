package com.agrosmart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agrosmart.ui.main.AppController;
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
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView mLogoView;
    private Context mContext;
    private CheckBox mCheckBox;
    private String usernome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.username || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        boolean isChecked = getSharedPreferences("Prefs", MODE_PRIVATE).getBoolean("checked", false);
        if (isChecked) {
            mCheckBox.setChecked(true);
            mUsernameView.setText(getSharedPreferences("Prefs", MODE_PRIVATE).getString("username", ""));
            mPasswordView.setText(getSharedPreferences("Prefs", MODE_PRIVATE).getString("password", ""));
        }

        SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

        usernome = getSharedPreferences("Prefs", MODE_PRIVATE).getString("username", null);

        Button mEmailSignInButton = (Button) findViewById(R.id.btn_login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView mRegisterTextView = (TextView) findViewById(R.id.registar_textView);
        mRegisterTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                registar();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mLogoView = findViewById(R.id.imageLogo);
        mContext = this;
    }

    private void registar() {
        Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("Password inválida");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError("Campo não pode estar vazio");
            focusView = mUsernameView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mUsernameView.setError("Username inválido");
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);

            loginVolley(email, password);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);
        }
    }

    private void loginVolley(final String email, final String password) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/login";

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("password", password);

            jsonObject.put("username", email);

            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("A Carregar...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
            final SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (usernome != null)
                                if (!usernome.equals(email)) {
                                    editor.clear();
                                    Log.d("ya", "deu clear!!!");
                                }
                            editor.putString("username", email);
                            editor.putString("password", password);
                            if (mCheckBox.isChecked())
                                editor.putBoolean("checked", true);
                            else
                                editor.putBoolean("checked", false);

                            editor.commit();
                            pDialog.hide();
                            voleyGetInfo();
                            // TODO: call the main activity (to be implemented) with data in the intent
                            //Intent myIntent = new Intent(LoginActivity.this, FeedActivity.class);

                            //LoginActivity.this.startActivity(myIntent);
                            //finish();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("erroLOGIN", "Error: " + error.getMessage());
                    // hide the progress dialog
                    pDialog.hide();
                    Toast.makeText(mContext, "Por favor verifique a sua ligação", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    try {

//                        pDialog.hide();
                        String jsonString = new String(response.data,
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

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
        //       return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
        //       return password.length() > 4;
    }

}