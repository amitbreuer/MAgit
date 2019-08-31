package engine;

import java.nio.file.Path;

public class DeltaComponent {
    private String name;
    private FolderComponent folderComponent;
    private String path;
    private String type;

    public DeltaComponent(FolderComponent folderComponent, String path, String name) {
        this.folderComponent = folderComponent;
        this.path = path;
        this.name = name;
        if (folderComponent instanceof Folder){
            this.type = "Directory";
        }
        else {
            this.type = "Text File";
        }
    }

    public String getType() {
        return type;
    }

    public FolderComponent getFolderComponent() {
        return folderComponent;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}
