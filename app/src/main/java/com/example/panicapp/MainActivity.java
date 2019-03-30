package com.example.panicapp;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private EditText textEmail;
    private EditText textPassword;
    private Button buttonSignIn;
    private Button buttonLogIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Referenciamos los views
        textEmail = (EditText) findViewById(R.id.emailText);
        textPassword = (EditText) findViewById(R.id.passwordText);

        buttonSignIn = (Button) findViewById(R.id.ingresar);
        buttonLogIn = (Button) findViewById(R.id.buttonLogin);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void signup() {
        //Obtenemos el email y la contraseña desde las cajas de texto
        String email = textEmail.getText().toString().trim();
        String password = textPassword.getText().toString().trim();

        //Verificamos que las cajas de texto no esten vacías
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Falta ingresar la contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Realizando registro en linea...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Se ha registrado el usuario con el email: " + textEmail.getText(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "No se pudo registrar el usuario ", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void login(){
        //Obtenemos el email y la contraseña desde las cajas de texto
        if( textEmail.getText().toString().isEmpty() ){
            textEmail.setText("pypelyncar@yahoo.com");
            textPassword.setText("1234567");
        }
        final String email = textEmail.getText().toString().trim();
        String password = textPassword.getText().toString().trim();


        //Verificamos que las cajas de texto no esten vacías
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Falta ingresar la contraseña", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Realizando Login...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Login Succesful: " + textEmail.getText(), Toast.LENGTH_LONG).show();
                    startGeoLocale(email);
                }
                else {
                    Toast.makeText(MainActivity.this, "Login Failed : " + textEmail.getText(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    private void startGeoLocale(String email){
        Intent maps = new Intent(this, MapsActivity.class);
        maps.putExtra("email", email);
        startActivity(maps);
    }
}
