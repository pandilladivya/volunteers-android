package vola.systers.com.android.activities;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vola.systers.com.android.R;
import vola.systers.com.android.adapter.EventListAdapter;
import vola.systers.com.android.manager.PrefManager;
import vola.systers.com.android.model.Event;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private String emailId, userToken,displayName;
    private EditText fname,lname,email;
    private Button logout;
    private PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        prefManager = new PrefManager(this);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailId = user.getEmail();
            userToken = user.getUid();
            displayName = user.getDisplayName();
            fname = (EditText) findViewById(R.id.input_fname);
            lname = (EditText) findViewById(R.id.input_lname);
            email = (EditText) findViewById(R.id.input_email);
            logout = (Button) findViewById(R.id.btn_logout);

            logout.setOnClickListener(this);
            if(user.getDisplayName()!=null)
            {
                String[] name= displayName.split(" ");
                fname.setText(name[0]);
                lname.setText(name[1]);
            }
            else
            {
                FirebaseDatabase eventsDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference eventsRef = eventsDatabase.getReference("users");
                ValueEventListener valueEventListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            lname.setText(dataSnapshot.child(userToken).child("last_name").getValue().toString());
                            fname.setText(dataSnapshot.child(userToken).child("first_name").getValue().toString());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "Failed to read value.", databaseError.toException());
                    }
                };
                eventsRef.addValueEventListener(valueEventListener);
            }

            email.setText(emailId);
            email.setEnabled(false);
            lname.setEnabled(false);
            fname.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void logoutUser()
    {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
        prefManager.setFirstTimeLaunch(true);
        Intent i=new Intent(ProfileActivity.this,SignInActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_logout:
                logoutUser();
                break;
        }

    }
}
