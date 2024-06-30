package il.ac.pac.weathercity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private TextView tw;
    private EditText et;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tw = findViewById(R.id.textTotsaa);
        et = findViewById(R.id.editCityName);
        buttonNext = findViewById(R.id.buttonNext);
    }

    public void getWeather(View w) {
        String inEdit = et.getText().toString().trim();
        if (inEdit.isEmpty()) {
            Toast.makeText(this, "No text in the input", Toast.LENGTH_LONG).show();
        } else {
            new GetWeatherFromInternet().execute(inEdit);
        }
    }

    public void goToAdditionalScreen(View w) {
        String cityName = et.getText().toString().trim();
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra("cityName", cityName);
        startActivity(intent);
    }


    private class GetWeatherFromInternet extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tw.setText("Wait...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String city = strings[0];
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=eef03ae5358ac601f22a4568f1c2a97f&units=metric&lang=he";
            HttpsURLConnection conn = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urlString);
                conn = (HttpsURLConnection) url.openConnection();
                conn.connect();

                InputStream stream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject json = new JSONObject(result);
                    double temp = json.getJSONObject("main").getDouble("temp");
                    tw.setText("Temperature: " + temp + "°C");
                    buttonNext.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    tw.setText("Error parsing weather data");
                    buttonNext.setVisibility(View.GONE);
                }
            } else {
                tw.setText("Error fetching weather data");
                buttonNext.setVisibility(View.GONE);
            }
        }
    }
}