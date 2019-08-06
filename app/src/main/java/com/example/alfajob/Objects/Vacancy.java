package com.example.alfajob.Objects;

public class Vacancy {
    String userId, userName, imgUrl, vacancyTitle, vacancyDescription, vacancyDate, vacancyId;

    public Vacancy(String userId, String userName, String imgUrl, String vacancyTitle, String vacancyDescription, String vacancyDate, String vacancyId) {
        this.userId = userId;
        this.userName = userName;
        this.imgUrl = imgUrl;
        this.vacancyTitle = vacancyTitle;
        this.vacancyDescription = vacancyDescription;
        this.vacancyDate = vacancyDate;
        this.vacancyId = vacancyId;
    }

    public Vacancy() {
    }

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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVacancyTitle() {
        return vacancyTitle;
    }

    public void setVacancyTitle(String vacancyTitle) {
        this.vacancyTitle = vacancyTitle;
    }

    public String getVacancyDescription() {
        return vacancyDescription;
    }

    public void setVacancyDescription(String vacancyDescription) {
        this.vacancyDescription = vacancyDescription;
    }

    public String getVacancyDate() {
        return vacancyDate;
    }

    public void setVacancyDate(String vacancyDate) {
        this.vacancyDate = vacancyDate;
    }

    public String getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(String vacancyId) {
        this.vacancyId = vacancyId;
    }
}