package com.example.gen.e_board;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener{
    public EditText m_email, m_password;
    public Button m_btn_login, m_btn_account;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ProgressDialog mProgressDialog;
    private Toolbar  toolbar;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        m_email = findViewById(R.id.ed_email);
        m_password = findViewById(R.id.ed_password);
        m_btn_account = findViewById(R.id.btncreatAccount);
        m_btn_login = findViewById(R.id.btnlogin);

        m_btn_account.setOnClickListener(this);
        m_btn_login.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser  = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }

            }
        };

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Login");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener !=  null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    public boolean validateInput() {
        if (TextUtils.isEmpty(m_email.getText().toString().trim())) {
            showToast("please  provide email address");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(m_email.getText().toString()).matches()) {
            showToast("Please Enter a Valid Email Address");
            return false;
        }
        if (TextUtils.isEmpty(m_password.getText().toString().trim())) {
            showToast("Please enter your password");
            return false;
        }

        return true;
    }
    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(Login.this);
            mProgressDialog.setMessage("Authenticating user Please wait");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();

    }

    private void hideDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnlogin) {
            if (validateInput()) {
                signIn();
            }

        }
        if (id == R.id.btncreatAccount) {
           startActivity(new Intent(Login.this,SignUp.class));
        }

    }
    public void signIn() {
        showDialog();
        mAuth.signInWithEmailAndPassword(m_email.getText().toString().trim(), m_password.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideDialog();
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            showToast("Authentication failed Please use correct email and password:" + task.getException());
                        }

                    }
                });
    }
}
