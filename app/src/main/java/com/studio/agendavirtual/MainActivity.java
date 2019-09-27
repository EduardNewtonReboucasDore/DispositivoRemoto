package com.studio.agendavirtual;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.ContextMenu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listView;
    List<Dispositivo> lista;

    //firebse banco
    final DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://agendavirtual-12d92.firebaseio.com");
    final DatabaseReference TB_DISPOSITIVO = db.child("dispositivo");
    //********************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaRegistrarDispositivo();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //processo de lista
        listView = (ListView)findViewById(R.id.listaDispositivos);
        //gera uma tela com menu
        registerForContextMenu(listView);
        //istancia a lista
        lista = new ArrayList<>();
        //filtra no banco os dados da lista
        consultaBanco();

        final DispositivoAdapter dispositivoAdapter = new DispositivoAdapter(MainActivity.this, lista);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Dispositivo dispositivo = dispositivoAdapter.getItem(i);

                Intent intent = new Intent(MainActivity.this, RegistrarDispositivo.class);
                intent.putExtra("codigo", dispositivo.getKey());
                startActivity(intent);
            }
        });
    }

    private void consultaBanco(){
        //selct * from Dispositivos
        TB_DISPOSITIVO.addListenerForSingleValueEvent(valueEventListener);
        //select * from Dispositivos WHERE nome = dudu
        /*Query query = TB_DISPOSITIVO.orderByChild("nome").equalTo("dudu");
        query.addListenerForSingleValueEvent(valueEventListener);*/
        /*Query query = TB_DISPOSITIVO.orderByChild("nome").startAt("d").endAt("d\uf8ff");
        query.addListenerForSingleValueEvent(valueEventListener);*/
    }

    //popula a lista
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            lista.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Dispositivo dispositivo = snapshot.getValue(Dispositivo.class);
                    lista.add(dispositivo);
                }
                DispositivoAdapter adapter = new DispositivoAdapter(MainActivity.this,lista);
                listView.setAdapter(adapter);
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Dispositivo dispositivoSelecionado = (Dispositivo) listView.getAdapter().getItem(info.position);

        final MenuItem itemApagar = menu.add("Apagar Dispositivo");

        itemApagar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Apagar Dispositivo?")
                        .setMessage("Deseja realmente apagar este Dispositivo?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TB_DISPOSITIVO.child(dispositivoSelecionado.getKey()).removeValue();
                                finish();
                                startActivity(getIntent());
                            }
                        })
                        .setNegativeButton("Não", null).show();
                return false;
            }
        });

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void desconectarUsuario(){
        FirebaseAuth.getInstance().signOut();
        abrirTelaLogin();
        Toast.makeText(MainActivity.this, "Usuario desconectado!", Toast.LENGTH_LONG).show();
    }

    private void abrirTelaLogin(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void abrirTelaRegistrarDispositivo(){
        Intent intent = new Intent(MainActivity.this, RegistrarDispositivo.class);
        intent.putExtra("codigo","");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_desconnect) {
            desconectarUsuario();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}