package ma.enset.ga.parallelGA;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ma.enset.ga.parallelGA.agents.IndividualAgent;
import ma.enset.ga.parallelGA.agents.MasterAgent;
import ma.enset.ga.parallelGA.dictionnary.GAUtils;
import ma.enset.ga.parallelGA.islands.IslandAgent_1;
import ma.enset.ga.parallelGA.islands.IslandAgent_2;
import ma.enset.ga.parallelGA.islands.IslandAgent_3;

public class SimpleContainer {
    public static void main(String[] args) throws StaleProxyException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController mainAgent=null;
        AgentController islandAgent_1=null;
        AgentController islandAgent_2=null;
        AgentController islandAgent_3=null;
        int k=0;
        for (int i=0;i< GAUtils.POPULATION_SIZE;i++){
            mainAgent = agentContainer.createNewAgent(String.valueOf(i), IndividualAgent.class.getName(), new Object[]{});
            mainAgent.start();
            k=i;
        }
        mainAgent = agentContainer.createNewAgent("masterAgent", MasterAgent.class.getName(), new Object[]{});
        mainAgent.start();
         islandAgent_1 = agentContainer.createNewAgent("islandAgent_1", IslandAgent_1.class.getName(), new Object[]{0,30});
         islandAgent_2 = agentContainer.createNewAgent("islandAgent_2", IslandAgent_2.class.getName(), new Object[]{30,60});
         islandAgent_3 = agentContainer.createNewAgent("islandAgent_3", IslandAgent_3.class.getName(), new Object[]{60,100});
         /**for islandAgent_1 ---> controls 200 individuals from index 0 to 200
           *for islandAgent_2 ---> controls 200 individuals from index 200 to 400
           *for islandAgent_3 ---> controls 200 individuals from index 400 to 600 **/
        islandAgent_1.start();
        islandAgent_2.start();
        islandAgent_3.start();



    }
}
