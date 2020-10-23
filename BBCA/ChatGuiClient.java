package BBCA3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

/**
 * For Java 8, javafx is installed with the JRE. You can run this program normally.
 * For Java 9+, you must install JavaFX separately: https://openjfx.io/openjfx-docs/
 * If you set up an environment variable called PATH_TO_FX where JavaFX is installed
 * you can compile this program with:
 *  Mac/Linux:
 *      > javac --module-path $PATH_TO_FX --add-modules javafx.controls day10_chatgui/ChatGuiClient.java
 *  Windows CMD:
 *      > javac --module-path %PATH_TO_FX% --add-modules javafx.controls day10_chatgui/ChatGuiClient.java
 *  Windows Powershell:
 *      > javac --module-path $env:PATH_TO_FX --add-modules javafx.controls day10_chatgui/ChatGuiClient.java
 *
 * Then, run with:
 *
 *  Mac/Linux:
 *      > java --module-path $PATH_TO_FX --add-modules javafx.controls day10_chatgui.ChatGuiClient
 *  Windows CMD:
 *      > java --module-path %PATH_TO_FX% --add-modules javafx.controls day10_chatgui.ChatGuiClient
 *  Windows Powershell:
 *      > java --module-path $env:PATH_TO_FX --add-modules javafx.controls day10_chatgui.ChatGuiClient
 *
 * There are ways to add JavaFX to your to your IDE so the compile and run process is streamlined.
 * That process is a little messy for VSCode; it is easiest to do it via the command line there.
 * However, you should open  Explorer -> Java Projects and add to Referenced Libraries the javafx .jar files
 * to have the syntax coloring and autocomplete work for JavaFX
 */

class ServerInfo {
    public final String serverAddress;
    public final int serverPort;

    public ServerInfo(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
}

public class ChatGuiClient extends Application {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Stage stage;
    private TextArea messageArea;
    private TextField textInput;
    private Button sendButton;
    private TextArea userList;
    private Button muteButton;
    private Button unmuteButton;

    private ServerInfo serverInfo;
    //volatile keyword makes individual reads/writes of the variable atomic
    // Since username is accessed from multiple threads, atomicity is important
    private volatile String username = "";
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //If ip and port provided as command line arguments, use them
        List<String> args = getParameters().getUnnamed();
        if (args.size() == 2){
            this.serverInfo = new ServerInfo(args.get(0), Integer.parseInt(args.get(1)));
        }
        else {
            //otherwise, use a Dialog.
            Optional<ServerInfo> info = getServerIpAndPort();
            if (info.isPresent()) {
                this.serverInfo = info.get();
            }
            else{
                Platform.exit();
                return;
            }
        }

        this.stage = primaryStage;
        BorderPane borderPane = new BorderPane();

        userList = new TextArea();
        userList.setWrapText(true);
        userList.setEditable(false);
        borderPane.setCenter(userList);

        messageArea = new TextArea();
        messageArea.setWrapText(true);
        messageArea.setEditable(false);
        borderPane.setLeft(messageArea);

        //At first, can't send messages - wait for WELCOME!
        textInput = new TextField();
        textInput.setEditable(false);
        textInput.setOnAction(e -> sendMessage());
        sendButton = new Button("Send");
        sendButton.setDisable(true);
        sendButton.setOnAction(e -> sendMessage());
        muteButton = new Button("Mute");
        muteButton.setDisable(true);
        muteButton.setOnAction(e -> mute());
        unmuteButton = new Button("Unmute");
        unmuteButton.setDisable(true);
        unmuteButton.setOnAction(e -> unmute());

        HBox hbox = new HBox();
        hbox.getChildren().addAll(new Label("Message: "), textInput, sendButton, muteButton, unmuteButton);
        HBox.setHgrow(textInput, Priority.ALWAYS);
        borderPane.setBottom(hbox);


        Scene scene = new Scene(borderPane, 800, 500);
        stage.setTitle("Chat Client");
        stage.setScene(scene);
        stage.show();



        ServerListener socketListener = new ServerListener();

