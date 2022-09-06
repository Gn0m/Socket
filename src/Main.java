
import Socket.Client;
import Socket.Server;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Thread serverThread = new Thread(() -> new Server("127.0.0.1", 1024).startServer());
        serverThread.start();
        Thread.sleep(1000);
        serverThread.join();


        Thread thread = new Thread(() -> new Client("127.0.0.1", 1024).startClient());
        thread.start();
        thread.join();

      
    }
}
