package com.example.maley735.anotherWeatherApp;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FragmentPage extends Fragment{

    private TextView tempView;
    private TextView tempMinView;
    private TextView tempMaxView;
    private TextView dateView;
    private ImageView cityView;
    private TextView dayView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view;

        // get Intent and the stored data within
        Intent intent = getActivity().getIntent();
        double[] temp_average = intent.getDoubleArrayExtra(StartScreen.TEMP_MESSAGE);
        double[] temp_min = intent.getDoubleArrayExtra(StartScreen.TEMP_MIN_MESSAGE);
        double[] temp_max = intent.getDoubleArrayExtra(StartScreen.TEMP_MAX_MESSAGE);
        String city = intent.getStringExtra(StartScreen.CITY_MESSAGE);


        //get the bundle from Swipe Adapter and page number
        Bundle bundle = getArguments();
        int fragmentNumber = bundle.getInt("count");
        Log.i("Page number", " " + fragmentNumber);

        // inflater creates the swipe pages
        view = inflater.inflate(R.layout.page_fragment_layout, container, false);

        //initialize text views
        tempView = view.findViewById(R.id.temp_val);
        tempMinView = view.findViewById(R.id.temp_min_val);
        tempMaxView = view.findViewById(R.id.temp_max_val);
        dateView = view.findViewById(R.id.datum);
        cityView = view.findViewById(R.id.cityView);
        dayView = view.findViewById(R.id.tag);

        //dates formatted for the user
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE");
        String[] days = new String[5];
        String[] dates = new String[5];
        dates[0] = sdf.format(cal.getTime());
        days[0] = sdf2.format(cal.getTime());
        for(int i=1; i<5; i++){
            cal.add(Calendar.DAY_OF_MONTH, 1);
            dates[i] = sdf.format(cal.getTime());
            days[i] = sdf2.format(cal.getTime());
        }

        //fill the textViews with data of the intent extracted from Json Response
        tempView.setText(String.valueOf((int) Math.round(temp_average[fragmentNumber])) + "°C");
        tempMinView.setText(String.valueOf((int) Math.round(temp_min[fragmentNumber])) + "°C");
        tempMaxView.setText(String.valueOf((int) Math.round(temp_max[fragmentNumber])) + "°C");
        dateView.setText(String.valueOf(dates[fragmentNumber]));
        dayView.setText(String.valueOf(days[fragmentNumber]));
        dateView.setText(dateView.getText() + "lel");


        // set picture associated with the selected city
        if(city.equals("Vienna")){
            cityView.setImageDrawable(getResources().getDrawable(R.drawable.vienna));
        }else if(city.equals("Paris")){
            cityView.setImageDrawable(getResources().getDrawable(R.drawable.paris));
        }else{
            cityView.setImageDrawable(getResources().getDrawable(R.drawable.london));
        }

        return view;
        
    }
}
