package com.example.alfajob.Objects;

public class Comment {

    String commentId, comment, userId;

    public Comment(String commentId, String comment, String userId) {
        this.commentId = commentId;
        this.comment = comment;
        this.userId = userId;
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
}
