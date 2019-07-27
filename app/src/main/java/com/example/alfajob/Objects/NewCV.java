package com.example.alfajob.Objects;

public class NewCV {

    private String id, cvTitle, userEmail, userPhone, cvUrl;

    public NewCV(String id, String cvTitle, String userEmail, String userPhone, String cvUrl) {
        this.id = id;
        this.cvTitle = cvTitle;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.cvUrl =  cvUrl;
    }

    public NewCV(String id, String cvTitle, String userEmail, String cvUrl) {
        this.id = id;
        this.cvTitle = cvTitle;
        this.userEmail = userEmail;
        this.cvUrl =  cvUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCvTitle() {
        return cvTitle;
    }

    public void setCvTitle(String cvTitle) {
        this.cvTitle = cvTitle;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }
}
