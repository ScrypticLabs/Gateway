/* 
* @File: Toolbar.java
* @Author: Abhi Gupta
* 
* The toolbar has been implemented for widgets but it is not displayed in the app, as it is not needed at the moment.
*/

package sample;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class Toolbar extends Group implements ViewController {
    private Rectangle panel, cover;
    private Button profile, contacts, groups, home, recent, quit;

    public Toolbar() {
        super();
        panel = new Rectangle(675,0,57,500);
        panel.setFill(Colour.BAR);
        cover = new Rectangle(675,0,57,500);
        cover.setFill(Colour.COVER);

        profile = new Button();
        profile.setGraphic(new ImageView(Img.PROFILE));
        profile.setLayoutX(675);
        profile.setLayoutY(24);
        profile.getStyleClass().add("toolbar-button");

        contacts = new Button();
        contacts.setGraphic(new ImageView(Img.CONTACTS));
        contacts.setLayoutX(674);
        contacts.setLayoutY(102);
        contacts.getStyleClass().add("toolbar-button");

        home = new Button();
        home.setGraphic(new ImageView(Img.HOME));
        home.setLayoutX(672);
        home.setLayoutY(191);
        home.getStyleClass().add("toolbar-button");

        recent = new Button();
        recent.setGraphic(new ImageView(Img.RECENT));
        recent.setLayoutX(676);
        recent.setLayoutY(278);
        recent.getStyleClass().add("toolbar-button");

        groups = new Button();
        groups.setGraphic(new ImageView(Img.GROUPS));
        groups.setLayoutX(674);
        groups.setLayoutY(363);
        groups.getStyleClass().add("toolbar-button");

        quit = new Button();
        quit.setGraphic(new ImageView(Img.QUIT));
        quit.setLayoutX(677);
        quit.setLayoutY(436);
        quit.getStyleClass().add("toolbar-button");

        getChildren().addAll(panel, profile, contacts, home, recent, groups, quit);
        setTranslateX(340);
    }

    @Override
    public String toString() {
        return "Toolbar";
    }
}
