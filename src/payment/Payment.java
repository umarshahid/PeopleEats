package payment;

import user.User;

class Payment {
    public void processCreditCardPayment(User user, double amount, String cardNumber) {
        // Simulate credit card payment processing
        System.out.println("Processing credit card payment for user: " + user.getUsername());
        System.out.println("Amount: $" + amount);
        System.out.println("Card Number: " + cardNumber);
        System.out.println("payment.Payment successful!");
    }

    // Other payment methods implementations...
}