package ui;

import db.DbConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewsDialog extends JDialog {
    
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color BG_COLOR = new Color(250, 250, 250);
    
    public ViewsDialog(Frame parent) {
        super(parent, "Database Views", true);
        setLayout(new BorderLayout());
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        
        JTabbedPane viewTabs = new JTabbedPane();
        viewTabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        viewTabs.addTab("Continents (>10 Movies)", createViewPanel("vw_ContinentsWithMoreThan10Movies"));
        viewTabs.addTab("Countries (<5 Movies)", createViewPanel("vw_CountriesWithLessThan5Movies"));
        viewTabs.addTab("Top 10 Actors", createViewPanel("vw_Top10ActorsWithMostMovies"));
        viewTabs.addTab("Top 5 Directors", createViewPanel("vw_Top5DirectorsWithMostMovies"));
        
        add(viewTabs, BorderLayout.CENTER);
    }
    
    private JPanel createViewPanel(String viewName) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header with refresh button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel("View: " + viewName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setBorder(new EmptyBorder(5, 15, 5, 15));
        
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        // Table
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(232, 234, 246));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        // Load data
        Runnable loadData = () -> {
            try (Connection conn = DbConnection.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM " + viewName)) {
                
                // Get column names
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                String[] columnNames = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    columnNames[i - 1] = metaData.getColumnName(i);
                }
                
                // Clear and set columns
                tableModel.setRowCount(0);
                tableModel.setColumnIdentifiers(columnNames);
                
                // Load rows
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = rs.getObject(i);
                    }
                    tableModel.addRow(row);
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        
        loadData.run();
        refreshButton.addActionListener(e -> loadData.run());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
}
