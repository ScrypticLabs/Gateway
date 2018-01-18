/* 
* @File: SignUp.java
* @Author: Abhi Gupta
* 
* This class handles the sign up view of the app (its UI and functionality)
*/

package sample;

import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.parse4j.ParseUser;
import java.util.HashMap;

public class SignUp extends Group implements ViewController {
    private TextField userID, name;
    private PasswordField password;
    protected Button loginButton, signUpButton;
    private ParseUser user;
    private final int SHIFT_FACTOR = 0; // value by which all nodes are shifted in the y-direction


    public SignUp() {
        super();
        user = new ParseUser();
        // Basic Setup
        createIcons();
        createTextFields();
        createLabels();
        createButtons();
        setLayoutX(0); setLayoutY(0);

    }

    public void createTextFields() {
        Text dummy = new Text("Connect with Gateway.");
        dummy.setY(60); dummy.setX(212);
        dummy.setFill(Colour.LIGHT_GREEN);
        dummy.getStyleClass().add("welcome-message");
        // Name
        name = new TextField();
        name.setPromptText("Name");
        name.getStyleClass().add("login-field");
        name.setLayoutX(247);
        name.setLayoutY(155+SHIFT_FACTOR);
        Line nameLine = new Line(190, 155+43+SHIFT_FACTOR, 540, 155+43+SHIFT_FACTOR);    // line below the field
        nameLine.setStroke(Colour.UNDERLINE);
        // Default Text Event Listener
        name.styleProperty().bind(
                Bindings
                        .when(name.focusedProperty())
                        .then("-fx-prompt-text-fill: transparent;")
                        .otherwise("-fx-prompt-text-fill: #2ABC9D;"));
        // User ID
        userID = new TextField();
        userID.setPromptText("Email Account");
        userID.getStyleClass().add("login-field");
        userID.setLayoutX(247);
        userID.setLayoutY(217+SHIFT_FACTOR);
        Line idLine = new Line(190, 260+SHIFT_FACTOR, 540, 260+SHIFT_FACTOR);
        idLine.setStroke(Colour.UNDERLINE);
        // Default Text Event Listener
        userID.styleProperty().bind(
                Bindings
                        .when(userID.focusedProperty())
                        .then("-fx-prompt-text-fill: transparent;")
                        .otherwise("-fx-prompt-text-fill: #2ABC9D;"));
        // Password
        password = new PasswordField();
        password.setPromptText("Password");
        password.getStyleClass().add("login-field");
        password.setLayoutX(247);
        password.setLayoutY(274+SHIFT_FACTOR);
        Line passLine = new Line(190, 317+SHIFT_FACTOR, 540, 317+SHIFT_FACTOR);
        passLine.setStroke(Colour.UNDERLINE);
        // Default Text Event Listener
        password.styleProperty().bind(
                Bindings
                        .when(password.focusedProperty())
                        .then("-fx-prompt-text-fill: transparent;")
                        .otherwise("-fx-prompt-text-fill: #2ABC9D;"));
        getChildren().addAll(dummy, name,nameLine,userID,idLine,password,passLine);
    }

    public void createIcons() {
        Text profile = new Text(Icons.PROFILE);
        profile.setX(197); profile.setY(188+SHIFT_FACTOR);
        profile.setFill(Colour.LIGHT_GREEN);
        profile.getStyleClass().add("home-icon");

        Text mail = new Text(Icons.EMAIL);
        mail.setX(197); mail.setY(254+SHIFT_FACTOR);
        mail.setFill(Colour.LIGHT_GREEN);
        mail.getStyleClass().add("home-icon");

        Text lock = new Text(Icons.PASSWORD);
        lock.setX(197); lock.setY(254+55+SHIFT_FACTOR);
        lock.setFill(Colour.LIGHT_GREEN);
        lock.getStyleClass().add("home-icon");

        getChildren().addAll(profile, mail, lock);

    }

    public void createButtons() {
        // Sign Up Button
        signUpButton = new Button("SIGN UP");
        signUpButton.setLayoutX(209);
        signUpButton.setLayoutY(378+SHIFT_FACTOR);
        signUpButton.getStyleClass().add("login-button");
        signUpButton.setPrefSize(310,44);
        // Login Button
        loginButton = new Button("Login.");
        loginButton.setLayoutX(426);
        loginButton.setLayoutY(472-10+SHIFT_FACTOR);
        loginButton.getStyleClass().add("sign-up-button");

        getChildren().addAll(loginButton,signUpButton);

    }

    public void createLabels() {
        // Sign up
        Label login = new Label("Already have an account?");
        login.getStyleClass().add("signUpNotifier");
        login.setLayoutX(229); // 229
        login.setLayoutY(480-10+SHIFT_FACTOR);
        getChildren().add(login);
    }

    // allows an event handler to be implemented for this class in another class 
    public <E extends Event> void addCodeHandler(EventType<E> eventType, EventHandler<E> handler) {
        loginButton.addEventHandler(eventType, handler);
    }

    // returns whether or not the userID and password fields have been filled in

    public boolean isValid() {
        return !userID.getText().equals("") && !password.getText().equals("") && !name.getText().equals("");
    }

    // Sign Up Parse dependency doesn't work so uses the Http REST API to sign up a user with custom code written on the back end to handle the request
    public boolean signUp() {
        user.setEmail(userID.getText());
        user.setUsername(userID.getText());
        user.setPassword(password.getText());
       return HttpClient.signUpUser(new HashMap<String, String>() {
            {
                put("firstName", name.getText().split(" ")[0]);
                put("lastName", name.getText().split(" ")[1]);
                put("password", password.getText());
                put("email", userID.getText());
            }
        });
    }

    public void clearFields() {
        userID.clear();
        password.clear();
        name.clear();
    }

    public String getUserID() { return userID.getText(); }
    public String getPassword() { return password.getText(); }

    @Override
    public String toString() {
        return "SignUp";
    }
}
