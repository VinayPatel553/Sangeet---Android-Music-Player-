package com.example.sangeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

public class loginactivity extends AppCompatActivity {
    TextView createNewAccount,forgot;
    EditText inputemail, inputpassword;
    Button loginbtn;
    ProgressDialog progressdialog;
    SQLiteDatabase db;
    Cursor c;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);
        forgot = findViewById(R.id.forgot);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotemail();
            }
        });






        progressdialog = new ProgressDialog(this);
        createNewAccount = findViewById(R.id.createNewAccount);
        inputemail = findViewById(R.id.inputemail);
        inputpassword = findViewById(R.id.inputpassword);
        loginbtn = findViewById(R.id.loginbtn);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db=openOrCreateDatabase("Mydata1", Context.MODE_PRIVATE,null);
        db.execSQL("Create Table if not exists"+" usert(email varchar,pass varchar)");

        c=db.rawQuery("Select * from usert;",null);

        if (c.moveToNext()){
            String email=c.getString(0);
            String pass=c.getString(1);
            Performlogin(email,pass);

        }


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(loginactivity.this, registeractivity.class));
                finish();
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Performlogin(inputemail.getText().toString(),inputpassword.getText().toString());

            }
        });


    }

    private void Performlogin(String email,String pass) {


        if (!email.matches(emailPattern)) {
            inputemail.setError("Enter Correct Email");

        } else if (false) {
            inputpassword.setError("Enter Proper Password");
        } else {
            Log.d("vvv",email + pass);

            progressdialog.setMessage("Please Wait While Login..");
            progressdialog.setTitle("Login");
            Log.d("vvv",email + pass);

            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.show();
            Log.d("vvv",email + pass);

            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                            progressdialog.dismiss();
                            sendUserToNextActivity();


                            db.execSQL("Insert into usert values('"+email+"','"+pass+"')");


                            Toast.makeText(loginactivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(loginactivity.this, "Please Verify your email first", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        progressdialog.dismiss();
                        Toast.makeText(loginactivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent=new Intent(loginactivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void forgotemail() {
        String emailx = inputemail.getText().toString();
        if (!emailx.matches(emailPattern)) {

            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();

        }
        else{
            mAuth.sendPasswordResetEmail(emailx).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(loginactivity.this, "Password reset link sent", Toast.LENGTH_SHORT).show();


                    }
                    else {
                        Toast.makeText(loginactivity.this, "Unregistered email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



    }
}