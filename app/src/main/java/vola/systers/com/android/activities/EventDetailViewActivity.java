package vola.systers.com.android.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import vola.systers.com.android.R;
import vola.systers.com.android.model.Event;

public class EventDetailViewActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView eventName,eventDescription,locationName,locationCity,locationCountry,eventTime,eventDate;
    private Button register;
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_view);
        Event event = (Event) getIntent().getSerializableExtra("selectedEvent");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(event.getName());
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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
        register=(Button)findViewById(R.id.btn_register);
        register.setOnClickListener(this);

        eventName.setText(name);
        eventDescription.setText(description);
        locationName.setText(location);
        locationCity.setText(city);
        locationCountry.setText(country);
        eventDate.setText(date);
        eventTime.setText(time);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerEvent(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Event event = (Event) getIntent().getSerializableExtra("selectedEvent");
            if(event.getStatus().equals("Registered"))
            {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(this);
                builder.setTitle("You are already Registered for this event!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i=new Intent(EventDetailViewActivity.this,SignInActivity.class);
                                startActivity(i);
                            }
                        })
                        .show();
            }
            else{
                Intent i=new Intent(EventDetailViewActivity.this,RegistrationActivity.class);
                i.putExtra("event",event);
                startActivity(i);
            }
        }
        else
        {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("You Need to Login to register for an Event!")
                    .setMessage("Do You want to Login?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i=new Intent(EventDetailViewActivity.this,SignInActivity.class);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_register:
                registerEvent();
                break;
        }
    }
}
