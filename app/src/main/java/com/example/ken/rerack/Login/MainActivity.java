package com.example.ken.rerack.Login;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ken.rerack.Hardware;
import com.example.ken.rerack.ItemMover;
import com.example.ken.rerack.R;
import com.example.ken.rerack.User;

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
    TextView tvtxtName, tvRestackedStatus, tvFitCoins, tvPoints, tvWelcome, tvListviewHeader,tvDescript;
    ProgressBar pbProgress;
    Hardware hardware;
    ItemMover itemMover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        hardware = new Hardware(this);
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
        tvtxtName = (TextView) findViewById(R.id.txtName);
        tvRestackedStatus = (TextView) findViewById(R.id.tvRestackStatus);
        tvFitCoins = (TextView) findViewById(R.id.tvFitcoins);
        pbProgress = (ProgressBar) findViewById(R.id.pbProgress);
        tvPoints = (TextView) findViewById(R.id.tvPoints);
        tvListviewHeader = (TextView)findViewById(R.id.textView);
        tvWelcome = (TextView)findViewById(R.id.txtWelcome);
        tvDescript = (TextView)findViewById(R.id.tvDescriptionGame);
        tvPoints.setVisibility(View.INVISIBLE);

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
        tvtxtName.setText(user.getUsername());
        tvRestackedStatus.setText(this.timesRestacked + "/4 Restacked");
        tvFitCoins.setText(user.getFitCoins() + " Fitcoins");
        pbProgress.setMax(4);
        pbProgress.setProgress(this.timesRestacked);
        addHistory();
    }
    public void onPause() {
        super.onPause();
        if(nfcAdapter != null)
        nfcAdapter.disableForegroundDispatch(this);
        itemMover.getTimerTask().cancel();
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
       // tvTagID.setText("TagID: " + this.tagId);
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
        hardware.vibrate(300);
        Toast.makeText(this, "Scanned in", Toast.LENGTH_LONG).show();
    }

    private void ScanOut() {
        if (isScanned()) {
            this.tagId = "";
            editor.putInt("timesRestacked", timesRestacked);
            editor.putString("tagId", "");
            editor.commit();
            if (timesRestacked < 4) {
                this.timesRestacked++;
                user.increaseFitCoins(25);
                tvFitCoins.setText(user.getFitCoins() + " Fitcoins");
                Toast.makeText(this, "Scanned out. You received 25 FitCoins, Tap for more!", Toast.LENGTH_SHORT).show();
                specialEffect();

            } else {
                Toast.makeText(this, "Scanned out.", Toast.LENGTH_LONG).show();
            }
            hardware.vibrate(600);
            updateHistory();
            tvRestackedStatus.setText(this.timesRestacked + "/4 Restacked");
            pbProgress.setProgress(this.timesRestacked);
        }
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
            currentWeight = randInt;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void updateHistory(){
        String kg = "";
        switch (currentWeight){
            case 1:
                kg = "2,5 KG - Vandaag";
                break;
            case 2:
                kg = "5 KG - Vandaag";
                break;
            case 3:
                kg = "10 KG - Vandaag";
                break;
            case 4:
                kg = "20 KG - Vandaag";
                break;
            default:
                break;
        }
        history.add(0,kg);
        listView.setAdapter(null);
        listView.setAdapter(adapter);
        img.setImageResource(R.drawable.scan);
    }

    private void specialEffect(){
        tvPoints.setVisibility(View.VISIBLE);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        itemMover = new ItemMover(size.x,size.y,tvPoints);
        itemMover.setAllItems(listView,img,tvRestackedStatus,tvtxtName,tvWelcome,tvListviewHeader,tvWelcome,pbProgress,tvDescript);
        itemMover.moveItem();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        itemMover.getTimerTask().cancel();
    }

    public void specialEffectTap(View v){
        user.increaseFitCoins(1);
        Toast.makeText(this, "+1 Fitcoins", Toast.LENGTH_SHORT).show();
        tvFitCoins.setText(user.getFitCoins() + " Fitcoins");
    }
}
