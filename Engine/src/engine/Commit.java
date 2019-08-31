package engine;

import org.apache.commons.codec.digest.DigestUtils;
import puk.team.course.magit.ancestor.finder.CommitRepresentative;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Commit implements CommitRepresentative {
    private String prevCommitSha1;
    private String anotherPrevCommitSha1;
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

    @Override
    public String getSha1() {
        return this.Sha1Commit();
    }

    @Override
    public String getFirstPrecedingSha1() {
        if(prevCommitSha1 == null){
            return "";
        }
        return prevCommitSha1;
    }

    @Override
    public String getSecondPrecedingSha1() {
        if(anotherPrevCommitSha1 == null){
            return "";
        }
        return anotherPrevCommitSha1;
    }

    public void setSecondPrevCommitSha1(String commitSha1) {
        this.anotherPrevCommitSha1 = commitSha1;
    }
}