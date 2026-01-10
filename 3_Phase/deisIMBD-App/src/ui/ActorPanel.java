package ui;

import db.DbConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ActorPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtGender;
    
    // Cores modernas
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Color BG_COLOR = new Color(250, 250, 250);
    
    public ActorPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Painel do formulário
        add(createFormPanel(), BorderLayout.NORTH);
        
        // Tabela
        add(createTablePanel(), BorderLayout.CENTER);
        
        loadActors();
    }
    
    private JPanel createFormPanel() {
        JPanel mainFormPanel = new JPanel(new BorderLayout());
        mainFormPanel.setBackground(Color.WHITE);
        mainFormPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Título
        JLabel titleLabel = new JLabel("Actor Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));
        mainFormPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Campos de entrada
        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        fieldsPanel.setBackground(Color.WHITE);
        
        txtId = createStyledTextField(5);
        txtName = createStyledTextField(20);
        txtGender = createStyledTextField(3);
        
        fieldsPanel.add(createFieldGroup("ID:", txtId));
        fieldsPanel.add(createFieldGroup("Name:", txtName));
        fieldsPanel.add(createFieldGroup("Gender (M/F):", txtGender));
        
        mainFormPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnAdd = createStyledButton("Add Actor", SUCCESS_COLOR);
        JButton btnDelete = createStyledButton("Delete Actor", DANGER_COLOR);
        JButton btnRefresh = createStyledButton("Refresh", PRIMARY_COLOR);
        
        btnAdd.addActionListener(e -> insertActor());
        btnDelete.addActionListener(e -> deleteSelectedActor());
        btnRefresh.addActionListener(e -> loadActors());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        
        mainFormPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainFormPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Gender"}, 0) {
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
    
    private JPanel createFieldGroup(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(70, 70, 70));
        
        panel.add(label);
        panel.add(textField);
        
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
        
        // Hover effect
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
    
    private void loadActors() {
        tableModel.setRowCount(0);
        try (Connection conn = DbConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT actorId, actorName, actorGender FROM Actor")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("actorId"),
                    rs.getString("actorName"),
                    rs.getString("actorGender")
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }
    
    private void insertActor() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String name = txtName.getText().trim();
            String gender = txtGender.getText().trim();
            
            String sql = "INSERT INTO Actor (actorId, actorName, actorGender) VALUES (?, ?, ?)";
            try (Connection conn = DbConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, gender);
                ps.executeUpdate();
            }
            
            loadActors();
            clearForm();
            JOptionPane.showMessageDialog(this, "Actor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    private void deleteSelectedActor() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an actor from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this actor?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Actor WHERE actorId = ?";
            try (Connection conn = DbConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
                loadActors();
                JOptionPane.showMessageDialog(this, "Actor deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }
    
    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtGender.setText("");
    }
    
    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
