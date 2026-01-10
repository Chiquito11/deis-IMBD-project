package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HomePanel extends JPanel {
    
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color INFO_COLOR = new Color(0, 188, 212);
    private static final Color BG_COLOR = new Color(250, 250, 250);
    
    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BG_COLOR);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        
        JLabel subtitleLabel = new JLabel("Explore your database content");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        
        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setBackground(Color.WHITE);
        titleContainer.add(titleLabel);
        titleContainer.add(Box.createVerticalStrut(5));
        titleContainer.add(subtitleLabel);
        
        headerPanel.add(titleContainer, BorderLayout.WEST);
        
        // Cards Panel
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        cardsPanel.setBackground(BG_COLOR);
        
        cardsPanel.add(createCard("Views", "Browse database views", PRIMARY_COLOR, () -> openViewsDialog()));
        cardsPanel.add(createCard("Listings", "View data listings", SUCCESS_COLOR, () -> openListingsDialog()));
        cardsPanel.add(createCard("Procedures", "Execute stored procedures", INFO_COLOR, () -> openProceduresDialog()));
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createCard(String title, String description, Color accentColor, Runnable action) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(30, 25, 30, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Accent bar on top
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(0, 4));
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(new Color(120, 120, 120));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        JButton actionButton = new JButton("Open");
        actionButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        actionButton.setForeground(Color.WHITE);
        actionButton.setBackground(accentColor);
        actionButton.setFocusPainted(false);
        actionButton.setBorderPainted(false);
        actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.setMaximumSize(new Dimension(120, 35));
        
        actionButton.addActionListener(e -> action.run());
        
        // Hover effect
        actionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                actionButton.setBackground(accentColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                actionButton.setBackground(accentColor);
            }
        });
        
        contentPanel.add(actionButton);
        
        card.add(accentBar, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        // Card hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2),
                    new EmptyBorder(30, 25, 30, 25)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(30, 25, 30, 25)
                ));
            }
        });
        
        return card;
    }
    
    private void openViewsDialog() {
        ViewsDialog dialog = new ViewsDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }
    
    private void openListingsDialog() {
        ListingsDialog dialog = new ListingsDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }
    
    private void openProceduresDialog() {
        ProceduresDialog dialog = new ProceduresDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }
}
