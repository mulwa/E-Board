package com.example.gen.e_board;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends Fragment implements View.OnClickListener {
    public EditText m_email, m_password;
    public Button m_btn_login, m_btn_account;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public login() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login,container,false);
        m_email = view.findViewById(R.id.ed_email);
        m_password = view.findViewById(R.id.ed_password);
        m_btn_account  = view.findViewById(R.id.btncreatAccount);
        m_btn_login = view.findViewById(R.id.btnlogin);

        m_btn_account.setOnClickListener(this);
        m_btn_login.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            currentUser = mAuth.getCurrentUser();
            showToast("User arlready signed");
        }
    }

    public boolean validateInput(){
        if(TextUtils.isEmpty(m_email.getText().toString().trim())){
            showToast("please  provide email address");
            return false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(m_email.getText().toString()).matches()){
            showToast("Please Enter a Valid Email Address");
            return false;
        }
        if(TextUtils.isEmpty(m_password.getText().toString().trim())){
            showToast("Please enter your password");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnlogin){
            if(validateInput()){
                signIn();
            }

        }
        if(id == R.id.btncreatAccount){
            showToast("Redirecting to create account");
        }

    }
    public void showToast(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }
    public void signIn(){
        mAuth.signInWithEmailAndPassword(m_email.getText().toString().trim(),m_password.getText().toString().trim())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            currentUser = mAuth.getCurrentUser();
                            showToast("Sign in successful");
                        }else{
                            showToast("Authentication failed Please use correct email and password:" + task.getException());
                        }

                    }
                });
    }
}
