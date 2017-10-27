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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ken.rerack.Login.Login;
import com.example.ken.rerack.R;
import com.example.ken.rerack.User;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] intentFiltersArray;
    ImageView img;
    String[][] techListsArray;
    User user;
    String tagId;
    int timesRestacked, currentWeight;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    List history;
    ArrayAdapter<String> adapter;
    ListView listView;
    TextView tvTagID, tvWelcomeText, tvRestackedStatus, tvFitCoins;
    ProgressBar pbProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();


    /*Begin NFC*/
        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            checkNFCStatus();

            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            ndef.addDataType("*/*");
            intentFiltersArray = new IntentFilter[]{ndef,};
            techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    /*End NFC*/
    }
    private void initialize() {
        //initialize layout items
        img = (ImageView)findViewById(R.id.imageView);
        tvWelcomeText = (TextView) findViewById(R.id.txtWelcome);
        tvRestackedStatus = (TextView) findViewById(R.id.tvRestackStatus);
        tvFitCoins = (TextView) findViewById(R.id.tvFitcoins);
        pbProgress = (ProgressBar) findViewById(R.id.pbProgress);
        tvTagID = (TextView) findViewById(R.id.txtTagId);

        //sharedPreferences
        sharedPreferences = getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.clear().commit();

        this.tagId = sharedPreferences.getString("tagId", "");
        this.timesRestacked = sharedPreferences.getInt("timesRestacked", 0);

        //get user
        Intent intent = getIntent();
        this.user = (User) intent.getSerializableExtra("user");

        //set value to layout items
        tvWelcomeText.setText("Welkom: " + user.getUsername());
        tvRestackedStatus.setText(this.timesRestacked + "/4 Restacked");
        tvFitCoins.setText(user.getFitCoins() + " Fitcoins");
        pbProgress.setMax(4);
        pbProgress.setProgress(this.timesRestacked);
        tvTagID.setText("Tag ID: "+this.tagId);
        addHistory();
    }
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        try {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void checkNFCStatus() {
        if (nfcAdapter != null & nfcAdapter.isEnabled()) {
            System.out.println("NFC works :) ");
            Toast.makeText(this, "NFC Available", Toast.LENGTH_LONG).show();
        } else {
            System.out.println("nope");
            Toast.makeText(this, "NFC not enabled", Toast.LENGTH_LONG).show();
        }
    }

    public void enableNFC(View v) {
        Switch s = (Switch) findViewById(R.id.switchNFC);
        if (nfcAdapter != null & nfcAdapter.isEnabled()) {
        }
        if (s.isEnabled()) {
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        } else {
            checkNFCStatus();
        }
    }

    public void onNewIntent(Intent intent) {
        Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String _tagId = ByteArrayToHexString(myTag.getId());
        System.out.print("Scanned id: " + _tagId);
        System.out.print("Old id: " + this.tagId);
        //New tag
        if (!Objects.equals(_tagId, this.tagId)) {
            this.tagId = _tagId;
            ScanIn();
        } else {
            ScanOut();
        }
        tvTagID.setText("TagID: " + this.tagId);
    }

    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    private void ScanIn() {
        editor.putString("tagId", tagId);
        editor.commit();
        changeImage();
        Toast.makeText(this, "Scanned in", Toast.LENGTH_LONG).show();
    }

    private void ScanOut() {
        if (isScanned()) {
            this.tagId = "";
            editor.putInt("timesRestacked", timesRestacked);
            editor.putString("tagId", "");
            editor.commit();
            if (timesRestacked <= 4) {
                this.timesRestacked++;
                user.increaseFitCoins(25);
                tvFitCoins.setText(user.getFitCoins() + " Fitcoins");
                Toast.makeText(this, "Scanned out. You received 25 FitCoins", Toast.LENGTH_LONG).show();

                updateHistory();
            } else {
                Toast.makeText(this, "Scanned out.", Toast.LENGTH_LONG).show();
            }
            tvRestackedStatus.setText(this.timesRestacked + "/4 Restacked");
            pbProgress.setProgress(this.timesRestacked);

        }
    }

    private void updateTimesRestacked() {
        //TODO:: update progressbar for TimesRestacked
    }

    private boolean isScanned() {
        if (tagId != "") return true;
        return false;
    }

    private void addHistory() {
        listView = (ListView) findViewById(R.id.lvHome);
        history = new ArrayList<String>(Arrays.asList("10 KG - 27-10-2017", " 5 KG - 20-10-2017", "10 KG - 20-10-2017", "10 KG - 20-10-2017", "10 KG - 20-10-2017", "10 KG - 20-10-2017"));
        adapter = new ArrayAdapter<>(listView.getContext(), android.R.layout.simple_list_item_1, history);
        listView.setAdapter(adapter);
    }

    private void changeImage(){
        Random rand = new Random();
        int randInt = rand.nextInt(4)+1;
        String image = "img" +randInt;
        int id = getResources().getIdentifier(image,"drawable",getPackageName());
        try{
            img.setImageResource(id);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void updateHistory(){
        switch (currentWeight){
            case 1:
                history.add(0,"2,5 KG - Vandaag");

                listView.setAdapter(null);
                listView.setAdapter(adapter);
                break;
            case 2:
                history.add(0,"5KG - Vandaag");
                listView.setAdapter(null);
                listView.setAdapter(adapter);
                break;
            case 3:
                history.add(0,"10KG - Vandaag");
                listView.setAdapter(null);
                listView.setAdapter(adapter);
                break;
            case 4:
                history.add(0,"20KG - Vandaag");
                listView.setAdapter(null);
                listView.setAdapter(adapter);

                break;
            default:
                break;
        }

    }

}
