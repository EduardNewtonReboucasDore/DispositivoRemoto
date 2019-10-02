package com.studio.agendavirtual;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import java.util.List;

public class DispositivoAdapter extends ArrayAdapter<Dispositivo> {

    private Context context;
    List<Dispositivo> lista;

    public DispositivoAdapter(Activity context, List<Dispositivo> lista){
        super(context, 0, lista);

        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Dispositivo getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(Dispositivo item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Dispositivo dispositivoPossicao = this.lista.get(position);
        convertView = LayoutInflater.from(this.context).inflate(R.layout.activity_dispositivo_adapter,null);

        TextView txtNome = (TextView)convertView.findViewById(R.id.txtNomeDispositivo);
        BootstrapButton btnStatus = (BootstrapButton)convertView.findViewById(R.id.btnUpdate);

        txtNome.setText(dispositivoPossicao.getNome());
        btnStatus.setText(dispositivoPossicao.getStatus());

        if(!dispositivoPossicao.getStatus().equals("Ligar")){
            btnStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
        }else{
            btnStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        }

        return convertView;
    }


}
