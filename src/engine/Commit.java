package engine;

import org.apache.commons.codec.digest.DigestUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Commit {
    private String prevCommitSha1;
    private String dateCreated;
    private String creator;
    private String message;
    private Folder mainFolder;

    public String getMessage() {
        return message;
    }

    public Commit(String creator, String message) {
        this.dateCreated = getDate();
        this.creator = creator;
        this.message = message;
    }
    public Commit(String creator,String message,String dateCreated){
        this.creator = creator;
        this.message = message;
        this.dateCreated = dateCreated;
    }

    public Folder getMainFolder() {
        return mainFolder;
    }

    public void setMainFolder(Folder mainFolder) {
        this.mainFolder = mainFolder;
    }


    public String getPrevCommitSha1() {
        return prevCommitSha1;
    }

    public void setPrevCommitSha1(String prevCommitSha1) {
        this.prevCommitSha1 = prevCommitSha1;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    private String getDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));
    }

    @Override
    public String toString() {
        return
                prevCommitSha1 + "\r\n" +
                        mainFolder.sha1Folder() + "\r\n" +
                        dateCreated + "\r\n" +
                        creator + "\r\n" +
                        message + "\r\n";
    }

    String Sha1Commit() {
        return DigestUtils.sha1Hex(this.toString());
    }

    public String getCreator() {
        return creator;
    }
}