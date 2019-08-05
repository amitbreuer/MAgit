package engine;

import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Commit {
    private String message;
    private String prevCommitSha1;
    private String dateCreated;
    private String creator;
    private String mainFolderSha1;

    public Commit(String message, String creator) { // add parameters
        this.message = message;
        this.dateCreated = getDate();
        this.creator = creator;
    }

    public String getPrevCommitSha1() {
        return prevCommitSha1;
    }

    public void setPrevCommitSha1(String prevCommitSha1) {
        this.prevCommitSha1 = prevCommitSha1;
    }

    public String getMainFolderSh1() {
        return mainFolderSha1;
    }

    @Override
    public String toString() {
        return
                prevCommitSha1 + '\r' + '\n' +
                        mainFolderSha1 + '\r' + '\n' +
                        dateCreated + '\r' + '\n' +
                        creator + '\r' + '\n' +
                        message + '\r' + '\n';
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

    private String Sha1Commit() {
        return DigestUtils.sha1Hex(this.toString());
    }
}