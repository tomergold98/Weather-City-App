package il.ac.pac.weathercity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity2 extends AppCompatActivity {
    private TextView cityNameTextView;
    private TextView minTempTextView;
    private TextView maxTempTextView;
    private TextView cloudView;
    private TextView pressureView;
    private TextView humidityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        cityNameTextView = findViewById(R.id.cityNameTextView);
        minTempTextView = findViewById(R.id.minTempTextView);
        maxTempTextView = findViewById(R.id.maxTempTextView);
        cloudView = findViewById(R.id.cloudView);
        pressureView = findViewById(R.id.pressureView);
        humidityView = findViewById(R.id.humidityView);

        Button returnToMainButton = findViewById(R.id.backButton);
        returnToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        String cityName = getIntent().getStringExtra("cityName");
        cityNameTextView.setText("City: " + cityName);

        new GetWeatherFromInternet().execute(cityName);
    }

    private class GetWeatherFromInternet extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            minTempTextView.setText("Wait...");
            maxTempTextView.setText("Wait...");
            cloudView.setText("Wait...");
            pressureView.setText("Wait...");
            humidityView.setText("Wait...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String city = strings[0];
            String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=eef03ae5358ac601f22a4568f1c2a97f&units=metric&lang=he";
            HttpsURLConnection conn = null;
            BufferedReader reader = null;

            try {
                URL myUrl = new URL(url);
                conn = (HttpsURLConnection) myUrl.openConnection();
                conn.connect();

                if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    InputStream ins = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(ins));
                    StringBuilder buf = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        buf.append(line).append("\n");
                    }
                    return buf.toString();
                } else {
                    return "Error: " + conn.getResponseMessage();
                }
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
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject json = new JSONObject(s);
                double tempMin = json.getJSONObject("main").getDouble("temp_min");
                double tempMax = json.getJSONObject("main").getDouble("temp_max");
                int cloud = json.getJSONObject("clouds").getInt("all");
                int pressure = json.getJSONObject("main").getInt("pressure");
                int humidity = json.getJSONObject("main").getInt("humidity");

                minTempTextView.setText("Min Temperature: " + tempMin + "°C");
                maxTempTextView.setText("Max Temperature: " + tempMax + "°C");
                cloudView.setText("Cloud status: " + cloud + "%");
                pressureView.setText("Pressure: " + pressure + " hPa");
                humidityView.setText("Humidity: " + humidity + "%");
            } catch (JSONException e) {
                e.printStackTrace();
                minTempTextView.setText("Error retrieving data");
                maxTempTextView.setText("Error retrieving data");
                cloudView.setText("Error retrieving data");
                pressureView.setText("Error retrieving data");
                humidityView.setText("Error retrieving data");
            }
        }
    }
}