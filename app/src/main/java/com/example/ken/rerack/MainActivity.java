package com.example.ken.rerack;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        checkNFCStatus();


    }

    public void checkNFCStatus(){
        if(nfcAdapter != null & nfcAdapter.isEnabled()){
            System.out.println("NFC works :) ");
            Toast.makeText(this,"NFC Available",Toast.LENGTH_LONG).show();
        }
        else {
            System.out.println("nope");
            Toast.makeText(this,"NFC not enabled",Toast.LENGTH_LONG).show();
        }
    }

    public void enableNFC(View v){
        Switch s = (Switch)findViewById(R.id.switchNFC);
        if (s.isEnabled()){
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        }
        else {
            checkNFCStatus();
        }
    }

}
