package com.agrosmart;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity {

    private EditText mEmailView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mPassConfirmView;
    private Button bRegistar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsernameView = (EditText) findViewById(R.id.input_username);
        mEmailView = (EditText) findViewById(R.id.input_email);
        mPasswordView = (EditText) findViewById(R.id.input_password);
        mPassConfirmView = (EditText) findViewById(R.id.input_password_confirmation);
        bRegistar = (Button) findViewById(R.id.btn_signup);


        bRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

    }

    private void attemptRegister() {



        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUsernameView.setError(null);
        mPassConfirmView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConf = mPassConfirmView.getText().toString();
        String username = mUsernameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("Password demasiado curta");
            focusView = mPasswordView;
            cancel = true;
        }

        if (!password.equals(passwordConf)) {
            mPassConfirmView.setError("Password diferentes");
            focusView = mPassConfirmView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Campo não pode estar vazio");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("Email inválido");
            focusView = mEmailView;
            cancel = true;
        }

        //check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError("Campo não pode estar vazio");
            focusView = mUsernameView;
            cancel = true;
        } else if (username.length() > 13) {
            mUsernameView.setError("Username demasiado grande");
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

            volleyRegister(email, password, username, passwordConf);
            //mAuthTask = new UserRegisterTask(email, password, username, passwordConf);
            //mAuthTask.execute((Void) null);
        }

    }

    private void volleyRegister(String email, String password, String username, String passwordConf) {

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("username", username);
            Log.e("User", username);
            Log.e("Pass", password);
            Log.e("Passconf", passwordConf);
            Log.e("Email", email);
            jsonObject.put("password", password);
            jsonObject.put("confirmation_password", passwordConf);
            jsonObject.put("email", email);
            jsonObject.put("role", "volunteer");

            String url = "https://novaleaf-197719.appspot.com/rest/register/";
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("A carregar...");
            pDialog.show();
            final SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("RegisterActivity", response.toString());
                            // TODO: call the main activity (to be implemented) with data in the intent
                            pDialog.dismiss();
                            Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            RegisterActivity.this.startActivity(myIntent);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    //RegisterActivity.this.startActivity(myIntent);
                    //finish();
                    VolleyLog.d("errolike", "Error: " + error.getMessage());
                    Toast.makeText(RegisterActivity.this, "Erro de ligação", Toast.LENGTH_SHORT).show();
//                    VolleyLog.d("ERRO", error.networkResponse.statusCode);
                    pDialog.dismiss();
                }
            });
            AppController.getInstance().addToRequestQueue(jsonObjectRequest, "registo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }



}

