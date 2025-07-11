package ui;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.SwingUtilities;


public class GridManager {
    private static GridManager instance;
    private GridUI gridUI;
    private Map<String, Point> agentPositions;
    private int gridSizeX;
    private int gridSizeY;

    private HashSet<Point> occupiedPositions = new HashSet<Point>();
    
    private GridManager() {
        this.gridSizeX = 8;
        this.gridSizeY = 6;

        this.agentPositions = new HashMap<>();
        this.gridUI = new GridUI(gridSizeX,gridSizeY, 75);
    }
    
    
    public static synchronized GridManager getInstance() {
        if (instance == null) {
            instance = new GridManager();
        }
        return instance;
    }
    public void logToUI(String message) {
    	 SwingUtilities.invokeLater(() -> {
    	        if (gridUI != null) {
    	            gridUI.appendToInfo(message);
    	        }
    	    });
    }
    
    public Point addAgent(String agentName, String gender) {
    	
    	int x;
		int y;
		do {
    		 x = (int) (Math.random() * gridSizeX);
	         y = (int) (Math.random() * gridSizeY);
    	        
		} while ( isPositionOccupied(x, y));
                      
        Point position = new Point(x, y);
        agentPositions.put(agentName, position);
        
        gridUI.updateAgentPosition(agentName, x, y, gender);
        occupiedPositions.add(new Point(position));
        return position;
    }
    
    private boolean isPositionOccupied(int desiredX, int desiredY) {
        for (Point p : occupiedPositions) {
            if (p.x == desiredX && p.y == desiredY) {
                return true;
            }
        }
        return false;
        
    }
    public Point moveAgentRandomly(String agentName, String gender) {
        Point currentPos = agentPositions.get(agentName);
        if (currentPos == null) {
            return null;
        }
        int newX = currentPos.x;
        int newY = currentPos.y;
        boolean isValidPosition = false;
        
        while (!isValidPosition) {
        	
    	newX = currentPos.x;
        newY = currentPos.y;
        int direction = (int) (Math.random() * 4);
      
        switch (direction) {
        case 0: // gore 
            newY = Math.max(0, currentPos.y - 1);
            break;
        case 1: // dqsno 
            newX = Math.min(gridSizeX - 1, currentPos.x + 1);
            break;
        case 2: // dolu 
            newY = Math.min(gridSizeY - 1, currentPos.y + 1); 
            break;
        case 3: // lqvo 
            newX = Math.max(0, currentPos.x - 1);
            break;
    }
    
    if (newX >= 0 && newX < gridSizeX && newY >= 0 && newY < gridSizeY && 
        !isPositionOccupied(newX, newY)) {
        isValidPosition = true;
    }
        }
        Point newPosition = new Point(newX, newY);
        // Update 
        agentPositions.put(agentName, newPosition);
        gridUI.updateAgentPosition(agentName, newX, newY, gender);
        
        return newPosition;
    }
    
   
    public void removeAgent(String agentName) {
        agentPositions.remove(agentName);
        gridUI.removeAgent(agentName);
    }
    
    
    public Point getAgentPosition(String agentName) {
        return agentPositions.get(agentName);
    }
    public  void closeMainWindow() {
        SwingUtilities.invokeLater(() -> {
        	gridUI.setVisible(false);
        	gridUI.dispose();
        });
    }
}