        //Handle GUI closed event
        stage.setOnCloseRequest(e -> {
            out.println("QUIT");
            socketListener.appRunning = false;
            try {
                socket.close();
            } catch (IOException ex) {}
        });

        new Thread(socketListener).start();
    }

    private void sendMessage() {
        String message = textInput.getText().trim();
        if (message.length() == 0)
            return;
        textInput.clear();
        if (message.startsWith("@")){
            String[] list = message.split(" ");
            int numUsers = 0;
            int index = 0;
            for (int i = 0; i < list.length; i++){
                if (list[i].startsWith("@")){
                    numUsers++;
                    index += list[i].length();
                }
                else {
                    break;
                }
            }
            String chat = message.substring(index+1);
            for (int i = 0; i < numUsers; i++){
                out.println(String.format("PCHAT %s %s", list[i].substring(1), chat));
            }
        }
        else{
            out.println("CHAT " + message);
        }
    }

    private void mute(){
        String name = muteWindow();
        out.println(String.format("MUTE %s", name));

    }

    private String muteWindow() {

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Enter Chat Name");
        nameDialog.setHeaderText("Please enter your username you would like to mute.");
        nameDialog.setContentText("Name: ");

        Optional<String> name = nameDialog.showAndWait();
        return name.get().trim();

        /*// Create a custom dialog for server ip / port
        Dialog<String> muteUserList = new Dialog<>();
        muteUserList.setTitle("Mute Usernames List");
        muteUserList.setHeaderText("Type who you would like to mute: ");

        // Set the button types.
        ButtonType connectButtonType = new ButtonType("Connect", ButtonData.OK_DONE);
        muteUserList.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        // Create the ip and port labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField textbox = new TextField();
        textbox.setPromptText("e.g. localhost, 127.0.0.1");
        grid.add(new Label("Username:"), 0, 0);
        grid.add(textbox, 1, 0);

        // Enable/Disable connect button depending on whether a address/port was entered.
        Node connectButton = muteUserList.getDialogPane().lookupButton(connectButtonType);
        connectButton.setDisable(true);

        muteUserList.getDialogPane().setContent(grid);

        textbox.textProperty().addListener((observable, oldValue, newValue) -> {
            connectButton.setDisable(newValue.trim().isEmpty());
        });

        return muteUserList.showAndWait();*/
    }

    private void unmute(){
        out.println("UNMUTE");
        unmuteButton.setDisable(true);
    }

