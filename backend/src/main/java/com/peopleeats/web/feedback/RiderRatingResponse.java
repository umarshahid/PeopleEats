package com.peopleeats.web.feedback;

public class RiderRatingResponse {
    private String riderName;
    private double averageRating;
    private int totalRatings;

    public RiderRatingResponse() {
    }

    public RiderRatingResponse(String riderName, double averageRating, int totalRatings) {
        this.riderName = riderName;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }
}
