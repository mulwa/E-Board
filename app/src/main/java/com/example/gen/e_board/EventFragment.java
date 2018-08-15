package com.example.gen.e_board;

import android.Manifest;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gen.e_board.Adapter.customInforAdapter;
import com.example.gen.e_board.MyIntentsServices.GeofenceService;
import com.example.gen.e_board.Pojo.Event;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final float GEOFENCE_RADIUS = 500.f;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private double currentLat, currentLong;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference eventsRef;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth.AuthStateListener mAuthlistener;
    private Marker currentLocationMarker;
    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private GeofencingClient geofencingClient;
    private Marker eventMarker;
    private Circle eventFenceLimit;


    public EventFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        eventsRef = database.getReference("Events");

        geofencingClient = LocationServices.getGeofencingClient(getContext());
        fetchEvents();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.event_map);
        mapFragment.getMapAsync(this);


        buildGoogleClient();

        return view;
    }

    private void fetchEvents() {
        showDialog();
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideDialog();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    Log.d("Event", "User name: " + event.getEventName() + ", email " + event.getCost());
                    // Add a marker in event places and move the camera
                    LatLng ev = new LatLng(event.getLat(), event.getLng());

//                    adding geofence to the events locations
                    addGeofence(event.getLat(), event.getLng(), event.getEventName());

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(ev)
                            .title(event.getEventName())
                            .snippet(event.getEventDesc())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    Event event1 = new Event();
                    event1.setEventDate(event.getEventDate());
                    event1.setEventTime(event.getEventTime());
                    event1.setCost(event.getCost());
                    event1.setPlaceName(event.getPlaceName());
                    event1.setTargetGroup(event.getTargetGroup());

                    customInforAdapter inforAdapter = new customInforAdapter(getContext());
                    mMap.setInfoWindowAdapter(inforAdapter);


                    eventMarker = mMap.addMarker(markerOptions);
                    eventMarker.setTag(event1);
                    eventMarker.showInfoWindow();


                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ev));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(getContext(), Login.class));
        } else {
            currentUser = mAuth.getCurrentUser();
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
        locationRequest.setInterval(5000);


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
        Toast.makeText(getContext(), "Onconnectionsusended called", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "connectionFailed", Toast.LENGTH_LONG).show();

    }


    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            currentLat = location.getLatitude();
            currentLong = location.getLongitude();
            LatLng positionLatLng = new LatLng(currentLat, currentLong);

//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(positionLatLng)
//                    .title("Your current  location")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//
//            if (currentLocationMarker != null)
//                currentLocationMarker.remove();
//            currentLocationMarker = mMap.addMarker(markerOptions);


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(positionLatLng).zoom(16f).tilt(70).build();

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
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        }

    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Please wait fetching events...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();

    }

    private void hideDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

    }

    private Geofence setGeofence(double lat, double lng, String key) {
        return new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(lat, lng, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLoiteringDelay(10000)
                .build();
    }

    private GeofencingRequest setGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private PendingIntent createPendingIntent() {
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;
        Intent intent = new Intent(getContext(), GeofenceService.class);
        return PendingIntent.getService(getContext(), GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void addGeofence(final double lat, final double Lng, String key) {
        Geofence geofence = setGeofence(lat, Lng, key);
//        GeofencingRequest geofencingRequest = setGeofencingRequest(geofence);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(
                setGeofencingRequest(geofence),
                createPendingIntent()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    drawFenceCircle(lat, Lng);
                } else {
                    showToast("Geofence could not be added" + task.getException().getMessage());
                    Log.d("geofence", task.getException().toString());
                }

            }
        });

    }

    private void drawFenceCircle(double lati, double lng) {
        if (eventFenceLimit != null)
            eventFenceLimit.remove();
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(lati, lng))
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(GEOFENCE_RADIUS);

        eventFenceLimit = mMap.addCircle(circleOptions);
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }
}
