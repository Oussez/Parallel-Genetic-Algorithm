package ma.enset.ga.parallelGA.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import ma.enset.ga.parallelGA.dictionnary.GAUtils;

import java.util.Random;

public class IndividualAgent extends Agent {
    private char genes[]=new char[GAUtils.CHROMOSOME_SIZE];
    private int fitness;
    Random rnd=new Random();
    @Override
    protected void setup() {
        /**Enregistrement des agents sur l'annuaire DFAgentDescription**/
        DFAgentDescription dfAgentDescription=new DFAgentDescription();
        dfAgentDescription.setName(getAID());
        ServiceDescription serviceDescription=new ServiceDescription();
        serviceDescription.setType("ga");
        serviceDescription.setName("ga_ma");
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this,dfAgentDescription);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        /**initialisation d'homogène d'agent**/
        for (int i=0;i<genes.length;i++){
            genes[i]= GAUtils.CHARATERS.charAt(rnd.nextInt(GAUtils.CHARATERS.length()));
        }
        /**Protocole de communication entre agent individual(receiver) et agent master(sender)**/
       addBehaviour(new CyclicBehaviour() {
           @Override
           public void action() {
               ACLMessage receivedMSG = blockingReceive();
                   switch (receivedMSG.getContent()){
                       case "mutation":mutation();break;
                       case "fitness" : calculateFitness(receivedMSG);break;
                       case "chromosome":sendChromosome(receivedMSG);break;
                       default:changeChromosome(receivedMSG);break; //case "['A', 'N', ...]"
                   }

           }
       });
    }

private void mutation(){
    int index=rnd.nextInt(GAUtils.CHROMOSOME_SIZE);
    if (rnd.nextDouble()<GAUtils.MUTATION_PROB){
        genes[index]=GAUtils.CHARATERS.charAt(rnd.nextInt(GAUtils.CHARATERS.length()));
    }
//    System.out.println("gene after mutation at  "+index+" -> "+Arrays.toString(genes) );
}

private void calculateFitness(ACLMessage receivedMSG){
    fitness=0;
    for (int i=0;i<GAUtils.CHROMOSOME_SIZE;i++) {
        if(genes[i]==GAUtils.SOLUTION.charAt(i))
            fitness+=1;
    }
    ACLMessage replyMsg=receivedMSG.createReply();
    replyMsg.setContent(String.valueOf(fitness));
    send(replyMsg);
}
private void sendChromosome(ACLMessage receivedMSG){
    ACLMessage replyMsg=receivedMSG.createReply();
    replyMsg.setContent(new String(genes));
    send(replyMsg);
}
private void  changeChromosome(ACLMessage receivedMSG){
    String ans=receivedMSG.getContent();//"['A', 'S', ...] "
    genes = ans.toCharArray();

}

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
