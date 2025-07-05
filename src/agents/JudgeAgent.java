package agents;

import java.util.Map;

import behaviours.DeliberateBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class JudgeAgent  extends RegistryAgent{

	private static final long serialVersionUID = -2397587730348445641L;
	private Map<String, String> args;
	private String severity;
	@SuppressWarnings("unchecked")
	@Override
	protected void onSetup() {
	        DFAgentDescription dfd = new DFAgentDescription();
	        dfd.setName(getAID());
	        Object[] args = getArguments();
	        if (args[0] instanceof Map) {
	        this.args = (Map<String, String>) args[0];
	        this.severity = this.args.get("Severity");
	        }
	        ServiceDescription serviceDesc = new ServiceDescription();
	        serviceDesc.setName("judge");
	        serviceDesc.setType("judge");
	        dfd.addServices(serviceDesc);
        
	        try {
	            // Register the service
	            DFService.register(this, dfd);
	        } catch (FIPAException fe) {
	            fe.printStackTrace();
	        }
	        addBehaviour(new DeliberateBehaviour(this.severity));
	        
	}
	@Override
	protected String getAgentType() {
		return "judge";
	}

}
