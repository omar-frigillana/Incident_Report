package com.example.incident_report;

public class Person {
    private String firstname;
    private String lastname;
    private String address;
    private String contact;
    private String email;
    private String password;
    private String cpassword;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String age) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return cpassword;
    }

    public void setConfirmPassword(String cpassword) {
        this.cpassword = cpassword;
    }


    public Person(String firstname, String lastname, String address, String contact, String email, String password, String cpassword) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.contact = contact;
        this.email = email;
        this.password = password;
        this.cpassword = cpassword;
    }
}
