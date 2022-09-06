package Socket;

public class ConnectionCount {

    private int connectionCount = 0;

    public int getConnectionCount() {
        return connectionCount;
    }


    public synchronized void increment(){
        connectionCount++;
    }

    public synchronized void decrement(){
        connectionCount--;
    }
}
