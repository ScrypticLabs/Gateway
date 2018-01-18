/* 
* @File: SegueController.java
* @Author: Abhi Gupta
* 
* This class handles transitions between the login view, sign up view and home view (where the user talks to other people).
* All event handlers for the login, signup and home classes are implemented here to be able to create animated transitions
* as well as provide notifications about whether or not the user successfully signed up, logged in, etc.
*/

package sample;

import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;

public class SegueController {
    private Stage stage;        // the main stage of the app
    private Map panels;         // login, signup and home panels
    private Scene defaultScene; // the panel currently being rendered
    private Pane graph;         // the parent of all panel objects
    private TrayNotification notificationCenter;    


    /**
    * Constructor.
    * Sets the stage with the login view being the default scene and adds all event listeners
    */
    public SegueController(Scene main, TrayNotification notificationCenter, Pane scene, Stage stage, Group... panels) {
        this.notificationCenter = notificationCenter;

        defaultScene = main;
        graph = scene;
        graph.setMinSize(ViewController.SCREEN_WIDTH,ViewController.SCREEN_HEIGHT);
        graph.setMaxSize(ViewController.SCREEN_WIDTH,ViewController.SCREEN_HEIGHT);
        this.stage = stage;
        this.panels = new HashMap<String, Group>();
        for (Group panel : panels) {
            this.panels.put(panel+"", panel);
        }
        addLoginEventHandlers();
        addSignUpEventHandlers();
        stage.setScene(defaultScene);
    }

    // event handlers that would've been implemented in the login class
    private void addLoginEventHandlers() {
        Login login = (Login)panels.get("Login");
        Home home = (Home)panels.get("Home");
        listenToLoginButtonFromLogin(login, home);
        listenToSignUpButtonFromLogin(login);
    }

    // event handlers that would've been implemented in the signup class
    private void addSignUpEventHandlers() {
        SignUp signUp = (SignUp)panels.get("SignUp");
        Home home = (Home)panels.get("Home");
        listenToLoginButtonFromSignUp(signUp);
        listenToSignUpButtonFromSignUp(signUp, home);
    }

    // Login Events
    public <E extends Event> void listenToLoginButtonFromLogin(Login login, Home home) {
        login.loginButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (login.isValid()) {      // create a transition from the login pane to the home pane
                if (home.loginUser(login.getUserID(), login.getPassword())) {
                    Transition.dissolve(graph, login, home, 1);
                }
            }
        });
    }
    public <E extends Event> void listenToSignUpButtonFromLogin(Login login) {
        login.signUpButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            login.clearFields();     // create a transition from the login pane to the sign up pane
            Transition.translateDown(graph, login, (Node)panels.get("SignUp"), 0.5);
        });
    }

    // Sign Up Events
    public <E extends Event> void listenToLoginButtonFromSignUp(SignUp signUp) {
        signUp.loginButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            signUp.clearFields();     // create a transition from the signup pane to the login pane
            ((Group)panels.get("Login")).setTranslateY(10);
            Transition.translateUp(graph, signUp, (Node)panels.get("Login"), 0.5);
        });
    }

     // create a transition from the signup pane to the home pane with real time notifications about the status of the process
    public <E extends Event> void listenToSignUpButtonFromSignUp(SignUp signUp, Home home) {
        signUp.signUpButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (signUp.isValid()) {
                if (signUp.signUp()) {   // user is registered properly in database
                    if (home.loginUser(signUp.getUserID(), signUp.getPassword())) {
                        Transition.dissolve(graph, signUp, home, 1);
                        notificationCenter.setHeader("Signed up!");
                        notificationCenter.setMessage("You're connected with Gateway");
                        notificationCenter.showAndDismiss(Duration.seconds(2));
                    } else {
                        // notify the user that he wasn't signed up because of an exisiting account
                        notificationCenter.setHeader("Sign Up Failed!");
                        notificationCenter.setMessage("You already have an account.");
                        notificationCenter.showAndDismiss(Duration.seconds(2));
                    }
                } else {
                    notificationCenter.setHeader("Invalid Credentials!");
                    notificationCenter.setMessage("You must provide valid information.");
                    notificationCenter.showAndDismiss(Duration.seconds(2));
                }
            } else {        // notify the user that he wasn't signed up because of invalid credentials
                notificationCenter.setHeader("Invalid Credentials!");
                notificationCenter.setMessage("You must provide valid information.");
                notificationCenter.showAndDismiss(Duration.seconds(2));
            }
        });
    }
}
