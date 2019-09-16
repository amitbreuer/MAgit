package app.subComponents.conflictsWindow;

import app.AppController;
import app.AppResourcesConstants;
import app.subComponents.singleConflictWindow.SingleConflictController;
import engine.ConflictComponent;
import engine.Conflicts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConflictsWindowController {

    @FXML
    Button resolveButton;
    @FXML
    ListView conflictsList;

    private AppController mainController;
    private Scene singleConflictWindowScene;
    private SingleConflictController singleConflictController;
    private Map<String, ConflictComponent> conflictsMap;

    @FXML
    private void initialize() {
        setSingleConflictWindow();
        conflictsMap = new HashMap<>();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    private void setSingleConflictWindow() {
        URL url = getClass().getResource(AppResourcesConstants.SINGLE_CONFLICT_WINDOW_FXML_PATH);

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        try {
            GridPane popupRoot = fxmlLoader.load();
            singleConflictWindowScene = new Scene(popupRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        singleConflictController = fxmlLoader.getController();
        singleConflictController.setMainController(this);
    }

    public void resolveButtonAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        stage.setTitle("Resolve Conflicts");
        String conflictComponentName = (String) conflictsList.getSelectionModel().getSelectedItem();
        ConflictComponent conflictComponent = conflictsMap.get(conflictComponentName);
        singleConflictController.SetVersions(conflictComponent.getOursFileContent(),conflictComponent.getTheirsFileContent(),conflictComponent.getAncestorsFileContent());
        stage.setScene(singleConflictWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        conflictComponent.setMergedFileContent(singleConflictController.getMergedContent());
        conflictsList.getItems().remove(conflictComponentName);
        if(conflictsList.getItems().isEmpty()){
            Stage mainStage = (Stage) resolveButton.getScene().getWindow();
            mainStage.close();
        }
    }

    public void SetConflictsList(List<ConflictComponent> conflicts) {
        for (ConflictComponent cc : conflicts) {
            conflictsList.getItems().add(cc.GetFullPath());
            conflictsMap.put(cc.GetFullPath(),cc);
        }
    }
}
