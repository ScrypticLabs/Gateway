/* 
* @File: Transition.java
* @Author: Abhi Gupta
* 
* This class handles tha animations and transitions between two panes such as login to signup or signup to home ...
*/

package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Transition {

    // Moves the old pane to the left,right,up or down while the new pane replaces its position in the specified time
    private static void translate(Direction path, Pane graph, Node oldScene, Node newScene, double time) {
        final boolean HORIZONTAL = path == Direction.LEFT || path == Direction.RIGHT;
        final boolean POSITIVE = path == Direction.LEFT || path == Direction.UP;
        final double THRESHOLD = HORIZONTAL ? graph.getWidth() : graph.getHeight();
        if (!graph.getChildren().contains(newScene)) graph.getChildren().add(newScene);     // add the new pane
        // Starting params
        KeyFrame start = new KeyFrame(Duration.ZERO,
                new KeyValue(HORIZONTAL ? newScene.translateXProperty() : newScene.translateYProperty(), POSITIVE ? THRESHOLD : -THRESHOLD),
                new KeyValue(HORIZONTAL ? oldScene.translateXProperty() : oldScene.translateYProperty(), 0));
        // Ending params
        KeyFrame end = new KeyFrame(Duration.seconds(time),
                new KeyValue(HORIZONTAL ? newScene.translateXProperty() : newScene.translateYProperty(), 0),
                new KeyValue(HORIZONTAL ? oldScene.translateXProperty() : oldScene.translateYProperty(), POSITIVE ? -THRESHOLD : THRESHOLD));
        Timeline slide = new Timeline(start, end);
        slide.setOnFinished(e -> {
            graph.getChildren().remove(oldScene);   // remove the old pane after the animation
        });
        slide.play();
    }

    // this method is only used when the 'Toolbar' class is actually rendered on the screen - at the moment it is not being used
    private static void translate(Direction path, Pane graph, Node oldScene, Node newScene1, Node newScene2, double time, int[] positions) {
        final boolean HORIZONTAL = path == Direction.LEFT || path == Direction.RIGHT;
        final boolean POSITIVE = path == Direction.LEFT || path == Direction.UP;
        final double THRESHOLD = HORIZONTAL ? graph.getWidth() : graph.getHeight();
        if (!graph.getChildren().contains(newScene2)) graph.getChildren().add(newScene2);
        if (!graph.getChildren().contains(newScene1)) graph.getChildren().add(newScene1);
        KeyFrame start = new KeyFrame(Duration.ZERO,
                new KeyValue(HORIZONTAL ? newScene1.translateXProperty() : newScene1.translateYProperty(), positions[0]),
                new KeyValue(HORIZONTAL ? newScene2.translateXProperty() : newScene2.translateYProperty(),ViewController.SCREEN_WIDTH),
                new KeyValue(HORIZONTAL ? oldScene.translateXProperty() : oldScene.translateYProperty(), positions[1]));
        KeyFrame end = new KeyFrame(Duration.seconds(time),
                new KeyValue(HORIZONTAL ? newScene1.translateXProperty() : newScene1.translateYProperty(), positions[2]),
                new KeyValue(HORIZONTAL ? newScene2.translateXProperty() : newScene2.translateYProperty(), 57),
                new KeyValue(HORIZONTAL ? oldScene.translateXProperty() : oldScene.translateYProperty(), positions[3]));

        Timeline slide = new Timeline(start, end);
        slide.setOnFinished(e -> {
            graph.getChildren().remove(oldScene);
        });
        slide.play();
    }

    public static void translateLeft(Pane graph, Node oldScene, Node newScene, double time) {
        translate(Direction.LEFT, graph, oldScene, newScene, time);
    }

    public static void translateLeft(Pane graph, Node oldScene, Node newScene1, Node newScene2, double time, int[] positions) {
        translate(Direction.LEFT, graph, oldScene, newScene1, newScene2, time, positions);
    }

    public static void translateRight(Pane graph, Node oldScene, Node newScene, double time) {
        translate(Direction.RIGHT, graph, oldScene, newScene, time);
    }

    public static void translateUp(Pane graph, Node oldScene, Node newScene, double time) {
        translate(Direction.UP, graph, oldScene, newScene, time);
    }

    public static void translateDown(Pane graph, Node oldScene, Node newScene, double time) {
        translate(Direction.DOWN, graph, oldScene, newScene, time);
    }

    // this animation is used to fade from the login or sign up scene to the home scne
    public static void dissolve(Pane graph, Node oldScene, Node newScene, double time) {
        // the old scene's opacity is reuced while the nes scene's opacity is increaded
        if (!graph.getChildren().contains(newScene)) graph.getChildren().add(newScene);
        final Timeline slide = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(graph.getChildren().get(1).opacityProperty(), 0.0), new KeyValue(graph.getChildren().get(0).opacityProperty(), 1.0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(graph.getChildren().get(1).opacityProperty(), 1.0), new KeyValue(graph.getChildren().get(0).opacityProperty(), 0.0))
        );
        slide.setOnFinished(e -> {
            graph.getChildren().remove(oldScene);
        });
        slide.play();
    }
}
