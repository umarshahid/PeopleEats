package feedback;

import user.User;

import java.util.ArrayList;
import java.util.List;

class Feedback {
    private List<String> feedbacks;

    public Feedback() {
        this.feedbacks = new ArrayList<>();
    }

    public void submitFeedback(User user, String content, int rating) {
        String feedback = "User.User: " + user.getUsername() + ", Rating: " + rating + ", Content: " + content;
        feedbacks.add(feedback);
        System.out.println("feedback.Feedback submitted successfully.");
    }

    public void filterFeedback() {
        // Filtering logic can be implemented here based on specific criteria
        // For example, filtering based on rating, user, or content
        // Here, we will print all feedbacks as an example
        System.out.println("Filtered Feedbacks:");
        for (String feedback : feedbacks) {
            System.out.println(feedback);
        }
    }
}
