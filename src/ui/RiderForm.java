package ui;

import user.Driver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Rider UI form
public class RiderForm {
    private JFrame frame;
    private JButton acceptTaskButton;

    public RiderForm(Driver rider) {
        frame = new JFrame("Rider UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 1));

        acceptTaskButton = new JButton("Accept Task");
        acceptTaskButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Example: Accept task for rider
                // Implement task acceptance logic here
                JOptionPane.showMessageDialog(frame, "Task accepted successfully!");
            }
        });
        panel.add(acceptTaskButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}


