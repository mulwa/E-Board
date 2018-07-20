package com.example.gen.e_board;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gen.e_board.Pojo.Event;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class AddEvent extends Fragment implements View.OnClickListener {
    private EditText mEventName, mEventDesc,mTarget,mCost,mDate,mTime,mLocation;
    private Button mSave;
    private DatePickerDialog.OnDateSetListener mDataListener;
    private TimePickerDialog.OnTimeSetListener mTimeListener;
    private  Calendar cal;
    private int PLACE_PICKER_REQUEST = 1;
    private FirebaseAuth mAuth;
    private FirebaseDatabase  database;
    private DatabaseReference  eventsRefs;
    private ProgressDialog mProgressDialog;
    public LatLng  eventLatLng;


    public AddEvent() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(getContext(),data);
                showToast("Place Latlng"+place.getLatLng());
                mLocation.setText(place.getName());
                eventLatLng = place.getLatLng();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.addevent_layout,container,false);
        mEventName = view.findViewById(R.id.edEvent_name);
        mEventDesc = view.findViewById(R.id.edEvent_desc);
        mTarget  = view.findViewById(R.id.edTarget);
        mCost = view.findViewById(R.id.edCost);
        mDate = view.findViewById(R.id.edEvent_date);
        mTime  = view.findViewById(R.id.edEvent_time);
        mLocation = view.findViewById(R.id.edEventLocation);
        mSave = view.findViewById(R.id.btnSave);
//        addevent listener
        mSave.setOnClickListener(this);
        mLocation.setOnClickListener(this);
        mDate.setOnClickListener(this);
        mTime.setOnClickListener(this);
//        set up firebase
        mAuth  = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        eventsRefs = database.getReference("Events");

        cal =  Calendar.getInstance();

        mDataListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day +"/"+ year;
                mDate.setText(date);
            }
        };

        mTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int min) {
                String time = hours + ":" + min;
                mTime.setText(time);

            }
        };
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            showToast("Please Login first");
        }
        if(!isLocationEnable()){
            // notify user
            showToast("You need to Activate location services inorder to get your location");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.edEvent_date){
            hideKeyboard();
            lauchDateicker();
        }else if (id == R.id.edEvent_time){
            hideKeyboard();
            launchTimePicker();
        }else if (id == R.id.edEventLocation){
            hideKeyboard();
            launchPlacePicker();
        }else if (id == R.id.btnSave){
            hideKeyboard();
            if(validateInputs()){
                saveEvent();
            }
        }

    }
    private void clearForm(){
        mEventName.setText("");
        mEventDesc.setText("");
        mTarget.setText("");
        mCost.setText("");
        mTime.setText("");
        mDate.setText("");
        mLocation.setText("");
    }
    private void saveEvent(){
        showDialog();
        String name = mEventName.getText().toString().trim().toLowerCase();
        String desc = mEventDesc.getText().toString().trim().toLowerCase();
        String target = mTarget.getText().toString().trim();
        String cost = mCost.getText().toString();
        String time = mTime.getText().toString();
        String date = mDate.getText().toString();
        String location = mLocation.getText().toString().trim();

        Event event = new Event(name,desc,target,cost,date,time,location,eventLatLng);
        eventsRefs.push().setValue(event, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                hideDialog();
                if(databaseError == null){
                    showToast("Event Successfully Added");
                    clearForm();
                }else {
                    showToast("Error Please Try again later::"+databaseError.getMessage());
                }
            }
        });
    }
    private boolean  validateInputs(){
        if(TextUtils.isEmpty(mEventName.getText().toString().trim())){
            showToast("Please provide the name of  the event");
            return false;
        }
        if(TextUtils.isEmpty(mEventDesc.getText().toString().trim())){
            showToast("Please provide Event Description");
            return false;
        }
        if(TextUtils.isEmpty(mTarget.getText().toString().trim())){
            showToast("Please Target Audience");
            return false;
        }
        if(TextUtils.isEmpty(mTime.getText().toString().trim())){
            showToast("Please the time of the event");
            return false;
        }
        if(TextUtils.isEmpty(mDate.getText().toString().trim())){
            showToast("Please provide date of the event");
            return false;
        }
        if(TextUtils.isEmpty(mLocation.getText().toString().trim())){
            showToast("Please provide location of the event");
            return false;
        }

        return true;
    }
    private void launchTimePicker(){
        int hr = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);


        TimePickerDialog dialog  = new TimePickerDialog(getContext(),
                android.R.style.Theme_Holo_Dialog_MinWidth,
                mTimeListener,
                hr,min,true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
    private void lauchDateicker(){
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDataListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    private void launchPlacePicker(){
        PlacePicker.IntentBuilder builder  = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }
    public void showToast(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }
    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
    private boolean isLocationEnable(){
        LocationManager mlocationManager;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        mlocationManager = (LocationManager)getActivity().getSystemService(getContext().LOCATION_SERVICE);

        gps_enabled = mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        network_enabled = mlocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!gps_enabled && !network_enabled) {
            return false;
        }
        return true;

    }
    private void showDialog(){
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Please wait");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();

    }
    private void hideDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

    }
}
