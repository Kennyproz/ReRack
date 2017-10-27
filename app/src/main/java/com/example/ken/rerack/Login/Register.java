package com.example.ken.rerack.Login;

/**
 * Created by Ken on 12-10-2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ken.rerack.ApiRequest;
import com.example.ken.rerack.ApiType;
import com.example.ken.rerack.AsyncResponse;
import com.example.ken.rerack.R;
import com.example.ken.rerack.User;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {
    EditText edUsername, edPass1, edPass2;
    String username, pass, pass1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void registerUser(View v){
         addUser();
    }

    private void addUser(){
        getViewIds();
        getFilledInText();
        if (pass.equals(pass1) && !pass.isEmpty()){
            if (username.isEmpty())
            {
                Toast.makeText(Register.this,"Please fill in all fields!",Toast.LENGTH_LONG).show();
            }
            else {
                //Add user to db.
                ApiRequest api = new ApiRequest(ApiType.REGISTER, this.username, this.pass, new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        if (output != null) {
                            registerSuccess();
                        } else {
                            Toast.makeText(Register.this,"Registration failed. Username already in use.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                api.execute();
            }
        }
        else {
            Toast.makeText(Register.this,"Password do not match!!",Toast.LENGTH_LONG).show();
        }
    }
    private void registerSuccess(){
        Toast.makeText(Register.this,"The user is registered succesfully!",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
    }
    private void getViewIds(){
        edUsername = (EditText)findViewById(R.id.editTextUsername);
        edPass1 = (EditText)findViewById(R.id.editTextPass1);
        edPass2 = (EditText)findViewById(R.id.editTextPass2);
    }

    private void getFilledInText(){
        username = edUsername.getText().toString();
        pass = edPass1.getText().toString();
        pass1 = edPass2.getText().toString();
    }
}
