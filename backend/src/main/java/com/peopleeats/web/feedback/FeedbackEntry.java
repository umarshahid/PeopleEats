package com.peopleeats.web.feedback;

public class FeedbackEntry {
    private int orderNo;
    private int rating;
    private String comment;
    private String createdAt;

    public FeedbackEntry() {
    }

    public FeedbackEntry(int orderNo, int rating, String comment, String createdAt) {
        this.orderNo = orderNo;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
