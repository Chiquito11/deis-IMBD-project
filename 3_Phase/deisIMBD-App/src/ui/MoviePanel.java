package ui;

import db.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MoviePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color BG_COLOR = new Color(250, 250, 250);
    
    public MoviePanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        
        loadMovies();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Movie Catalog");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton btnRefresh = createStyledButton("Refresh", PRIMARY_COLOR);
        btnRefresh.addActionListener(e -> loadMovies());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnRefresh);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Duration", "Budget", "Release Date", "Register Date", "Age Rating", "Country"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(232, 234, 246));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Ajustar larguras das colunas
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(250); // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Duration
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Budget
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Release Date
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Register Date
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Age Rating
        table.getColumnModel().getColumn(7).setPreferredWidth(100); // Country
        
        // Header styling
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        
        // Centralizar células
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 20, 8, 20));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void loadMovies() {
        tableModel.setRowCount(0);
        String sql = "SELECT m.movieId, m.movieName, m.movieDuration, m.movieBudget, " +
                     "m.movieReleaseDate, m.movieRegisterDate, " +
                     "ar.ageRatingCode, c.countryName " +
                     "FROM Movies m " +
                     "LEFT JOIN AgeRating ar ON m.ageRatingId = ar.ageRatingId " +
                     "LEFT JOIN Country c ON m.countryId = c.countryId " +
                     "ORDER BY m.movieId";

        try (Connection conn = DbConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("movieId"),
                    rs.getString("movieName"),
                    rs.getObject("movieDuration"),
                    rs.getBigDecimal("movieBudget"),
                    rs.getDate("movieReleaseDate"),
                    rs.getDate("movieRegisterDate"),
                    rs.getString("ageRatingCode"),
                    rs.getString("countryName")
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    
    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
