package vola.systers.com.android.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vola.systers.com.android.R;

import static android.content.ContentValues.TAG;

public class EventsMapFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.eventsmap_fragment, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference coordinatesRef = database.getReference("events");
        ValueEventListener vs = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BitmapDescriptor event_icon = BitmapDescriptorFactory.fromResource(R.drawable.mapicon);
                BitmapDescriptor event_reqVolunteers_icon = BitmapDescriptorFactory.fromResource(R.drawable.mapicon_volunteers);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.i(TAG,ds.toString());
                    LatLng marker = new LatLng(Double.parseDouble(ds.child("location").child("latitude").getValue().toString()), Double.parseDouble(ds.child("location").child("longitude").getValue().toString()));
                    if(ds.child("needs_volunteers").getValue().toString()=="true")
                    {
                        map.addMarker(new MarkerOptions().position(marker).icon(event_reqVolunteers_icon).title(ds.child("name").getValue().toString()));
                    }
                    else {
                        map.addMarker(new MarkerOptions().position(marker).icon(event_icon).title(ds.child("name").getValue().toString()));
                    }
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,14));

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        };
        coordinatesRef.addValueEventListener(vs);
    }

}




