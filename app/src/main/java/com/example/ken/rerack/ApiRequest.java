package com.example.ken.rerack;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ken.rerack.Login.Login;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiRequest extends AsyncTask<String, Void, JSONObject>
{
    Exception mException = null;
    String TYPE = "login";
    public String username;
    public String password;
    public Login login;
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        this.mException = null;
    }

    @Override
    protected JSONObject doInBackground(String... params)
    {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://i315016.hera.fhict.nl/DUO.php");
        urlString.append("?type=").append(TYPE);
        urlString.append("&username=").append(username);
        urlString.append("&password=").append(password);

        Log.d("String: ",urlString.toString());

        HttpURLConnection urlConnection = null;
        URL url = null;
        JSONObject object = null;

        try
        {
            url = new URL(urlString.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream inStream = null;
            inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null)
                response += temp;
            bReader.close();
            inStream.close();
            urlConnection.disconnect();

            object = (JSONObject) new JSONTokener(response).nextValue();

        }
        catch (Exception e)
        {
            this.mException = e;
        }

        return (object);
    }

    @Override
    protected void onPostExecute(JSONObject result)
    {
       super.onPostExecute(result);
        try {

            if(result.getBoolean("Success")){
                String username = result.getString("Username");
                int fitcoins = result.getInt("FitCoins");
                login.loginSuccess(new User(username, fitcoins));
            }else{
                login.loginFailed();
            }
        }catch(Exception e){};

    }
}
