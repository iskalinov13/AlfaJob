package com.example.alfajob.Objects;

public class AppliedCV {

    private String id, cvTitle, cvSkills,  userEmail, userPhone, cvUrl, starCount, commentCount;

    public AppliedCV(String id, String cvTitle, String cvSkills, String userEmail, String userPhone, String cvUrl, String starCount, String commentCount) {
        this.id = id;
        this.cvTitle = cvTitle;
        this.cvSkills = cvSkills;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.cvUrl = cvUrl;
        this.starCount = starCount;
        this.commentCount = commentCount;
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

    public String getCvSkills() {
        return cvSkills;
    }

    public void setCvSkills(String cvSkills) {
        this.cvSkills = cvSkills;
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

    public String getStarCount() {
        return starCount;
    }

    public void setStarCount(String starCount) {
        this.starCount = starCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }
}
