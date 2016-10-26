import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class defines a Router and everything it needs to create a routing table
 * @author Alimuddin Khan (aak5031@rit.edu)
 */

public class MyRouter implements Serializable{

    // This hashmap stores all the possible routes to a destination
    HashMap<String,HashMap<String, Integer>> DV;

    // This set stores the set of neighbors
    HashSet<String> nbrs;

    // This stores the IP of the router
    String myIP;

    // This is a flag and is used to show debug messages
    boolean showDebug;

    // This is used to set the auto update time default is 15 sec
    int updateInterval;

    // This stores the lastUpdated Time of the routing table
    String lastUpdateTime;

    // flag to show if update in routing table had haapened in last triggered update
    boolean dvchanged;


    /**
     * This constructor initializes the router object with tha given IP and
     * all the data structures it need
     * @param myIP IP of the router
     */
    public MyRouter(String myIP) {
        this.myIP = myIP;
        this.DV = new HashMap<>();
        this.nbrs = new HashSet<>();
        this.showDebug = true;
        this.updateInterval = 1000;
        this.lastUpdateTime = this.getDateTime();
        this.dvchanged = false;
    }


    /**
     * This method returns routing table update info
     * @return
     */
    public boolean isDvchanged() {
        return dvchanged;
    }

    /**
     * This method sets routing table updation flag
     * @param dvchanged
     */
    public void setDvchanged(boolean dvchanged) {

        this.dvchanged = dvchanged;
    }

    /**
     * This constructor initializes the router object with tha given IP and
     * all the data structures it needs
     */
    public MyRouter() {
        this.DV = new HashMap<>();
        this.nbrs = new HashSet<>();
        this.showDebug = true;
        this.updateInterval = 30000;
        this.lastUpdateTime = this.getDateTime();
    }


    /**
     * Returns the distance vector of the router with all the available
     * paths to a destination
     * @return DV of the router
     */
    public HashMap<String, HashMap<String, Integer>> getDV() {
        return DV;
    }


    /**
     * This method returns the set of neighbors the router has
     * @return
     */
    public HashSet<String> getNbrs() {
        return nbrs;
    }


    /**
     * This method returns the IP of the current running router
     * @return
     */
    public String getMyIP() {
        return myIP;
    }


    /**
     * This method returns debug flag
     * @return
     */
    public boolean isShowDebug() {
        return showDebug;
    }


    /**
     * This method returns the interval after which we have to print the DV
     * @return
     */
    public int getUpdateInterval() {
        return updateInterval;
    }

    /**
     * This method returns the Date Time when the Dv was last updated
     * @return
     */
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method sets debug flag
     * @param showDebug
     */
    public void setShowDebug(boolean showDebug) {
        this.showDebug = showDebug;
    }

