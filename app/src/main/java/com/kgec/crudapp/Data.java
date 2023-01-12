package com.kgec.crudapp;

public class Data {
   private String Email,Name,address,phone,uid;

    public Data() {
    }

    public Data(String email, String name, String address, String phone,String uid) {
        Email = email;
        Name = name;
        this.address = address;
        this.phone = phone;
        this.uid=uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
