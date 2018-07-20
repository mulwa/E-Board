package com.example.gen.e_board.Pojo;

public class User {
    private String surname;
    private String firstname;
    private String email;
    private String mobile;

    public User() {
    }

    public User(String surname, String firstname, String email, String mobile) {
        this.surname = surname;
        this.firstname = firstname;
        this.email = email;
        this.mobile = mobile;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
