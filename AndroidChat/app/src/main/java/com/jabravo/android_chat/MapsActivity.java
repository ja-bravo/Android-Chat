package com.jabravo.android_chat;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener
{

    private GoogleMap mMap;
    private Location targetPosition;
    private Location myPosition;

    private Button buttonMapType, buttonFriendPosition, buttonMyPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        targetPosition = getIntent().getExtras().getParcelable("target_position");
        myPosition = getIntent().getExtras().getParcelable("my_position");

        buttonMapType = (Button) findViewById(R.id.map_style);
        buttonFriendPosition = (Button) findViewById(R.id.map_target);
        buttonMyPosition = (Button) findViewById(R.id.map_me);

        buttonMapType.setOnClickListener(this);
        buttonFriendPosition.setOnClickListener(this);
        buttonMyPosition.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address = getResources().getString(R.string.map_target);
        try
        {
            List<Address> addresses = geocoder.getFromLocation(targetPosition.getLatitude(), targetPosition.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        LatLng targetMarker = new LatLng(targetPosition.getLatitude(), targetPosition.getLongitude());
        LatLng myMarker = new LatLng(myPosition.getLatitude(), myPosition.getLongitude());

        mMap.addMarker(new MarkerOptions().position(targetMarker).title(address));
        mMap.addMarker(new MarkerOptions().position(myMarker).title("You"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(targetMarker)
                .zoom(17)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void changeMapType()
    {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        else
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == buttonMapType.getId())
        {
            changeMapType();
        }
        else if (v.getId() == buttonMyPosition.getId())
        {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(myPosition.getLatitude(), myPosition.getLongitude()))
                    .zoom(17)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else
        {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(targetPosition.getLatitude(), targetPosition.getLongitude()))
                    .zoom(17)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }
}
