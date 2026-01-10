package ui;

import db.DbConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ListingsDialog extends JDialog {
    
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Color BG_COLOR = new Color(250, 250, 250);
    
    public ListingsDialog(Frame parent) {
        super(parent, "Data Listings", true);
        setLayout(new BorderLayout());
        setSize(1200, 700);
        setLocationRelativeTo(parent);
        
        JTabbedPane listingTabs = new JTabbedPane();
        listingTabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        listingTabs.addTab("1. Movies by Genre", createListingPanel(
            "Movies by Genre (Action)",
            "SELECT * FROM Movies m " +
            "LEFT JOIN MovieGenre mg ON m.movieId = mg.movieId " +
            "LEFT JOIN Genre g ON mg.genreId = g.genreId " +
            "WHERE LOWER(TRIM(g.genreName)) = 'action'"
        ));
        
        listingTabs.addTab("2. Directors by Country", createListingPanel(
            "Directors from USA",
            "SELECT DISTINCT d.directorId, d.directorName, d.hidden " +
            "FROM Director d " +
            "INNER JOIN MovieDirector md ON d.directorId = md.directorId " +
            "INNER JOIN Movies m ON md.movieId = m.movieId " +
            "INNER JOIN Country c ON m.countryId = c.countryId " +
            "WHERE c.countryName = 'Estados Unidos'"
        ));
        
        listingTabs.addTab("3. Male Actors (Asia)", createListingPanel(
            "Male Actors in Asian Movies",
            "SELECT DISTINCT a.actorId, a.actorName, a.actorGender " +
            "FROM Actor a " +
            "INNER JOIN MovieActor ma ON a.actorId = ma.actorId " +
            "INNER JOIN Movies m ON ma.movieId = m.movieId " +
            "INNER JOIN Country c ON m.countryId = c.countryId " +
            "INNER JOIN Continent cn ON c.continentId = cn.continentId " +
            "WHERE a.actorGender = 'M' AND cn.continentName = 'Ásia'"
        ));
        
        listingTabs.addTab("4. Movies (May-Jul)", createListingPanel(
            "Movies Released in May, June, July",
            "SELECT m.movieId, m.movieName, m.movieDuration, m.movieReleaseDate, " +
            "MONTH(m.movieReleaseDate) AS ReleaseMonth " +
            "FROM Movies m " +
            "WHERE MONTH(m.movieReleaseDate) IN (5, 6, 7) " +
            "ORDER BY m.movieReleaseDate"
        ));
        
        listingTabs.addTab("5. Action EU (Dec)", createListingPanel(
            "Action Movies from Europe Released in December",
            "SELECT DISTINCT m.movieId, m.movieName, m.movieReleaseDate, c.countryName " +
            "FROM Movies m " +
            "INNER JOIN MovieGenre mg ON m.movieId = mg.movieId " +
            "INNER JOIN Genre g ON mg.genreId = g.genreId " +
            "INNER JOIN Country c ON m.countryId = c.countryId " +
            "INNER JOIN Continent cn ON c.continentId = cn.continentId " +
            "WHERE LOWER(TRIM(g.genreName)) = 'action' " +
            "AND cn.continentName = 'Europa' " +
            "AND MONTH(m.movieReleaseDate) = 12 " +
            "ORDER BY m.movieName"
        ));
        
        listingTabs.addTab("6. Movies 18+", createListingPanel(
            "Movies for Ages 18+",
            "SELECT m.movieId, m.movieName, m.movieReleaseDate, ar.ageRatingCode " +
            "FROM Movies m " +
            "LEFT JOIN AgeRating ar ON m.ageRatingId = ar.ageRatingId " +
            "WHERE ar.ageRatingCode = '+18' " +
            "ORDER BY m.movieName"
        ));
        
        listingTabs.addTab("7. Kids Movies by Continent", createListingPanel(
            "Movies for Kids (-10) by Continent",
            "SELECT cn.continentId, cn.continentName, " +
            "COUNT(DISTINCT m.movieId) AS TotalMovies " +
            "FROM Movies m " +
            "LEFT JOIN AgeRating ar ON m.ageRatingId = ar.ageRatingId " +
            "LEFT JOIN Country c ON m.countryId = c.countryId " +
            "LEFT JOIN Continent cn ON c.continentId = cn.continentId " +
            "WHERE ar.ageRatingId = 1 " +
            "GROUP BY cn.continentId, cn.continentName " +
            "ORDER BY TotalMovies DESC"
        ));
        
        listingTabs.addTab("8. Adult Movies EU by Country", createListingPanel(
            "Movies 18+ by European Country",
            "SELECT c.countryId, c.countryName, " +
            "COUNT(DISTINCT m.movieId) AS TotalMovies " +
            "FROM Movies m " +
            "LEFT JOIN AgeRating ar ON m.ageRatingId = ar.ageRatingId " +
            "LEFT JOIN Country c ON m.countryId = c.countryId " +
            "LEFT JOIN Continent cn ON c.continentId = cn.continentId " +
            "WHERE ar.ageRatingId = 5 AND cn.continentName = 'Europa' " +
            "GROUP BY c.countryId, c.countryName " +
            "ORDER BY TotalMovies DESC"
        ));
        
        listingTabs.addTab("9. Top 10 Directors", createListingPanel(
            "Top 10 Directors by Average Rating",
            "SELECT TOP 10 d.directorId, d.directorName, " +
            "COUNT(DISTINCT m.movieId) AS TotalFilmes, " +
            "AVG(mv.movieRating) AS AverageRating " +
            "FROM Director d " +
            "LEFT JOIN MovieDirector md ON d.directorId = md.directorId " +
            "LEFT JOIN Movies m ON md.movieId = m.movieId " +
            "LEFT JOIN MovieVotes mv ON m.movieId = mv.movieId " +
            "GROUP BY d.directorId, d.directorName " +
            "HAVING COUNT(DISTINCT m.movieId) > 0 " +
            "ORDER BY AverageRating DESC"
        ));
        
        add(listingTabs, BorderLayout.CENTER);
    }
    
    private JPanel createListingPanel(String title, String sqlQuery) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header with title and buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(SUCCESS_COLOR);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setBorder(new EmptyBorder(5, 15, 5, 15));
        
        JButton exportButton = new JButton("Export CSV");
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exportButton.setForeground(Color.WHITE);
        exportButton.setBackground(PRIMARY_COLOR);
        exportButton.setFocusPainted(false);
        exportButton.setBorderPainted(false);
        exportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportButton.setBorder(new EmptyBorder(5, 15, 5, 15));
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Table
        DefaultTableModel tableModel = new DefaultTableModel() {
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
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        table.getTableHeader().setBackground(SUCCESS_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        // Status label
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(120, 120, 120));
        statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Load data function
        Runnable loadData = () -> {
            try {
                statusLabel.setText("Loading...");
                statusLabel.setForeground(PRIMARY_COLOR);
                
                try (Connection conn = DbConnection.getConnection();
                     Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery(sqlQuery)) {
                    
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    String[] columnNames = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames[i - 1] = metaData.getColumnName(i);
                    }
                    
                    tableModel.setRowCount(0);
                    tableModel.setColumnIdentifiers(columnNames);
                    
                    int rowCount = 0;
                    while (rs.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            row[i - 1] = rs.getObject(i);
                        }
                        tableModel.addRow(row);
                        rowCount++;
                    }
                    
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                        table.getColumnModel().getColumn(i).setPreferredWidth(150);
                    }
                    
                    statusLabel.setText("Loaded " + rowCount + " rows");
                    statusLabel.setForeground(SUCCESS_COLOR);
                    
                } catch (SQLException ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    statusLabel.setForeground(DANGER_COLOR);
                    throw ex;
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        
        // Export to CSV function
        exportButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save as CSV");
            fileChooser.setSelectedFile(new java.io.File(title.replaceAll("[^a-zA-Z0-9]", "_") + ".csv"));
            
            int userSelection = fileChooser.showSaveDialog(panel);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                    for (int i = 0; i < tableModel.getColumnCount(); i++) {
                        writer.print(tableModel.getColumnName(i));
                        if (i < tableModel.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                    
                    for (int row = 0; row < tableModel.getRowCount(); row++) {
                        for (int col = 0; col < tableModel.getColumnCount(); col++) {
                            Object value = tableModel.getValueAt(row, col);
                            writer.print(value != null ? value.toString() : "");
                            if (col < tableModel.getColumnCount() - 1) writer.print(",");
                        }
                        writer.println();
                    }
                    
                    JOptionPane.showMessageDialog(panel, "Data exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        loadData.run();
        refreshButton.addActionListener(e -> loadData.run());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        return panel;
    }
}
