package com.example.gen.e_board;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    public EditText m_email, m_firstname, m_surname, m_password, m_mobile, m_password2;
    public Button m_btn_sign_up, m_login;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference customerRef;
    private String surname;
    private String firstname;
    private String email;
    private String mobile;
    private String TAG = "signup";
    private ProgressDialog mProgressDialog;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        m_surname = findViewById(R.id.ed_surname);
        m_firstname = findViewById(R.id.ed_firstname);
        m_email = findViewById(R.id.ed_email);
        m_password = findViewById(R.id.ed_password);
        m_password2 = findViewById(R.id.ed_password2);
        m_btn_sign_up = findViewById(R.id.btnsignUp);
        m_mobile = findViewById(R.id.ed_mobile);
        m_login = findViewById(R.id.btnlogin);
        toolbar = findViewById(R.id.toolbar);

        m_login.setOnClickListener(this);
        m_btn_sign_up.setOnClickListener(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Sign Up");
        }

//        get firebaseAuth instance and firebase database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        customerRef = database.getReference("customers");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnsignUp:
                showDialog();
                if (validateinput()) {
                    showToast("ready to");
                    mAuth.createUserWithEmailAndPassword(m_email.getText().toString().trim(), m_password.getText().toString().trim())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        saveUser();
                                    } else {
                                        Log.e(TAG, "createUserWithEmail:failure", task.getException());
                                        showToast("Unable to create account Please try a valid email" + task.getException());
                                    }

                                }
                            });
                } else {
                    hideDialog();
                }
                break;
            case R.id.btnlogin:
                startActivity(new Intent(SignUp.this,Login.class));
                break;
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void saveUser() {
        getInput();
        User customer = new User(surname, firstname, email, mobile);
        customerRef.push().setValue(customer, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                hideDialog();
                if (databaseError == null) {
                    showToast("Successfully Created account");
                    clearUi();
                } else {
                    showToast("Please try again later" + databaseError.getMessage());
                }
            }
        });

    }
    public void getInput() {
        surname = m_surname.getText().toString().trim();
        firstname = m_firstname.getText().toString().trim();
        email = m_email.getText().toString().trim();
        mobile = m_mobile.getText().toString().trim();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(SignUp.this);
            mProgressDialog.setMessage("Creating account Please wait");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();

    }

    private void hideDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

    }

    private void clearUi(){
        m_email.setText("");
        m_surname.setText("");
        m_firstname.setText("");
        m_mobile.setText("");
        m_password2.setText("");
        m_password.setText("");
    }


    public boolean validateinput() {
        if (TextUtils.isEmpty(m_email.getText().toString().trim())) {
            showToast("please provide a valid email address");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(m_email.getText().toString()).matches()) {
            showToast("Please Enter a Valid Email Address");
            return false;
        }
        if (TextUtils.isEmpty(m_firstname.getText().toString().trim())) {
            showToast("Please provide firstname");
            return false;
        }
        if (TextUtils.isEmpty(m_surname.getText().toString())) {
            showToast("Please provide your surname");
            return false;

        }
        if (TextUtils.isEmpty(m_mobile.getText().toString().trim())) {
            showToast("please provide mobile Number");
            return false;
        }
        if (TextUtils.isEmpty(m_password.getText().toString().trim())) {
            showToast("Please enter your password");
            return false;
        }
        if (TextUtils.isEmpty(m_password2.getText().toString())) {
            showToast("Please confirm  your password");
            return false;
        }
        if (!TextUtils.equals(m_password.getText().toString(), m_password2.getText().toString().trim())) {
            showToast("Password did not match");
            return false;
        }
        if (!isNetworkAvailable()) {
            showToast("Please turn data on  or connect to WIFI");
            return false;
        }

        return true;
    }

}
