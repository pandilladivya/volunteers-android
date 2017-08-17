package vola.systers.com.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import vola.systers.com.android.R;
import vola.systers.com.android.model.Event;

public class EventDetailView extends AppCompatActivity {

    private TextView eventName,eventDescription,locationName,locationCity,locationCountry,eventTime,eventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Event event = (Event) getIntent().getSerializableExtra("selectedEvent");
        String name = event.getName();
        String description = event.getDescription();
        String city = "CITY : " +event.getCity();
        String location = "LOCATION : " +event.getLocationName();
        String country = "COUNTRY : " +event.getCountry();
        String date = "DATE : " + event.getStartDate()+" to "+event.getEndDate();
        String time = "TIME : " + event.getStartTime()+" to "+event.getEndTime();

        eventName = (TextView) findViewById(R.id.event_name);
        eventDescription = (TextView)findViewById(R.id.event_description);
        locationName = (TextView) findViewById(R.id.event_location_name);
        locationCity = (TextView)findViewById(R.id.event_location_city);
        locationCountry = (TextView) findViewById(R.id.event_location_state);
        eventTime=(TextView)findViewById(R.id.event_time);
        eventDate=(TextView)findViewById(R.id.event_date);

        eventName.setText(name);
        eventDescription.setText(description);
        locationName.setText(location);
        locationCity.setText(city);
        locationCountry.setText(country);
        eventDate.setText(date);
        eventTime.setText(time);
    }
}
