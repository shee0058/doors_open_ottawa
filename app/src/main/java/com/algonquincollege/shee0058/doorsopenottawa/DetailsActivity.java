package com.algonquincollege.shee0058.doorsopenottawa;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String mAdress;
    private Geocoder mGeoCoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mGeoCoder = new Geocoder(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView buildingName = (TextView) findViewById(R.id.nameTextView);
        TextView buildingAddress = (TextView) findViewById(R.id.addressTextView);
        TextView buildingDescription = (TextView) findViewById(R.id.descriptionTextView);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String nameFromList = bundle.getString("name");
            String addressFromList = bundle.getString("address");
            String descriptionFromList = bundle.getString("description");

            buildingName.setText(nameFromList);
            buildingAddress.setText(addressFromList);
            buildingDescription.setText(descriptionFromList);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mAdress = bundle.getString("address");
            pin(mAdress);
        }
    }

    private void pin(String locationName) {
        try {
            Address address = mGeoCoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(ll).title(locationName));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
        }
    }
}
