package com.example.maley735.anotherWeatherApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class StartScreen extends AppCompatActivity implements View.OnClickListener {

    //base URL
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";
    // App ID to use OpenWeather data
    final String APP_ID = "fd42d4b218adf8a0572a50b770c567f5";

    //hashmap with built URLs for different cities
    private HashMap<String, String> cityURLMap = new HashMap<String, String>() {{
        put("Vienna", WEATHER_URL + "Vienna,at&units=metric&appid=" + APP_ID);
        put("Paris", WEATHER_URL + "Paris,fr&units=metric&appid=" + APP_ID);
        put("London", WEATHER_URL + "London,uk&units=metric&appid=" + APP_ID);
    }};

    //intent.putExtra()-Method Keys
    public static final String TEMP_MESSAGE = "temp";
    public static final String TEMP_MIN_MESSAGE = "temp_min";
    public static final String TEMP_MAX_MESSAGE = "temp_max";
    public static final String CITY_MESSAGE = "city";

    private String cityName;
    private Spinner citySpinner;
    private Button confirmButton;

/**
 * A request dispatch queue with a thread pool of dispatchers.
 * Calling add(Request) will enqueue the given Request for dispatch,
 * resolving from either cache or network on a worker thread, and then delivering
 * a parsed response on the main thread.
 */
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        //initialize requestQueue
        requestQueue = Volley.newRequestQueue(this);

        //initialize adapter for the spinner list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<String>(cityURLMap.keySet()));

        //set layout resource for spinner dropdown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //initialize spinner and set its adapter
        citySpinner = findViewById(R.id.citySpinner);
        citySpinner.setAdapter(adapter);

        //initialize confirm button and its listener
        confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);

    }

    //returns a String array with the dates of the next 5 days
    private String[] fiveDays(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] array = new String[5];
        array[0] = sdf.format(cal.getTime());
        for(int i=1; i<5; i++){
            cal.add(Calendar.DAY_OF_MONTH, 1);
            array[i] = sdf.format(cal.getTime());
        }
        return array;
    }

    private void processResult(JSONObject apiResponse, String selectedCity){
        try {
            double[] temp_average_array = {0.0, 0.0, 0.0, 0.0, 0.0};
            double[] temp_min_array = {1000.0, 1000.0, 1000.0, 1000.0, 1000.0};
            double[] temp_max_array = {-1000.0, -1000.0, -1000.0, -1000.0, -1000.0};
            int[] count = {0, 0, 0, 0, 0};
            String[] dates_array = fiveDays();

            JSONArray weathers = apiResponse.getJSONArray("list");
            for (int i = 0; i < weathers.length(); i++){
                JSONObject weather = weathers.getJSONObject(i);
                for(int k=0; k<5; k++) {
                    // Error handling: On the day the app is running if it's past 21:00 pm,
                    // there is no more 3-hour period for that day and no data can be
                    // extracted from the JSON for the initial day. Just use the next days first 3-hour period.
                    if(count[0]==0 && !weather.getString("dt_txt").contains(dates_array[0])){
                        temp_average_array[0] = weather.getJSONObject("main").getDouble("temp");
                        temp_min_array[0] = weather.getJSONObject("main").getDouble("temp_min");
                        temp_max_array[0] = weather.getJSONObject("main").getDouble("temp_max");
                    }
                    //if the JsonObject has the given date, do the following
                    if (weather.getString("dt_txt").contains(dates_array[k])) {
                        //store temp values and increment count of their occurences to later calculating the average
                        temp_average_array[k] += weather.getJSONObject("main").getDouble("temp");
                        count[k]++;
                        //if new temp_min smaller than stored temp_min, overwrite
                        if (temp_min_array[k] > weather.getJSONObject("main").getDouble("temp_min")) {
                            temp_min_array[k] = weather.getJSONObject("main").getDouble("temp_min");
                        }
                        //if new temp_max bigger than stored temp_max, overwrite
                        if (temp_max_array[k] < weather.getJSONObject("main").getDouble("temp_max")) {
                            temp_max_array[k] = weather.getJSONObject("main").getDouble("temp_max");
                        }
                    }
                }

            }
            //calculate temp_average for every day and log out the data
            for(int k=0; k<5; k++) {
                //if the count of 3-hour periods for that day is zero, dont divide.
                if(count[k]!=0){
                    temp_average_array[k] = temp_average_array[k] / count[k];
                }
                Log.i("LEL", "date ="+ dates_array[k] + " count="+ count[k] + "  temp=" + temp_average_array[k]
                        + " temp_min=" + temp_min_array[k] + " temp_max=" +temp_max_array[k]);
            }

            //change activity to main activity providing the stored data for it
            Intent result = new Intent(this, MainActivity.class);
            result.putExtra(TEMP_MESSAGE, temp_average_array);
            result.putExtra(TEMP_MIN_MESSAGE, temp_min_array);
            result.putExtra(TEMP_MAX_MESSAGE, temp_max_array);
            result.putExtra(CITY_MESSAGE, cityName);
            startActivity(result);

        } catch (JSONException e) {
            Toast.makeText(StartScreen.this,
                    "Could not parse API response!",
                    Toast.LENGTH_LONG).show();
            Log.e("PARSER_ERROR", e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        final String selectedCity = citySpinner.getSelectedItem().toString();
        cityName = selectedCity;
        String URL = cityURLMap.get(selectedCity);
        Log.i("URL", URL);

        JsonRequest weatherForecastRequest = new JsonObjectRequest(
                Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("API_RESPONSE", response.toString());
                        processResult(response, selectedCity);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(StartScreen.this,
                                "Please try again!",
                                Toast.LENGTH_LONG).show();
                        Log.e("API_ERROR", error.getMessage());
                    }
                });

        requestQueue.add(weatherForecastRequest);
    }
}
