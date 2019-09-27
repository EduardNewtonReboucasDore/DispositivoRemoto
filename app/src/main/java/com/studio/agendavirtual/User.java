package com.studio.agendavirtual;

import com.google.firebase.database.Exclude;

public class User {

    private String name;
    private String email;
    private String sex;
    private String keyUser;
    private String password;

    public User(String name, String email, String sex, String keyUser, String password) {
        this.name = name;
        this.email = email;
        this.sex = sex;
        this.keyUser = keyUser;
        this.password = password;
    }

    public User() { }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getSex() { return sex; }

    public void setSex(String sex) { this.sex = sex; }

    public String getKeyUser() { return keyUser; }

    public void setKeyUser(String keyUser) { this.keyUser = keyUser; }

    @Exclude
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}
