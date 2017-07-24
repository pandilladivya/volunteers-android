package vola.systers.com.android.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;

import vola.systers.com.android.R;
import vola.systers.com.android.fragments.EventsListFragment;
import vola.systers.com.android.fragments.EventsMapFragment;

public class EventDetailView extends AppCompatActivity {

    private TextView eventName,eventDescription,locationName,locationCity,locationCountry,link_location_Details,eventTime,eventDate,eventTimeZone;
    public static String url,eventUrl,eventId,name;
    private FloatingActionButton fab;
    public static String starred="false";

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
        fab = (FloatingActionButton) findViewById(R.id.fab);


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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(starred == "true")
                {
                    // TODO: Write Unstar an event
                }
                else {
                    String email = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("email", "defaultStringIfNothingFound");
                    Log.i("EMAIL", email);
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
                    String hexStr = makeSHA1Hash(email);
                    myRef.child(hexStr).child("starred_events").child(eventId).child("name").setValue(name);

                }
            }

        });
    }

    public String makeSHA1Hash(String input)
    {
        String hexStr = "";
        try{
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.reset();
            byte[] buffer = input.getBytes("UTF-8");
            md.update(buffer);
            byte[] digest = md.digest();


            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return hexStr;
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
