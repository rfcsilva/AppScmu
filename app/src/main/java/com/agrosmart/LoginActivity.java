package com.agrosmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agrosmart.Models.LoginData;
import com.agrosmart.Models.User;
import com.agrosmart.Utils.InformationChecker;
import com.agrosmart.exceptions.InvalidPasswordException;
import com.agrosmart.exceptions.InvalidUsernameException;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    public static final String LOGIN_ENDPOINT = "https://jersey-scmu-server.appspot.com/rest/user/login";
    public static final String USER_ENDPOINT = "https://jersey-scmu-server.appspot.com/rest/withtoken/user/";

    //static variables
    private static final Gson gson = new Gson();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String AUTHORIZATION = "Authorization";
    public static final String POR_FAVOR_VERIFIQUE_A_SUA_LIGAÇÃO = "Por favor verifique a sua ligação";
    private static final String WRONGPASSWORD = "Password errada.";
    public static OkHttpClient client = new OkHttpClient();


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

            requestLogin(username, password);

        }catch (InvalidPasswordException | InvalidUsernameException invalidationException) {
            focusedView.requestFocus();
        }
    }

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