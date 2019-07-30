package engine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Commit {
    private String message;

    public void setCommitsHistory(List<String> commitsHistory) {
        this.commitsHistory = commitsHistory;
    }

    public List<String> getCommitsHistory() {
        return commitsHistory;
    }

    private List<String> commitsHistory;
    private String dateCreated;
    private String creator;
    private String mainFolderSh1;

    public Commit(String message, String creator) {
        this.message = message;
        this.commitsHistory = null;
        this.dateCreated = getDate();
        this.creator = creator;
    }

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