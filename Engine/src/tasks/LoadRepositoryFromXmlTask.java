package tasks;

import engine.MagitManager;
import engine.XmlManager;
import exceptions.XmlPathContainsNonRepositoryObjectsException;
import exceptions.XmlRepositoryAlreadyExistsException;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class LoadRepositoryFromXmlTask extends Task<Boolean> {

    private MagitManager magitManager;
    private String fileFullPath;
    private Consumer<String> errorNotifier;
    private Runnable runIfPathContainsRepository;
    private Runnable runIfFinishedProperly;

    public LoadRepositoryFromXmlTask(MagitManager magitManager, String fileFullPath, Consumer<String> errorNotifier, Runnable runIfPathContainsRepository, Runnable runIfFinishedProperly) {
        this.magitManager = magitManager;
        this.fileFullPath = fileFullPath;
        this.errorNotifier = errorNotifier;
        this.runIfPathContainsRepository = runIfPathContainsRepository;
        this.runIfFinishedProperly = runIfFinishedProperly;
    }

    @Override
    protected Boolean call() {
        try {
            this.magitManager.ValidateAndLoadXMLRepository(fileFullPath);
            Platform.runLater(
                    runIfFinishedProperly
            );
        } catch (XmlRepositoryAlreadyExistsException e1) {
            Platform.runLater(
                    runIfPathContainsRepository
            );
        } catch (Exception e2) {
            Platform.runLater(
                    () -> errorNotifier.accept(e2.getMessage())
            );
        }

        return Boolean.TRUE;
    }
}
