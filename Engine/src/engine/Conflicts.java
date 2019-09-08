package engine;

import java.util.ArrayList;
import java.util.List;

public class Conflicts {
    private List<ConflictComponent> conflictFiles = new ArrayList<>();

    public void AddConflictComponent(ConflictComponent conflictComponent){
        this.conflictFiles.add(conflictComponent);
    }

    public List<ConflictComponent> getConflictFiles() {
        return conflictFiles;
    }
}
