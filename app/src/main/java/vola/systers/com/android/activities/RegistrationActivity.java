package vola.systers.com.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import vola.systers.com.android.R;
import vola.systers.com.android.handler.HttpHandler;
import vola.systers.com.android.model.Event;


public class RegistrationActivity extends AppCompatActivity {

    private EditText fname, lname, email, affiliations;
    public static String userToken="",emailId="";
    private TextView title;
    private Button btnRegister;
    private RadioGroup attendeeTypeGroup;
    private RadioButton attendeeTypeButton;
    public static String eventId,eventName;
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Event Registration");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        fname = (EditText) findViewById(R.id.input_fname);
        lname = (EditText) findViewById(R.id.input_lname);
        email = (EditText) findViewById(R.id.input_email);
        affiliations = (EditText)findViewById(R.id.input_affiliations);
        title=(TextView) findViewById(R.id.title);
        btnRegister = (Button) findViewById(R.id.btn_register);
        attendeeTypeGroup= (RadioGroup)findViewById(R.id.radioAttendee);
        Event event = (Event) getIntent().getSerializableExtra("event");
        eventId = event.getId();
        eventName = event.getName();

        title.setText("Register to "+ eventName);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailId = user.getEmail();
            userToken = user.getUid();
        }
        email.setText(emailId);
    }

    public void registerToEvent(View view) {
        if (emailId != "") {
            int selectedId = attendeeTypeGroup.getCheckedRadioButtonId();
            attendeeTypeButton = (RadioButton) findViewById(selectedId);
            String selectedAttendeeType = attendeeTypeButton.getText().toString();

            DatabaseReference eventsRef = database.getReference("event_registrations");
            eventsRef.child(userToken).child(eventId).child("attendee_type").setValue(selectedAttendeeType);
            eventsRef.child(userToken).child(eventId).child("first_name").setValue(fname.getText().toString());
            eventsRef.child(userToken).child(eventId).child("last_name").setValue(lname.getText().toString());
            eventsRef.child(userToken).child(eventId).child("email").setValue(emailId);
            eventsRef.child(userToken).child(eventId).child("affiliation").setValue(affiliations.getText().toString());

            Toast.makeText(this, "Registration Successfull!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(RegistrationActivity.this,MenuActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}

