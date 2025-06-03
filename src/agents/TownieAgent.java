package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import behaviours.AccusationListeningBehavior;
import behaviours.GossipListeningBehavior;
import behaviours.RandomMoveBehaviour;
import behaviours.TownieActivitiesBehaviour;
import behaviours.UpdateRelationshipsBehaviour;
import helpers.AgentFinder;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import onthology.OntologyExtractor;
import ui.GridManager;

public class TownieAgent extends Agent {
	private static final long serialVersionUID = -6918905687097287626L;
	public Map<String, Integer> relationships = new HashMap<>(); 
	private Map<String, String> preferences;
    public TownieAgent() {
        // Auto-generated constructor stub
    }

    public Map<String, Integer> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, Integer> relationships) {
        this.relationships = relationships;
    }

    @SuppressWarnings("unchecked")
	protected void setup() {
        super.setup();
        Object[] args = getArguments();
        if (args[0] instanceof Map) {
        this.preferences = (Map<String, String>) args[0];
        }
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        
        ServiceDescription serviceDesc = new ServiceDescription();
        serviceDesc.setName("townie");
        serviceDesc.setType("townie");
        dfd.addServices(serviceDesc);
        GridManager gridManager = GridManager.getInstance();
        gridManager.addAgent(getLocalName());
        
        try {
            // Register the service
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        
        Random random = new Random();
        addBehaviour(new RandomMoveBehaviour(this, random.nextInt(10000, 20000)));
        addBehaviour(new GossipListeningBehavior(this));
        addBehaviour(new TownieActivitiesBehaviour(this));

        addBehaviour(new AccusationListeningBehavior());
        addBehaviour(new UpdateRelationshipsBehaviour());
        initializeRelationships(AgentFinder.findAgentsByTypeExcludingSelf(this, "townie"),preferences);

    }

    public void initializeRelationships(ArrayList<String> townies, Map<String, String> preferences) {
        Random random = new Random();
        int value;
        OntologyExtractor ontologyExtractor = null;
		try {
			ontologyExtractor = new OntologyExtractor();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        IRI townieIRI = ontologyExtractor.findClassIRIByFragment("Townie");
//        System.out.println(townies.size());
//        System.out.println(ontologyExtractor.getIndividuals(townieIRI).size() - 1);
//        while(townies.size() != ontologyExtractor.getIndividuals(townieIRI).size() - 1) {
//        	continue;
//        }
        for (String townie : townies) {
     
        	String name = preferences.getOrDefault(townie, "noPreference");
//        	System.out.println(name);
        	switch (name) {
            case "likes":
                value = random.nextInt(6, 9);
                break;
            case "dislikes":
                value = random.nextInt(0, 3);
                break;
            case "relative_of":
                value = 10;
                break;
            default:
                value = random.nextInt(4, 6);
                break;
        }
            relationships.put(townie, value);
        }
    }
   
   
    public String getLeastLikedTownie() {
    	   return relationships.entrySet()
    		        .stream()
    		        .min(Map.Entry.comparingByValue())
    		        .map(Map.Entry::getKey)
    		        .orElse(null);
    }
    

    
    public void removeFromRelationships(String agentName) {
       relationships.remove(agentName);
     
    }
} 
    