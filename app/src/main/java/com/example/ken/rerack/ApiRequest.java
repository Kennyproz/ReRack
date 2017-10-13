package com.example.ken.rerack;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiRequest extends AsyncTask<String, Void, JSONObject> {
    Exception mException = null;

    private ApiType type = ApiType.LOGIN;
    private String username;
    private String password;
    private int uid;
    private String weight;

    public AsyncResponse callBack = null;//Call back interface

    //public Login login;

    /**
     * @param type     LOGIN or REGISTER
     * @param username username
     * @param password password
     */
    public ApiRequest(ApiType type, String username, String password, AsyncResponse callBack) {
        if (type == ApiType.LOGIN || type == ApiType.REGISTER) {
            this.type = type;
            this.username = username;
            this.password = password;
            this.callBack = callBack;
        } else {
            this.type = null;
        }
    }

    /**
     * @param type GETHISTORY or UPDATEHISTORY
     * @param id   GETHISTORY = userId, UPDATEHISTORY = historyId
     */
    public ApiRequest(ApiType type, int id) {
        if (type == ApiType.GETHISTORY || type == ApiType.UPDATEHISTORY) {
            this.type = type;
            this.uid = id;
        } else {
            this.type = null;
        }
    }

    /**
     * @param type   ADDHISTORY
     * @param id     userId
     * @param weight weight
     */
    public ApiRequest(ApiType type, int id, String weight) {
        if (type == ApiType.ADDHISTORY) {
            this.type = type;
            this.uid = id;
            this.weight = weight;
        } else {
            this.type = null;
        }
    }

    private String getTypeString(ApiType type) {
        switch (type) {
            case LOGIN:
                return "login";
            case REGISTER:
                return "register";
            case ADDHISTORY:
                return "addHistory";
            case UPDATEHISTORY:
                return "updateHistory";
            case GETHISTORY:
                return "getHistory";
            default:
                return null;
        }
    }

    private StringBuilder buildUrl() {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://i315016.hera.fhict.nl/DUO.php");
        urlString.append("?type=").append(getTypeString(type));

        switch (type) {
            case REGISTER:
            case LOGIN:
                urlString.append("&username=").append(this.username);
                urlString.append("&password=").append(this.password);
                break;
            case ADDHISTORY:
                urlString.append("&userId=").append(this.uid);
                urlString.append("&weight=").append(this.weight);
                break;
            case UPDATEHISTORY:
                urlString.append("&historyId=").append(this.uid);
                break;
            case GETHISTORY:
                urlString.append("&userId=").append(this.uid);
            default:
                return null;
        }
        return urlString;

    }

    private Object resultToClass(JSONObject result) {
        try {
            if ((boolean)result.get("Success") == true) {
                switch (type) {
                    case LOGIN:
                        int id = result.getInt("Id");
                        String username = result.getString("Username");
                        int fitcoins = result.getInt("FitCoins");
                        return new User(id, username, fitcoins);
                    case REGISTER:
                        return true;
                    case ADDHISTORY:
                        return true;
                    case UPDATEHISTORY:
                        return true;
                    case GETHISTORY:
                        //TODO: build and return history
                        return new History();
                    default:
                        return null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        this.mException = null;
    }

    @Override
    protected JSONObject doInBackground(String... params)
    {
        StringBuilder urlString = buildUrl();
        if(urlString != null) {
            Log.d("String: ", urlString.toString());

            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject object = null;

            try {
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

            } catch (Exception e) {
                this.mException = e;
            }

            return (object);
        }return null;
    }

    @Override
    protected void onPostExecute(JSONObject result)
    {
       //super.onPostExecute(result);
        Object returnme = resultToClass(result);
       this.callBack.processFinish(returnme);
    }
}
