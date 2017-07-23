package vola.systers.com.android.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
import vola.systers.com.android.handler.HttpHandler;

public class ScheduleFragment extends Fragment {


    public ScheduleFragment() {
    }

    private String TAG = ScheduleFragment.class.getSimpleName();
    ArrayList registeredEvents = new ArrayList();
    private ProgressDialog pDialog;
    private ListView lv;
    static String startDate, endDate, id, name, startTime, endTime, eventid, eventlocation;

    private static String eventUrl = "https://www.eiseverywhere.com/api/v2/ereg/getEvent.json?accesstoken=";
    private static String gettingTokenUrl = "https://www.eiseverywhere.com/api/v2/global/authorize.json?accountid=7157&key=74b7ba663ce4885fd0c1ecefbb57fe8580e2a932";
    private static String token = "";

    ArrayList<HashMap<String, String>> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.schedule_list_fragment, container, false);
        eventList = new ArrayList<>();
        lv = (ListView) rootView.findViewById(R.id.list);
        String email = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("email", "defaultStringIfNothingFound");
        Log.i("EMAIL", email);
        String hexStr = makeSHA1Hash(email);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.i("HEX", hexStr);
        DatabaseReference usersRef = database.getReference("users").child(hexStr).child("events");
        ValueEventListener vs = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("EVENT IDS", ds.getKey().toString());
                    registeredEvents.add(ds.getKey().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }

        };
        usersRef.addValueEventListener(vs);

        new GetEvents().execute(email);
        return rootView;
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

        @Override
        protected Void doInBackground(String... args) {

            HttpHandler sh = new HttpHandler();
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
            for (int i = 0; i < registeredEvents.size(); i++) {

                String id = registeredEvents.get(i).toString();
                Log.i("ID", id);
                try {
                    String eventDetailsUrl = eventUrl + token + "&eventid=" + id;
                    String eventDetailsJsonStr = sh.makeServiceCall(eventDetailsUrl);

                    JSONObject eventDetailsJsonObject = new JSONObject(eventDetailsJsonStr);
                    Log.i("JSON", eventDetailsJsonObject.toString());
                    JSONObject location;
                    if (eventDetailsJsonObject.has("location") && eventDetailsJsonObject.getString("location") != "null" && eventDetailsJsonObject.getString("location").isEmpty() == false) {
                        location = eventDetailsJsonObject.getJSONObject("location");
                    }
                    id = eventDetailsJsonObject.getString("eventid");
                    name = eventDetailsJsonObject.getString("name");
                    startDate = eventDetailsJsonObject.getString("startdate");
                    endDate = eventDetailsJsonObject.getString("enddate");
                    startTime = eventDetailsJsonObject.getString("starttime");
                    endTime = eventDetailsJsonObject.getString("endtime");
                    eventid = eventDetailsJsonObject.getString("eventid");
                    eventlocation = eventDetailsJsonObject.getString("locationname");

                    HashMap<String, String> event = new HashMap<>();

                    // adding each child node to HashMap key => value
                    event.put("id", id);
                    event.put("name", name);
                    event.put("date", startDate + " to " + endDate);
                    event.put("time", startTime + " to " + endTime);
                    event.put("eventlocation", eventlocation);

                    eventList.add(event);

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
                    R.layout.schedule_list_item, new String[]{"name", "date", "time", "eventlocation"}, new int[]{R.id.event_name, R.id.date, R.id.time, R.id.location});

            lv.setAdapter(adapter);
        }

    }

}