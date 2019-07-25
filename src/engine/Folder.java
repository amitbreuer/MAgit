package engine;

public class Folder {
    FolderComponent[] components;

    private static class FolderComponent {
        private enum eType {
            FOLDER, FILE
        }

        private eType type;
        private String name;
        private String Sh1;
        private String lastModifier;
        private String lastModifiedDate;
    }

}