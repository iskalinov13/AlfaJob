package com.example.alfajob.Objects;

public class ApprovedCV {
    private String cvId, cvTitle, cvSkills,  cvUserEmail, cvUserPhone, cvUrl, cvStarCount, cvCommentCount;

    public ApprovedCV(String cvId, String cvTitle, String cvSkills, String cvUserEmail, String cvUserPhone, String cvUrl, String cvStarCount, String cvCommentCount) {
        this.cvId = cvId;
        this.cvTitle = cvTitle;
        this.cvSkills = cvSkills;
        this.cvUserEmail = cvUserEmail;
        this.cvUserPhone = cvUserPhone;
        this.cvUrl = cvUrl;
        this.cvStarCount = cvStarCount;
        this.cvCommentCount = cvCommentCount;
    }

    public ApprovedCV() {
    }

    public String getCvId() {
        return cvId;
    }

    public void setCvId(String cvId) {
        this.cvId = cvId;
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

    public String getCvUserEmail() {
        return cvUserEmail;
    }

    public void setCvUserEmail(String cvUserEmail) {
        this.cvUserEmail = cvUserEmail;
    }

    public String getCvUserPhone() {
        return cvUserPhone;
    }

    public void setCvUserPhone(String cvUserPhone) {
        this.cvUserPhone = cvUserPhone;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public String getCvStarCount() {
        return cvStarCount;
    }

    public void setCvStarCount(String cvStarCount) {
        this.cvStarCount = cvStarCount;
    }

    public String getCvCommentCount() {
        return cvCommentCount;
    }

    public void setCvCommentCount(String cvCommentCount) {
        this.cvCommentCount = cvCommentCount;
    }
}
