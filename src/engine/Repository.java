package engine;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Repository {
    private Path path;
    private Folder mainFolderInLastCommit;

    public Repository(String path) {
        this.path = Paths.get(path);
        this.mainFolderInLastCommit = new Folder();
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Folder getMainFolder() {
        return mainFolderInLastCommit;
    }

    public void setMainFolder(Folder mainFolder) {
        this.mainFolderInLastCommit = mainFolder;
    }

}
