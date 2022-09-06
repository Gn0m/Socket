package Socket;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Client {

    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedReader in;
    private BufferedWriter out;
    private final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private String ip;
    private int port;
    private boolean checkAuth = true;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void startClient() {
        String inValue;
        String[] inArr;
        createLogger();
        try {
            clientSocket = new Socket(ip, port);
            reader = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String connected = in.readLine();
            String[] con = connected.split("-");

            if (con[0].equals("long")) {

                outStrArr(con[1], con[2]);

            } else if (connected.equals("done")) {

                while (checkAuth) {

                    outLogAndPas();

                    inValue = in.readLine();
                    inArr = inValue.split("-");


                    if (inArr[0].equals("0")) {
                        System.out.println(inArr[1]);
                        checkAuth = false;

                        while (!clientSocket.isClosed()) {

                            outNextOrExit();

                            inValue = in.readLine();
                            inArr = inValue.split("-");

                            if (inArr[0].equals("1")) {

                                outStrArr(inArr);

                            } else if (inArr[0].equals("2")) {

                                outStrArr(inArr);

                                break;
                            } else if (inArr[0].equals("3")) {

                                outStrArr(inArr);

                                break;
                            }
                        }
                    } else if (inArr[0].equals("1")) {

                        outStrArr(inArr);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createLogger() {
        LOGGER.setLevel(Level.INFO);
        ConsoleHandler ch = new ConsoleHandler();
        SimpleFormatter sf = new SimpleFormatter();
        ch.setFormatter(sf);
    }

    private String hashPassword(String password) {
        StringBuilder str = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] data = digest.digest(password.getBytes());

            for (byte b : data) {
                str.append(String.format("%02X", b));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    private void outLogAndPas() throws IOException {
        StringBuilder str = new StringBuilder();
        System.out.println("Введите логин: ");
        String login = reader.readLine();
        System.out.println("Введите пароль: ");
        String password = reader.readLine();

        password = hashPassword(password);

        str.append(login).append("-").append(password);

        out.write(str + "\n");
        out.flush();
    }

    private void outNextOrExit() throws IOException {
        System.out.println("--- Введите next чтобы получить цитату или exit для выхода. ---");
        String word = reader.readLine();
        LOGGER.info("Введёное значение: " + word);
        out.write(word + "\n");
        out.flush();
    }

    private void outStrArr(String[] inArr) {
        LOGGER.info(inArr[1]);
        System.out.println(inArr[1]);
    }

    private void outStrArr(String logStr, String str) {
        LOGGER.info(logStr);
        System.out.println(str);
    }

}
