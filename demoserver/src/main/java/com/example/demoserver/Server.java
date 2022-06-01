package com.example.demoserver;

import java.io.*;
import java.net.*;
import java.util.*;

import com.shared.dto.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Server extends Application {

    private int clientNo = 0;
    private TextArea ta = new TextArea();
    String[]  roles  = new String[] {"Правда, думай сам)",
            "Ложь! Скажи, что ты никогда не прогуливал универ",
            "Ложь! Скажи, что у тебя аллергия на чёрную икру",
            "Ложь! Скажи, что ты не ел яблок",
            "Ложь! Скажи, что у тебя шесть пальцев на ноге",
            "Ложь! Скажи, что в детстве ты хотел сменить пол"};

    private ArrayList<ThreadClient> clientsArray = new ArrayList<ThreadClient>();

    @Override
    public void start (Stage primaryStage) throws Exception {
        ta.setEditable(false);

        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Server");
        primaryStage.show();

        new Thread( () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8182);
                Platform.runLater( () -> {
                    ta.appendText("Server started time " + new Date() + '\n');
                });

                while (true) {
                    Socket socket = serverSocket.accept();
                    clientNo++;
                    ThreadClient client = new ThreadClient(socket);
                    clientsArray.add(client);

                    Platform.runLater( () -> {
                        ta.appendText("Starting client " + clientNo + " at " + new Date() + '\n');
                        ta.appendText("Client " + clientNo + " IP Address is " + socket.getInetAddress().getHostAddress() + '\n');
                    });

                    new Thread(client).start();
                }
            } catch (Exception e) {
                ta.appendText(e.toString() + '\n');
            }
        }).start();
    }

    class ThreadClient implements Runnable {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket;

        ThreadClient(Socket socket) throws IOException {
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        }
            @Override
            public void run () {
                while (true) {
                    try {
                        Message message = (Message) in.readObject();
                        if (message.getMsgType() == MessageType.BROADCAST) {
                            sendToAll(message);
                        }
                        if (message.getMsgType() == MessageType.SINGLE) {
                            String msg = sendToOne();
                            Message m = Message.single(msg);
                            write(m);
                        }

                        Platform.runLater(() -> {
                            ta.appendText("Text received from client: " + message + '\n');
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            public void write (Object obj){
                try {
                    out.writeObject(obj);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    public String sendToOne () {
        int rnd = new Random().nextInt(roles.length);
        return roles[rnd];
    }

        public void sendToAll(Object message) {
            for (ThreadClient client : clientsArray)
                client.write(message);
        }


        public static void main(String[] args) {
            Application.launch(args);
        }
}