package com.example.alfajob.Objects;

public class User {

    private String userId, userName, userEmail, userPassword, userPhotoUrl;

    public User(){}

    public User(String userId, String userName, String userEmail, String userPassword) {

        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }
    public User(String userId, String userName, String userEmail, String userPassword, String userPhotoUrl) {

        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userPhotoUrl = userPhotoUrl;
    }

    public User(String userEmail, String userPassword) {

        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    //Getter methods

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }
}
