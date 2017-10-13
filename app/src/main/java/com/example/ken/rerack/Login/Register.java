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

import com.example.ken.rerack.R;
import com.example.ken.rerack.User;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {
    List<User> userList;
    EditText edUsername, edPass1, edPass2;
    String username, pass, pass1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void registerUser(View v){
        EditText username = (EditText)findViewById(R.id.editTextUsername);
        for (User u : userList){
            if (u.getUsername().toString().equals(username.getText().toString())){
                Toast.makeText(Register.this,"Username already in Use!!",Toast.LENGTH_LONG).show();
            }
            else{
                addUser();
                //Intent intent = new Intent(this,Login.class);
                //startActivity(intent);
                //Toast.makeText(Register.this,"The user is registered succesfully!",Toast.LENGTH_LONG).show();
                break;
            }
        }
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
                //TODO
                //Add user to db
            }
        }
        else {
            Toast.makeText(Register.this,"Password do not match!!",Toast.LENGTH_LONG).show();
        }
    }

    private void getViewIds(){
        edUsername = (EditText)findViewById(R.id.editTextUsername);
        edPass1 = (EditText)findViewById(R.id.editTextPass);
        edPass2 = (EditText)findViewById(R.id.editTextpass);
    }

    private void getFilledInText(){
        username = edUsername.getText().toString();
        pass = edPass1.getText().toString();
        pass1 = edPass2.getText().toString();
    }
}
