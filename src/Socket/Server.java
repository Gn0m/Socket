package Socket;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    private Socket clientSocket;
    private ServerSocket server;
    private final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(5);
    private ConnectionCount connectionCount = new ConnectionCount();
    private BufferedWriter out;
    private String ip;
    private int port;

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void startServer() {

        Runnable serverTask = () -> {
            try {
                server = new ServerSocket(port, 50, Inet4Address.getByName(ip));

                while (true) {
                    clientSocket = server.accept();
                    connectionCount.increment();
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    if (connectionCount.getConnectionCount() >= 3) {

                        StringBuilder stringBuilder = new StringBuilder();

                        stringBuilder.append("long").append("-")
                                .append("Отключён, превышено количество подключений")
                                .append("-").append("Попробуйте позже");

                        out.write(stringBuilder + "\n");
                        out.flush();

                    } else {

                        out.write("done" + "\n");
                        out.flush();
                        clientProcessingPool.submit(new ClientTask(clientSocket, connectionCount));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (clientSocket != null && !clientSocket.isClosed()) {
                        clientSocket.close();
                    }

                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }
}
