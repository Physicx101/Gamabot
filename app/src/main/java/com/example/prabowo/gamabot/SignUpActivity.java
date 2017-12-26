package com.example.prabowo.gamabot;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button BTregister;
    private EditText ETemail,ETnamapengguna;
    private EditText ETpassword;
    private TextView TVsignin;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        BTregister = (Button) findViewById(R.id.BTregister);
        ETemail = (EditText) findViewById(R.id.ETemail);
        ETpassword = (EditText) findViewById(R.id.ETpassword);
        ETnamapengguna = (EditText) findViewById(R.id.ETnamapengguna);

        TVsignin = (TextView) findViewById(R.id.TVsignin);

        BTregister.setOnClickListener(this);
        TVsignin.setOnClickListener(this);

    }

    public void registerUser() {

        String email = ETemail.getText().toString().trim();
        String password = ETpassword.getText().toString().trim();
        final String namaku = ETnamapengguna.getText().toString().trim();
        System.out.print("Ini lho :" + namaku);




        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Masukkan Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Masukkan Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (namaku.length() > 16){
            Toast.makeText(this, "Nama Maksimal 16 Karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();



        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Registered Succesfull", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("namaku",namaku));

                        } else {
                            Toast.makeText(SignUpActivity.this, "Registered unsuccesfull", Toast.LENGTH_SHORT).show();
                        }
                    }


                });




    }

    @Override
    public void onClick(View v) {

        if (v == BTregister) {
            registerUser();
        }

        if (v == TVsignin) {
            startActivity(new Intent(this,LoginActivity.class));

        }


    }
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Apa anda ingin keluar ?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {  finish(); System.exit(0);
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }


}

