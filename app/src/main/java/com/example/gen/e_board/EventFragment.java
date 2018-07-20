package com.example.gen.e_board;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EventFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private double currentLat,currentLong;


    public EventFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.event_map);
        mapFragment.getMapAsync(this);
        



        buildGoogleClient();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in event places and move the camera
        LatLng event = new LatLng(-1.006299, 37.011695);
        mMap.addMarker(new MarkerOptions().position(event).title("Dance event"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(event));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

        // Add a marker in event places and move the camera
//        LatLng currentLocation = new LatLng(currentLat, currentLong);
//        mMap.addMarker(new MarkerOptions().position(currentLocation).title("mycurre location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
//        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));



    }

    private void buildGoogleClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);






    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getContext(),"Onconnectionsusended called",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(),"connectionFailed",Toast.LENGTH_LONG).show();

    }


    @Override
    public void onLocationChanged(Location location) {

        if(location != null){
            currentLat = location.getLatitude();
            currentLong = location.getLongitude();
            LatLng positionLatLng = new LatLng(currentLat,currentLong);
            mMap.addMarker(new MarkerOptions().position(positionLatLng).title("My Current Location"));

        }

        Toast.makeText(getContext(),"Location changed:"+currentLat +"Longtitude:"+currentLong,Toast.LENGTH_LONG).show();

    }
}
