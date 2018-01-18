/* 
* @File: Main.java
* @Author: Abhi Gupta
* 
* Gateway is a chat client application that allows users to talk with each other just as any other native chat app.
* You can create new conversations with others, talk to multiple people at once, and get yours messages delivered
* instantly with no latency. This application also features realtime notifications that allow users to be notified
* of any new messages or conversations during the run of the app. The entire UI and UX has been designed to optimize
* functionality and minimize complexity. If anything goes wrong during the run of the app, you will be notifed via
* notifications. All of the user's data is encoded as JSON and handled using HTTP POST requests (not GET) thereby proving
* to be as secure and safe as possible. Data encryption and transmission is no problem for Gateway. The heart of
* Gateway is being connected to everyone and everything, hence this program implements server to server connections with a
* Javascript server talking to a Java server, clients talking to other clients, and finally clients talking to servers
* asynchrounously. The biggest challenge of this project has been managing multiple threads and async blocks. Fortunately,
* this program's most unique feature is that Gateway itself is an AI chatbot. Trained specifically for this app, Gateway
* can provide answers to most questions, engage in small talk, make intelligent queries to the web in search on answers,
* and overall just get you the information that you are looking for.
*/

package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.parse4j.*;

public class Main extends Application {
    // Drawing Panels
    Canvas canvas;
    GraphicsContext gc;
    // JavaFX Graph
    Pane graph;
    Scene defaultScene;
    Group login, signUp, home, toolbar;
    Stage stage;
    // Scene Transition
    SegueController segueController;
    TrayNotification notificationCenter;


    @Override
    public void start(Stage primaryStage) throws Exception{
        loadFonts();
        Parse.initialize(App.ID, App.REST_API_ID);

        // Main Panels
        stage = primaryStage;
        canvas = new Canvas(ViewController.SCREEN_WIDTH, ViewController.SCREEN_HEIGHT);    // only foreground is rendered on root, as it is easier to organize the layout of its children
        gc = canvas.getGraphicsContext2D();                                                // only background is rendered on canvas, as it takes the back-most view by default

        notificationCenter = new TrayNotification();
        // Login Pane
        login = new Login();

        // Sign Up Pane
        signUp = new SignUp();
        // Home Pane
        home = new Home(notificationCenter);
        // Toolbar
        toolbar = new Toolbar();

        graph = new Pane();
        graph.getChildren().add(login);
        graph.getStyleClass().add("graph");

        // Segue Controller
        defaultScene = new Scene(graph, ViewController.SCREEN_WIDTH, ViewController.SCREEN_HEIGHT);
        defaultScene.setFill(new LinearGradient(1, 0, 1, 1, true,
                CycleMethod.REFLECT,
                new Stop(0.0, Colour.DARK_GRAY),
                new Stop(1.0, Colour.LIGHT_GRAY)));
        defaultScene.getStylesheets().add("defaultScene.css");

        segueController = new SegueController(defaultScene, notificationCenter, graph, stage,  home, login, signUp);

        // Window Settings
        stage.setTitle("Gateway");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    public void loadFonts() {
        Font.loadFont(getClass()
                .getResourceAsStream("/fonts/avenirnext-ultralight.ttf"), 10);
        Font.loadFont(getClass()
                .getResourceAsStream("/fonts/avenirnext-regular.ttf"), 10);
        Font.loadFont(getClass()
                .getResourceAsStream("/fonts/avenirnext-demibold.ttf"), 10);
        Font.loadFont(getClass()
                .getResourceAsStream("/fonts/avenirnextmedium.ttf"), 10);
        Font.loadFont(getClass()
                .getResourceAsStream("/fonts/gateway.ttf"), 10);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
