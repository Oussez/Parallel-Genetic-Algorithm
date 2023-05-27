package ma.enset.ga.parallelGA.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import ma.enset.ga.parallelGA.islands.IslandAgent_1;
import ma.enset.ga.parallelGA.islands.IslandAgent_2;
import ma.enset.ga.parallelGA.islands.IslandAgent_3;

import java.util.ArrayList;
import java.util.List;

public class MasterAgent extends Agent {

    @Override
    protected void setup() {
        IslandAgent_1.masterAgent = this.getAID();
        IslandAgent_2.masterAgent = this.getAID();
        IslandAgent_3.masterAgent = this.getAID();
        addBehaviour(new Behaviour() {
            int i = 0;
            List<String> responses = new ArrayList<>();
            @Override
            public void action() {
                ACLMessage bestIndividualResponse = blockingReceive();
                responses.add(bestIndividualResponse.getContent());
                i++;
            }

            @Override
            public boolean done() {
                if(i==3) {
                    System.out.println("**** FINAL RESULT BY ORDER : ");
                    responses.forEach(response -> {
                        System.out.println(response);
                    });
                }
                return i==3;
            }
        });

    }
}