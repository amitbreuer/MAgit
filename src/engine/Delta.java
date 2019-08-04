package engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Delta {
    List<File> addedFiles;
    List<File> updatedFiles;
    List<File> deletedFiles;

    public Delta() {
        addedFiles = new ArrayList<>();
        updatedFiles = new ArrayList<>();
        deletedFiles = new ArrayList<>();
    }
}