    /**
     * This method sets the print interval time to a new value as desired
     * @param updateInterval
     */
    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }


    /**
     * This method prints the routing table of the Router
     * Router Table has destination -> next hop for min cost -> min cost
     */
    public void printRoutingTable(){
        try {
            System.out.println("#### Routing table of " + this.getMyIP() + "  (" +
                    InetAddress.getByName(this.getMyIP()).getHostName() + ") ####");
            System.out.println("Current Time : " + this.getDateTime());
            System.out.println("Last Updated : " + this.lastUpdateTime);
            System.out.printf("|%-25s|%-25s|%-11s|\n", "Destination", "Next Hop", "MinCost");
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - " +
                    "- - -");

            // looking for all the possible destinations

            for (String destination : this.DV.keySet()) {
                String nextHop = this.getMinimumNextHopToDestination(destination);
                Integer minCost = this.getMinCostToDestination(destination);

                // printing only reachable entries getNetworkPrefix

                if (minCost != null && nextHop != null) {
                    System.out.printf("|%-25s|%-25s|%-11d|\n", this.getNetworkPrefix(destination) , nextHop + "(" +
                                    this.getHostName(nextHop) + ")",
                            minCost);
                } else if (nextHop != null && minCost == null) {
                    System.out.printf("|%-25s|%-25s|%-11d|\n", this.getNetworkPrefix(destination) , nextHop + "(" +
                                    this.getHostName(nextHop) + ")",
                            "un-reachable");
                } else if (nextHop == null && minCost == null) {
                    System.out.printf("|%-25s|%-25s|%-11s|\n", this.getNetworkPrefix(destination), "not-available",
                            "un-reachable");
                }
            }
        } catch(UnknownHostException e){

        }
        System.out.println();
    }


    /**
     * This method adds a neighbor to the router
     * @param name  Ip address string of the neighbour
     * @param cost  cost to reach the string
     */
    public synchronized void addNeighbor2(String name, Integer cost){

        // adding a key in the DV if we don't have this destination

        if( this.nbrs.contains(name)){
            if(isShowDebug()){
                System.out.println("FAILURE: (Addition of Neighbor): " + name +
                        " is already in the neighbor's list\n" +
                        "use 'update' command to change the link cost");
                return;
            }
        }

        this.nbrs.add(name);

        // also adding the nbr to the destination list

        this.addDestination(name, cost);
    }

    /**
     * This method is used to neigbours as destination. Note that there is no next hop entry
     * in the parameter list
     * @param name name of the destination (IP address of the destination)
     * @param cost
     */
    public synchronized void addDestination(String name, Integer cost){
        if( !this.DV.containsKey(name)){
            // making a new entry if the neighbor is not in the list

            HashMap<String, Integer> nextHops = new LinkedHashMap<>();
            nextHops.put(name,cost);
            this.DV.put(name, nextHops);
            if( this.showDebug ) {
                System.out.println(this.getDateTime() + ": (Destination Addition) : Added neighbor "
                        + name + " in the destination list");
            }
            // update the last update time
            this.lastUpdateTime = this.getDateTime();

        } else{
            // updating the cost to the neighbor if the entry didn;t exist

            if(this.DV.get(name) != null){
                this.DV.get(name).put(name, cost);

                // update the  last update time
                this.lastUpdateTime = this.getDateTime();
            }else{
                HashMap<String, Integer> nextHops = new LinkedHashMap<>();
                nextHops.put(name,cost);
                this.DV.put(name, nextHops);

                // update the last update time
                this.lastUpdateTime = this.getDateTime();
            }
        }
    }


    /**
     * This method takes neighbor router's DV and updates the table as per the
     * Neighbor's distance vector. This method also makes sure to couter;
     * 1} count-to-infinity
     * 2} looping effect
     * It implements Split Horizon Poison reverse to avoid count-to infinity and
     * looping effect
     * @param otherRouter This is one of the neighbor router's object
     */
    public synchronized void  updateDistanceVector(MyRouter otherRouter){

        // This is a falg which indicate whether update in routing table happened or not

        boolean changeHappened = false;

        // Making sure that the route update has come from one of the neighbor's only

        if( !this.nbrs.contains(otherRouter.getMyIP())
                && this.getMyIP().matches(otherRouter.getMyIP())){
           // no update to be made here :)
        }else{

            for( String destination: otherRouter.DV.keySet()) {
                try{

                if (!destination.equals(this.getMyIP())) {
                    // Implementing Split horizon poison reverse by not allowing path to destination
                    // though itself from the nbr
                    if(otherRouter.getMinimumNextHopToDestination(destination) != null &&
                            !otherRouter.getMinimumNextHopToDestination(destination).equals(this.myIP)) {

                        if (otherRouter.getMinimumNextHopToDestination(destination) != null
                                && otherRouter.getMinCostToDestination(destination) != null) {

                            // adding the newly learned path from the neighbor to the destination list

                            this.addDestination3(destination, otherRouter.getMyIP(),
                                    this.getMinCostToDestination(otherRouter.getMyIP())
                                    + otherRouter.getMinCostToDestination(destination));

                        } else if ( otherRouter.getMinimumNextHopToDestination(destination) == null ||
                                otherRouter.getMinCostToDestination(destination) == null) {

                            // making all destinations unreachable through paths which have
                            // neighbor as next hop. This helps in recalculating path once on of the
                            // path becomes unavailable

                            if (this.showDebug) {
                                System.out.println(this.getDateTime() + ": (Distance Vector Update) " + destination +
                                        " is now unreachable through " + otherRouter.getMyIP());
                            }
                            this.DV.get(destination).put(otherRouter.getMyIP(), null);
                            changeHappened = true;

                        }
                    } else {
                        if (this.showDebug) {

                            // This section indicates we have avoided the count-toinfinity by
                            // implementing split horizon
                            // since nbr is routing the route through you. You no more need
                            // path to destination with nbr as next hop

                            this.DV.get(destination).put(otherRouter.getMyIP(), null);
                        }
                    }
                }

            } catch (Exception e){
                    // will catch NullPointerException if we do not have any neighbor's
                }
            }
        }

        if( changeHappened == true){

            // triggering the update in neighbor's
            this.triggerUpdateInNeighbors();

            // updating last update time here
            this.lastUpdateTime = this.getDateTime();
        }
    }


    /**
     * This method adds a next hop to a destination
     * @param name      name of the destination
     * @param nextHop   name of the next hop
     * @param cost      cost to reach the destination
     */
    public synchronized void addDestination3(String name, String nextHop, Integer cost){

        // a flag which indicates whether an update has occurred or not

        boolean updatedDV = false;

        if( !this.DV.containsKey(name)){
            updatedDV = true;
            HashMap<String, Integer> nextHops = new LinkedHashMap<>();
            nextHops.put(nextHop,cost);

            // making a new destination entry in the DV for the new destination found

            this.DV.put(name, nextHops);
            System.out.println(this.getDateTime() + ": (Found New destination) Adding destination "
                    + getHostName(name) + " via " + nextHop);
        } else{

            if(this.DV.get(name) != null){

                // check if the new cost is lower than the previous one
                if( this.DV.get(name).containsKey(nextHop)){

                    // Triggering update only when the new cost is less than the previous one

                    if(this.DV.get(name).get(nextHop) != null
                            && this.DV.get(name).get(nextHop) > cost){
                        updatedDV = true;
                        System.out.println(this.getDateTime() + ": (Cost updation ) Found new min Path to  "
                                + getHostName(name) + " via " + nextHop);
                    }
                }

                this.DV.get(name).put(nextHop, cost);
            }else{

                // making updated as next hop never existed for this destination
                // means we have discvered a new path to destination

                updatedDV = true;
                HashMap<String, Integer> nextHops = new LinkedHashMap<>();
                nextHops.put(nextHop,cost);
                this.DV.put(name, nextHops);
            }
        }


        if(updatedDV ==  true){

            // updating the last update time of routing table

            this.lastUpdateTime = this.getDateTime();

            // Triggering the update in the neighbor routers

            this.triggerUpdateInNeighbors();
        }
    }


    /**
     * Here most probably I will wring a trigger to send the updated DV to neighbors
     * This is the most important method for now
     */
    public void triggerUpdateInNeighbors(){
        this.dvchanged = true;
    }


    /**
     * This method returns the direct cost to a neighbor
     * @param nbr Neighbor router
     * @return
     */
    public Integer getDirectCost(String nbr){
        if(!this.nbrs.contains(nbr)){
            return null;
        }else{
            return this.DV.get(nbr).get(nbr);
        }
    }


    /**
     * This method updates any possible local link change
     * @param nbr           Ip of the neighbor router
     * @param newDistance   New distance to that neighbor
     */
    public synchronized void updateLocalLinkChange(String nbr, Integer newDistance){
        Integer oldDistance = this.getDirectCost(nbr);
        if(this.nbrs.contains(nbr)){

            // update all the costs to destinations which have nbr as next hop

            for(HashMap<String, Integer> nextHops: this.DV.values()){
                if(nextHops.containsKey(nbr)){
                    // making sure if cost is not null

                    if(nextHops.get(nbr) != null){
                        this.lastUpdateTime = this.getDateTime();
                        nextHops.put(nbr, nextHops.get(nbr) + newDistance - oldDistance);
                    }else{
                        // if cost was null then this is the new distance

                        nextHops.put(nbr, newDistance);
                    }
                }
            }
        } else{
            if(this.showDebug) {
                System.out.println(this.getDateTime() + " : (LocalLinkChange)" + nbr +
                        " is not a neighbor of " + this.getMyIP() +
                        ",\n hence no effect of " + nbr + "'s local link change on this router");
            }
        }
    }

    /**
     * This method takes care of removal of local link to a destination
     * @param nbr IP of neighbor whose local link is to be removed
     */
    public synchronized void removeLocalLink(String nbr){
        if( this.nbrs.contains(nbr)){
            if(this.showDebug) {
                System.out.println(this.getDateTime() + ": (LocalLinkRemoval) : "
                        + " Removed " + nbr + " from neighbor list");
            }
            this.nbrs.remove(nbr);

            // making all the paths un-reachable who had nb as next hop

            for( String destination: this.DV.keySet()){
                if(this.DV.get(destination).containsKey(nbr)){

                    // updating the cost to null
                    this.DV.get(destination).put(nbr, null);
                }
            }
        }else {
            if(this.showDebug) {
                System.out.println(this.getDateTime() + ": (LocalLinkRemoval) : "
                        + " Can't remove local link to " + nbr +
                        " as we don't have it as neighbor");
            }
        }
    }


    /**
     * This method returns the next hop which has the minimum cost to the
     * destination
     * @param destination destination
     * @return Next hop which has the minimum cost to the destination
     */
    public String getMinimumNextHopToDestination(String destination){
        String minHop = null;
        // returning null if destination is not present in the distance vector
        if( ! this.DV.containsKey( destination )){
            return null;
        }

        else{
            int minDistance = Integer.MAX_VALUE;
            boolean firstHop = false;

            // finding minimum cost next hop for the destination

            for(String hop: this.DV.get(destination).keySet()){
                if(this.DV.get(destination).get(hop)!= null ) {

                    if(firstHop == false){
                        if(this.getNbrs().contains(hop)) {
                            minHop = hop;
                            minDistance = this.DV.get(destination).get(hop);
                            firstHop = true;
                        }
                    }else{
                        if (this.getNbrs().contains(hop) &&
                                minDistance > this.DV.get(destination).get(hop)) {
                            minHop = hop;
                            minDistance = this.DV.get(destination).get(hop);
                        }
                    }
                }
            }
        }
        return minHop;
    }


    /**
     * This method returns the minimum cost available to a particular destination
     * @param destination
     * @return
     */
    public Integer getMinCostToDestination(String destination){
        if( this.DV.containsKey(destination)){
            if( this.DV.get(destination) != null) {
                return this.DV.get(destination).get((getMinimumNextHopToDestination(destination)));
            }else{
                return null;
            }
        }
        else{

            // since destination is not-available in the DV we will return null

            return null;
        }
    }


    /**
     * This is a Debugging method and used to print all the available paths to a destination.
     * This method prints all the next hops and their corresponding costs
     * @param destination destination IP
     */
    public  void printNextHops(String destination){
        System.out.println("##For Destination -> " + destination + "(" + this.getHostName(destination)+ " ) -> ( Min cost : " +
                this.getMinCostToDestination(destination) + " via " +
                this.getMinimumNextHopToDestination(destination) + " )");
        System.out.printf("|%-25s|%-25s|\n","Next Hop", "Cost");
        System.out.println("- - - - - - - - - - - - - - - - - ");
        for(String nextHop:  this.DV.get(destination).keySet()){
            if(this.DV.get(destination).get(nextHop) != null) {
                System.out.printf("|%-25s|%-25d|\n", this.getHostName(nextHop), this.DV.get(destination).get(nextHop));
            } else {
                System.out.printf("|%-25s|%-25s|\n", this.getHostName(nextHop), "un-reachable");
            }
        }
        System.out.println("-x-x-x-x");
    }

    /**
     * This method prints the distance vector of a router.
     * It includes all the avaiable paths to a destination
     */
    public void printDV(){
        System.out.println("-------Distance Vector( " + this.getHostName(this.getMyIP()) + " )-------");
        for(String destination: this.DV.keySet()){
            this.printNextHops(destination);
        }
    }

    /**
     * This method prints the neighbors if the router
     */
    public void printNeighbors(){
        if( this.nbrs.isEmpty()){
            System.out.println("SORRY currently this router has no neighbor.\n" +
                    "Use add or addall commands to start addding your neighbors");
        }
        for(String nbr: this.nbrs){
            System.out.println(nbr);
        }
    }


    /**
     * Returns the current time stamp
     * @return string formatted current date time in yyyy-MM-dd HH:mm:ss
     */
    public  String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateTime = sdf.format(date);
        return dateTime;
    }


    /**
     * This method returns the hostname of the destination
     * @param destinationString
     * @return
     */
    public String getHostName(String destinationString){
        InetAddress destination = null;
        try {
            destination = InetAddress.getByName(destinationString);
        } catch (UnknownHostException e) {

        }
        return destination.getHostName();
    }

    /**
     * This method computes the netwrok prefix given ip address
     * It assumes subnet mask to be 255.255.255.0
     * @param ipAddress IP address
     * @return network prefix of the IP address
     */
    public  String getNetworkPrefix(String ipAddress){
        String[] nwPrefixArray = ipAddress.split("\\.");
        String nwPrefix = "";
        for(int i = 0; i < 3; i++){
            nwPrefix += nwPrefixArray[i] + ".";
        }
        nwPrefix += "00";
        return nwPrefix;
    }


}


