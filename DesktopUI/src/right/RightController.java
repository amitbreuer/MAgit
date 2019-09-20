package right;

import app.AppController;
import engine.Commit;
import engine.Folder;
import generated.Item;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class RightController {

    @FXML
    TitledPane topTitledPane;
    @FXML
    TitledPane bottomTitledPane;
//    @FXML
//    ScrollPane topScrollPane;
    @FXML
    TreeView<String> filesTree;
    @FXML
    ScrollPane bottomScrollPane;

    private AppController mainController;

    @FXML
    private void initialize() {
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void ShowSingleCommitFilesTree(Commit commit,String repositoryName) {
        Image folderImage = new Image("/resources/blue-folder-icon.png");
        Image textImage = new Image("/resources/file-text-icon.png"); //move to right resources constants?

        TreeItem<String> root = new TreeItem<>(repositoryName, new ImageView(folderImage));
        root.setExpanded(Boolean.TRUE);
        createFilesTreeFromFolder(root, commit.getMainFolder(), folderImage, textImage);

        filesTree.setRoot(root);
    }

    private void createFilesTreeFromFolder(TreeItem<String> root, Folder mainFolder, Image folderImage, Image textImage) {
        for (Folder.ComponentData fc : mainFolder.getComponents()) {
            TreeItem newItem;
            if (fc.getFolderComponent() instanceof Folder) {
                newItem = new TreeItem<>(fc.getName(), new ImageView(folderImage));
                createFilesTreeFromFolder(newItem, (Folder) fc.getFolderComponent(), folderImage, textImage);
            } else {
                newItem = new TextFileTreeItem(fc.getName(),fc.getFolderComponent().toString(), new ImageView(textImage));
            }
            root.getChildren().add(newItem);
        }
    }

    public void ShowCommitInfo(String commitInfo) {
        bottomScrollPane.setContent(new Label(commitInfo));
    }

    public void Clear() {
        //filesTree.refresh(); maybe for just adding commitNodes instead of recreating tree
        filesTree.setRoot(new TreeItem<>());
        //topScrollPane.setContent(new Label(""));
        bottomScrollPane.setContent(new Label(""));
    }

    public void mouseClickedAction(MouseEvent mouseEvent) {
        TreeItem item = filesTree.getSelectionModel().getSelectedItem();
        if(item instanceof TextFileTreeItem){
            mainController.ShowFileContent(((TextFileTreeItem) item).getName(),item.toString());
        }
    }
}
