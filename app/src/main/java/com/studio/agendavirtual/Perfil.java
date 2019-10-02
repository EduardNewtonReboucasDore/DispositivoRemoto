package com.studio.agendavirtual;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity {

    private BootstrapEditText edtName;
    private BootstrapEditText edtEmail;
    private BootstrapEditText edtConfirmPassword;
    private BootstrapEditText edtPassword;

    private BootstrapButton btnUpdate;
    private BootstrapButton btnCancel;

    private RadioButton rdMasc;
    private RadioButton rdFam;

    //FireBase
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference profileRef;

    //Dialog personalizado
    private Dialog dialog;
    private BootstrapThumbnail imgPictureProfile;
    private TextView txt_upload;
    private TextView txt_take_picture;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        inicializarCampos();

        popularDadosUsuario();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailCurrentUser = firebaseAuth.getCurrentUser().getEmail();

                //analisa se é o mesmo usuario
                reference.child("users").orderByChild("email").equalTo(emailCurrentUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                            User user = userSnapshot.getValue(User.class);

                            if (edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {

                                if (rdFam.isChecked()) {
                                    upDateUser(edtName.getText().toString(), edtEmail.getText().toString(),
                                            "Feminino", user.getKeyUser(), user.getUid(), edtPassword.getText().toString());
                                } else if (rdMasc.isChecked()) {
                                    upDateUser(edtName.getText().toString(), edtEmail.getText().toString(),
                                            "Masculino", user.getKeyUser(), user.getUid(), edtPassword.getText().toString());
                                }
                            }else{
                                Toast.makeText(Perfil.this, "As senhas não correspondem!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        edtName = (BootstrapEditText)findViewById(R.id.edtName);
        edtEmail = (BootstrapEditText)findViewById(R.id.edtEmail);
        edtPassword = (BootstrapEditText)findViewById(R.id.edtPassword);
        edtConfirmPassword = (BootstrapEditText)findViewById(R.id.edtConfirmPassword);
        btnUpdate = (BootstrapButton)findViewById(R.id.btnUpdate);
        btnCancel = (BootstrapButton)findViewById(R.id.btnCancel);
        rdMasc = (RadioButton)findViewById(R.id.rdMasculino);
        rdFam = (RadioButton)findViewById(R.id.rdFem);
        imgPictureProfile = (BootstrapThumbnail)findViewById(R.id.imgPictureProfile);
    }

    private void popularDadosUsuario(){

        String emailCurrentUser = firebaseAuth.getCurrentUser().getEmail();

        reference.child("users").orderByChild("email").equalTo(emailCurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    edtName.setText(user.getName());
                    edtEmail.setText(user.getEmail());

                    if (user.getSex().equals("Masculino")){
                        rdMasc.setChecked(true);
                    }else if (user.getSex().equals("Feminino")){
                        rdFam.setChecked(true);
                    }
                    preencherImagemPerfil();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void upDateUser(String name, String email, String sex, String keyUser, String uid, String newPassword){

        reference.child("users");

        User user = new User( name,  email,  sex,  keyUser,  uid, newPassword);

        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + keyUser, userValues);

        reference.updateChildren(childUpdates);

        if (!newPassword.equals("")){
            userAuth.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            }
                        }
                    });
        }

        gerarUsuario(user);

        Toast.makeText(this, "Perfil atualizado", Toast.LENGTH_LONG).show();
        finish();
    }

    private void preencherImagemPerfil(){

        final String uId = userAuth.getUid();

        storageRef.child("profile/"+ uId +".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).into(imgPictureProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
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
                Toast.makeText(Perfil.this, "Falha ao enviar Imagem", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }
}