    private Optional<ServerInfo> getServerIpAndPort() {
        // In a more polished product, we probably would have the ip /port hardcoded
        // But this a great way to demonstrate making a custom dialog
        // Based on Custom Login Dialog from https://code.makery.ch/blog/javafx-dialogs-official/

        // Create a custom dialog for server ip / port
        Dialog<ServerInfo> getServerDialog = new Dialog<>();
        getServerDialog.setTitle("Enter Server Info");
        getServerDialog.setHeaderText("Enter your server's IP address and port: ");

        // Set the button types.
        ButtonType connectButtonType = new ButtonType("Connect", ButtonData.OK_DONE);
        getServerDialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        // Create the ip and port labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ipAddress = new TextField();
        ipAddress.setPromptText("e.g. localhost, 127.0.0.1");
        grid.add(new Label("IP Address:"), 0, 0);
        grid.add(ipAddress, 1, 0);

        TextField port = new TextField();
        port.setPromptText("e.g. 54321");
        grid.add(new Label("Port number:"), 0, 1);
        grid.add(port, 1, 1);


        // Enable/Disable connect button depending on whether a address/port was entered.
        Node connectButton = getServerDialog.getDialogPane().lookupButton(connectButtonType);
        connectButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        ipAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            connectButton.setDisable(newValue.trim().isEmpty());
        });

        port.textProperty().addListener((observable, oldValue, newValue) -> {
            // Only allow numeric values
            if (! newValue.matches("\\d*"))
                port.setText(newValue.replaceAll("[^\\d]", ""));

            connectButton.setDisable(newValue.trim().isEmpty());
        });

        getServerDialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> ipAddress.requestFocus());


        // Convert the result to a ServerInfo object when the login button is clicked.
        getServerDialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                return new ServerInfo(ipAddress.getText(), Integer.parseInt(port.getText()));
            }
            return null;
        });

        return getServerDialog.showAndWait();
    }

    private String getName(){
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Enter Chat Name");
        nameDialog.setHeaderText("Please enter your username.");
        nameDialog.setContentText("Name: ");

        while(username.equals("")) {
            Optional<String> name = nameDialog.showAndWait();
            if (!name.isPresent() || name.get().trim().equals(""))
                nameDialog.setHeaderText("You must enter a nonempty name: ");
            else if (name.get().trim().contains(" "))
                nameDialog.setHeaderText("The name must have no spaces: ");
            else
                username = name.get().trim();
        }
        return username;
    }

    class ServerListener implements Runnable {

        volatile boolean appRunning = false;

        public void run() {
            try {
                // Set up the socket for the Gui
                socket = new Socket(serverInfo.serverAddress, serverInfo.serverPort);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                appRunning = true;
                //Ask the gui to show the username dialog and update username
                //Send to the server
                Platform.runLater(() -> {
                    out.println("NAME " + getName());
                    out.println("LIST");
                });

                //handle all kinds of incoming messages
                String incoming = "";
                while (appRunning && (incoming = in.readLine()) != null) {
                    if (incoming.startsWith("WELCOME")) {
                        String user = incoming.substring(8);
                        //got welcomed? Now you can send messages!
                        if (user.equals(username)) {
                            Platform.runLater(() -> {
                                stage.setTitle("Chatter - " + username);
                                textInput.setEditable(true);
                                sendButton.setDisable(false);
                                muteButton.setDisable(false);
                                messageArea.appendText("Welcome to the chatroom, " + username + "!\n");
                            });
                        }
                        else {
                            Platform.runLater(() -> {
                                messageArea.appendText(user + " has joined the chatroom.\n");
                            });
                        }

                    } else if (incoming.equals("UNMUTE")){
                        Platform.runLater(() -> {
                            messageArea.appendText("You are unmuted. You can talk!\n");
                        });
                    } else if (incoming.startsWith("MUTE ")){
                        String name = incoming.substring(5);
                        Platform.runLater(() -> {
                            messageArea.appendText("You are muted by " + name + ".\n");
                        });
                        unmuteButton.setDisable(false);
                    } else if (incoming.startsWith("MUTED")) {
                        Platform.runLater(() -> {
                            messageArea.appendText("You are muted. Click unmute button to unmute yourself.\n");
                        });
                    } else if (incoming.startsWith("CHAT")) {
                        int split = incoming.indexOf(" ", 5);
                        String user = incoming.substring(5, split);
                        String msg = incoming.substring(split + 1);

                        Platform.runLater(() -> {
                            messageArea.appendText(user + ": " + msg + "\n");
                        });
                    } else if(incoming.startsWith("PCHAT")){
                        incoming = incoming.substring(6);
                        int index = incoming.indexOf(" ");
                        String user = incoming.substring(0, index);
                        String msg = incoming.substring(index+1);

                        Platform.runLater(() -> {
                            messageArea.appendText(user + " (private): " + msg + "\n");
                        });
                    }
                    else if (incoming.startsWith("EXIT")) {
                        String user = incoming.substring(5);
                        Platform.runLater(() -> {
                            messageArea.appendText(user + "has left the chatroom.\n");
                        });
                    }
                    else if (incoming.startsWith("LIST")){
                        incoming = incoming.substring(6);
                        String[] users = incoming.split(", ");
                        String list = "";
                        for (int i = 0; i < users.length; i++){
                            list += users[i] + "\n";
                        }
                        String str = list;
                        Platform.runLater(() -> {
                            userList.setText(str);
                        });
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (Exception e) {
                if (appRunning)
                    e.printStackTrace();
            }
            finally {
                Platform.runLater(() -> {
                    stage.close();
                });
                try {
                    if (socket != null)
                        socket.close();
                }
                catch (IOException e){
                }
            }
        }
    }
}