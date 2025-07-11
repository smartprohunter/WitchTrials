package main;

import jade.core.Runtime;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import helpers.Helpers;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import ui.GridManager;


public class MainClass {
	public static void main(String[] args) throws OWLOntologyCreationException {

	    
		Runtime runtime = Runtime.instance();
        GridManager.getInstance();
        // Create a container
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true");
        System.setProperty("jade.util.Logger.level", "INFO");
        AgentContainer container = runtime.createMainContainer(profile);
        
        try {
        		AgentController manager = container.createNewAgent(
                "SimulationManager", 
                "agents.TownieSimulationAgent", 
                new Object[] {}
            );
            manager.start();
            Helpers.simulationStartTime = Timestamp.valueOf(LocalDateTime.now());
            System.out.println("Witch Trial Simulation started successfully.");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error starting the simulation: " + e.getMessage());
        }
    }
}
