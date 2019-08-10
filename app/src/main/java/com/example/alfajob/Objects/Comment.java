package com.example.alfajob.Objects;

public class Comment {

    String commentId, comment, userId, date;

    public Comment(String commentId, String comment, String userId, String date) {
        this.commentId = commentId;
        this.comment = comment;
        this.userId = userId;
        this.date = date;
    }

    public Comment(){}

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
