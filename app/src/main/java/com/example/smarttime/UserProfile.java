package com.example.smarttime;

public class UserProfile {
    private String emailAddress;
    private String name;

    public UserProfile(String emailAddress, String name) {
        this.emailAddress = emailAddress;
        this.name = name;
    }

    public UserProfile() {
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
