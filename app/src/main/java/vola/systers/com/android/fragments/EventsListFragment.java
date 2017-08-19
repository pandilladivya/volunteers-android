package vola.systers.com.android.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vola.systers.com.android.R;
import vola.systers.com.android.activities.EventDetailViewActivity;
import vola.systers.com.android.model.Event;
import vola.systers.com.android.adapter.EventListAdapter;

import static vola.systers.com.android.fragments.ScheduleFragment.database;

public class EventsListFragment extends Fragment {


    public EventsListFragment() {
    }

    private String TAG = EventsListFragment.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView eventListView;
    private static EventListAdapter eventListAdapter;
    public static String userToken="";
    static String startDate, endDate, id,name,startTime,endTime,locationName,description,latitude,longitude,status,max_attendees,city,country;

    ArrayList<Event> eventList = new ArrayList<>();
    ArrayList registeredEvents = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.eventslist_fragment, container, false);
        eventList = new ArrayList<>();
        eventListView = (ListView) rootView.findViewById(R.id.list);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userToken = user.getUid();
        }
        DatabaseReference usersRef = database.getReference("event_registrations").child(userToken);
        ValueEventListener vs = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("EVENT IDS", ds.getKey().toString());
                    registeredEvents.add(ds.getKey().toString());
                }
                new GetEvents().execute();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        };
        usersRef.addValueEventListener(vs);
        return rootView;
    }

    private class GetEvents extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
            if(getActivity()!=null) {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            FirebaseDatabase eventsDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference eventsRef = eventsDatabase.getReference("events");

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot data_snap : dataSnapshot.getChildren()) {

                        id = data_snap.getKey();
                        name = data_snap.child("name").getValue().toString();
                        startDate = data_snap.child("startdate").getValue().toString();
                        endDate = data_snap.child("enddate").getValue().toString();
                        startTime = data_snap.child("starttime").getValue().toString();
                        endTime=data_snap.child("endtime").getValue().toString();
                        locationName=data_snap.child("location").child("name").getValue().toString();
                        description=data_snap.child("description").getValue().toString();
                        city=data_snap.child("location").child("city").getValue().toString();
                        country=data_snap.child("location").child("country").getValue().toString();
                        latitude=data_snap.child("location").child("latitude").getValue().toString();
                        longitude=data_snap.child("location").child("longitude").getValue().toString();

                        if(registeredEvents.contains(id))
                        {
                            status="Registered";
                        }
                        else if(data_snap.child("needs_volunteers").getValue().toString()=="true")
                        {
                            status="Require Volunteers";
                        }
                        else {
                            status="";
                        }
                        eventList.add(new Event(id, name, startDate,endDate,startTime,endTime,locationName,description,city,country,latitude,longitude,status));

                    }
                    if(getContext()!=null) {
                        eventListAdapter = new EventListAdapter(eventList, getContext());
                        eventListView.setAdapter(eventListAdapter);
                        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Event selectedEvent = eventList.get(position);
                                Intent intent = new Intent(getActivity(), EventDetailViewActivity.class);
                                intent.putExtra("selectedEvent", selectedEvent);
                                startActivity(intent);
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }
            };
            eventsRef.addValueEventListener(valueEventListener);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

        }
    }

}
