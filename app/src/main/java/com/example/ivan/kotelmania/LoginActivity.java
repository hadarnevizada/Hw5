package com.example.ivan.kotelmania;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    EditText editUserName;
    EditText editPassNum;
    FirebaseAuth auth;
    private String TAG = "LoginActivity";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            //user is already exist and logged in
            Log.e(TAG,"user allready exist and logged in: " + auth.getUid());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivityForResult(intent, 1);
        }

        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editUserName = findViewById(R.id.UserName);
                editPassNum = findViewById(R.id.Password);
                final String userName = editUserName.getText().toString();
                final String passNum = editPassNum.getText().toString();

                auth.signInWithEmailAndPassword(userName,passNum).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //login success
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivityForResult(intent, 1);
                        } else {
                            //login failed - create new user and signIn
                            auth.createUserWithEmailAndPassword(userName,passNum).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //success to create new user and login
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivityForResult(intent, 1);
                                    } else {
                                        //failed to create new user
                                        Log.e(TAG,task.toString());
                                    }
                                }
                            });
                        }
                    }
                });

//                if(!userName.equals("Dari") && !PassNum.equals("Dari") ) {
//                    Toast.makeText(LoginActivity.this, "User or password incorect.", Toast.LENGTH_LONG).show();
//                    return ;
//                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }
}
