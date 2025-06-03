	package behaviours;
	

import helpers.AgentFinder;
	import jade.core.AID;
	import jade.core.behaviours.CyclicBehaviour;
	import jade.lang.acl.ACLMessage;
	import jade.lang.acl.MessageTemplate;
	
	public class DeliberateBehaviour  extends CyclicBehaviour{
	
		
		private static final long serialVersionUID = 9148033641075367149L;
	
	
		private String severity;
		public DeliberateBehaviour(String severity) {
			super();
			this.severity = severity;
			}
		
		@Override
		public void action() {
			   MessageTemplate mt = MessageTemplate.and(
			            MessageTemplate.MatchConversationId("deliberate"+ System.currentTimeMillis()%10000 + "_" ),
			            MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
			        );
			        
		       ACLMessage msg = myAgent.receive(mt);
		       if(msg != null) {
		    	  String content = msg.getContent();
		    	  String[] parts = content.split("::", 3);
		    	  AID accuserAID = AgentFinder.findOtherAgentAIDbyName(parts[2], myAgent, "townie");
;		    	  boolean verdict = getVerdict();
		    	  ACLMessage acl;
		    	  if (verdict) {
		    		   acl = new ACLMessage(ACLMessage.CONFIRM);
		   	    	System.out.println("Judge " + myAgent.getLocalName() + " has found the suspect guilty of witchcraft."  );
	
		    	  }else {
		    		  	acl = new ACLMessage(ACLMessage.DISCONFIRM);
			   	    	System.out.println("Judge " + myAgent.getLocalName() + " has found the suspect innocent of witchcraft."  );
	
	
		    	  }
				acl.addReceiver(accuserAID);
		    	acl.setContent("VERDICT::" + parts[1]);  
		    	acl.setConversationId("verdict");
		    	myAgent.send(acl);
		       }
		}
	
		private boolean getVerdict() {
			double convictionThreshold;
			switch (this.severity) {
			
			
	        case "Aggressive":
	           
	            convictionThreshold = 0.25; 
	            break;
	            
	        case "Moderate":
	            convictionThreshold = 0.5; 
	            break;
	            
	        case "Lenient":
	            convictionThreshold = 0.75; 
	            break;
	            
	        default:
	            convictionThreshold = 0.5;
	            break;
		}
		    double randomValue = Math.random();	
		    return randomValue < convictionThreshold;
		}
	}
