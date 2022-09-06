package Socket;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ClientTask implements Runnable {

    private final Socket clientSocket;
    private ConnectionCount connectionCount;
    private BufferedReader in;
    private BufferedWriter out;
    private final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private int count = 0;
    private String[] strings = {"Что нашим врагам нравится, то нам вредно!", "Воевать без нефти нельзя, " +
            "а кто имеет преимущество в деле нефти, тот имеет шансы на победу в грядущей войне", "Вот вам физиономия наших шахтёров." +
            " Это не просто рабочие, а герои.", "Мы имеем врагов внутренних. Мы имеем врагов внешних. " +
            "Об этом нельзя забывать, товарищи, ни на одну минуту.", "Мы отстали от передовых стран на 50-100 лет. " +
            "Мы должны пробежать это расстояние в десять лет. Либо мы сделаем это, либо нас сомнут.", "Троцкизм есть " +
            "передовой отряд контрреволюционной буржуазии.", "Сын за отца не отвечает."};
    private Map<String, String> map;
    private boolean authCheck = true;

    public ClientTask(Socket clientSocket, ConnectionCount connectionCount) {
        this.clientSocket = clientSocket;
        this.connectionCount = connectionCount;
        map = new HashMap<>();
        map.put("admin", "21232F297A57A5A743894A0E4A801FC3");
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            auth();

            while (true) {
                String word = in.readLine();
                count++;
                LOGGER.info("От клиента: " + word);

                if (word.equalsIgnoreCase("next") && count <= 5) {

                    outQuotation();

                } else if (word.equalsIgnoreCase("exit") || count > 5) {

                    exit();
                    break;

                } else {

                    incorrectInput();

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
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void auth() throws IOException {
        while (authCheck) {

            String str = in.readLine();
            String[] arr = str.split("-");

            for (String key : map.keySet()) {

                if (key.equals(arr[0]) && map.get(key).equals(arr[1])) {
                    out.write("0-Авторизованы" + "\n");
                    out.flush();
                    LOGGER.info("IP: " + clientSocket.getInetAddress() + ", LocalPort: " + clientSocket.getLocalPort()
                            + ", Port: " + clientSocket.getPort());
                    authCheck = false;
                } else {
                    out.write("1-Не верный логин/пароль" + "\n");
                    out.flush();
                    LOGGER.info("Не верный ввод логина/пароля");
                }
            }
        }
    }

    private void outQuotation() throws IOException {
        String s = strings[(int) (Math.random() * strings.length)] + " И.В.Сталин ";
        LOGGER.info("Цитата: " + s);
        out.write("1-" + s + "\n");
        out.flush();
    }

    private void exit() throws IOException {
        if (count > 5) {
            out.write("2-Клиент отключён" + "\n");
            out.flush();
            connectionCount.decrement();
            LOGGER.info("Клиент отключён " + clientSocket.getInetAddress() + ", LocalPort: " + clientSocket.getLocalPort()
                    + ", Port: " + clientSocket.getPort());
        } else {
            out.write("3-Клиент отключился" + "\n");
            out.flush();
            connectionCount.decrement();
            LOGGER.info("Клиент отключился " + clientSocket.getInetAddress() + ", LocalPort: " + clientSocket.getLocalPort()
                    + ", Port: " + clientSocket.getPort());
        }
    }

    private void incorrectInput() throws IOException {
        out.write("Не верный ввод" + "\n");
        out.flush();
        LOGGER.info("Не верный ввод значения для получения ответа");
    }
}
