package vola.systers.com.android.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import vola.systers.com.android.R;
import vola.systers.com.android.handler.HttpHandler;


public class RegistrationActivity extends AppCompatActivity {

    private EditText fname, lname, email;
    private TextView title;
    private Button btnRegister;
    public static String eventId,eventName;
    private static String registerEvent = " https://www.eiseverywhere.com/api/v2/ereg/createAttendee.json?accesstoken=$$&email=##&eventid=%%";
    private static String gettingTokenUrl = "https://www.eiseverywhere.com/api/v2/global/authorize.json?accountid=7157&key=74b7ba663ce4885fd0c1ecefbb57fe8580e2a932";
    private static String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fname = (EditText) findViewById(R.id.input_fname);
        lname = (EditText) findViewById(R.id.input_lname);
        email = (EditText) findViewById(R.id.input_email);
        title=(TextView) findViewById(R.id.title);
        btnRegister = (Button) findViewById(R.id.btn_register);
        eventId = getIntent().getStringExtra("id");
        eventName = getIntent().getStringExtra("name");

        title.setText("Register to "+ eventName);
    }

    public void registerToEvent(View view) {

        Toast.makeText(this, eventId, Toast.LENGTH_SHORT).show();
        new Register().execute(eventId, email.getText().toString());
    }

    private class Register extends AsyncTask<String, String, String> {

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


        @Override
        protected String doInBackground(String... args) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String tokenJsonStr = sh.makeServiceCall(gettingTokenUrl);
            Log.i("######", "Token Response : " + tokenJsonStr);
            if (tokenJsonStr != null) {
                try {
                    JSONObject tokenObject = new JSONObject(tokenJsonStr);
                    token = tokenObject.getString("accesstoken");

                    if(args[0]!=null && args[1]!=null){
                        URL url = new URL(registerEvent.replace("$$", token).replace("##", args[1]).replace("%%", args[0]));
                        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
                        String line;
                        StringBuffer jsonString = new StringBuffer();

                        uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        uc.setRequestMethod("POST");
                        uc.setDoInput(true);
                        uc.setInstanceFollowRedirects(false);
                        uc.connect();
                        OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");

                        writer.close();
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                            while ((line = br.readLine()) != null) {
                                jsonString.append(line);
                            }
                            br.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        // uc.disconnect();
                        Log.i("$$$$$$", jsonString.toString());
                        JSONObject jsonObj = new JSONObject(jsonString.toString());
                        Log.i("Response is ",String.valueOf(uc.getResponseCode()));

                        if(String.valueOf(uc.getResponseCode()).equals("200")){
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
                            String hexStr = makeSHA1Hash(args[1]);
                            myRef.child(hexStr).child("events").child(args[0]).child("name").setValue(eventName);
                            myRef.child(hexStr).child("events").child(args[0]).child("attendee_id").setValue(jsonObj.get("attendeeid"));


                            Handler handler = new Handler(Looper.getMainLooper());

                            handler.post(new Runnable() {

                                @Override
                                public void run() {

                                    Toast.makeText(RegistrationActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            finish();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}

