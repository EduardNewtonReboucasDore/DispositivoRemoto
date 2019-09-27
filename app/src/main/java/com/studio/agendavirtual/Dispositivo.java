package com.studio.agendavirtual;

public class Dispositivo {

    private String key;
    private String nome;
    private String status;

    public Dispositivo() { }

    public Dispositivo(String key, String nome, String status) {
        this.key = key;
        this.nome = nome;
        this.status = status;
    }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
