package agents;

import helpers.AgentRegistry;
import jade.core.Agent;

public abstract class RegistryAgent extends Agent {

    private static final long serialVersionUID = 1L;
    protected abstract String getAgentType();
    @Override
    protected final void setup() {
    	
        String agentType = getAgentType();
        
        if ("judge".equalsIgnoreCase(agentType)) {
            AgentRegistry.registerJudge(this);
        } else if ("townie".equalsIgnoreCase(agentType)) {
            AgentRegistry.registerTownie(this);
        } 
        
//        System.out.println(getLocalName() + " registered as " + agentType);
        
        onSetup();
    }
    
    @Override
    protected final void takeDown() {
        String agentType = getAgentType();
        
        if ("judge".equalsIgnoreCase(agentType)) {
            AgentRegistry.unregisterJudge(getAID());
        } else {
            AgentRegistry.unregisterTownie(getAID());
        }
        
        System.out.println(getLocalName() + " (" + agentType + ") has unregistered");
        onTakeDown();
    }
    

    
    // Override these methods instead of setup()/takeDown()
    protected abstract void onSetup();
    protected void onTakeDown() {}
}