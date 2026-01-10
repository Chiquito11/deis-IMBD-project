

import java.awt.*;
import javax.swing.*;
import ui.*;

public class MainWindow extends JFrame {
    
    public MainWindow() {
        setTitle("deisIMDB");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Look and Feel moderno (FlatLaf alternativo sem Maven)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Configurações globais de UI
        customizeUIDefaults();
        
        // Criar painel principal com padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.setBackground(Color.WHITE);
        
        tabs.addTab("Home", new HomePanel());
        tabs.addTab("Actors", new ActorPanel());
        tabs.addTab("Directors", new DirectorPanel());
        tabs.addTab("Movies", new MoviePanel());
        
        mainPanel.add(tabs, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void customizeUIDefaults() {
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
