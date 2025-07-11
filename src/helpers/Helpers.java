package helpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

import connectors.PostgreSQLConnection;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import ui.GridManager;

public class Helpers {
	public static int agentsWhoDeletedExecutedCounter = 0;
	public static int hysteria = 0;

	public static boolean restartTriggered = false;
	public static Timestamp simulationStartTime;
	public static Timestamp simulationEndTime;
	
	 public static final ReentrantLock lock = new ReentrantLock();
	    public static final AtomicReference<String> lockOwner = new AtomicReference<>();
	    
	    public static boolean tryAcquireLock(String agentName) {
	        try {
	            if (lock.tryLock(1, TimeUnit.SECONDS)) {
	                lockOwner.set(agentName);
	                System.out.println(agentName + " is trying to acquire the lock...");

	                return true;
	            }
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	        return false;
	    }
	    
	    public static void releaseLock(String agentName) {
	        if (agentName.equals(lockOwner.get())) {
	            lockOwner.set(null);
	            lock.unlock();
	        } else {
	            System.err.println("Agent " + agentName + " trying to release lock owned by " + lockOwner.get());
	        }
	    }
	public static void shutDownSimulation( )  {
        
        simulationEndTime = Timestamp.valueOf(LocalDateTime.now());
        try {
			storeSimulationResults();
		} catch (SQLException e) {
			e.printStackTrace();
		}
     
        GridManager instance = GridManager.getInstance();
        instance.closeMainWindow();
        System.exit(0);
                
               
        
    }
	 
	public static void storeSimulationResults() throws SQLException {
	
		 Connection conx = PostgreSQLConnection.getConnection();
	     PreparedStatement s = conx.prepareStatement(
	     	    "INSERT INTO SIMULATIONRESULTS.simulation_results (simulation_start, simulation_end, simulation_runtime) " +
	     	    "VALUES (?, ?, ?)"
	     	);
	
	     	s.setTimestamp(1, simulationStartTime);  
	     	s.setTimestamp(2, simulationEndTime);   
	
	     	long runtimeMillis = simulationEndTime.getTime() - simulationStartTime.getTime();
	     	org.postgresql.util.PGInterval interval = new org.postgresql.util.PGInterval();
	     	interval.setSeconds(runtimeMillis / 1000.0);
	     	s.setObject(3, interval);  
	
	     	s.executeUpdate();
	     
     



	}
	 

}
