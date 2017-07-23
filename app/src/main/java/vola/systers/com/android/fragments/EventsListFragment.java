package vola.systers.com.android.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

import vola.systers.com.android.R;
import vola.systers.com.android.activities.EventDetailView;
import vola.systers.com.android.activities.RegistrationActivity;
import vola.systers.com.android.handler.HttpHandler;

import static android.content.ContentValues.TAG;
import static java.lang.Double.parseDouble;


public class EventsListFragment extends Fragment {


    public EventsListFragment() {
    }

    private String TAG = EventsListFragment.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
    public TextView label;
    static String startDate, endDate, eventid, name, startTime, endTime, description, mapUrl, locationName, locationCity, locationCountry, timeZone, eventsUrl,eventStatus;

    // URL to get events JSON
    private static String listEventsUrl = "https://www.eiseverywhere.com/api/v2/global/listEvents.json?accesstoken=";
    private static String eventUrl = "https://www.eiseverywhere.com/api/v2/ereg/getEvent.json?accesstoken=";
    private static String gettingTokenUrl = "https://www.eiseverywhere.com/api/v2/global/authorize.json?accountid=7157&key=74b7ba663ce4885fd0c1ecefbb57fe8580e2a932";
    private static String token = "";
    //public static status="Require Volunteers";
    ArrayList registeredEvents = new ArrayList();
    ArrayList<HashMap<String, String>> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.eventslist_fragment, container, false);
        eventList = new ArrayList<>();
        lv = (ListView) rootView.findViewById(R.id.list);
        String email = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("email", "defaultStringIfNothingFound");

        new GetEvents().execute(email);
        return rootView;
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetEvents extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        public String makeSHA1Hash(String input) {
            String hexStr = "";
            try {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.reset();
                byte[] buffer = input.getBytes("UTF-8");
                md.update(buffer);
                byte[] digest = md.digest();


                for (int i = 0; i < digest.length; i++) {
                    hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return hexStr;
        }

        @Override
        protected Void doInBackground(String... args) {

            String hexStr = makeSHA1Hash(args[0]);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("users").child(hexStr).child("events");

            ValueEventListener vs = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        registeredEvents.add(ds.getKey().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }

            };
            usersRef.addValueEventListener(vs);


            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String tokenJsonStr = sh.makeServiceCall(gettingTokenUrl);


            Log.e(TAG, "Token Response : " + tokenJsonStr);

            if (tokenJsonStr != null) {
                try {
                    JSONObject tokenObject = new JSONObject(tokenJsonStr);
                    token = tokenObject.getString("accesstoken");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            String eventsJsonStr = sh.makeServiceCall(listEventsUrl + token);
            Log.e("EVENTS", "Response from url: " + eventsJsonStr);

            if (eventsJsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray events = new JSONArray(eventsJsonStr);

                    // looping through All events
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject eventJs = events.getJSONObject(i);

                        String id = eventJs.getString("eventid");
                        String eventDetailsUrl = eventUrl + token + "&eventid=" + id;
                        String eventDetailsJsonStr = sh.makeServiceCall(eventDetailsUrl);
                        JSONObject eventDetailsJsonObject = new JSONObject(eventDetailsJsonStr);
                        JSONObject location = null;
                        if (eventDetailsJsonObject.has("location") && eventDetailsJsonObject.getString("location") != "null" && eventDetailsJsonObject.getString("location").isEmpty() == false) {
                            location = eventDetailsJsonObject.getJSONObject("location");
                        }

                        name = eventJs.getString("name");
                        startDate = eventDetailsJsonObject.getString("startdate") != null ? eventDetailsJsonObject.getString("startdate") : "2016-07-07";
                        endDate = eventDetailsJsonObject.getString("enddate");
                        startTime = eventDetailsJsonObject.getString("starttime");
                        endTime = eventDetailsJsonObject.getString("endtime");
                        description = eventDetailsJsonObject.getString("description");
                        locationName = eventDetailsJsonObject.getString("locationname");
                        locationCity = eventDetailsJsonObject.getString("city");
                        locationCountry = eventDetailsJsonObject.getString("country");
                        timeZone = eventDetailsJsonObject.getString("timezone");
                        eventsUrl = eventDetailsJsonObject.getString("homepage");
                        eventid = eventDetailsJsonObject.getString("eventid");
                        if(registeredEvents.contains(eventid))
                        {
                            eventStatus="Registered";

                        }
                        else {
                            eventStatus="Require Volunteers";
                        }
                        if (location == null)
                            mapUrl = "null";
                        else if (location.has("map"))
                            mapUrl = location.getString("map");
                        else
                            mapUrl = null;

                        // tmp hash map for single event
                        HashMap<String, String> event = new HashMap<>();

                        // adding each child node to HashMap key => value
                        event.put("id", id);
                        event.put("name", name);
                        event.put("date", startDate + " to " + endDate);
                        event.put("time", startTime + " to " + endTime);
                        event.put("description", description);
                        event.put("url", mapUrl);
                        event.put("locationCity", locationCity);
                        event.put("locationName", locationName);
                        event.put("locationCountry", locationCountry);
                        event.put("timeZone", timeZone);
                        event.put("eventUrl", eventsUrl);
                        event.put("status",eventStatus);
                        // adding event to event list
                        eventList.add(event);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), eventList,
                    R.layout.events_list_item, new String[]{"name", "date", "time", "locationName","status"},
                    new int[]{R.id.event_name, R.id.date, R.id.time, R.id.location,R.id.event_status});

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                    Object object = lv.getItemAtPosition(position);
                    HashMap s = (HashMap) object;

                    Intent intent = new Intent(getActivity(), EventDetailView.class);
                    intent.putExtra("eventName", s.get("name").toString());
                    intent.putExtra("eventDescription", s.get("description").toString());
                    intent.putExtra("locationName", s.get("locationName").toString());
                    intent.putExtra("locationCity", s.get("locationCity").toString());
                    intent.putExtra("locationCountry", s.get("locationCountry").toString());
                    intent.putExtra("url", s.get("url").toString());
                    intent.putExtra("date", s.get("date").toString());
                    intent.putExtra("time", s.get("time").toString());
                    intent.putExtra("timeZone", s.get("timeZone").toString());
                    intent.putExtra("eventUrl", s.get("eventUrl").toString());
                    intent.putExtra("eventId", s.get("id").toString());
                    startActivity(intent);
                }

            });
        }

    }

}
