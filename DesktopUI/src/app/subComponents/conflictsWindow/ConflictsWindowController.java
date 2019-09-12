package app.subComponents.conflictsWindow;

import app.AppController;
import engine.ConflictComponent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ConflictsWindowController {

    @FXML
    Button resolveButton;
    @FXML
    ListView conflictsList;

    private AppController mainController;

    @FXML
    private void initialize() {
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void ResolveConflicts(List<ConflictComponent> conflicts){

        for (ConflictComponent cc : conflicts) {

        }
    }

//    private void resolveSingleConflict(ConflictComponent conflictComponent) {
//        Stage stage = new Stage();
//        stage.setTitle("Resolve Conflicts");
//        singleConflictController.SetVersions(conflictComponent.getOursFileContent(),conflictComponent.getTheirsFileContent(),conflictComponent.getAncestorsFileContent());
//        stage.setScene(singleConflictWindowScene);
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.showAndWait();
//        conflictComponent.setMergedFileContent(singleConflictController.getMergedContent());
//    }
}
