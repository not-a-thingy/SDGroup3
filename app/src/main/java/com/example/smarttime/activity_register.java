package com.example.smarttime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activity_register extends AppCompatActivity {
    EditText Fullname, Username, Email, Password;
    Button Go;
    TextView Login;
    FirebaseAuth fAuth;
    String email,name,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Fullname = findViewById(R.id.tv_fullname);
        Username = findViewById(R.id.tv_username2);
        Email = findViewById(R.id.tv_email);
        Password = findViewById(R.id.tv_password2);
        Go = findViewById(R.id.btn_go);
        Login = findViewById(R.id.tv_text3);

        fAuth = FirebaseAuth.getInstance();

        Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    String user_email = Email.getText().toString().trim();
                    String user_password = Password.getText().toString().trim();
                    fAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendEmailVerification();
                            }else{
                                Toast.makeText(activity_register.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),activity_login.class));
            }
        });
    }

    private Boolean validate(){
        Boolean result = false;

        name = Username.getText().toString();
        password = Password.getText().toString();
        email = Email.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Email.setError("Email is Required.");
        }

        else if (TextUtils.isEmpty(password)) {
            Password.setError("Password is Required.");
        }

        else if (password.length() < 8) {
            Password.setError("Password Must be >= 8 Characters");
        }

        else if (TextUtils.isEmpty(name)){
            Username.setError("Username is Required.");

        }else{
            result = true;
        }

        return result;
    }

    private void sendEmailVerification(){
        FirebaseUser fuser = fAuth.getCurrentUser();
        if(fuser!=null){
            fuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(activity_register.this, "User register complete,verification link has been sent.", Toast.LENGTH_LONG).show();
                        //sendUserData();
                        fAuth.signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), activity_login.class));
                    }else {
                        Toast.makeText(activity_register.this, "Verification link has not been sent.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}