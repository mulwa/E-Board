package com.example.gen.e_board;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gen.e_board.Pojo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signUp extends Fragment implements View.OnClickListener{
    public EditText m_email, m_firstname, m_surname, m_password, m_mobile, m_password2;
    public Button m_btn_sign_up, m_login;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase  database;
    private DatabaseReference customerRef;
    private String surname;
    private String firstname;
    private String email;
    private String mobile;
    private String TAG = "signup";

    public signUp() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup,container,false);
        m_surname = view.findViewById(R.id.ed_surname);
        m_firstname = view.findViewById(R.id.ed_firstname);
        m_email = view.findViewById(R.id.ed_email);
        m_password = view.findViewById(R.id.ed_password);
        m_password2 = view.findViewById(R.id.ed_password2);
        m_btn_sign_up = view.findViewById(R.id.btnsignUp);
        m_mobile = view.findViewById(R.id.ed_mobile);
        m_login = view.findViewById(R.id.btnlogin);

        m_login.setOnClickListener(this);
        m_btn_sign_up.setOnClickListener(this);

//        get firebaseAuth instance and firebase database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        customerRef = database.getReference("customers");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        check if user is signed in
        currentUser  =  mAuth.getCurrentUser();
        if(currentUser != null){
            showToast("User already signed in please sign out first");
        }else {
            showToast("User dont  exist");
        }

    }


    public boolean validateinput(){
        getInput();
        if(TextUtils.isEmpty(m_email.getText().toString().trim())){
            showToast("please provide a valid email address");
            return false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(m_email.getText().toString()).matches()){
            showToast("Please Enter a Valid Email Address");
            return false;
        }
        if(TextUtils.isEmpty(m_firstname.getText().toString().trim())){
            showToast("Please provide firstname");
            return false;
        }
        if(TextUtils.isEmpty(m_surname.getText().toString())){
            showToast("Please provide your surname");
            return false;

        }
        if(TextUtils.isEmpty(m_mobile.getText().toString().trim())){
            showToast("please provide mobile Number");
            return false;
        }
        if(TextUtils.isEmpty(m_password.getText().toString().trim())){
            showToast("Please enter your password");
            return false;
        }
        if(TextUtils.isEmpty(m_password2.getText().toString())){
            showToast("Please confirm  your password");
            return false;
        }
        if(!TextUtils.equals(m_password.getText().toString(),m_password2.getText().toString().trim())){
            showToast("Password did not match");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.btnsignUp:
                if(validateinput()){
                    showToast("ready to");
                    mAuth.createUserWithEmailAndPassword(email,m_password.getText().toString().trim())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        saveUser();
                                    }else {
                                        Log.e(TAG, "createUserWithEmail:failure", task.getException());
                                        showToast("Unable to create account Please try a valid email"+task.getException());
                                    }

                                }
                            });
                }
                break;
            case R.id.btnlogin:
                showToast("redirect to login");
                break;
        }
    }
    public void showToast(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }
    public void saveUser(){
        User customer =  new User(surname,firstname,email,mobile);
        customerRef.push().setValue(customer, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    showToast("Successfully Created account");
                }else {
                    showToast("Please try again later"+databaseError.getMessage());
                }
            }
        });

    }
    public void getInput(){
        surname = m_surname.getText().toString().trim();
        firstname = m_firstname.getText().toString().trim();
        email = m_email.getText().toString().trim();
        mobile =  m_mobile.getText().toString().trim();
    }
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }
}
