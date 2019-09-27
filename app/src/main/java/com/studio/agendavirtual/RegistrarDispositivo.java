package com.studio.agendavirtual;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistrarDispositivo extends AppCompatActivity {

    private EditText edtNome;
    private BootstrapButton btnSalvar;
    private BootstrapButton btnCancelar;

    final Dispositivo dispositivo = new Dispositivo();

    //firebase
    final DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://agendavirtual-12d92.firebaseio.com");
    final DatabaseReference TB_DISPOSITIVO = db.child("dispositivo");
    //********************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_dispositivo);
        inicializarCampos();

        final String codigo = getIntent().getExtras().getString("codigo");

        if(!codigo.isEmpty()){
            TB_DISPOSITIVO.addListenerForSingleValueEvent(valueEventListener);
        }

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edtNome.getText().toString().equals(null)) {
                    dispositivo.setNome(edtNome.getText().toString());
                    dispositivo.setStatus("Ligar");

                    if(!codigo.isEmpty()){
                        TB_DISPOSITIVO.child(codigo).child("nome").setValue(edtNome.getText().toString());
                        abrirMainActivity();
                    }else {
                        insertDispositivoDB(dispositivo);
                    }
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirMainActivity();
            }
        });

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Dispositivo dispositivo = snapshot.getValue(Dispositivo.class);
                    edtNome.setText(dispositivo.getNome().toString());
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    private void inicializarCampos(){

        edtNome = (EditText)findViewById(R.id.edtNameDispositivo);
        btnSalvar = (BootstrapButton)findViewById(R.id.btnSalvarDispositivo);
        btnCancelar = (BootstrapButton)findViewById(R.id.btnCancelDispositivo);
    }

    private void insertDispositivoDB(Dispositivo dispositivo){

        String key = TB_DISPOSITIVO.push().getKey();
        dispositivo.setKey(key);
        TB_DISPOSITIVO.child(key).setValue(dispositivo);

        Toast.makeText(RegistrarDispositivo.this, "Dispositivo adicionado!", Toast.LENGTH_SHORT).show();
        abrirMainActivity();
    }

    private void abrirMainActivity(){

        Intent intent = new Intent(RegistrarDispositivo.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
