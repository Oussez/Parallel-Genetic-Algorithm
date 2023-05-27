package ma.enset.ga.parallelGA.islands;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import ma.enset.ga.parallelGA.agents.AgentFitness;
import ma.enset.ga.parallelGA.dictionnary.GAUtils;

import java.util.*;

public class IslandAgent_1 extends Agent {
    //Agent who controls the populations and set the crossOver , mutation , selection operations on it
    List<AgentFitness> agentsFitness=new ArrayList<>();
    static int totalIndividuals;
    static int firstIndex;
    static int lastIndex;
    public static AID masterAgent;

    @Override
    protected void setup() {
        lastIndex = (int) getArguments()[1];
        firstIndex = (int) getArguments()[0];
        totalIndividuals = lastIndex-firstIndex;
        DFAgentDescription dfAgentDescription=new DFAgentDescription();
        ServiceDescription serviceDescription=new ServiceDescription();
        serviceDescription.setType("ga");
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFAgentDescription[] agentsDescriptions = DFService.search(this, dfAgentDescription);
            for(int i=firstIndex;i<lastIndex;i++) {
                agentsFitness.add(new AgentFitness(agentsDescriptions[i].getName(),0));
            }
            System.out.println("** total nbr of online agents of island 1 :"+ agentsFitness.size());
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
        /** 1 etape : initialisation de fitness de chaque individual agent**/
        calculateFitness();

        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            int cpt=0;
            @Override
            public void action() {
                ACLMessage receivedMSG = receive();
                if (receivedMSG!=null){
                    cpt++;
                    int fintess=Integer.parseInt(receivedMSG.getContent());
                    AID sender=receivedMSG.getSender();
                    System.out.println(">ISLAND 1> ("+cpt+") Agent ["+sender.getLocalName()+"] ---> Fitness:  "+fintess);
                    setAgentFitness(sender,fintess);
                }else {
                    block();
                }
            }

            @Override
            public boolean done() {
                if(cpt==totalIndividuals){
                    Collections.sort(agentsFitness,Collections.reverseOrder());
                    return true;
                }
                return false;
            }

        });
        /**2eme etape : Boucle de selection + crossover + mutation + calculeMeilleurFitness
         * cette boucle s'arrete si : 1) nbr d'iterations = totalIndividuals
         *                            2) la valeur fitness de meilleur individual Agent = GAUtils.FITNESS
         */
        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            int iteration = 0;
            char [] newChromosome_1 = new char[GAUtils.CHROMOSOME_SIZE];
            char [] newChromosome_2 = new char[GAUtils.CHROMOSOME_SIZE];
            @Override
            public void action() {
                selection();
                crossOver();
                mutation(newChromosome_1, agentsFitness.size()-2);
                mutation(newChromosome_2, agentsFitness.size()-1);
                calculateFitness(agentsFitness.size()-2);
                calculateFitness(agentsFitness.size()-1);
                /**ordering**/
                Collections.sort(agentsFitness,Collections.reverseOrder());
                iteration++;
                /**This print below show the progress of each individual per iteration**/
//                System.out.println(">> "+iteration+" --> "+Arrays.toString(getBestIndividual()));

            }

            public void selection(){
                /**Selecting two best individual agents:**/
                AgentFitness bestIndividual_1 = agentsFitness.get(0);
                AgentFitness bestIndividual_2 = agentsFitness.get(1);
                /**Getting the chromosome of each best individual agent **/
                ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                aclMessage.setContent("chromosome");
                aclMessage.addReceiver(bestIndividual_1.getAid());
                aclMessage.addReceiver(bestIndividual_2.getAid());
                send(aclMessage);
            }

            public void crossOver(){
                ACLMessage response_1 = blockingReceive();
                ACLMessage response_2 = blockingReceive();
                char [] chromosome_1 = response_1.getContent().toCharArray();
                char [] chromosome_2 = response_2.getContent().toCharArray();


                int size = GAUtils.CHROMOSOME_SIZE;
                int crossPoint = new Random().nextInt(size-2);
                crossPoint++;

                /**initialisation of newAgent_1 and newAgent_2 with bestIndividual_1, bestIndividual_2*/
                for(int i=0;i<GAUtils.CHROMOSOME_SIZE;i++){
                    newChromosome_1[i] = chromosome_1[i];
                    newChromosome_2[i] = chromosome_2[i];
                }
                /**CrossOver operation**/
                for(int i=0;i<crossPoint;i++){
                    newChromosome_1[i] = chromosome_2[i];
                    newChromosome_2[i] = chromosome_1[i];
                }

//                System.out.println("*** BEFORE CROSSOVER ***");
//                System.out.println(">> "+ Arrays.toString(chromosome_1));
//                System.out.println(">> "+ Arrays.toString(chromosome_2));
//                System.out.println("*** After CROSSOVER ***");
//                System.out.println(">> crossPoint : "+crossPoint);
//                System.out.println(">> "+ Arrays.toString(newChromosome_1));
//                System.out.println(">> "+ Arrays.toString(newChromosome_2));

            }
            private void mutation(char [] chromosome, int index){
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.setContent(new String(chromosome));
                request.addReceiver(agentsFitness.get(index).getAid());
                send(request); //la methode changeChromosome() de IndividualAgent sera déclenchée.
                request.setContent("mutation");
                send(request);//la methode mutation() de IndividualAgent sera déclenchée.
            }
            private void calculateFitness(int index){
//               System.out.println("before calculate : "+agentsFitness.get(index).getFitness());
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.setContent("fitness");
                request.addReceiver(agentsFitness.get(index).getAid());
                send(request); //la methode changeChromosome() de IndividualAgent sera déclenchée.
                ACLMessage newFitness = blockingReceive();
                agentsFitness.get(index).setFitness(Integer.parseInt(newFitness.getContent()));
//               System.out.println(">> new fitness : "+agentsFitness.get(index).getFitness());
            }
            private char [] getBestIndividual(){
                ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                aclMessage.setContent("chromosome");
                aclMessage.addReceiver(agentsFitness.get(0).getAid());
                send(aclMessage);
                ACLMessage response = blockingReceive();
                return response.getContent().toCharArray();

            }
            @Override
            public boolean done() {
                if(iteration == 10*GAUtils.POPULATION_SIZE|| agentsFitness.get(0).getFitness() == GAUtils.MAX_FITNESS){
                    String response;
                    if(iteration == 10*GAUtils.POPULATION_SIZE)
                        response= ">>> ISLAND 1 : NO OPTIMUM INDIVIDUAL FOUND ***";
                    else{
                        response=">>> ISLAND 1 [iteration : "+iteration+"] THE BEST OPTIMUM FOUND  : "+Arrays.toString(getBestIndividual())+" --> fitness : "+agentsFitness.get(0).getFitness();
                    }
                    ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                    aclMessage.setContent(response);
                    aclMessage.addReceiver(masterAgent);
                    send(aclMessage);
                    return true;
             }
             return false;
            }
        });

        addBehaviour(sequentialBehaviour);

    }


    private void calculateFitness(){
    ACLMessage message=new ACLMessage(ACLMessage.REQUEST);

    for (AgentFitness agf:agentsFitness) {
        message.addReceiver(agf.getAid());
    }
    message.setContent("fitness");
    send(message);

}
private void setAgentFitness(AID aid,int fitness){
        for (int i=0;i<totalIndividuals;i++){
            if(agentsFitness.get(i).getAid().equals(aid)){
                agentsFitness.get(i).setFitness(fitness);
                //System.out.println(fitness+" =:= "+agentsFitness.get(i).getFitness());
                break;
            }
        }
}

private void showPopulation(){
    for (AgentFitness agentFitness:agentsFitness) {
//        System.out.println(agentFitness.getAid().getLocalName()+" "+agentFitness.getFitness());
    }
}
}
