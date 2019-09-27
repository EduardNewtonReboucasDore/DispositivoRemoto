package com.studio.agendavirtual;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    private BootstrapEditText edtName;
    private BootstrapEditText edtEmail;
    private BootstrapEditText edtPassword;
    private BootstrapEditText edtConfirmPassword;

    private BootstrapButton btnRegister;
    private BootstrapButton btnCancel;

    private RadioButton rdMasc;
    private RadioButton rdFam;

    private ImageView imgPictureProfile;

    final User user = new User();

    //Firebase Autenticação
    private FirebaseAuth mAuth;
    //firebse banco
    final DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://agendavirtual-12d92.firebaseio.com");
    final DatabaseReference TB_USERS = db.child("users");
    //********************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inicializarCampos();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())){
                    user.setName(edtName.getText().toString());
                    user.setEmail(edtEmail.getText().toString());
                    user.setPassword(edtPassword.getText().toString());

                    if(rdMasc.isChecked()){
                        user.setSex("Masculino");
                    }else{
                        user.setSex("Feminino");
                    }

                    criarConta(user);

                }else {
                    Toast.makeText(RegisterActivity.this, "As Senhas Não Correspondem", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirLoginActivity();
            }
        });

        imgPictureProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void inicializarCampos(){

        mAuth = FirebaseAuth.getInstance();

        edtName = (BootstrapEditText)findViewById(R.id.edtName);
        edtEmail = (BootstrapEditText)findViewById(R.id.edtEmailRecovery);
        edtPassword = (BootstrapEditText)findViewById(R.id.edtPassword);
        edtConfirmPassword = (BootstrapEditText)findViewById(R.id.edtConfirmPassword);
        btnRegister = (BootstrapButton)findViewById(R.id.btnStatus);
        btnCancel = (BootstrapButton)findViewById(R.id.btnCancel);
        rdMasc = (RadioButton)findViewById(R.id.rdMasculino);
        rdFam = (RadioButton)findViewById(R.id.rdFem);
        imgPictureProfile = (ImageView)findViewById(R.id.imgPictureProfile);
    }

    private void criarConta(final User user){
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            insertUserDB(user);
                            abrirMainActivity();
                            Toast.makeText(RegisterActivity.this, "Cadastro efetuado!", Toast.LENGTH_LONG).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void abrirMainActivity(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void abrirLoginActivity(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void insertUserDB(User user){
        String key = TB_USERS.push().getKey();
        user.setKeyUser(key);
        TB_USERS.child(key).setValue(user);
    }
}
