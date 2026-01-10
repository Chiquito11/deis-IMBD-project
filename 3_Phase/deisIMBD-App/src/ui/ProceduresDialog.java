package ui;

import db.DbConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ProceduresDialog extends JDialog {
    
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Color INFO_COLOR = new Color(0, 188, 212);
    private static final Color BG_COLOR = new Color(250, 250, 250);
    
    public ProceduresDialog(Frame parent) {
        super(parent, "Stored Procedures", true);
        setLayout(new BorderLayout());
        setSize(1100, 650);
        setLocationRelativeTo(parent);
        
        JTabbedPane procedureTabs = new JTabbedPane();
        procedureTabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        procedureTabs.addTab("1. Movies by Month/Year", createMoviesMonthYearPanel());
        procedureTabs.addTab("2. Movies by Director", createMoviesDirectorPanel());
        procedureTabs.addTab("3. Get Actors by Director", createActorsByDirectorPanel());

        
        add(procedureTabs, BorderLayout.CENTER);
    }
    
    // 1. COUNT_MOVIES_MONTH_YEAR
    private JPanel createMoviesMonthYearPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Count Movies by Month and Year");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        
        JTextField txtMonth = createStyledTextField(5);
        JTextField txtYear = createStyledTextField(8);
        JTextField txtResult = createStyledTextField(10);
        txtResult.setEditable(false);
        txtResult.setBackground(new Color(245, 245, 245));
        
        inputPanel.add(new JLabel("Month (1-12):"));
        inputPanel.add(txtMonth);
        inputPanel.add(new JLabel("Year:"));
        inputPanel.add(txtYear);
        inputPanel.add(Box.createHorizontalStrut(20));
        inputPanel.add(new JLabel("Result:"));
        inputPanel.add(txtResult);
        
        JButton btnExecute = createStyledButton("Execute", SUCCESS_COLOR);
        inputPanel.add(btnExecute);
        
        JPanel formContent = new JPanel(new BorderLayout(10, 10));
        formContent.setBackground(Color.WHITE);
        formContent.add(titleLabel, BorderLayout.NORTH);
        formContent.add(inputPanel, BorderLayout.CENTER);
        
        formPanel.add(formContent);
        
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(new Color(245, 248, 250));
        descPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel descLabel = new JLabel("<html><b>Description:</b> Counts movies released in a specific month and year.</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descPanel.add(descLabel);
        
        btnExecute.addActionListener(e -> {
            try {
                int month = Integer.parseInt(txtMonth.getText().trim());
                int year = Integer.parseInt(txtYear.getText().trim());
                
                try (Connection conn = DbConnection.getConnection();
                     CallableStatement stmt = conn.prepareCall("{call COUNT_MOVIES_MONTH_YEAR(?, ?)}")) {
                    
                    stmt.setInt(1, month);
                    stmt.setInt(2, year);
                    
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        int count = rs.getInt("MovieCount");
                        txtResult.setText(String.valueOf(count));
                        JOptionPane.showMessageDialog(panel, 
                            "Found " + count + " movies", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                } catch (SQLException ex) {
                    txtResult.setText("Error");
                    showError(panel, ex);
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, 
                    "Please enter valid numbers", 
                    "Invalid Input", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(descPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // 2. COUNT_MOVIES_DIRECTOR
    private JPanel createMoviesDirectorPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Count Movies by Director");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        
        JTextField txtFullName = createStyledTextField(30);
        
        inputPanel.add(new JLabel("Director Full Name:"));
        inputPanel.add(txtFullName);
        
        JButton btnExecute = createStyledButton("Execute", SUCCESS_COLOR);
        inputPanel.add(btnExecute);
        
        JPanel formContent = new JPanel(new BorderLayout(10, 10));
        formContent.setBackground(Color.WHITE);
        formContent.add(titleLabel, BorderLayout.NORTH);
        formContent.add(inputPanel, BorderLayout.CENTER);
        
        formPanel.add(formContent);
        
        // Results Table
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Director ID", "Director Name", "Movie Count"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(232, 234, 246));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(false);
        
        table.getTableHeader().setBackground(INFO_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(new Color(245, 248, 250));
        descPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel descLabel = new JLabel("<html><b>Description:</b> Returns the director info and count of movies directed by the specified director.</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descPanel.add(descLabel);
        
        btnExecute.addActionListener(e -> {
            String fullName = txtFullName.getText().trim();
            
            if (fullName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, 
                    "Please enter director name", 
                    "Invalid Input", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            tableModel.setRowCount(0);
            
            try (Connection conn = DbConnection.getConnection();
                 CallableStatement stmt = conn.prepareCall("{call COUNT_MOVIES_DIRECTOR(?)}")) {
                
                stmt.setString(1, fullName);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("directorId"),
                        rs.getString("directorName"),
                        rs.getInt("MovieCount")
                    });
                    
                    JOptionPane.showMessageDialog(panel, 
                        "Director found with " + rs.getInt("MovieCount") + " movies", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panel, 
                        "No director found with that name", 
                        "No Results", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (SQLException ex) {
                showError(panel, ex);
            }
        });
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.add(descPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // 3. GET_ACTORS_BY_DIRECTOR
    private JPanel createActorsByDirectorPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Get Actors by Director Name");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(Color.WHITE);

        JTextField txtNum = createStyledTextField(8);
        JTextField txtDirName = createStyledTextField(30);

        inputPanel.add(new JLabel("Number of Actors:"));
        inputPanel.add(txtNum);
        inputPanel.add(new JLabel("Director Name:"));
        inputPanel.add(txtDirName);

        JButton btnExecute = createStyledButton("Execute", SUCCESS_COLOR);
        inputPanel.add(btnExecute);

        JPanel formContent = new JPanel(new BorderLayout(10, 10));
        formContent.setBackground(Color.WHITE);
        formContent.add(titleLabel, BorderLayout.NORTH);
        formContent.add(inputPanel, BorderLayout.CENTER);

        formPanel.add(formContent);

        // Results Table
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Actor ID", "Actor Name"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(232, 234, 246));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(false);

        table.getTableHeader().setBackground(INFO_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(new Color(245, 248, 250));
        descPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel descLabel = new JLabel("<html><b>Description:</b> Returns top N actors who worked with the specified director.</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descPanel.add(descLabel);

        btnExecute.addActionListener(e -> {
            try {
                int num = Integer.parseInt(txtNum.getText().trim());
                String dirName = txtDirName.getText().trim();

                if (dirName.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, 
                        "Please enter director name", 
                        "Invalid Input", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                tableModel.setRowCount(0);

                try (Connection conn = DbConnection.getConnection();
                     CallableStatement stmt = conn.prepareCall("{call GET_ACTORS_BY_DIRECTOR(?, ?)}")) {
                    
                    stmt.setInt(1, num);
                    stmt.setString(2, dirName);
                    
                    ResultSet rs = stmt.executeQuery();
                    int count = 0;
                    while (rs.next()) {
                        tableModel.addRow(new Object[]{
                            rs.getInt("actorId"),
                            rs.getString("actorName")
                        });
                        count++;
                    }

                    JOptionPane.showMessageDialog(panel, 
                        "Found " + count + " actors", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (SQLException ex) {
                    showError(panel, ex);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, 
                    "Please enter a valid number", 
                    "Invalid Input", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.add(descPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }
    
    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return field;
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
    
    private void showError(JPanel panel, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
