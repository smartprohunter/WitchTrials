package agents;

import java.util.Map;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import onthology.OntologyExtractor;

public class TownieSimulationAgent  extends Agent{
	  private static final long serialVersionUID = 8527483876081856794L;
    
   private OntologyExtractor ontologyExtractor = null;
    @Override
    protected void setup() {
        
    	DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription serviceDesc = new ServiceDescription();
        serviceDesc.setName("simulationAgent");
        serviceDesc.setType("simulationAgent");
        dfd.addServices(serviceDesc);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new OneShotBehaviour() {
          	            

			private static final long serialVersionUID = 7635246859552240783L; 

			@Override
            public void action() {
				try {
					ontologyExtractor = new OntologyExtractor();
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}   
			        IRI townieIRI = ontologyExtractor.findClassIRIByFragment("Townie");
			        IRI judgeIRI = ontologyExtractor.findClassIRIByFragment("Judge");

			        try {
						createAgentsFromClass(townieIRI, "TownieAgent");
						createAgentsFromClass(judgeIRI, "JudgeAgent");

					} catch (StaleProxyException e) {
						e.printStackTrace();
					}
			        
            }

        });
    }

    public void createAgentsFromClass(IRI classIRI, String className) 
            throws StaleProxyException {
        Map<String, Map<String, String>> individuals = ontologyExtractor.getIndividualsOfClass(classIRI);
        AgentContainer mainContainer = getContainerController();

        for (Map.Entry<String, Map<String, String>> entry : individuals.entrySet()) {
        	String agentName = entry.getKey();
            Map<String, String>properties = entry.getValue();
            
            Object[] args = new Object[] { properties};
            AgentController agentController =  mainContainer.createNewAgent(
            agentName, "agents." + className, args);

            agentController.start();
        }
    }

}
