package com.example.ken.rerack.Login;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ken.rerack.Login.Login;
import com.example.ken.rerack.R;
import com.example.ken.rerack.User;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] intentFiltersArray;
    String[][] techListsArray;
    User user;
    String tagId;
    int timesRestacked;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView tagID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tagID = (TextView) findViewById(R.id.txtTagId);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();
        this.tagId = sharedPreferences.getString("tagId","");
        this.timesRestacked = sharedPreferences.getInt("timesRescacked",0);
        welcome();
    /*Begin NFC*/
        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            checkNFCStatus();

            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            ndef.addDataType("*/*");
            intentFiltersArray = new IntentFilter[]{ndef,};
            techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
        }catch(Exception ex){
            ex.printStackTrace();
        }
    /*End NFC*/
    }
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        try {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }catch(Exception ex){
            ex.printStackTrace();
        }
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
        if (nfcAdapter != null & nfcAdapter.isEnabled()){}
        if (s.isEnabled()){
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        }
        else {
            checkNFCStatus();
        }
    }

    private void welcome(){
        Intent intent = getIntent();
        this.user = (User)intent.getSerializableExtra("user");
        TextView text = (TextView)findViewById(R.id.txtWelcome);
        text.setText("Welkom: "+user.getUsername()+" You have "+user.getFitCoins()+" fitCoins.");
    }

    public void onNewIntent(Intent intent)
    {
        Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String _tagId = ByteArrayToHexString(myTag.getId());
        System.out.print("Scanned id: "+_tagId);
        System.out.print("Old id: "+this.tagId);
        //New tag
        if(!Objects.equals(_tagId, this.tagId)){
            this.tagId = _tagId;
            ScanIn();
        }else{
            ScanOut();
        }
        tagID.setText("TagID: " + this.tagId);
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
    private void ScanIn(){
        editor.putString("tagId", tagId);
        editor.commit();
        Toast.makeText(this,"Scanned in",Toast.LENGTH_LONG).show();
    }
    private void ScanOut(){
        if(isScanned()) {
            this.tagId = "";
            this.timesRestacked++;
            editor.putInt("timesRescacked", timesRestacked);
            editor.putString("tagId", "");
            editor.commit();
            if(timesRestacked <= 4){
                user.increaseFitCoins(25);
                Toast.makeText(this,"Scanned out. You received 25 FitCoins",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Scanned out.",Toast.LENGTH_LONG).show();

            }
        }
    }
    private void updateTimesRestacked(){
        //TODO:: update progressbar for TimesRestacked
    }
    private boolean isScanned(){
        if(tagId != "")return true;
        return false;
    }

}
