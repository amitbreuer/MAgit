package engine;

import java.util.ArrayList;
import java.util.List;

public class Folder {

    private List<FolderComponent> components = new ArrayList<>();

    public List<FolderComponent> getComponents() {
        return components;
    }

    public void setComponents(List<FolderComponent> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        String folderToString = new String();

        for (FolderComponent f : components) {
            folderToString += f.toString()+"\r\n";
        }
        return  folderToString;
    }

    protected static class FolderComponent implements Comparable<FolderComponent> {
        public FolderComponent(String name, String sha1, String type, String username, String date) {
            this.name = name;
            this.sh1 = sha1;
            this.type = eType.valueOf(type);
            this.lastModifier = username;
            this.lastModifiedDate = date;
        }

        @Override
        public int compareTo(FolderComponent folderComponent) {
            return this.name.compareTo(folderComponent.name);
        }

        private enum eType {
            FOLDER, BLOB
        }

        private String name;
        private String sh1;
        private eType type;
        private String lastModifier;
        private String lastModifiedDate;

        @Override
        public String toString() {
            String delimiter = ",";
            String resultString;

            resultString = this.name +
                    delimiter +
                    this.sh1 +
                    delimiter +
                    this.type.toString() +
                    delimiter +
                    this.lastModifier +
                    delimiter +
                    this.lastModifiedDate;

            return resultString;
        }
    }


}