package com.example.ken.rerack.Login;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ken.rerack.Login.Login;
import com.example.ken.rerack.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] intentFiltersArray;
    String[][] techListsArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcome();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        try{
            checkNFCStatus();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //nfc zooi
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[] {ndef, };
        techListsArray = new String[][] { new String[] { NfcF.class.getName() } };
    }
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
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
        if (nfcAdapter != null & nfcAdapter.isEnabled()){

        }

        if (s.isEnabled()){
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        }
        else {
            checkNFCStatus();
        }
    }

    private void welcome(){
        Intent intent = getIntent();
        String textMessage = intent.getStringExtra(Login.EXTRA_MESSAGE);

        TextView text = (TextView)findViewById(R.id.txtWelcome);
        text.setText("Welkom: " + textMessage);
    }

    public void onNewIntent(Intent intent)
    {
        Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        TextView tagID = (TextView) findViewById(R.id.txtTagId);
        tagID.setText("TagID: " + ByteArrayToHexString(myTag.getId()));
        ListView listView = (ListView) findViewById(R.id.lvHome);

    }
    private String ByteArrayToHexString(byte [] inarray)
    {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

}
