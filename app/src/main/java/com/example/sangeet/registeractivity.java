package com.example.sangeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class registeractivity extends AppCompatActivity {
TextView alreadyHaveAccount;
EditText inputemail,inputpassword,inputconfirmpassword;
Button registerbtn;
ProgressDialog progressdialog;
String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeractivity);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        inputemail = findViewById(R.id.inputemail);
        inputpassword = findViewById(R.id.inputpassword);
        inputconfirmpassword = findViewById(R.id.inputconfirmpassword);
        registerbtn = findViewById(R.id.loginbtn);
        progressdialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(registeractivity.this,loginactivity.class));
                finish();
            }
        });
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerormAuth();
            }
        });
    }
    private void PerormAuth() {
        String email=inputemail.getText().toString();
        String pass=inputpassword.getText().toString();
        String cpass=inputconfirmpassword.getText().toString();

        if(!email.matches(emailPattern)){
            inputemail.setError("Enter Correct Email");
        }
        else if(pass.isEmpty()|| pass.length()<6){
            inputpassword.setError("Enter Proper Password");
        }
        else if(!pass.equals(cpass)){
            inputconfirmpassword.setError("Password Does not match");
        }else{
            progressdialog.setMessage("Please Wait While Registration..");
            progressdialog.setTitle("Registration");
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.show();
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressdialog.dismiss();
                        sendUserToNextActivity();
//                        Toast.makeText(registeractivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        sendverificationEmail();
                    }
                    else {
                        progressdialog.dismiss();
                        Toast.makeText(registeractivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void sendverificationEmail() {
    FirebaseAuth.getInstance().getCurrentUser()
            .sendEmailVerification()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressdialog.dismiss();
                    Toast.makeText(registeractivity.this, "Email Verification Sent", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressdialog.dismiss();
                    Toast.makeText(registeractivity.this, "Email Not Sent", Toast.LENGTH_SHORT).show();
                }
            });

    }


    private void sendUserToNextActivity() {
        Intent intent=new Intent(registeractivity.this,loginactivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}