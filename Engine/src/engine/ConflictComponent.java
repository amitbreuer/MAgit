package engine;

public class ConflictComponent {
    String oursFileContent;
    String theirsFileContent;
    String ancestorsFileContent;
    Folder containingFolder;

    public ConflictComponent(String oursFileContent, String theirsFileContent, String ancestorsFileContent, Folder containingFolder) {
        this.oursFileContent = oursFileContent;
        this.theirsFileContent = theirsFileContent;
        this.ancestorsFileContent = ancestorsFileContent;
        this.containingFolder = containingFolder;
    }

    public String getOursFileContent() {
        return oursFileContent;
    }

    public String getTheirsFileContent() {
        return theirsFileContent;
    }

    public String getAncestorsFileContent() {
        return ancestorsFileContent;
    }

}

