package com.example.panicapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersActivity extends AppCompatActivity {
    private EditText textEmail;
    private Button buttonAddUser;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseUsers;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        //Referenciamos los views
        textEmail = (EditText) findViewById(R.id.userEmail);

        buttonAddUser = (Button) findViewById(R.id.addUser);

        //attaching listener to button
        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
    }

    private void addUser () {
        String email = textEmail.getText().toString();

        if (!TextUtils.isEmpty(email)){
            //getting a unique id using push().getKey() method
            String idUser = databaseUsers.push().getKey();

            //creating an User Object
            User user = new User(idUser, email);
            user.setLatitude("43.730610");
            user.setLongitude("-70.935242");
            user.setStatus("true");

            databaseUsers.child(idUser).setValue(user);


            addUserAuth(email);

            //displaying a success toast
            Toast.makeText(this, "User added", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,"Please set a email", Toast.LENGTH_LONG).show();
        }
    }

    private void addUserAuth (final String email){
        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, "1234567")
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                }
            });
    }
}
