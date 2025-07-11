package ui;

import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import helpers.Helpers;

public class GridUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private int gridWidth;  
    private int gridHeight; 
    
    private int cellSize; 
    private GridPanel gridPanel;
    private GraveyardPanel graveyardPanel;
    private ConcurrentHashMap<String, Point> agentPositions;
    private ConcurrentHashMap<String, String> agentGenders; 
    private Map<String, GraveyardEntry> graveyardPositions;
    private JTextPane infoTextPane; 
    private JLabel statusLabel;
    private JLabel hysteriaLabel;
    private JLabel aliveCountLabel;
    private JLabel deadCountLabel;
    
    // Images for villagers
    private BufferedImage maleVillagerImage;
    private BufferedImage femaleVillagerImage;
    
    // Enhanced color scheme
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);       
    private static final Color GRID_COLOR = new Color(200, 200, 200);            
    private static final Color TEXT_COLOR = new Color(30, 30, 30);               
    private static final Color GRAVEYARD_COLOR = new Color(230, 230, 230);       
    private static final Color PANEL_BORDER_COLOR = new Color(180, 180, 180);    
    private static final Color BORDER_TEXT_COLOR = new Color(50, 50, 150);  
    
    // Graveyard configuration
    private static final int GRAVEYARD_WIDTH = 8;
    private static final int GRAVEYARD_HEIGHT = 3;
    private int graveyardNextX = 0;
    private int graveyardNextY = 0;
    
    public GridUI(int gridWidth, int gridHeight, int cellSize) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cellSize = cellSize;
        this.agentPositions = new ConcurrentHashMap<>();
        this.agentGenders = new ConcurrentHashMap<>(); 
        this.graveyardPositions = new HashMap<>();
        
        loadVillagerImages();
        setupUI();
    }
    
    private void loadVillagerImages() {
        try {
         
            maleVillagerImage = ImageIO.read(new File("images/male_villager.png"));
            femaleVillagerImage = ImageIO.read(new File("images/female_villager.png"));
        } catch (IOException e) {
            System.out.println("Could not load villager images, using fallback graphics");
        }
    }
    
    
    
    private void setupUI() {
        setTitle("🕯️ Salem Witch Trials - Year 1692 🕯️");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setBackground(BACKGROUND_COLOR);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 0));

        add(createStatusPanel(), BorderLayout.NORTH);
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(createLeftPanel(), BorderLayout.CENTER);
        mainPanel.add(createRightPanel(), BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        updateCounters();
    }
    

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(235, 235, 235));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PANEL_BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        hysteriaLabel = new JLabel("Hysteria: " + Helpers.hysteria, SwingConstants.LEFT);
        hysteriaLabel.setForeground(TEXT_COLOR);
        statusLabel = new JLabel(" Simulation Running", SwingConstants.CENTER);
        statusLabel.setForeground(TEXT_COLOR);
        JPanel countersPanel = new JPanel(new FlowLayout());
        countersPanel.setBackground(new Color(235, 235, 235));
        aliveCountLabel = new JLabel("Alive: 0");
        aliveCountLabel.setForeground(new Color(0, 128, 0));
        deadCountLabel = new JLabel("Dead: 0");
        deadCountLabel.setForeground(new Color(200, 0, 0));
        countersPanel.add(aliveCountLabel);
        countersPanel.add(Box.createHorizontalStrut(20));
        countersPanel.add(deadCountLabel);
        statusPanel.add(hysteriaLabel, BorderLayout.WEST);
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(countersPanel, BorderLayout.EAST);
        return statusPanel;
    }
    
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(0, 5)); // Remove horizontal gap
        leftPanel.setBackground(BACKGROUND_COLOR);
        
        // Create village grid
        gridPanel = new GridPanel();
        JPanel gridContainer = new JPanel(new BorderLayout());
        gridContainer.setBorder(createThemedBorder("Salem Village"));
        gridContainer.setBackground(BACKGROUND_COLOR);
        gridContainer.add(gridPanel, BorderLayout.CENTER);
        
        // Create graveyard
        graveyardPanel = new GraveyardPanel();
        JPanel graveyardContainer = new JPanel(new BorderLayout());
        graveyardContainer.setBorder(createThemedBorder("Graveyard"));
        graveyardContainer.setBackground(GRAVEYARD_COLOR);
        graveyardContainer.add(graveyardPanel, BorderLayout.CENTER);
        
        leftPanel.add(gridContainer, BorderLayout.CENTER);
        leftPanel.add(graveyardContainer, BorderLayout.SOUTH);
        
        return leftPanel;
    }
    
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(400, 0));
        rightPanel.setBackground(BACKGROUND_COLOR);
        infoTextPane = new JTextPane();
        infoTextPane.setEditable(false);
        infoTextPane.setBackground(new Color(250, 250, 250));
        infoTextPane.setForeground(TEXT_COLOR);
        JScrollPane scrollPane = new JScrollPane(infoTextPane);
        scrollPane.setBorder(createThemedBorder("Village Gossip & Accusations"));
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setBackground(BACKGROUND_COLOR);
        scrollPane.getHorizontalScrollBar().setBackground(BACKGROUND_COLOR);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        return rightPanel;
    }

    
    private Border createThemedBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PANEL_BORDER_COLOR, 2),
            title,
            0, 0,
            new Font("Serif", Font.BOLD, 12),
            BORDER_TEXT_COLOR 
        );
    }
    
   
    
    public void updateAgentPosition(String agentName, int x, int y, String gender) {
        agentPositions.put(agentName, new Point(x, y));
        agentGenders.put(agentName, gender);
        SwingUtilities.invokeLater(() -> {
            gridPanel.repaint();
            updateCounters();
        });
    }
    
  
    
    public void removeAgent(String agentName) {
        SwingUtilities.invokeLater(() -> {
            agentPositions.remove(agentName);
            agentGenders.remove(agentName); 
            addToGraveyard(agentName);
            
            gridPanel.repaint();
            graveyardPanel.repaint();
            updateCounters();
            
        });
    }
    
    private void addToGraveyard(String agentName) {
        Point graveyardPos = new Point(graveyardNextX, graveyardNextY);
        GraveyardEntry entry = new GraveyardEntry(
            graveyardPos 
            
        );
        
        graveyardPositions.put(agentName, entry);
        
        graveyardNextX++;
        if (graveyardNextX >= GRAVEYARD_WIDTH) {
            graveyardNextX = 0;
            graveyardNextY++;
            if (graveyardNextY >= GRAVEYARD_HEIGHT) {
                graveyardNextY = 0;
            }
        }
    }
    
    // Helper method to extract first name from agent name
    private String getFirstName(String fullName) {
        if (fullName == null) return "";
        int underscoreIndex = fullName.indexOf('_');
        return underscoreIndex > 0 ? fullName.substring(0, underscoreIndex) : fullName;
    }
    
    public void appendToInfo(String message) {
        appendToInfo(message, TEXT_COLOR);
    }
    
    public void appendToInfo(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = infoTextPane.getStyledDocument();
                SimpleAttributeSet attributes = new SimpleAttributeSet();
                StyleConstants.setForeground(attributes, color);
                StyleConstants.setFontFamily(attributes, "Serif");
                
                            
                doc.insertString(doc.getLength(),( message + "\n"), attributes);
                infoTextPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }
    
    private void updateCounters() {
        int aliveCount = agentPositions.size();
        int deadCount = graveyardPositions.size();
        
        aliveCountLabel.setText("Alive: " + aliveCount);
        deadCountLabel.setText("Dead: " + deadCount);
        hysteriaLabel.setText("Hysteria: " + Helpers.hysteria);
        // Fix status logic - only show warning when there are actually few survivors
        if (aliveCount <= 5) {
            statusLabel.setText("⚠️ Few Survivors Remain ⚠️");
            statusLabel.setForeground(Color.ORANGE);
        } else {
            statusLabel.setText("Simulation Running");
            statusLabel.setForeground(TEXT_COLOR);
        }
    }
    
    private static class GraveyardEntry {
        Point position;
     
        public GraveyardEntry(Point position) {
            this.position = position;
         
        }
    }
    
    private class GridPanel extends JPanel {
        private static final long serialVersionUID = -5694250528574644023L;
        
        public GridPanel() {
            setPreferredSize(new Dimension(gridWidth * cellSize, gridHeight * cellSize));
            setBackground(new Color(255, 255, 255));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw the grid with aged look
            g2d.setColor(GRID_COLOR);
            g2d.setStroke(new BasicStroke(1.0f));
            
            // Draw vertical lines (columns)
            for (int i = 0; i <= gridWidth; i++) {
                g2d.drawLine(i * cellSize, 0, i * cellSize, gridHeight * cellSize);
            }
            
            // Draw horizontal lines (rows)
            for (int i = 0; i <= gridHeight; i++) {
                g2d.drawLine(0, i * cellSize, gridWidth * cellSize, i * cellSize);
            }
            
            // Draw living agents with villager images
            HashMap<String, Point> positionsCopy = new HashMap<>(agentPositions);
            for (Map.Entry<String, Point> entry : positionsCopy.entrySet()) {
                String agentName = entry.getKey();
                Point pos = entry.getValue();
                
                int x = pos.x * cellSize;
                int y = pos.y * cellSize;
                
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 60));
                g2d.fillOval(x + 3, y + cellSize - 8, cellSize - 6, 6);
                
                // Get gender and draw appropriate villager image
                String gender = agentGenders.getOrDefault(agentName, "Female");
                BufferedImage villagerImage = "Male".equals(gender) ? maleVillagerImage : femaleVillagerImage;
                
                if (villagerImage != null) {
                    // Scale and draw the image
                    int imgSize = cellSize - 4;
                    g2d.drawImage(villagerImage, x + 2, y + 2, imgSize, imgSize, null);
                } else {
                    // Fallback if no image loaded
                    g2d.setColor(Color.GRAY);
                    g2d.fillOval(x + 2, y + 2, cellSize - 4, cellSize - 4);
                }
                
                // Draw name with better visibility
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Serif", Font.BOLD, 9));
                FontMetrics fm = g2d.getFontMetrics();
                String displayName = getFirstName(agentName);
                int textWidth = fm.stringWidth(displayName);
                
                // Draw text outline for better visibility
                g2d.setColor(Color.BLACK);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        g2d.drawString(displayName, x + (cellSize - textWidth) / 2 + dx, y + cellSize - 3 + dy);
                    }
                }
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.drawString(displayName, x + (cellSize - textWidth) / 2, y + cellSize - 3);
            }
        }
    }
    
    private class GraveyardPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        
        public GraveyardPanel() {
            setPreferredSize(new Dimension(GRAVEYARD_WIDTH * cellSize, GRAVEYARD_HEIGHT * cellSize));
            setBackground(GRAVEYARD_COLOR);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(new Color(60, 50, 40));
            g2d.setStroke(new BasicStroke(0.5f));
            for (int i = 0; i <= GRAVEYARD_WIDTH; i++) {
                g2d.drawLine(i * cellSize, 0, i * cellSize, GRAVEYARD_HEIGHT * cellSize);
            }
            for (int i = 0; i <= GRAVEYARD_HEIGHT; i++) {
                g2d.drawLine(0, i * cellSize, GRAVEYARD_WIDTH * cellSize, i * cellSize);
            }
            
            HashMap<String, GraveyardEntry> graveyardCopy = new HashMap<>(graveyardPositions);
            for (Map.Entry<String, GraveyardEntry> entry : graveyardCopy.entrySet()) {
                String agentName = entry.getKey();
                GraveyardEntry graveyardEntry = entry.getValue();
                Point pos = graveyardEntry.position;
                
                int x = pos.x * cellSize;
                int y = pos.y * cellSize;
                

                
                // Draw tombstone
                g2d.setColor(new Color(120, 120, 120));
                g2d.fillRoundRect(x + 8, y + 8, cellSize - 16, cellSize - 16, 8, 8);
                
                // Draw tombstone border
                g2d.setColor(new Color(90, 90, 90));
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawRoundRect(x + 8, y + 8, cellSize - 16, cellSize - 16, 8, 8);
                
                // Draw cross (scaled for larger cells)
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(3.0f));
                int centerX = x + cellSize / 2;
                int centerY = y + cellSize / 2;
                g2d.drawLine(centerX, centerY - 15, centerX, centerY + 12);
                g2d.drawLine(centerX - 10, centerY - 6, centerX + 10, centerY - 6);
                
                // Draw name (larger font for bigger cells)
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setFont(new Font("Serif", Font.BOLD, 11));
                FontMetrics fm = g2d.getFontMetrics();
                String displayName = getFirstName(agentName); // Use first name only
                int textWidth = fm.stringWidth(displayName);
                g2d.drawString(displayName, x + (cellSize - textWidth) / 2, y + cellSize - 5);
            }
        }
    }
}