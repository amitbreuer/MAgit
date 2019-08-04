package engine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Commit {
    private String message;
    private String prevCommitSha1;
    private String dateCreated;
    private String creator;
    private String mainFolderSha1;
    private Folder mainFolder;

    public String getPrevCommitSha1() {
        return prevCommitSha1;
    }

    public void setPrevCommitSha1(String prevCommitSha1) {
        this.prevCommitSha1 = prevCommitSha1;
    }

    public String getMainFolderSh1() {
        return mainFolderSha1;
    }

    public Commit(String message) { // add parameters
        this.message = message;
        this.dateCreated = getDate();
        //this.creator = creator;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    private String getDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));
    }

    public void setMainFolderSh1(String mainFolderSh1) {
        this.mainFolderSha1 = mainFolderSh1;
    }

    public Folder getMainFolder() {
        return mainFolder;
    }
}