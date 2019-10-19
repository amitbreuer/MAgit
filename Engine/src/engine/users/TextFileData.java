package engine.users;

public class TextFileData implements FileData {
    private String fileName;
    private String content;

    public TextFileData(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }
}
