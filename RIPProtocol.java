import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * This class implements basic RIP protocol
 * @author  Alimuddin Khan aak5031@rit.edu
 */
public class RIPProtocol
        implements Runnable{
    // router object
    private MyRouter router;

    // Route update listener
    private RouteUpdateListener routeUpdateListener;

    // Thread to start listener socket
    private Thread listenerThread;

    // update sender socket
    private DatagramSocket socket;

    // port from which we will send the updates
    private int port;


    /**
     * This is the countructor to initialize the router object
     * @param routerIP Router IPV4 IP
     */
    public RIPProtocol(String routerIP) {
        this.router = new MyRouter(routerIP);

        // This is the port number on which sender socket will run
        this.port = 55557;
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println(this.getRouter().getDateTime() +
                    "(Sender) Unable to start sender the socket. \n" +
                    "Make sure that " + this.port + " is not used by other sockets");
        }

        // initializing the listener

        this.routeUpdateListener = new RouteUpdateListener(this.router, 55556);
        this.listenerThread = new Thread(routeUpdateListener);
        this.listenerThread.start();

    }

    /**
     * This method sends the router object to the desied destination
     * @param dest
     * @throws IOException
     */
    public void sendPacket(String dest)
            throws IOException{
        try {
            // creating Inet Addres object for the destination

            InetAddress destination = InetAddress.getByName(dest);
            byte[] routerByteArray = this.convertObjectToByteArray(this.router);
            int destinationport = 55556;
            DatagramPacket packet = new DatagramPacket(routerByteArray,
                    routerByteArray.length, destination, destinationport);

            // sending the packet to destination

            this.socket.send(packet);
        } catch (UnknownHostException e){
            System.out.println(this.getRouter().getDateTime() + ": (Sender) No such " +
                    "destination called " + dest + " exists");
        } catch (NullPointerException e){
            System.out.println(this.getRouter().getDateTime() + ": (Sender)" +
                    " sender socket was not properly initialized)");
        }
    }


    /**
     * This method sends the update messages to all the nbrs.
     * In short it acts as message trigger for passing the updates
     * @throws IOException
     */
    public void sendToAll() throws  IOException{
        if(this.getRouter().nbrs.size() == 0){
        }else {

            // passing updates to all the neighbors
            for (String nbr : this.getRouter().nbrs) {
                sendPacket(nbr);
            }
        }
    }


    /**
     * This method converts the Router object into byte array
     * Which will be useful in sendig the object as packet
     * @param object Router object to be converted into byte array
     * @return byte[] format of the object Router
     */
    public   byte[] convertObjectToByteArray(MyRouter object){
        byte[] myObjectByteArray = new byte[10240];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput output= null;
        try{
            output = new ObjectOutputStream(bos);
            output.writeObject(object);
            output.flush();
            myObjectByteArray = bos.toByteArray();

        }catch(Exception e){
            System.out.println(e.getStackTrace());
        }
        return myObjectByteArray;
    }


    @Override
    /**
     * This method acts runs in background and sends updates to
     * all nbrs
     */
    public void run() {

        while (true) {
            try {

                this.sendToAll();
            } catch (IOException e) {
                System.out.println(this.getRouter().getDateTime() + ":" +
                        " (Triggered Update) Socket unable to send the update\n" +
                        "Make sure the same socket is not being used by other programs");
                break;
            }


            try {
                // sleeping for fixed amount of time before sending updats again

                Thread.sleep(this.getRouter().getUpdateInterval());
            } catch (InterruptedException e) {
                break;
            }
        }
    }


    /**
     * This method returns the router object of RIP protocol
     * @return
     */
    public MyRouter getRouter() {
        return router;
    }


    /**
     * This method prints the welcome message
     */
    public void welcomeMessage(){
        System.out.println("################################################\n" +
                "Welcome to Ali's basic implementation of RIP protocol\n" +
                "Type help to see available set commands : \n" +
                "In case of difficulty in using this protocol \n" +
                "please read README.txt file carefully\n" +
                "###################################################");
    }
    /**
     * This method prints the all available usage commands
     */
    public  void printUsage(){
        System.out.println(
                        "1. add <neighbor's IP> <cost> :  " +
                        "This command adds one neighbor with the given cost\n" +
                        "2. addall : This method takes you to the wizard to add more than" +
                        "neighbor's at a time \n" +
                        "3. remove <IP-of-nbr> : This command removes the local link to" +
                        " specified neighbor\n" +
                        "4. removeall : removes all the neighbor's from the network\n" +
                        "5. update <nbrs-IP> <updated-cost> : This command helps you to change" +
                        " the cost to a neighbor\n" +
                        "6. me :  returns the IP of the router\n" +
                        "7. list : This commands prints all the nbrs of the router\n" +
                        "8. send <nbr> :  send updates to a particular neighbor\n" +
                        "9. sendall : sends update to all the neighbors" +
                        "10. debug <true/false> : This command helps you to set or reset the " +
                        "debug message flag\n" +
                        "11. refresh <time-in-milli-seconds> : This command helps you to change" +
                        " the routing table update/refresh time\n" +
                        "12. print : To print routing table\n" +
                        "13. printdv : To print all available paths to a destination with cost\n" +
                        "14. quit : This method helps you to properly terminate the program\n" +
                        "15. help : To print this instruction\n"
        );
    }


    /**
     * This method returns the IP address of the current router
     * @return String IP of the current router
     * @throws UnknownHostException
     */
    public static String getMyIPAddress() {
        InetAddress myAddress = null;
        try {
            myAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            //e.printStackTrace();
        }
        return myAddress.getHostAddress();

    }

    /**
     * This method prints the error message if the user has types a wrong username
     * @param command
     */
    public void printInvalidCommandUsageMessage(String command){
        System.out.println("ERROR: In correct usage of '" + command + "' command\n" +
                "Please type 'help' to see correct usage");
    }

    /**
     * This method validates a string as a valid IPv4 ip or not
     * @param ipAddress
     * @return
     */
    public  Boolean isValidIP(String ipAddress){
        try {
            InetAddress myAddress = InetAddress.getByName(ipAddress);
            boolean isIp = myAddress instanceof Inet4Address;
            //return isIp;
            return true;
        } catch (UnknownHostException e) {
            //return false;
            return true;

        }
    }

    /**
     * This method reads neighbor's in Bulk
     */
    public void readAllNeighbors(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please type number of Neighbours : ");
        int n = scanner.nextInt();
        // collect all neighbors info
        for(int i = 0; i < n; i++){
            try {
                System.out.println("Please type IPv4 address of neighbor: ");
                String nbr = scanner.next();
                if(!this.isValidIP(nbr)){
                    System.out.println("ERROR: Neighbor IP is not a valid ipV4 address");
                    this.printInvalidCommandUsageMessage("addall");
                    break;
                }
                System.out.println("Please type cost to that address: ");
                Integer cost = scanner.nextInt();
                this.getRouter().addNeighbor2(nbr, cost);
            } catch (Exception e){
                System.out.println("ERROR: Please type valid inputs");
                this.printInvalidCommandUsageMessage("addall");
                return;
            }
        }
    }


    /**
     * This method removes all the neighbor's from the network which are directly connected
     * to the router
     */
    public void removeAllNeighbors(){
        String[] nbrs = this.getRouter().getNbrs().toArray(new String[this.getRouter().getNbrs().size()]);
        for(String nbr: nbrs){
            this.router.removeLocalLink(nbr);
            try {
                this.sendToAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * This method safely terminates all the sockets and running threads
     */
    public void terminateRouter(){
        try {
            this.socket.close();
        } catch (NullPointerException e){

        }
        this.routeUpdateListener.stopListener();
    }

    /**
     * This method starts queeg server with default network parameters
     */
    public void startQueeg(){
        System.out.println("Staring Queeg server routing table with default neighbors");
        this.getRouter().addNeighbor2("129.21.34.80", 2);
        this.getRouter().addNeighbor2("129.21.37.49", 10);
        this.getRouter().printRoutingTable();
        this.router.dvchanged = true;
    }

    /**
     * This method starts queeg server with default network parameters
     */
    public void startRhea(){
        System.out.println("Staring Rhea server routing table with default neighbors");
        this.getRouter().addNeighbor2("129.21.30.37", 10);
        this.getRouter().addNeighbor2("129.21.22.196", 3);
        this.getRouter().printRoutingTable();
        this.router.dvchanged = true;
    }

    /**
     * This method starts queeg server with default network parameters
     */
    public void startGlados(){
        System.out.println("Staring Glados server routing table with default neighbors");
        this.getRouter().addNeighbor2("129.21.34.80", 5);
        this.getRouter().addNeighbor2("129.21.37.49", 3);
        this.getRouter().printRoutingTable();
        this.router.dvchanged = true;
    }

    /**
     * This method starts queeg server with default network parameters
     */
    public void startComet(){
        System.out.println("Staring Comet server routing table with default neighbors");
        this.getRouter().addNeighbor2("129.21.30.37", 2);
        this.getRouter().addNeighbor2("129.21.22.196", 5);
        this.getRouter().printRoutingTable();
        this.router.dvchanged = true;
    }

    /**
     * This methods prints the correct usage of this program
     */
    public void usage(){
        System.out.println("Correct Usage : \njava RIPProtocol <machine-name> <update-interval>\n" +
                "<mahine-name> : Optionale. Can be rhea, comet, glados or queeg\n" +
                "<update-interval> : This is also optional.\n" +
                "You can set the trigger update inteval here.\n" +
                " Defaul is 1000 milliseconds");
    }


    /**
     * This is the main method which interacts with the user for various tasks
     * @param args machine name for example queeg, glados, rhea, comet
     * @throws UnknownHostException
     */
    public static void main(String[] args)
            throws UnknownHostException, IOException{

        // create a router object
        RIPProtocol ripProtocol = new RIPProtocol(getMyIPAddress());

        // printing instructions for usage
        ripProtocol.welcomeMessage();

        // read the command line arugumers
        String commandString = "";

        if( args.length > 2){
            ripProtocol.usage();
            ripProtocol.terminateRouter();
            return;
        }else if(args.length == 1){
            // read the mashine name
            commandString = args[0];
        }else if(args.length == 2){
            commandString = args[0];
            try {
                int updateInterval = Integer.parseInt(args[1]);
                ripProtocol.getRouter().setUpdateInterval(updateInterval);
            }catch (NumberFormatException e){
                System.out.println("ERROR: Update interval must be integer");
                ripProtocol.usage();
                ripProtocol.terminateRouter();
                return;
            }
        }


        // staring respective default netwroks for the machine passed as cmd parameter

        if(commandString.equals("comet")){
            ripProtocol.startComet();
        }else if(commandString.equals("rhea")){
            ripProtocol.startRhea();
        }else if(commandString.equals("glados")){
            ripProtocol.startGlados();
        }else if(commandString.equals("queeg")){
            ripProtocol.startQueeg();
        }


        // scanner to read commands from the user
        Scanner scanner = new Scanner(System.in);

        // initializing update trigger thread
        Thread updateTriggerThread = new Thread(ripProtocol);
        updateTriggerThread.start();

        // keep reading commands from the user until he types quit
        while( !commandString.matches("quit") ){
            commandString = scanner.nextLine();
            String[] commandArray = commandString.split(" ");
            String command = commandArray[0];
            switch (command){
                case "add":
                    if(commandArray.length != 3){
                        ripProtocol.printInvalidCommandUsageMessage("add");
                        break;
                    }

                    if(!ripProtocol.isValidIP(commandArray[1])){
                        System.out.println("Please provide a valid IPV4 IP address");
                        ripProtocol.printInvalidCommandUsageMessage("add");
                        break;
                    }

                    int cost = -1;
                    try{
                        cost = Integer.parseInt(commandArray[2]);
                    }catch (NumberFormatException e){
                        System.out.println("ERROR: cost must be a valid integer");
                        ripProtocol.printInvalidCommandUsageMessage("add");
                        break;
                    }
                    // making sure that cost doesn't go above 15
                    if(cost > 15){
                        System.out.println("ERROR: cost of more than 15" +
                                " is equivalent to un-reachable");
                        break;
                    }
                    System.out.println("SUCCESS: Adding " + commandArray[1] + " as neighbor with cost "
                            + cost);
                    ripProtocol.getRouter().addNeighbor2(commandArray[1], cost);

                    if(ripProtocol.getRouter().dvchanged == true){
                        ripProtocol.sendToAll();
                        ripProtocol.getRouter().dvchanged = false;
                    }
                    ripProtocol.getRouter().printRoutingTable();
                    break;

                case "addall":
                    if(commandArray.length != 1){
                        ripProtocol.printInvalidCommandUsageMessage("addall");
                        break;
                    }
                    ripProtocol.readAllNeighbors();

                    if(ripProtocol.getRouter().dvchanged == true){
                        ripProtocol.sendToAll();
                        ripProtocol.getRouter().dvchanged = false;
                    }
                    ripProtocol.getRouter().printRoutingTable();
                    break;

                case "debug":
                    if(commandArray.length != 2 ){
                        ripProtocol.printInvalidCommandUsageMessage("debug");
                        break;
                    }
                    if( !commandArray[1].matches("true") && !commandArray[1].matches("false") ){
                        ripProtocol.printInvalidCommandUsageMessage("debug");
                        break;
                    }

                    boolean debugFlag = Boolean.valueOf(commandArray[1]);
                    ripProtocol.getRouter().setShowDebug(debugFlag);
                    System.out.println("SUCCESS: Debug flag has been updated to " +
                            ripProtocol.getRouter().isShowDebug());
                    break;

                case "refresh":
                    if(commandArray.length != 2 ){
                        ripProtocol.printInvalidCommandUsageMessage("refresh");
                        break;
                    }
                    System.out.println("We will be updating route tables in " + commandArray[1] + " milliseconds");
                    int updateInerval = ripProtocol.getRouter().getUpdateInterval();
                    try {
                        updateInerval = Integer.parseInt(commandArray[1]);
                    }
                    catch (NumberFormatException e){
                        System.out.println("ERROR: Please type interval as integer value in milliseconds");
                        break;
                    }
                    ripProtocol.getRouter().setUpdateInterval(updateInerval);
                    System.out.println("SUCCESS: Updated updating_interval to " + ripProtocol.getRouter().getUpdateInterval());
                    break;

                case "list":
                    if(commandArray.length != 1 ){
                        ripProtocol.printInvalidCommandUsageMessage("list");
                        break;
                    }
                    ripProtocol.getRouter().printNeighbors();
                    break;

                case "remove":
                    if(commandArray.length != 2 ){
                        ripProtocol.printInvalidCommandUsageMessage("remove");
                        break;
                    }
                    if(!ripProtocol.isValidIP(commandArray[1])){
                        System.out.println("ERROR: Please provide a valid host address for nbr");
                        ripProtocol.printInvalidCommandUsageMessage("remove");
                        break;
                    }
                    ripProtocol.getRouter().removeLocalLink(commandArray[1]);
                    ripProtocol.getRouter().printRoutingTable();
                    break;

                case "removeall":
                    if(commandArray.length != 1 ){
                        ripProtocol.printInvalidCommandUsageMessage("removeall");
                        break;
                    }
                    ripProtocol.removeAllNeighbors();
                    if(ripProtocol.getRouter().dvchanged == true){
                        ripProtocol.sendToAll();
                        ripProtocol.getRouter().dvchanged = false;
                    }
                    ripProtocol.getRouter().printRoutingTable();
                    break;

                case "update":
                    if(commandArray.length != 3 ){
                        ripProtocol.printInvalidCommandUsageMessage("update");
                        if(ripProtocol.getRouter().dvchanged == true){
                            ripProtocol.sendToAll();
                            ripProtocol.getRouter().dvchanged = false;
                        }
                        break;
                    }
                    // validating nbr as proper IPV4 address
                    if(!ripProtocol.isValidIP(commandArray[1])){
                        System.out.println("Please provide a valid host address for nbr");
                        ripProtocol.printInvalidCommandUsageMessage("update");
                        break;
                    }

                    int newcost = -1;
                    try{
                        newcost = Integer.parseInt(commandArray[2]);
                    }catch (NumberFormatException e){
                        System.out.println("ERROR: cost must be a valid integer");
                        ripProtocol.printInvalidCommandUsageMessage("update");
                        break;
                    }
                    ripProtocol.getRouter().updateLocalLinkChange(commandArray[1], newcost);

                    if(ripProtocol.getRouter().dvchanged == true){
                        ripProtocol.sendToAll();
                        ripProtocol.getRouter().dvchanged = false;
                    }
                    ripProtocol.getRouter().printRoutingTable();
                    break;

                case "me":
                    if(commandArray.length != 1 ){
                        ripProtocol.printInvalidCommandUsageMessage("me");
                        break;
                    }
                    System.out.println(ripProtocol.getRouter().getMyIP() + "("
                            + ripProtocol.getRouter().getHostName(ripProtocol.getRouter().getMyIP()) + ")");
                    break;

                case "send":
                    if(commandArray.length != 2 ){
                        ripProtocol.printInvalidCommandUsageMessage("send");
                        break;
                    }
                    ripProtocol.sendPacket(commandArray[1]);
                    break;

                case "sendall":
                    if(commandArray.length !=  1){
                        ripProtocol.printInvalidCommandUsageMessage("sendall");
                        break;
                    }
                    ripProtocol.sendToAll();
                    break;

                case "help":
                    ripProtocol.printUsage();
                    break;

                case "print":
                    if(commandArray.length != 1 ){
                        ripProtocol.printInvalidCommandUsageMessage("print");
                        break;
                    }
                    ripProtocol.getRouter().printRoutingTable();
                    break;

                case "printdv":
                    if(commandArray.length != 1 ){
                        ripProtocol.printInvalidCommandUsageMessage("printdv");
                        break;
                    }
                    ripProtocol.getRouter().printDV();
                    break;

                case "queeg":
                    if(commandArray.length != 1 ){
                        ripProtocol.printInvalidCommandUsageMessage("queeg");
                        break;
                    }
                    ripProtocol.startQueeg();
                    break;

                case "rhea":
                    if(commandArray.length != 1 ){
                        ripProtocol.printInvalidCommandUsageMessage("rhea");
                        break;
                    }
                    ripProtocol.startRhea();
                    break;

                case "glados":
                    if(commandArray.length != 1 ){
                        ripProtocol.printInvalidCommandUsageMessage("glagos");
                        break;
                    }
                    ripProtocol.startGlados();
                    break;

                case "comet":
                    if(commandArray.length != 1 ){
                        ripProtocol.printInvalidCommandUsageMessage("comet");
                        break;
                    }
                    ripProtocol.startComet();
                    break;


                case "quit":
                    ripProtocol.terminateRouter();
                    //updateTriggerThread.interrupt();
                    break;

                default:
                    System.out.println("Please type valid command OR type help " +
                            "to see list of available commands");
            }

        }

        // terminating trigger update thread
        updateTriggerThread.interrupt();
        scanner.close();
    }


}
