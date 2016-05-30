package src.people;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map.Entry;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Evaluate extends OneShotBehaviour {

	AID place;
	double quality;
	PersonReceiver receiver;
	
	static AID global;
	
	public Evaluate (Agent a, AID place) {
		myAgent = a;
		this.place = place;
		System.out.println("qualcosa");
		
		if (global == null){
			global = getGlobal();
		}
	}
	
	public Evaluate (Agent a, double quality, AID place, PersonReceiver receiver) {
		myAgent = a;
		this.quality = quality;
		this.place = place;
		this.receiver = receiver;
		
		if (global == null){
			global = getGlobal();
		}
	}
	
	AID getGlobal(){
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Global");
		dfd.addServices(sd);
		try {
			AID gAID = DFService.search(myAgent, dfd)[0].getName();
			//System.out.println(gAID.getName());
			return gAID;
		} catch (FIPAException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Evaluate(Agent a) {
		super(a);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void action() {
		if(place != null) {
		double think = ((Person)myAgent).restMap.get(place);
		double dThink = ((Person)myAgent).boldness * (quality - think);
		((Person)myAgent).restMap.put(place, think + dThink);
		
		System.out.println("Now I, " + myAgent.getLocalName() 
							+ ", think of " + place.getLocalName()
							+ " this: " +(think + dThink));
		}
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(global);
		try {
			msg.setContentObject(((Person)myAgent).restMap);
			//System.out.println("sent map");
		} catch(IOException e) {
			e.printStackTrace();
		}
		myAgent.send(msg);
		
		ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
		msg2.setOntology("Reviews");
		
		Hashtable<AID, Double> entry = new Hashtable<AID, Double>();
		entry.put(place, ((Person)myAgent).restMap.get(place));
		try {
			msg2.setContentObject(entry);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		for(AID address : ((Person)myAgent).friends){
			msg2.addReceiver(address);
		}
		myAgent.send(msg2);
	}
}
