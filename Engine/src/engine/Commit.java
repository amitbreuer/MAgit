package engine;

import org.apache.commons.codec.digest.DigestUtils;
import puk.team.course.magit.ancestor.finder.CommitRepresentative;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        this.prevCommitSha1 = null;
        this.anotherPrevCommitSha1 = null;
    }

    public Commit(String creator, String message, String dateCreated) {
        this.creator = creator;
        this.message = message;
        this.dateCreated = dateCreated;
        this.prevCommitSha1 = null;
        this.anotherPrevCommitSha1 = null;
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

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
        Date date = new Date();
        return formatter.format(date);

    }

    @Override
    public String toString() {
        return
                prevCommitSha1 + "\r\n" +
                        anotherPrevCommitSha1 + "\r\n" +
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
        return prevCommitSha1;
    }

    @Override
    public String getSecondPrecedingSha1() {
        return anotherPrevCommitSha1;
    }

    public void setSecondPrevCommitSha1(String commitSha1) {
        this.anotherPrevCommitSha1 = commitSha1;
    }
}