package left;

import app.AppController;
import engine.Delta;
import engine.DeltaComponent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class LeftController {

    @FXML
    Label topLabel;
    @FXML
    ScrollPane displayScrollPane;

    private AppController mainController;

    @FXML
    private void initialize() {
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void ShowFileContent(String fileName, String fileContent) {
        topLabel.textProperty().setValue(fileName + "  Content:");
        displayScrollPane.setContent(new Label(fileContent));
    }

    public void ShowWCStatus(Delta delta){
        topLabel.textProperty().setValue("WC Status");
        if(delta.isEmpty()) {
            displayScrollPane.setContent(new Label("No open changes"));
        } else {
            TreeView<String> tree = createDeltaTree(delta);
            displayScrollPane.setContent(tree);
        }
    }

    public void ShowDeltaBetweenTwoCommits(Delta delta) {
        topLabel.textProperty().setValue("Changes");
        TreeView<String> tree = createDeltaTree(delta);
        displayScrollPane.setContent(tree);
    }

    private TreeView<String> createDeltaTree(Delta delta){
        Image addImage = new Image("/app/resources/add-icon.png");
        Image deleteImage = new Image("/app/resources/trash-icon.png");
        Image updateImage = new Image("/app/resources/edit-icon.png");


        List<DeltaComponent> addedComponents = delta.getAddedFiles();
        List<DeltaComponent> deletedComponents = delta.getDeletedFiles();
        List<DeltaComponent> updatedComponents = delta.getUpdatedFiles();

        TreeItem<String> root = new TreeItem<>();
        TreeItem<String> addedItems = new TreeItem<>("Added",new ImageView(addImage));
        TreeItem<String> deletedItems = new TreeItem<>("Deleted",new ImageView(deleteImage));
        TreeItem<String> updatedItems = new TreeItem<>("Updated",new ImageView(updateImage));

        addDeltaComponentsToDeltaTree(addedComponents,addedItems);
        addDeltaComponentsToDeltaTree(deletedComponents,deletedItems);
        addDeltaComponentsToDeltaTree(updatedComponents,updatedItems);

        if(!addedItems.isLeaf()){
            root.getChildren().add(addedItems);
        }
        if(!deletedItems.isLeaf()){
            root.getChildren().add(deletedItems);
        }
        if(!updatedItems.isLeaf()){
            root.getChildren().add(updatedItems);
        }

        TreeView<String> tree = new TreeView<>(root);
        tree.setShowRoot(false);

        return tree;
    }

    private void addDeltaComponentsToDeltaTree(List<DeltaComponent> components,TreeItem<String> items){
        Image folderImage = new Image("/app/resources/blue-folder-icon.png");
        Image textImage = new Image("/app/resources/file-text-icon.png");

        for(DeltaComponent component : components) {
            TreeItem item;
            ImageView image;
            if(component.getType().equals("Text File")){
                image = new ImageView(textImage);
            } else {
                image = new ImageView(folderImage);
            }
            item = new TreeItem(component.GetFullName(),image);
            items.getChildren().add(item);
        }
    }

    public void Clear() {
        topLabel.setText("");
        displayScrollPane.setContent(new Label(""));
    }
}
