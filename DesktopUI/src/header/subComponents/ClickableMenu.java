package header.subComponents;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.beans.EventHandler;

public class ClickableMenu extends Menu {
    private final Label label;

    public ClickableMenu(String title, Image menuIcon) {
        MenuItem dummyItem = new MenuItem();
        dummyItem.setVisible(false);
        getItems().add(dummyItem);
        this.label = new Label(title);
        this.label.setGraphic(new ImageView(menuIcon));

        label.setOnMouseClicked(evt -> {
            dummyItem.fire();
        });
        setGraphic(label);
    }

}
