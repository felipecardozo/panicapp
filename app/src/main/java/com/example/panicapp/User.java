package com.example.panicapp;

public class User {
    private String userId;
    private String email;
    private String latitude;
    private String longitude;
    private String status;

    public User(){}

    public User(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public User(String email, String latitude, String longitude, String status) {
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
