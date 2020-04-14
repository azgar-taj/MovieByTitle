package com.example.abdulazgartaj.moviebytitle;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String MOVIE_URL_SEARCH = "http://www.omdbapi.com/?apikey=bef9e210&s=";
    public static movieAdapter adapter;
    private static Button speak;
    private static EditText inputed;
    public static final int REQ_CODE_SPEECH_INPUT = 1234;
    public static  ListView lst;

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Speech not Supported",
                    Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    inputed.setText(result.get(0));
                    start_search(speak);
                }
                break;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lst = findViewById(R.id.searches);
        inputed = findViewById(R.id.movie_name);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new_movie movie = (new_movie) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),movie.getMovie_name(),Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this,About_Movie.class);
                i.putExtra("movie_id", movie.getMovie_id());
                startActivity(i);
            }
        });

        speak = findViewById(R.id.speak_button);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });
    }

    public void start_search(View view) {
        JSONSearch must = new JSONSearch();
        must.execute();
    }

    private class JSONSearch extends AsyncTask<String,Integer,ArrayList<new_movie>>{

        @Override
        protected ArrayList<new_movie> doInBackground(String... strings) {
            String MOVIE_NAME = inputed.getText().toString().replaceAll(" ","+");
            BufferedReader br = null;
            HttpURLConnection connection = null;
            StringBuilder reqJSON = new StringBuilder();
            try {
                URL url = new URL(MOVIE_URL_SEARCH+MOVIE_NAME);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                connection.disconnect();
                return ConvertToSearch(reqJSON.toString());
            }
        }

        @Override
        protected void onPostExecute(ArrayList<new_movie> movies) {
            lst.setEmptyView(findViewById(R.id.empty_dude));
            adapter = new movieAdapter(getApplicationContext(),movies);
            lst.setAdapter(adapter);
        }
    }
    public ArrayList<new_movie> ConvertToSearch(String JSONstring){
        ArrayList<new_movie> movies = new ArrayList<>();
        try {
            JSONObject reqJSON = new JSONObject(JSONstring);
            if(reqJSON.getString("Response").equals("False")){
                return movies;
            }
            else {
                JSONArray movis = reqJSON.getJSONArray("Search");
                for (int i = 0; i < movis.length(); i++) {
                    JSONObject movi = movis.getJSONObject(i);
                    movies.add(new new_movie(movi.getString("Title"), movi.getString("Year"),movi.getString("imdbID")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return movies;
        }
    }
}
