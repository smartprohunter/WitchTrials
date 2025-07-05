	package behaviours;
	

import java.util.Arrays;


import agents.TownieAgent;
import helpers.AgentRegistry;
import helpers.Helpers;
import jade.core.AID;
	import jade.core.behaviours.CyclicBehaviour;
	import jade.lang.acl.ACLMessage;
	import jade.lang.acl.MessageTemplate;
import ui.GridManager;
	
	public class DeliberateBehaviour  extends CyclicBehaviour{
	
		
		private static final long serialVersionUID = 9148033641075367149L;
	
		private String[] suspiciousProfessions = {"tavern keeper", "Unemployed", "widow"};
		
		GridManager instance = GridManager.getInstance();
	
		private String severity;
		public DeliberateBehaviour(String severity) {
			super();
			this.severity = severity;
			}
		
		@Override
		public void action() {
			   MessageTemplate mt = MessageTemplate.and(
			            MessageTemplate.MatchConversationId("deliberate"),
			            MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
			        );
			        
		       ACLMessage msg = myAgent.receive(mt);
		       if(msg != null) {
		    	  String content = msg.getContent();
		    	  String[] parts = content.split("::", 3);
		    	  AID accusedAID = AgentRegistry.findOtherTownieAIDbyName(parts[1], myAgent);
		    	  AID accuserAID = AgentRegistry.findOtherTownieAIDbyName(parts[2], myAgent);
;		    	  boolean verdict = getVerdict(accusedAID, accuserAID);
		    	  ACLMessage acl;
		    	  if (verdict) {
		    		   acl = new ACLMessage(ACLMessage.CONFIRM);
		    		   instance.logToUI
//		    		   System.out.println
		    		   ("Judge " + myAgent.getLocalName() + " has found the suspect guilty of witchcraft."  );
	
		    	  }else {
		    		  	acl = new ACLMessage(ACLMessage.DISCONFIRM);
//		    		  	System.out.println
		    		  	 instance.logToUI("Judge " + myAgent.getLocalName() + " has found the suspect innocent of witchcraft."  );
	
	
		    	  }
				acl.addReceiver(accuserAID);
		    	acl.setContent("VERDICT::" + parts[1]);  
		    	acl.setConversationId("verdict");
		    	myAgent.send(acl);
		       }
		       else {
		    	   block();
		       }
		}
	
		private boolean getVerdict(AID accusedAID, AID accuserAID) {
			double convictionThreshold = 0.1;
			
			TownieAgent accusedAgent = AgentRegistry.getTownie(accusedAID);
			TownieAgent accuserAgent =  AgentRegistry.getTownie(accuserAID);
			

			if (accusedAgent.getGender() == "Female") {
				convictionThreshold  += 0.4;
			}
			if (Arrays.stream(suspiciousProfessions).anyMatch(accusedAgent.getProfession()::equals)) {
			   convictionThreshold += 0.25;
			}
			if (accusedAgent.getSocialStatus() == -1) {
				convictionThreshold += 1;
			} 
			if (accusedAgent.getSocialStatus() > accuserAgent.getSocialStatus()) {
				convictionThreshold -= 0.25;
			}
			if (accuserAgent.getGender() == "Female") {
				convictionThreshold -= 0.1;
			}
			
			switch (this.severity) {
			
			
	        case "Aggressive":
	           
	            convictionThreshold += 0.6; 
	            break;
	            
	        case "Moderate":
	            break;
	            
	        case "Lenient":
	            convictionThreshold -= 0.2; 
	            break;
	            

		}

			double randomValue = Math.round(Math.random() * 100.0) / 100.0;
		    return true;
//		    		randomValue < convictionThreshold;
		}
	}
