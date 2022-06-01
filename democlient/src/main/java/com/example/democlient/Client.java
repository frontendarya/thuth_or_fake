package com.example.democlient;

//import java.awt.TextArea;
import java.io.*;
import java.net.*;
import java.util.Random;

import com.shared.dto.Message;
import com.shared.dto.MessageType;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Client extends Application {

    ObjectOutputStream toServer = null;
    ObjectInputStream fromServer = null;
    Socket socket;
    String  myName;
    ObservableList<String> usersList = FXCollections.observableArrayList("List of users");

    GridPane gridpane = new GridPane();

    Image image = new Image("C:/Users/work/IdeaProjects/democlient/src/main/java/com/example/democlient/logo.png", 100, 80, false, false);

    TextField field = new TextField();

    Button button_connect = new Button();

    TextArea area = new TextArea();

    TextField field_chat = new TextField();

    Button button_choose = new Button();

    TextArea area_from_server = new TextArea();

    Button button_send_result = new Button();

    ListView<String> list_users = new ListView<String>(usersList);


    Scene scene = new Scene(gridpane, 1000, 400);

    @Override
    public void start (Stage primaryStage) throws Exception {
        gridpane.setPadding(new Insets(20));
        gridpane.setHgap(25);
        gridpane.setVgap(15);
        //gridpane.add(new Label("List of users: "), 2, 0); // столбец=2 строка=0

        gridpane.add(new ImageView(image),0, 0);

        field.setPrefSize(700, 30);
        field.setPromptText("Enter your name");
        gridpane.add(field, 1, 0);

        button_connect.setPrefSize(550, 30);
        button_connect.setText("Connect");
        gridpane.add(button_connect, 2, 0);

        area.setEditable(false);
        area.setPrefSize(700, 30);
        gridpane.add(area, 0, 1, 1, 1);

        field_chat.setPromptText("Enter message to chat");
        gridpane.add(field_chat, 0, 3);

        button_choose.setPrefSize(550, 30);
        button_choose.setText("Choose action");
        gridpane.add(button_choose, 2, 1);

        area_from_server.setEditable(false);
        gridpane.add(area_from_server, 2, 2);

        button_send_result.setPrefSize(550, 30);
        button_send_result.setText("Send action to chat");
        gridpane.add(button_send_result, 2, 3);

        list_users.setPrefSize(550, 150);
        gridpane.add(list_users, 3, 0);

        GridPane.setColumnSpan(area, 2);
        GridPane.setRowSpan(area, 2);
        GridPane.setColumnSpan(field_chat, 2);
        GridPane.setRowSpan(list_users, 4);
        primaryStage.setTitle("Truth or Fake");
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            socket = new Socket("localhost", 8182);
            toServer = new ObjectOutputStream(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            area.appendText(e.toString() + '\n');
        }

        button_connect.setOnAction(e -> {
            try {
                Message message = Message.broadcast(field.getText());

                toServer.writeObject(message);
                Message name = (Message)fromServer.readObject();
                area.appendText("Hi! " + name.getPayload() + '\n');
                field.clear();

                String c = name.getPayload()+(list_users.getItems().size()+1);
                list_users.getItems().add(c);
                list_users.scrollTo(list_users.getItems().size() - 1);

                new AnimationTimer() {
                    int frameCount = 0 ;
                    @Override
                    public void handle(long now) {
                        frameCount++ ;
                        if (frameCount > 1) {
                            list_users.edit(list_users.getItems().size() - 1);
                            stop();
                        }
                    }

                }.start();
            } catch (Exception e2) {
                area.appendText(e2.toString() + '\n');
            }
        });

        button_choose.setOnAction(e -> {
            try {
                Message message = Message.single(field_chat.getText());

                toServer.writeObject(message);
                Message action = (Message)fromServer.readObject();
                area_from_server.appendText(action.getPayload() + '\n');
            } catch (Exception e2) {
                area_from_server.appendText(e2.toString() + '\n');
            }
        });

        button_send_result.setOnAction(e -> {
            try {
                Message message = Message.broadcast(area_from_server.getText());
                toServer.writeObject(message);
                Message result = (Message)fromServer.readObject();
                area.appendText(result.getPayload() + '\n');
            } catch (Exception e2) {
                area.appendText(e2.toString() + '\n');
            }
        });

        field_chat.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                try {
                    Message message = Message.broadcast(area.getText());
                    toServer.writeObject(message);
                    Message result = (Message)fromServer.readObject();
                    area.appendText(result.getPayload() + '\n');
                    field_chat.clear();
                } catch (Exception e2) {
                    area.appendText(e2.toString() + '\n');
                }
            }
        } );

    }
    @Override
    public void stop(){
        try {
            Message message = Message.broadcast(field.getText());

            toServer.writeObject(message);
            Message name = (Message)fromServer.readObject();
            area.appendText("Bye( " + name.getPayload() + '\n');

            String c = name.getPayload()+(list_users.getItems().size()+1);
            list_users.getItems().add(c);
            list_users.scrollTo(list_users.getItems().size() - 1);

            new AnimationTimer() {
                int frameCount = 0 ;
                @Override
                public void handle(long now) {
                    frameCount++ ;
                    if (frameCount > 1) {
                        list_users.edit(list_users.getItems().size() - 1);
                        stop();
                    }
                }

            }.start();
        } catch (Exception e2) {
            area.appendText(e2.toString() + '\n');
        }
    }

    public static void main (String[] args) {
        Application.launch(args);
    }

}
