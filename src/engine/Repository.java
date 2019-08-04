package engine;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Repository {
    private Path path;
    private Folder mainFolder;
    private Commit lastCommit;

    public Repository(String path) {
        this.path = Paths.get(path);
        this.mainFolder = new Folder();
        this.lastCommit = null;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Folder getMainFolder() {
        return mainFolder;
    }

    public void setMainFolder(Folder mainFolder) {
        this.mainFolder = mainFolder;
    }

    public Commit getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(Commit lastCommit) {
        this.lastCommit = lastCommit;
    }
}
