package com.peopleeats.web.feedback;

public class FeedbackResponse {
    private boolean success;
    private String message;

    public FeedbackResponse() {
    }

    public FeedbackResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
