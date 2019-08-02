package engine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Commit {
    private String message;
    //private List<String> commitsHistory;
    private Commit prevCommit;
    private String dateCreated;
    private String creator;
    private String mainFolderSh1;

    public Commit getPrevCommit() {
        return prevCommit;
    }

    public void setPrevCommit(Commit prevCommit) {
        this.prevCommit = prevCommit;
    }

    public String getMainFolderSh1() {
        return mainFolderSh1;
    }

    public Commit(String message){//, String creator,Commit prevCommit) {
        this.message = message;
        //this.prevCommit = null;
        this.dateCreated = getDate();
        //this.creator = creator
    //this.prevCommit = prevCommit;
    }
/*
    public void setCommitsHistory(List<String> commitsHistory) {
        this.commitsHistory = commitsHistory;
    }

    public List<String> getCommitsHistory() {
        return commitsHistory;
    }
*/
    public String getDateCreated() {
        return dateCreated;
    }

    private String getDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));
    }

    public void setMainFolderSh1(String mainFolderSh1) {
        this.mainFolderSh1 = mainFolderSh1;
    }
}