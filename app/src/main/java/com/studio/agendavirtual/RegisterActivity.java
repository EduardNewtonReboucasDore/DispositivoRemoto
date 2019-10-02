package com.studio.agendavirtual;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapThumbnail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;


public class RegisterActivity extends AppCompatActivity {

    private BootstrapEditText edtName;
    private BootstrapEditText edtEmail;
    private BootstrapEditText edtPassword;
    private BootstrapEditText edtConfirmPassword;

    private BootstrapButton btnRegister;
    private BootstrapButton btnCancel;

    private RadioButton rdMasc;
    private RadioButton rdFam;

    private BootstrapThumbnail imgPictureProfile;
    private TextView txt_upload;
    private TextView txt_take_picture;

    //Dialog personalizado
    private Dialog dialog;

    final User user = new User();

    //Firebase Autenticação
    private FirebaseAuth mAuth;
    private FirebaseUser userAuth;
    //firebse banco
    final DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://agendavirtual-12d92.firebaseio.com");
    final DatabaseReference TB_USERS = db.child("users");
    //firebase storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    private StorageReference profileRef;
    //********************

    //processo de foto e img usuario
    private static final int REQUEST_IMAGE_CAPTURE = 1;

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
                abrirDaialog();
            }
        });
    }

    private void inicializarCampos(){

        mAuth = FirebaseAuth.getInstance();

        edtName = (BootstrapEditText)findViewById(R.id.edtName);
        edtEmail = (BootstrapEditText)findViewById(R.id.edtEmail);
        edtPassword = (BootstrapEditText)findViewById(R.id.edtPassword);
        edtConfirmPassword = (BootstrapEditText)findViewById(R.id.edtConfirmPassword);
        btnRegister = (BootstrapButton)findViewById(R.id.btnUpdate);
        btnCancel = (BootstrapButton)findViewById(R.id.btnCancel);
        rdMasc = (RadioButton)findViewById(R.id.rdMasculino);
        rdFam = (RadioButton)findViewById(R.id.rdFem);
        imgPictureProfile = (BootstrapThumbnail)findViewById(R.id.imgPictureProfile);
    }

    private void criarConta(final User user){
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            gerarUsuario(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void gerarUsuario(final User user){

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        final String uId = userAuth.getUid();
        user.setUid(uId);

        storageRef = storage.getReference();
        profileRef = storageRef.child("profile/"+ uId + ".jpg");

        // Get the data from an ImageView as bytes
        imgPictureProfile.setDrawingCacheEnabled(true);
        imgPictureProfile.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgPictureProfile.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(RegisterActivity.this, "Falha ao enviar Imagem", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                insertUserDB(user);
                abrirMainActivity();
                Toast.makeText(RegisterActivity.this, "Cadastro efetuado!", Toast.LENGTH_LONG).show();
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

    public void abrirDaialog(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_picture);

        txt_upload = (TextView) dialog.findViewById(R.id.txt_uploadd);
        txt_take_picture = (TextView)dialog.findViewById(R.id.txt_tirar_foto);

        txt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fazerUploadFoto();
                dialog.dismiss();
            }
        });

        txt_take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tirarFoto();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void fazerUploadFoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Selecione uma magem"), 123);
    }

    public void tirarFoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //caso tenha cancelado a foto ou venha null
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //para saber se esta vindo do tirar foto
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 123) {
                Uri image = data.getData();
                Picasso.get().load(image.toString()).rotate(90).into(imgPictureProfile);
            }else if (requestCode == REQUEST_IMAGE_CAPTURE){
                Bundle extra = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extra.get("data");
                imgPictureProfile.setImageBitmap(imageBitmap);
            }
        }
    }
}
