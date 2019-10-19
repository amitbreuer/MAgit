package engine.users;

import java.io.File;
import java.util.List;

public class FolderData implements FileData {
    private String fileName;
    private List<FileData> filesDatas;

    public void createFilesDatasFromFiles(File[] files) {
        for(File file : files){
            if(file.isDirectory()) {
                FolderData fileData = new FolderData();
                fileData.createFilesDatasFromFiles(file.listFiles());
            } else {
            }
        }
    }
}
