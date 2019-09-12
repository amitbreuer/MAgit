package right;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

public class TextFileTreeItem extends TreeItem {
    private String name;
    private String content;

    public TextFileTreeItem(String name, String content,Node graphic) {
        super(name, graphic);
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return content;
    }
}
