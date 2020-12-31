package com.example.smartbus3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.AsyncTask;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    public void addaswipe(View view)
    {
        ToneGenerator toneg1=new ToneGenerator(AudioManager.STREAM_MUSIC,100);
        toneg1.startTone(ToneGenerator.TONE_CDMA_PIP,150);


        long currentmillis= (new Date()).getTime();
        DateFormat simple = new SimpleDateFormat("dd-MMM-yyyy hh.mm.ss.SSSSSS aa");

        // Creating date from milliseconds
        // using Date() constructor

        // Formatting Date according to the
        // given format
        String ts=simple.format(currentmillis);
        double latitude=10;
        double longitude=10;
        int sid=9;

        try {
            downloadJSON("http://employees.oneonta.edu/zhangs/csci242disabled/smartbus/newswipe.php?sid="
                    + sid + "&long=" + longitude + "&lat=" + latitude + "&ts=" + URLEncoder.encode(ts, "utf-8"));
        }
        catch (Exception e)
        {

            Context context = getApplicationContext();
            CharSequence text = "Something wrong "+e.toString();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        downloadJSON("http://employees.oneonta.edu/zhangs/csci242disabled/smartbus/t4.php");
    }


    private void downloadJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] stocks = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            stocks[i] = obj.getString("name") + " " + obj.getString("price");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stocks);
        listView.setAdapter(arrayAdapter);
    }
}