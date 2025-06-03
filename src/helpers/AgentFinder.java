package helpers;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import ui.GridManager;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AgentFinder {
    
    private static ConcurrentHashMap<AID, String> agentRegistry = new ConcurrentHashMap<AID, String>();
    private static boolean isInitialized = false;
    
    private static synchronized void initializeRegistry(Agent agent) {
        if (!isInitialized) {
            // Query all service types you're interested in
            String[] serviceTypes = {"townie", "judge", "simulationAgent"}; // Add more as needed
            
            for (String serviceType : serviceTypes) {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType(serviceType);
                template.addServices(sd);
                
                try {
                    DFAgentDescription[] dfds = DFService.search(agent, template);
                    for (DFAgentDescription dfd : dfds) {
                        agentRegistry.put(dfd.getName(), serviceType);
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
            isInitialized = true;
        }
    }
    
    public static void remakeRegistry(Agent agent, boolean forceInitialization) {
        if (forceInitialization) {
            isInitialized = false;
            agentRegistry.clear();
            initializeRegistry(agent);
        }
    }
    
    private static void ensureInitialized(Agent agent) {
        if (!isInitialized) {
            initializeRegistry(agent);
        }
    }
    
    private static ArrayList<String> findAgentNamesByFilter(Agent agent, String serviceType, 
                                                          Predicate<AID> filter) {
        ensureInitialized(agent);
        
        return agentRegistry.entrySet().stream()
            .filter(entry -> entry.getValue().equals(serviceType))
            .map(entry -> entry.getKey())
            .filter(filter == null ? aid -> true : filter)
            .map(AID::getLocalName)
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    private static Set<AID> findAgentAIDsByFilter(Agent agent, String serviceType, 
                                                Predicate<AID> filter) {
        ensureInitialized(agent);
        
        return agentRegistry.entrySet().stream()
            .filter(entry -> entry.getValue().equals(serviceType))
            .map(entry -> entry.getKey())
            .filter(filter == null ? aid -> true : filter)
            .collect(Collectors.toSet());
    }
    
    // Public methods that return String lists remain the same
    public static ArrayList<String> findTownies(Agent agent) {
        return findAgentNamesByFilter(agent, "townie", null);
    }
    
    public static ArrayList<String> findOtherTownies(Agent agent) {
        return findAgentNamesByFilter(agent, "townie", 
            aid -> !aid.equals(agent.getAID()));
    }
    
    public static ArrayList<String> findJudges(Agent agent) {
        return findAgentNamesByFilter(agent, "judge", null);
    }
    
    public static Set<AID> findTowniesAIDs(Agent agent) {
        return findAgentAIDsByFilter(agent, "townie", null);
    }
    
    public static Set<AID> findJudgesAIDs(Agent agent) {
        return findAgentAIDsByFilter(agent, "judge", null);
    }
    
    public static ArrayList<String> findAgentsByType(Agent agent, String serviceType) {
        return findAgentNamesByFilter(agent, serviceType, null);
    }
    
    public static Set<AID> findAgentsAIDsByTypeExcludingTarget(Agent agent, String serviceType, String targetName) {
        return findAgentAIDsByFilter(agent, serviceType, 
            aid -> !aid.getLocalName().equals(targetName));
    }
    
    public static Set<AID> findAgentsAIDsByTypeExcludingSelfAndTarget(Agent agent, String serviceType, String targetName) {
        return findAgentAIDsByFilter(agent, serviceType, 
            aid -> !aid.equals(agent.getAID()) && !aid.getLocalName().equals(targetName));
    }
    
    public static ArrayList<String> findAgentsByTypeExcludingSelfAndTarget(Agent agent, String serviceType, String targetName) {
        return findAgentNamesByFilter(agent, serviceType, 
            aid -> !aid.equals(agent.getAID()) && !aid.getLocalName().equals(targetName));
    }
    
    public static ArrayList<String> findAgentsByTypeExcludingSelf(Agent agent, String serviceType) {
        return findAgentNamesByFilter(agent, serviceType, 
            aid -> !aid.equals(agent.getAID()));
    }
    
    public static Set<AID> findAgentAIDsByTypeExcludingSelf(Agent agent, String serviceType) {
        return findAgentAIDsByFilter(agent, serviceType, 
            aid -> !aid.equals(agent.getAID()));
    }
    
    public static Set<AID> findAgentAIDsByType(Agent agent, String serviceType) {
        return findAgentAIDsByFilter(agent, serviceType, null);
    }
    
    public static AID findOtherAgentAIDbyName(String name, Agent agent, String serviceType) {
        ensureInitialized(agent);
        
        return agentRegistry.entrySet().stream()
            .filter(entry -> entry.getValue().equals(serviceType))
            .map(entry -> entry.getKey())
            .filter(aid -> aid.getLocalName().equals(name))
            .findFirst()
            .orElse(null);
    }
    
    public static ArrayList<AID> findTowniesAIDAtSamePosition(Agent myAgent) {
        ArrayList<AID> samePositionTownies = new ArrayList<>();
        GridManager gridManager = GridManager.getInstance();
        
        Point myPosition = gridManager.getAgentPosition(myAgent.getLocalName());
        if (myPosition == null) {
            return samePositionTownies; 
        }
        
        for (AID townieAID : findAgentAIDsByTypeExcludingSelf(myAgent, "townie")){
          
            Point otherPosition = gridManager.getAgentPosition(townieAID.getLocalName());
            if (otherPosition != null && 
                otherPosition.x == myPosition.x && 
                otherPosition.y == myPosition.y) {
                samePositionTownies.add(townieAID);
            }
        }
        
        return samePositionTownies;
    }
}