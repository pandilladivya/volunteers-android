package vola.systers.com.volunteers_android.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import vola.systers.com.volunteers_android.R;
import vola.systers.com.volunteers_android.handler.HttpHandler;

public class EventsListFragment extends Fragment {


    public EventsListFragment() {
    }

    private String TAG = EventsListFragment.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
    static String startDate, endDate, id,name,startTime,endTime;

    // URL to get events JSON
    private static String url = "http://divya-gsoc.esy.es/sample/data.json";
    private static String url2 = "http://divya-gsoc.esy.es/sample/data2.json";

    ArrayList<HashMap<String, String>> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.eventslist_fragment, container, false);
        eventList = new ArrayList<>();
        lv = (ListView) rootView.findViewById(R.id.list);
        new GetEvents().execute();
        return rootView;
    }

    /**
     * Async task class to get json by making HTTP call
     */

    private class GetEvents extends AsyncTask<Void, Void, Void> {

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
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String eventsJsonStr = sh.makeServiceCall(url);
            String eventDetailsJsonStr = sh.makeServiceCall(url2);
            Log.e(TAG, eventsJsonStr);
            Log.e(TAG, eventDetailsJsonStr);

            if (eventsJsonStr != null && eventDetailsJsonStr != null) {
                try {
                    JSONObject eventsJsonObj = new JSONObject(eventsJsonStr);
                    JSONObject eventDetailsJsonObject = new JSONObject(eventDetailsJsonStr);

                    // Getting JSON Array node
                    JSONArray events = eventsJsonObj.getJSONArray("events");

                    // looping through All events
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject eventJs = events.getJSONObject(i);

                        id = eventJs.getString("eventid");
                        name = eventJs.getString("name");
                        startDate = eventDetailsJsonObject.getString("startdate");
                        endDate = eventDetailsJsonObject.getString("enddate");
                        startTime = eventDetailsJsonObject.getString("starttime");
                        endTime=eventDetailsJsonObject.getString("endtime");

                        // tmp hash map for single event
                        HashMap<String, String> event = new HashMap<>();

                        // adding each child node to HashMap key => value
                        event.put("id", id);
                        event.put("name", name);
                        event.put("date", startDate+" to "+endDate);
                        event.put("time",startTime+" to "+endTime);
                        event.put("location","Orlando");

                        // adding event to event list
                        eventList.add(event);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, String.valueOf(R.string.parsing_error) + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),
                                    R.string.parsing_error + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, String.valueOf(R.string.json_error));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                R.string.json_error,
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
                    R.layout.list_item, new String[]{"name", "date", "time","location"}, new int[]{R.id.event_name, R.id.date, R.id.time,R.id.location});

            lv.setAdapter(adapter);

        }

    }

}