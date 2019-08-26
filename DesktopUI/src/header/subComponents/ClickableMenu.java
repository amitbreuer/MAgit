package header.subComponents;

import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.beans.EventHandler;

public class ClickableMenu extends Menu {
    private final Label label;

    public ClickableMenu(String title) {
        MenuItem dummyItem = new MenuItem();
        dummyItem.setVisible(false);
        getItems().add(dummyItem);
        this.label = new Label();
        label.setText(title);
        label.setOnMouseClicked(evt -> {
            dummyItem.fire();
        });
        setGraphic(label);
    }

}
