import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * This class acts as the routing table update message handler
 * It updates the router's routing table as per the messages received
 * @author  Alimuddin Khan aak5031@rit.edu
 */
public class RouteUpdateListener
        implements Runnable{
    // a socket to listen for the incoming messages
    private DatagramSocket socket;

    // port number to listen the messages
    private int port;

    // current router object
    private MyRouter router;

    // flag to decide when to stop listening for the update messages
    private boolean keepListening;

    /**
     * This constructor initializes the Listener Object
     * @param router
     */
    public RouteUpdateListener(MyRouter router, int port) {
        this.router = router;
        this.port = port;
        this.keepListening = true;
        try {
            this.socket = new DatagramSocket(this.port);
        } catch (SocketException e) {
            System.out.println(this.router.getDateTime() + ": (Listener) Unable to start listener the socket\n" +
                    "Make sure that " + this.port + " is not used by other sockets");
        }
    }


    /**
     * Getter for listening flag
     * @return
     */
    public boolean isKeepListening() {
        return keepListening;
    }


    /**
     * Setter for listening flag
     * @param keepListening
     */
    public void setKeepListening(boolean keepListening) {
        this.keepListening = keepListening;
    }


    /**
     * properly closing the update listener socket
     */
    public void stopListener(){
        try {
            this.socket.close();
        }catch (NullPointerException e){

        }
        this.setKeepListening(false);
    }


    /**
     * This method converts the byte array into object MyRouter
     * @param bytes a byte[] representation of the object
     * @return returns the Router object
     */
    public   MyRouter convertByteArrayToObject(byte[] bytes){
        MyRouter object = new MyRouter();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput oin = null;
        try{
            oin = new ObjectInputStream(bis);
            object = (MyRouter) oin.readObject();

        }catch (IOException e){
        }catch (ClassNotFoundException e){
        }catch (Exception e){
        }
        finally {
            try {
                oin.close();
            } catch (IOException e) {
            }
        }
        return object;
    }


    @Override
    /**
     * This method runs in background and servers any available updates
     */
    public  void run() {
        while (isKeepListening()){
            byte[] recivedRouterByteArray = new byte[102400];
            DatagramPacket packet = new DatagramPacket(recivedRouterByteArray, recivedRouterByteArray.length);
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                this.keepListening = false;
                break;
            }
            MyRouter rcvRouter = this.convertByteArrayToObject(recivedRouterByteArray);
            if(rcvRouter != null) {
                this.router.updateDistanceVector(rcvRouter);
                if(this.router.getNbrs().size() > 0) {

                    // printing the routing table after every route update message
                    this.router.printRoutingTable();
                    this.router.dvchanged = false;
                }
            }

        }
    }

}
