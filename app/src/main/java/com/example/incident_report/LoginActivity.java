package com.example.incident_report;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.incident_report.object.UserDetail;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;

    private Button mLogin;
    private Button mSignUp;
    private SignInButton mGoogleSign;

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        /*mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        })*/;

        goGoogleSignIn();

    }

    private void initComponents(){
        mEmail = (EditText) findViewById(R.id.editTextEmail);
        mPassword = (EditText) findViewById(R.id.editTextPassword);

        mLogin = (Button) findViewById(R.id.buttonLogin);
        //mSignUp = (Button) findViewById(R.id.buttonSignUp);
        mGoogleSign = findViewById(R.id.sign_in_button);
        mGoogleSign.setSize(SignInButton.SIZE_STANDARD);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);
    }

    private void login(){
        mProgress.setTitle("Logging in");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.show();

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            mEmail.setError("Required field!");
            mProgress.dismiss();
            return;
        }
        if (TextUtils.isEmpty(password)){
            mPassword.setError("Required field!");
            mProgress.dismiss();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
                else{
                    if (!mAuth.getCurrentUser().isEmailVerified()) {
                        Toast.makeText(getApplicationContext(), "Please verify your account through your email!", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                        return;
                    }
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        });
    }

    private void goGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
    }

    private void googleSignIn(){
        mProgress.setTitle("Logging in");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.show();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //handleSignInResult(task);
            if (task.isSuccessful()) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                firebaseAuthWithGoogle(result.getSignInAccount());
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mProgress.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            createProfile(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            mProgress.dismiss();
                            Toast.makeText(LoginActivity.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createProfile(FirebaseUser user){
        DatabaseReference profileRef = mDatabase.child("userProfile").child(user.getUid());

        UserDetail userDetail = new UserDetail();
        userDetail.setUid(user.getUid());
        userDetail.setName(user.getDisplayName());
        userDetail.setEmail(user.getEmail());


        profileRef.setValue(userDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}