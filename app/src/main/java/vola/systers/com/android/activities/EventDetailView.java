package vola.systers.com.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import vola.systers.com.android.R;

public class EventDetailView extends AppCompatActivity {

    private TextView eventName,eventDescription,locationName,locationCity,locationCountry,link_location_Details,eventTime,eventDate,eventTimeZone;
    public static String url,eventUrl,eventId,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = getIntent().getStringExtra("eventName");
        String description = getIntent().getStringExtra("eventDescription");
        String city = getIntent().getStringExtra("locationCity");
        String location = getIntent().getStringExtra("locationName");
        String country = getIntent().getStringExtra("locationCountry");
        url = getIntent().getStringExtra("url");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String timeZone = getIntent().getStringExtra("timeZone");
        eventUrl=getIntent().getStringExtra("eventUrl");
        eventId=getIntent().getStringExtra("eventId");

        eventName = (TextView) findViewById(R.id.event_name);
        eventDescription = (TextView)findViewById(R.id.event_description);
        locationName = (TextView) findViewById(R.id.event_location_name);
        locationCity = (TextView)findViewById(R.id.event_location_city);
        locationCountry = (TextView) findViewById(R.id.event_location_state);
        link_location_Details=(TextView)findViewById(R.id.link_location_Details);
        eventTime=(TextView)findViewById(R.id.event_time);
        eventDate=(TextView)findViewById(R.id.event_date);
        eventTimeZone=(TextView)findViewById(R.id.event_timeZone);


        eventName.setText(name);
        eventDescription.setText(description);
        locationName.setText(location);
        locationCity.setText(city);
        locationCountry.setText(country);
        eventDate.setText(date);
        eventTimeZone.setText(timeZone);
        eventTime.setText(time);

        if(location.isEmpty() || location=="null" || location==null)
        {
            locationName.setVisibility(View.GONE);
        }
        if(description.isEmpty() || description=="null" || description==null)
        {
            eventDescription.setVisibility(View.GONE);
        }
        if(city.isEmpty() || city=="null" || city==null)
        {
            locationCity.setVisibility(View.GONE);
        }
        if(country.isEmpty() || country=="null" || country==null)
        {
            locationCountry.setVisibility(View.GONE);
        }
        if(url==null || url=="null")
        {
            link_location_Details.setVisibility(View.GONE);
        }
        if(date.isEmpty() || date=="null" || date==null)
        {
            eventDate.setVisibility(View.GONE);
        }
        if(time.isEmpty() || time=="null" || time==null)
        {
            eventTime.setVisibility(View.GONE);
        }
        if(timeZone==null || timeZone=="null" || timeZone.isEmpty())
        {
            eventTimeZone.setVisibility(View.GONE);
        }
    }

    public void onLocationClicked(View view) {
        Intent intent = new Intent(EventDetailView.this,LocationDetails.class);
        intent.putExtra("url",url);
        startActivity(intent);
    }

    public void onEventClicked(View view) {
        Intent intent = new Intent(EventDetailView.this,LocationDetails.class);
        intent.putExtra("url",eventUrl);
        startActivity(intent);
    }

    public void onRegisterClicked(View view) {
        Intent intent = new Intent(EventDetailView.this,RegistrationActivity.class);
        intent.putExtra("id",eventId);
        intent.putExtra("name",name);
        startActivity(intent);
    }

}
