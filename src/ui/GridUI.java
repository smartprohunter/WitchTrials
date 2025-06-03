package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class GridUI extends JFrame {
    private static final long serialVersionUID = 1L;
	private int gridSize;
    private int cellSize;
    private GridPanel gridPanel;
    private Map<String, Point> agentPositions;

    public GridUI(int gridSize, int cellSize) {
        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.agentPositions = new HashMap<>();

        // Set up the frame
        setTitle("Agent Grid");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Create and add the grid panel
        gridPanel = new GridPanel();
        add(gridPanel);

        // Set the size and make it visible
        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

   
    public void updateAgentPosition(String agentName, int x, int y) {
        agentPositions.put(agentName, new Point(x, y));
        gridPanel.repaint();
    }

    public void removeAgent(String agentName) {
        agentPositions.remove(agentName);
        gridPanel.repaint();
    }


    private class GridPanel extends JPanel {
        private static final long serialVersionUID = -5694250528574644023L;

		public GridPanel() {
            setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw the grid
            g.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i <= gridSize; i++) {
                // Draw horizontal lines
                g.drawLine(0, i * cellSize, gridSize * cellSize, i * cellSize);
                // Draw vertical lines
                g.drawLine(i * cellSize, 0, i * cellSize, gridSize * cellSize);
            }
            
            HashMap<String, Point> positionsCopy = new HashMap<>(agentPositions);
            for (Map.Entry<String, Point> entry : positionsCopy.entrySet()) {
                String agentName = entry.getKey();
                Point pos = entry.getValue();
                
                g.setColor(new Color(Math.abs(agentName.hashCode()) % 0xFFFFFF));
                
                g.fillOval(pos.x * cellSize + 2, pos.y * cellSize + 2, 
                           cellSize - 4, cellSize - 4);
                
                g.setColor(Color.BLACK);
                g.drawString(agentName, pos.x * cellSize + 5, pos.y * cellSize + cellSize/2);
            }
        }
    }
}