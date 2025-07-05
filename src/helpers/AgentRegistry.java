package helpers;

import jade.core.AID;
import jade.core.Agent;

import ui.GridManager;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import agents.TownieAgent;

public class AgentRegistry {
    
    private static ConcurrentHashMap<AID, Agent> townieMap = new ConcurrentHashMap<AID, Agent>();
    
    private static ConcurrentHashMap<AID, Agent> judgeMap = new ConcurrentHashMap<AID, Agent>();

   
    public static void registerTownie(Agent agent) {
        AID aid = agent.getAID();
        townieMap.put(aid, agent);
        
    }
    
   
    public static void unregisterTownie(AID aid) {
    	townieMap.remove(aid);
        
            }
     
    public static void registerJudge(Agent agent) {
        AID aid = agent.getAID();
        judgeMap.put(aid, agent);
        
    }
    
   
    public static void unregisterJudge(AID aid) {
    	judgeMap.remove(aid);
        
            }
   
    private static ArrayList<String> findAgentNamesByFilter(Agent agent,
                                                          Predicate<AID> filter) {
        
        return townieMap.entrySet().stream()
            .map(entry -> entry.getKey())
            .filter(filter == null ? aid -> true : filter)
            .map(AID::getLocalName)
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    private static Set<AID> findAgentAIDsByFilter(Agent agent, String serviceType,Predicate<AID> filter) {
        
    	if (serviceType == "townie"){
        return townieMap.entrySet().stream()
            .map(entry -> entry.getKey())
            .filter(filter == null ? aid -> true : filter)
            .collect(Collectors.toSet());
    	}
    	else if (serviceType == "judge"){
            return judgeMap.entrySet().stream()
                    .map(entry -> entry.getKey())
                    .filter(filter == null ? aid -> true : filter)
                    .collect(Collectors.toSet());
            	}
    	else {
    		return null;
    	}
    		
    	
    }
    
    public static TownieAgent getTownie(AID aid) {
       
        return (TownieAgent) townieMap.get(aid);
    }

   

    
    public static Set<AID> findAgentsAIDsByTypeExcludingTarget(Agent agent, String serviceType, String targetName) {
        return findAgentAIDsByFilter(agent,serviceType,
            aid -> !aid.getLocalName().equals(targetName));
    }
    
    public static Set<AID> findAgentsAIDsByTypeExcludingSelfAndTarget(Agent agent, String serviceType, String targetName) {
        return findAgentAIDsByFilter(agent,  serviceType,
            aid -> !aid.equals(agent.getAID()) && !aid.getLocalName().equals(targetName));
    }
    
    public static ArrayList<String> findAgentsByTypeExcludingSelfAndTarget(Agent agent,  String targetName) {
        return findAgentNamesByFilter(agent, 
            aid -> !aid.equals(agent.getAID()) && !aid.getLocalName().equals(targetName));
    }
    
    public static ArrayList<String> findAgentsByTypeExcludingSelf(Agent agent ) {
        return findAgentNamesByFilter(agent, 
            aid -> !aid.equals(agent.getAID()));
    }
    
    public static Set<AID> findAgentAIDsByTypeExcludingSelf(Agent agent, String serviceType) {
        return findAgentAIDsByFilter(agent,  serviceType,
            aid -> !aid.equals(agent.getAID()));
    }
    
    public static Set<AID> findAgentAIDsByType(Agent agent, String serviceType) {
        return findAgentAIDsByFilter(agent,serviceType,null);
    }
    
    public static AID findOtherTownieAIDbyName(String name, Agent agent) {
        
        return townieMap.entrySet().stream()
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