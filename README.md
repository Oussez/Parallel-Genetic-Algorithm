# Distributed genetic algorithms
### 1. INTRODUCTION (EN): 
**Parallel genetic algorithm** is such an algorithm that uses multiple genetic algorithms to solve a single task. All these algorithms try to solve the same task and after they’ve completed their job, the best individual of every algorithm is selected, then the best of them is selected, and this is the solution to a problem. This is one of the most popular approach to parallel genetic algorithms, even though there are others. This approach is often called ‘island model’ because populations are isolated from each other, like real-life creature populations may be isolated living on different islands. Image below illustrates that.

![islands model](https://miro.medium.com/v2/resize:fit:640/format:webp/1*EtWIjfFjiyHkS12sCZAGrA.png)
### 2. Partie conception : 
Le systeme multi agent de cette application base sur l'achitecture CENTRALISEE comme suit : 

 Il existe 2 type d'agents : 
- Master Agent ( agent qui controle les opérations de genetic algorithm : selection, crossover, mutation, getFitness, get_optimum_individual...)

- individual Agent : il represente un individuel et il applique des opérations demandées par le Master Agent.

![islands modelxx](/Parallel Genetic Algorithm/SCREENS/Execution.png)
