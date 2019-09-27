package com.studio.agendavirtual;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final int PERMISSIONS_CAMERA = 10;
    private static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 11;
    private static final int PERMISSIONS_READ_EXTERNAL_STORAGE = 12;
    private BootstrapButton btnLogin;
    private BootstrapButton btnCancelar;
    private BootstrapButton btnRegister;
    private BootstrapButton btnSendEmail;
    private BootstrapButton btnCancelAlert;

    private BootstrapEditText edtLogin;
    private BootstrapEditText edtPassword;
    private BootstrapEditText edtEmail;

    private TextView txtRecoveryPassord;

    //Dialog personalizado
    private Dialog dialog;

    //Instanciando FireBase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializarCampos();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarLogin(edtLogin.getText().toString(), edtPassword.getText().toString());
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtLogin.setText("");
                edtPassword.setText("");
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCadastro();
            }
        });

        txtRecoveryPassord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialog();
            }
        });
    }

    private void inicializarCampos(){

        mAuth = FirebaseAuth.getInstance();

        btnLogin = (BootstrapButton)findViewById(R.id.btnLogin);
        btnCancelar = (BootstrapButton)findViewById(R.id.btnCancel);
        btnRegister = (BootstrapButton)findViewById(R.id.btnStatus);
        edtLogin = (BootstrapEditText)findViewById(R.id.edtEmailRecovery);
        edtPassword = (BootstrapEditText)findViewById(R.id.edtSenha);
        txtRecoveryPassord = (TextView)findViewById(R.id.txtRecoveryPassord);
    }

    private void efetuarLogin(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            abrirManActivity();

                            Toast.makeText(LoginActivity.this, "Login OK!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void abrirCadastro(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void abrirManActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarPermissoes();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) abrirManActivity();
    }

    private void abrirDialog(){
        dialog = new Dialog(LoginActivity.this);

        dialog.setContentView(R.layout.alert_recover_password);

        btnSendEmail = (BootstrapButton)dialog.findViewById(R.id.btnSendEmail);
        btnCancelAlert = (BootstrapButton)dialog.findViewById(R.id.btnCancel);
        edtEmail = (BootstrapEditText)dialog.findViewById(R.id.edtEmailRecovery);

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.sendPasswordResetEmail(edtEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this,
                                            "Verifique sua caixa de E-mail!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(LoginActivity.this, "E-mail invalido!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                dialog.dismiss();
            }
        });

        btnCancelAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void verificarPermissoes(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)){
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSIONS_CAMERA);

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_WRITE_EXTERNAL_STORAGE);

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }
}
