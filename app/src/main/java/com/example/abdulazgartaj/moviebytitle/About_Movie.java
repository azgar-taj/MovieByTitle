package com.example.abdulazgartaj.moviebytitle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class About_Movie extends AppCompatActivity {
    private TextView movie_title;
    private TextView movie_desc;
    private String id,reqURL;
    private ImageView img_view;
    public static final String MOVIE_URL_STORY = "http://www.omdbapi.com/?apikey=1cc1fde4&plot=full&i=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_movie);

        Intent i = getIntent();
        id = i.getStringExtra("movie_id");

        img_view = (ImageView)findViewById(R.id.pic);
        movie_title = findViewById(R.id.movie_title);
        movie_desc = findViewById(R.id.movie_desc);

        JSONStory req = new JSONStory();
        req.execute();
    }
    private class JSONStory extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... strings) {
            BufferedReader br = null;
            HttpURLConnection connection = null;
            StringBuilder reqJSON = new StringBuilder();
            try {
                URL url = new URL(MOVIE_URL_STORY+id);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.connect();
                if(connection.getResponseCode()==200) {
                    InputStream inx = connection.getInputStream();
                    br = new BufferedReader(new InputStreamReader(inx, Charset.forName("UTF-8")));
                    String line = br.readLine();
                    while (line != null) {
                        reqJSON.append(line);
                        line = br.readLine();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                connection.disconnect();
                return reqJSON.toString();
            }
        }

        @Override
        protected void onPostExecute(String JSONmovie) {
            try {
                JSONObject movie_data = new JSONObject(JSONmovie);
                movie_title.setText(movie_data.getString("Title"));
                movie_desc.setText(movie_data.getString("Plot"));
                reqURL = movie_data.getString("Poster");
                picTask task = new picTask();
                task.execute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class picTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            InputStream ins;
            Bitmap reqImage = null;
            try {
                URL url = new URL(reqURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                ins = connection.getInputStream();
                reqImage = BitmapFactory.decodeStream(ins);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            return reqImage;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            img_view.setImageBitmap(s);
        }
    }

}
