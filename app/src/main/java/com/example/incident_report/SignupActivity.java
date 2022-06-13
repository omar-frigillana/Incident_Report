package com.example.incident_report;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import object.UserDetail;

public class SignupActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton imageButtonBack;

    private EditText editFirstname;
    private EditText editLastname;
    private EditText editAddress;
    private EditText editContact;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private Button btnRegister;

    private ProgressDialog mProgress;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initComponents();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

    }

    private void initComponents(){
        toolbar = (Toolbar) findViewById(R.id.toolBarSignUp);
        imageButtonBack = (ImageButton) findViewById(R.id.imgBack);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        editFirstname = (EditText) findViewById(R.id.editFirstname);
        editLastname = (EditText) findViewById(R.id.editLastname);
        editAddress = (EditText) findViewById(R.id.editAddress);
        editContact = (EditText) findViewById(R.id.editContact);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);

        btnRegister = (Button) findViewById(R.id.btnRegister);

        mProgress = mProgress = new ProgressDialog(this);

    }

    private void registerUser() {
        mProgress.setTitle("Registering a user");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.show();

        String firstname = editFirstname.getText().toString().trim();
        String lastname = editLastname.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String contact = editContact.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmpassword = editConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(firstname)) {
            editFirstname.setError("Required field!");
            mProgress.dismiss();
            return;
        }
        if (TextUtils.isEmpty(lastname)) {
            editLastname.setError("Required field!");
            mProgress.dismiss();
            return;
        }
       if (TextUtils.isEmpty(address)) {
            editAddress.setError("Required field!");
            mProgress.dismiss();
            return;
        }
        if (TextUtils.isEmpty(contact)) {
            editContact.setError("Required field!");
            mProgress.dismiss();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Required field!");
            mProgress.dismiss();
            return;
        }
        if (!email.contains("@")) {
            editEmail.setError("Invalid email!");
            mProgress.dismiss();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Required field!");
            mProgress.dismiss();
            return;
        }
        if (TextUtils.isEmpty(confirmpassword)) {
            editConfirmPassword.setError("Required field!");
            mProgress.dismiss();
            return;
        }
        if (!password.equals(confirmpassword)) {
            editConfirmPassword.setError("Password should be equal to confirm password!");
            mProgress.dismiss();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                }
                else{
                    final FirebaseUser user = mAuth.getCurrentUser();
                    final String uid = mAuth.getCurrentUser().getUid();

                    //instance of an object
                    final UserDetail userDetail = new UserDetail();
                    userDetail.setUid(uid);
                    userDetail.setFirstname(firstname);
                    userDetail.setLastname(lastname);
                    userDetail.setEmail(email.toLowerCase());

                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (!task.isSuccessful()) {
                                mProgress.dismiss();
                                Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                            else {
                                DatabaseReference userProfile = mDatabase.child("userProfile").child(uid);
                                userProfile.setValue(userDetail);
                                mProgress.dismiss();
                                Toast.makeText(getApplicationContext(), "Please verify your account in email", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            }
                        }
                    });
                }
            }
        });
    }
}

