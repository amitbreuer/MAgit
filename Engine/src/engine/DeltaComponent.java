package engine;

import java.nio.file.Path;

public class DeltaComponent {
    private String name;
    private FolderComponent folderComponent;
    private Path path;
    private String type;

    public DeltaComponent(FolderComponent folderComponent, Path path,String name) {
        this.folderComponent = folderComponent;
        this.path = path;
        this.name = name;
        if (folderComponent instanceof Folder){
            this.type = "Directory";
        }
        else {
            this.type = "Text file";
        }
    }

    public String getType() {
        return type;
    }

    public FolderComponent getFolderComponent() {
        return folderComponent;
    }

    public Path getPath() {

        return path;
    }

    public String getName() {
        return name;
    }
}
