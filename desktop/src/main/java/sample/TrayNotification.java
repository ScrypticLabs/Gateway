/* 
* @File: TrayNotification.java
* @Author: Abhi Gupta
* 
* This class represents the UI of the notification and allows users to set its content based on the tyep of notification
*/

package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.net.URL;

public final class TrayNotification {
    private Pane pane;
    private Text header;    // the bolded message - title
    private Text message;   // the message - less prominent
    private CustomStage stage;  // the stage on which the notification will be rendered
    private NotificationType notificationType;
    private AnimationType animationType;   
    private EventHandler<ActionEvent> onDismissedCallBack, onShownCallback;
    private TrayAnimation animator;
    private AnimationProvider animationProvider;

    /**
     * Initializes an instance of the tray notification object
     * @param title The title text to assign to the tray
     * @param body The body text to assign to the tray
     * @param img The image to show on the tray
     */
    public TrayNotification(String title, String body, Image img) {
        initTrayNotification(title, body, NotificationType.CUSTOM);
    }

    /**
     * Initializes an instance of the tray notification object
     * @param title The title text to assign to the tray
     * @param body The body text to assign to the tray
     * @param notificationType The notification type to assign to the tray
     */
    public TrayNotification(String title, String body, NotificationType notificationType ) {
        initTrayNotification(title, body, notificationType);
    }

    /**
     * Initializes an empty instance of the tray notification
     */
    public TrayNotification() {
        initTrayNotification("", "", NotificationType.CUSTOM);
    }

    private void initTrayNotification(String title, String message, NotificationType type) {
            initStage();
            initAnimations();
            setTray(title, message, type);
    }

    private void initAnimations() {
        animationProvider =
            new AnimationProvider(new FadeAnimation(stage), new SlideAnimation(stage), new PopupAnimation(stage));
        //Default animation type
        setAnimationType(AnimationType.SLIDE);
    }

    private void initStage() {
        pane = new Pane();
        pane.setPrefHeight(65);
        pane.setPrefWidth(300);
        pane.setId("NOTIFICATION");

        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("defaultScene.css");
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) { // this event handler can be used to make notifications interactive in the future
                System.out.println("mouse click detected on notification " + mouseEvent.getSource());
            }
        });

        // the background of the notification
        Rectangle rect = new Rectangle(0,0,300,65);
        rect.setArcHeight(10);
        rect.setArcWidth(10);
        rect.setFill(Paint.valueOf("#2D383E"));
        pane.getChildren().add(rect);

        // the icon shown on the notification
        Text icon = new Text(Icons.NOTIFICATION);
        icon.setFill(Paint.valueOf("#2ABC9D"));
        icon.setLayoutX(12);
        icon.setLayoutY(52);
        icon.setId("ICON");
        pane.getChildren().add(icon);

        // the message
        message = new Text("Hey, want to watch a movie?");
        message.getStyleClass().add("MESSAGE");
        message.setX(70);
        message.setY(48);
        message.setFill(Paint.valueOf("#989DA0"));

        // the bolded title
        header = new Text("Abhi Gupta");
        header.getStyleClass().add("HEADER");
        header.setX(70);
        header.setY(24);
        header.setFill(Colour.SEMI_WHITE);

        pane.getChildren().addAll(header, message);

        stage = new CustomStage(pane, StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setLocation(stage.getBottomRight());
    }

    public void setHeader(String text) {
        if (pane.getChildren().contains(header)) {
            pane.getChildren().remove(header);
            header.setText(text);
            pane.getChildren().add(header);
        }
    }

    public void setMessage(String text) {
        if (pane.getChildren().contains(message)) {
            pane.getChildren().remove(message);
            message.setText(text);
            pane.getChildren().add(message);
        }
    }

    public void setNotificationType(NotificationType nType) {
        notificationType = nType;
        URL imageLocation = null;
        String paintHex = null;

        switch (nType) {

            case INFORMATION:
                imageLocation = getClass().getResource("/info.png");
                paintHex = "#2C54AB";
                break;

            case NOTICE:
                imageLocation = getClass().getResource("/notice.png");
                paintHex = "#8D9695";
                break;

            case SUCCESS:
                imageLocation = getClass().getResource("/success.png");
                paintHex = "#009961";
                break;

            case WARNING:
                imageLocation = getClass().getResource("/warning.png");
                paintHex = "#E23E0A";
                break;

            case ERROR:
                imageLocation = getClass().getResource("/error.png");
                paintHex = "#CC0033";
                break;

            case CUSTOM:
                return;
        }
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setTray(String title, String message, NotificationType type) {
        setTitle(title);
        setNotificationType(type);
    }

    public void setTray(String title, String message, Image img, AnimationType animType) {
        setAnimationType(animType);
    }

    public boolean isTrayShowing() {
        return animator.isShowing();
    }

    /**
     * Shows and dismisses the tray notification
     * @param dismissDelay How long to delay the start of the dismiss animation
     */
    public void showAndDismiss(Duration dismissDelay) {
        if (isTrayShowing()) {
            dismiss();
        } else {
            stage.show();
            onShown();
            animator.playSequential(dismissDelay);
        }
        onDismissed();
    }

    /**
     * Displays the notification tray
     */
    public void showAndWait() {
        if (! isTrayShowing()) {
            stage.show();
            animator.playShowAnimation();
            onShown();
        }
    }

    /**
     * Dismisses the notifcation tray
     */
    public void dismiss() {
        if (isTrayShowing()) {
            animator.playDismissAnimation();
            onDismissed();
        }
    }

    private void onShown() {
        if (onShownCallback != null)
            onShownCallback.handle(new ActionEvent());
    }

    private void onDismissed() {
        if (onDismissedCallBack != null)
            onDismissedCallBack.handle(new ActionEvent());
    }

    /**
     * Sets an action event for when the tray has been dismissed
     * @param event The event to occur when the tray has been dismissed
     */
    public void setOnDismiss(EventHandler<ActionEvent> event) {
        onDismissedCallBack  = event;
    }

    /**
     * Sets an action event for when the tray has been shown
     * @param event The event to occur after the tray has been shown
     */
    public void setOnShown(EventHandler<ActionEvent> event) {
        onShownCallback  = event;
    }

    /**
     * Sets a title to the tray
     * @param txt The text to assign to the tray icon
     */
    public void setTitle(String txt) {
    }

    public String getTitle() {
        return "";
    }

    public String getMessage() {
        return "";
    }

    public void setAnimationType(AnimationType type) {
        animator = animationProvider.findFirstWhere(a -> a.getAnimationType() == type);
        animationType = type;
    }

    public AnimationType getAnimationType() {
        return animationType;
    }
}