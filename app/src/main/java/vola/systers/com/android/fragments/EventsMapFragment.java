package vola.systers.com.android.fragments;



import android.os.Bundle;
import android.provider.Settings;
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

/*
 * @author divyapandilla
 * @since 2017-06-10
 */

public class EventsMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

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
        mMap = googleMap;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference coordinatesRef = database.getReference("coordinates");

        ValueEventListener vs = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.mapicon);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.i(TAG,ds.toString());
                    mMap.setMinZoomPreference(20.0f);
                    LatLng marker = new LatLng(Double.parseDouble(ds.child("0").getValue().toString()), Double.parseDouble(ds.child("1").getValue().toString()));
                    mMap.addMarker(new MarkerOptions().position(marker).icon(icon));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));

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