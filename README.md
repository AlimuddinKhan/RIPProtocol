# RIP PROTOCOL
#### Author: Alimuddin Khan
##### Email ID: aak5031@rit.edu

This program implements the RIP Protocol specifications which uses distance vetor routing algorithm in the backend.
Using this program you can dynamically add, remove, update the neighbor's to a router and cost to reach them.

This impemmntation also takes care of link removal and count-to-infinity issues.

------

## Step1: Compile All the files
 You can compile all the files using the following command
 ```shell
 javac *.java
 ```
## Step2: Run the RIPProtocol
 You can run this program in the following way;
 ```shell
 java RIPProtocol 
 ```

## Step3: Interacting with the program 
You have following set of commands to interact with the program

| command |  details |
| --------| ---------|
| add \<neighbor's IP\> \<cost\> |  This command adds one neighbor with the given cost|
|addall | This method takes you to the wizard to add more than one neighbor at a time|
|remove \<IP-of-nbr\> | This command removes the local link to specified neighbor |
| removeall | removes all the neighbor's from the network |
| update \<nbrs-IP\> \<updated-cost\> | This command helps you to change the cost to a neighbor |
| me | returns the IP of the router on which this program is running |
| list | This commands prints all the nbrs of the router |
| send \<nbr\> |  send route updates to a particular neighbor |
| sendall | sends update to all the neighbors |
| debug \<true/false\> | This command helps you to set or reset the debug message flag |                        
| refresh \<time-in-milli-seconds\> | This command helps you to change the routing table update/refresh time |
| print | To print routing table |
| printdv | To print all available paths to a destination with cost |
| quit | This method helps you to properly terminate the program |
| help | To print this instruction |




1. add <neighbor's IP> <cost> :  This command adds one neighbor with the given cost
2. addall : This method takes you to the wizard to add more than one neighbor at a time
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
















