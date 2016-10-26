 /**
Author: Alimuddin Khan
 **/
 Step1: Compile All the files
 You can compile all the files using the following command
 javac *.java
 Step2: Run the RIPProtocol -> 
 You can run this program in the following way;
 java RIPProtocol <machine-name> <update-interval>

<mahine-name>           : Optionale. Can be rhea, comet, glados or queeg\n" +
<update-interval>       : This is also optional.
                          You can set the trigger update inteval here.\n" +
                        Default is 1000 milliseconds.
e.g. 
 java RIPProtocol comet 30000
 (if you are running this program on comet server and want update interval
 to be 30 seconds. Default update interval is 1 second)

Step3: Interacting with the program -> 
You have following set of commands to interact with the program

1. add <neighbor's IP> <cost> :  This command adds one neighbor with the given cost
2. addall : This method takes you to the wizard to add more than neighbor's at a time
3. remove <IP-of-nbr> : This command removes the local link to specified neighbor
4. removeall : removes all the neighbor's from the network
5. update <nbrs-IP> <updated-cost> : This command helps you to change the cost to a neighbor
6. me :  returns the IP of the router on which this program is running
7. list : This commands prints all the nbrs of the router
8. send <nbr> :  send updates to a particular neighbor
9. sendall : sends update to all the neighbors
10. debug <true/false> : This command helps you to set or reset the debug message flag                        
11. refresh <time-in-milli-seconds> : This command helps you to change the routing table update/refresh time\n" +
12. print : To print routing table
13. printdv : To print all available paths to a destination with cost
14. quit : This method helps you to properly terminate the program
15. help : To print this instruction

Step4: Common Tasks
A} Initializing th default network.
I am using the following default netwrok;
comet(129.21.34.80) - - - - - 5 - - - - - glados(129.21.22.196)
        |                                               |
        |                                               | 
        |                                               |

        2                                               3

        |                                               |
        |                                               |
        |                                               |
queeg(129.21.30.37) - - - - - 10 - - - - - rhea(129.21.37.49)

To start this default network you can use following command
java RIPProtocol comet  -> to start comet server
java RIPProtocol glados -> to start glados server
java RIPProtocol queeg  -> to start queeg server
java RIPProtocol rhea   -> to start rhea server

Distance vector algorithm will run and we will get the shortest path accross all 
the routers

B} Update the link:
To update link cost you can use update command 
during the execution of the program.
e.g. to update the link between comet and glados you can run followin command
update 129.21.22.196 17 -> run this command in comet server and 17 is the new cost
update 129.21.22.196 17 -> run this command in glados server and 17 is the new cost

The distance vector algorithm will run and new routes will be updated in 
all the router within a couple of seconds.
It takes care of count-to-infinity by implementing Split horizon poison revrese.
(NOTE: while typing the update command, don't bother about the current
outputs being printed on the terminal. Even if your input goes in two different lines
, the java is smart enough which part has been typed by you.)

C} Remove the link:
You can use remove command to do that.
(Like all other commands, this command is also interactive and 
can be type in during the execution of the program.)
e.g. remove link between comet and glados
remove 129.21.22.196    -> type this command in comet server to remove glados as neighbor
remove 129.21.34.80     -> type this command in glados server to remove comet as neighbor 

he distance vector algorithm will run and new routes will be updated in 
all the router within a couple of seconds.
It takes care of count-to-infinity by implementing Split horizon poison revrese.

D} extension of the network:
You can extend network using add and addall commands
e.g. Lets say I am adding one more router nessie.cs.rit.edu
in the network. 
add 129.21.22.195 10 -> if you run this command in glados server while RIPProtocol program
is running then it will add one neighbor in glados router with cost being 10
Similarly you have to run following command to let nessie know that glados is your neighbor;
add 129.21.22.196 10
{NOTE: cost of above 15 is not allowed as above 15 is equivalent to un-reachable}

E} Last words:
Entire program is pretty interactive. Please try different network configurations
 and try to break the code. I would be more than happy to solve them. I added those options 
 to make my debugging and testing part easier and I feel they will make
 your grading part a lot easier. :)

















