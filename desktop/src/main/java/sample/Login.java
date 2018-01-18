/* 
* @File: Login.java
* @Author: Abhi Gupta
* 
* This class handles the login view of the app (its UI and functionality)
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class Login extends Group implements ViewController {
    private TextField userID;
    private PasswordField password;
    protected Button loginButton, signUpButton;
    private final int SHIFT_FACTOR = 10;            // value by which all nodes are shifted in the y-direction

    public Login() {
        super();
        createIcons();
        createTextFields();
        createLabels();
        createButtons();
        setLayoutX(0); setLayoutY(0);
    }

    public void createTextFields() {
        // User ID
        userID = new TextField();
        userID.setPromptText("Email Account");
        userID.getStyleClass().add("login-field");
        userID.setLayoutX(247);
        userID.setLayoutY(217+SHIFT_FACTOR);
        Line idLine = new Line(190, 260+SHIFT_FACTOR, 540, 260+SHIFT_FACTOR);      // line below the field
        idLine.setStroke(Colour.UNDERLINE);
        // Default Text Event Listener
        userID.styleProperty().bind(
                Bindings
                        .when(userID.focusedProperty())
                        .then("-fx-prompt-text-fill: transparent;")
                        .otherwise("-fx-prompt-text-fill: #2ABC9D;"));
        // Password
        password = new PasswordField();                 // put a password field here
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
        getChildren().addAll(userID,idLine,password,passLine);
    }

    public void createIcons() {
        // Logo
        Group box = new Group();                // this dummy box and logo will keep the real logo bound to its static location
        box.setLayoutY(110+SHIFT_FACTOR); box.setLayoutX(267);
        Text logo = new Text("\uD83C\uDFB6");
        logo.setX(0); logo.setY(170+SHIFT_FACTOR);
        logo.getStyleClass().add("logo-shadow");
        logo.setFill(Colour.SHADOW);

        Text shadow = new Text(Icons.LOGO);
        shadow.setX(3); logo.setY(25+SHIFT_FACTOR);
        shadow.getStyleClass().add("logo");
        shadow.setFill(Color.TRANSPARENT);

        Text real = new Text(Icons.LOGO);
        real.setX(3); real.setY(20+SHIFT_FACTOR);
        real.getStyleClass().add("logo");
        real.setFill(Colour.LIGHT_GREEN);

        box.getChildren().addAll(logo, shadow, real);

        Text mail = new Text(Icons.EMAIL);
        mail.setX(197); mail.setY(254+SHIFT_FACTOR);
        mail.setFill(Colour.LIGHT_GREEN);
        mail.getStyleClass().add("home-icon");

        Text lock = new Text(Icons.PASSWORD);
        lock.setX(197); lock.setY(254+55+SHIFT_FACTOR);
        lock.setFill(Colour.LIGHT_GREEN);
        lock.getStyleClass().add("home-icon");

        getChildren().addAll(box, mail, lock);
    }

    public void createButtons() {
        // Login Button
        loginButton = new Button("LOGIN");
        loginButton.setLayoutX(209);
        loginButton.setLayoutY(378+SHIFT_FACTOR);
        loginButton.getStyleClass().add("login-button");
        loginButton.setPrefSize(310,44);
        // Sign Up Button
        signUpButton = new Button("Sign Up.");
        signUpButton.setLayoutX(426);
        signUpButton.setLayoutY(452+SHIFT_FACTOR);
        signUpButton.getStyleClass().add("sign-up-button");

        getChildren().addAll(loginButton,signUpButton);

    }

    public void createLabels() {
        // Sign up
        Label signUp = new Label("Don't have an account yet?");
        signUp.getStyleClass().add("signUpNotifier");
        signUp.setLayoutX(220); // 220
        signUp.setLayoutY(460+SHIFT_FACTOR);
        getChildren().add(signUp);
    }

    // returns whether or not the userID and password fields have been filled in
    public boolean isValid() {
        return !userID.getText().equals("") && !password.getText().equals("");
    }

    // allows an event handler to be implemented for this class in another class 
    public <E extends Event> void addCodeHandler(EventType<E> eventType, EventHandler<E> handler) {
        loginButton.addEventHandler(eventType, handler);
    }

    public String getUserID() { return userID.getText(); }
    public String getPassword() { return password.getText(); }

    public void clearFields() {
        userID.clear();
        password.clear();
    }

    @Override
    public String toString() {
        return "Login";
    }

}
