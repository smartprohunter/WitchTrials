package ui;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class GridManager {
    private static GridManager instance;
    private GridUI gridUI;
    private Map<String, Point> agentPositions;
    private int gridSize;
    private HashSet<Point> occupiedPositions = new HashSet<Point>();
    
    private GridManager() {
        this.gridSize = 6;
        this.agentPositions = new HashMap<>();
        this.gridUI = new GridUI(gridSize, 50);
    }
    
    
    public static synchronized GridManager getInstance() {
        if (instance == null) {
            instance = new GridManager();
        }
        return instance;
    }
    
    
    public Point addAgent(String agentName) {
    	
    	int x;
		int y;
		do {
    		 x = (int) (Math.random() * gridSize);
	         y = (int) (Math.random() * gridSize);
    	        
		} while ( isPositionOccupied(x, y));
                      
        Point position = new Point(x, y);
        agentPositions.put(agentName, position);
        
        gridUI.updateAgentPosition(agentName, x, y);
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
    public Point moveAgentRandomly(String agentName) {
        Point currentPos = agentPositions.get(agentName);
        if (currentPos == null) {
            return null;
        }
        int newX = currentPos.x;
        int newY = currentPos.y;
        
        boolean isValidPosition = false;
        
        while (!isValidPosition) {
        int direction = (int) (Math.random() * 4);
        
      
        switch (direction) {
            case 0: // gore
                newY = Math.max(0, currentPos.y - 1);
                break;
            case 1: // dqsno
                newX = Math.min(gridSize - 1, currentPos.x + 1);
                break;
            case 2: // dolu
                newY = Math.min(gridSize - 1, currentPos.y + 1);
                break;
            case 3: // lqvo
                newX = Math.max(0, currentPos.x - 1);
                break;
        }
        
        if (newX < gridSize && newY < gridSize) {
        	isValidPosition = true;
        }
        }
        // Update 
        agentPositions.put(agentName, new Point(newX, newY));
        gridUI.updateAgentPosition(agentName, newX, newY);
        
        return new Point(newX, newY);
    }
    
   
    public void removeAgent(String agentName) {
        agentPositions.remove(agentName);
        gridUI.removeAgent(agentName);
    }
    
    
    public Point getAgentPosition(String agentName) {
        return agentPositions.get(agentName);
    }
}
