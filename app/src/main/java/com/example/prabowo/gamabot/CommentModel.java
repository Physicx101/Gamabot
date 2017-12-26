package com.example.prabowo.gamabot;

/**
 * Created by Fauziw97 on 11/19/17.
 */

public class CommentModel {

    private String user;
    private String commentId;
    private String comment;

    public CommentModel() {

    }


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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
