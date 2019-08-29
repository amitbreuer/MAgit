package engine;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Folder implements FolderComponent {

    private List<ComponentData> components = new ArrayList<>();

    public List<ComponentData> getComponents() {
        return components;
    }

    @Override
    public String toString() {
        String folderToString = new String();

        for (ComponentData c : components) {
            folderToString += c.toString() + "\r\n";
        }
        return folderToString;
    }

    public String sha1Folder() {
        return DigestUtils.sha1Hex(this.toString());
    }

    public static class ComponentData implements Comparable<ComponentData> {
        private String name;
        private String sha1;
        private FolderComponent folderComponent;
        private String lastModifier;
        private String lastModifiedDate;

        public String getLastModifier() {
            return lastModifier;
        }

        public String getLastModifiedDate() {
            return lastModifiedDate;
        }

        public void setLastModifiedDate(String lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
        }

        public void setLastModifier(String lastModifier) {
            this.lastModifier = lastModifier;
        }

        public ComponentData(String name, String sha1, FolderComponent folderComponent, String lastModifier, String date) {
            this.name = name;
            this.sha1 = sha1;
            this.folderComponent = folderComponent;
            this.lastModifier = lastModifier;
            this.lastModifiedDate = date;
        }

        public ComponentData(String name, String sha1, String type, String username, String date) {
            this.name = name;
            this.sha1 = sha1;
            if (type.equals("Blob")) {
                this.folderComponent = new Blob("");
            } else {
                this.folderComponent = new Folder();
            }
            this.lastModifier = username;
            this.lastModifiedDate = date;
        }

        public String getName() {
            return name;
        }

        public String getSha1() {
            return sha1;
        }

        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }

        public FolderComponent getFolderComponent() {
            return folderComponent;
        }

        public void setFolderComponent(FolderComponent folderComponent) {
            this.folderComponent = folderComponent;
        }

        @Override
        public int compareTo(ComponentData component) {
            return this.name.compareTo(component.name);
        }

        @Override
        public String toString() {
            String delimiter = ",";
            String resultString;

            resultString = this.name +
                    delimiter +
                    this.sha1 +
                    delimiter +
                    this.getType() +
                    delimiter +
                    this.lastModifier +
                    delimiter +
                    this.lastModifiedDate;

            return resultString;
        }

        public String getType() {
            return this.folderComponent.getClass().getSimpleName();
        }

    }

}
