package engine;

import org.apache.commons.codec.digest.DigestUtils;

public class Blob implements FolderComponent{
    private String content;

    public Blob(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}