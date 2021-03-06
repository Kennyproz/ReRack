package com.example.ken.rerack.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ken.rerack.ApiRequest;
import com.example.ken.rerack.ApiType;
import com.example.ken.rerack.AsyncResponse;
import com.example.ken.rerack.R;
import com.example.ken.rerack.User;

/**
 * Created by Ken on 12-10-2017.
 */

public class Login extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.ken.rerack.MESSAGE";

    boolean isLoggedIn;
    EditText un, ps;
    String username, password;
    CheckBox chkbox;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getViewIds();
        isLoggedIn = false;

        sharedPreferences = getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String uname = sharedPreferences.getString("Username", null);
        String pass = sharedPreferences.getString("Password", null);

        checkString(uname, un);
        checkString(pass, ps);
    }

    private void checkString(String string, EditText editText) {
        if (string != null) {
            editText.setText(string);
        } else {
            return;
        }
    }

    public void register(View v) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void login(View v) {
        getViewIds();
        getLoginStrings();
        if (!username.isEmpty() && !password.isEmpty()) {
            ApiRequest api = new ApiRequest(ApiType.LOGIN, this.username, this.password, new AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    if (output != null) {
                        User user = (User) output;
                        loginSuccess(user);
                    } else {
                        loginFailed();
                    }
                }
            });
            api.execute();
        } else {
            Toast.makeText(Login.this, "Username or password is empty!", Toast.LENGTH_LONG).show();
        }
    }

    public void loginSuccess(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        String textMessage = user.getUsername();
        isLoggedIn = true;
        intent.putExtra("user",user);
        saveCredentials();
        System.out.println("Logged in");
        startActivity(intent);

        isLoggedIn();
    }

    public void loginFailed() {
        isLoggedIn();
    }

    private void saveCredentials() {
        if (chkbox.isChecked()) {
            editor.putString("Username", username);
            editor.putString("Password", password);
            editor.commit();
        } else {
            editor.clear().commit();
        }
    }

    private void getViewIds() {
        un = (EditText) findViewById(R.id.tbUsername);
        ps = (EditText) findViewById(R.id.tbPassword);
        chkbox = (CheckBox) findViewById(R.id.chkCredent);
    }

    private void getLoginStrings() {
        username = un.getText().toString();
        password = ps.getText().toString();
    }

    private void isLoggedIn() {
        if (isLoggedIn) {
            Toast.makeText(Login.this, "Logged in!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Login.this, "Login Failed!", Toast.LENGTH_LONG).show();
        }
    }

}
