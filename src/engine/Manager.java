package engine;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Manager {
    String username;
    Repository repository;
    Commit headBranch;

    public Manager() {
        username = new String();
    }

    public void UpdateUserName(String userName) {
        this.username = userName;
    }

    public void CreateEmptyRepository(String repositoryPath) throws FileAlreadyExistsException {
        if (Files.exists(Paths.get(repositoryPath))) {
            throw new FileAlreadyExistsException("The path you have entered already exists");
        } else {
            new File(repositoryPath).mkdirs();
            new File(repositoryPath + "/.magit").mkdir();
            new File(repositoryPath + "/.magit/objects").mkdir();
            new File(repositoryPath + "/.magit/branches").mkdir();

            this.repository = new Repository(repositoryPath);
            createFileInBranches("HEAD.txt", "master");
            createFileInBranches("master.txt", "");
        }
    }

    public void createFileInBranches(String fileName, String fileContent) {
        Writer out = null;
        Path path = Paths.get(this.repository.getPath().toString() + "/.magit/branches");

        File writeTo = new File(path + "\\" + fileName);
        try {
            out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(writeTo)));
            out.write(fileContent);
        } catch (IOException e) {
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void switchRepository(String repositoryPath) throws Exception {
        Path path = Paths.get(repositoryPath);
        boolean isRepository = false;

        if (this.repository != null && this.repository.getPath().equals(path)) {
            throw new Exception("You already working with the repository - " + path.toString());
        } else {
            isRepository = Files.exists(Paths.get(repositoryPath + "/.magit"));

            if (isRepository) {
                this.repository = new Repository(repositoryPath);
            } else {
                throw new Exception("This folder is not a repository in M.A.git");
            }
        }
    }

    public Boolean directoryIsEmpty(Path path) {
        File file = new File(path.toString());
        return file.list().length == 0;
    }

    private Folder createFolderFromObjectFile(File textFile) {
        Folder folder = new Folder();

        FileReader file;
        String line = "";
        try {
            file = new FileReader(textFile);
            BufferedReader reader = new BufferedReader(file);
            try {
                while ((line = reader.readLine()) != null) {
                    folder.getComponents().add(Folder.FolderComponent.createFolderComponentFromString(line));
                }
                Collections.sort(folder.getComponents());
            } finally {
                reader.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        } catch (IOException e) {
            throw new RuntimeException("IO Error occured");
        }
        return folder;
    }

    public void ExcecuteCommit(String message) {
        String mainFolderSha1;
        String zipFilePath;
        Folder lastCommitMainFolder = new Folder();
        Path objectsFolderPath = Paths.get(repository.getPath().toString() + "/.magit/objects");
        Path branchesFolderPath = Paths.get(repository.getPath().toString() + "/.magit/branches");
        String headBranchName = convertTextFileToString(branchesFolderPath.toString() + "/HEAD.txt");

        if (!directoryIsEmpty(Paths.get(branchesFolderPath.toString() + "/" + headBranchName))) {//if there are previous commits - it's not the first commit
            String prevCommitSha1 = convertTextFileToString(branchesFolderPath + "/" + headBranchName);
        }

        Commit newCommit = new Commit(message);// add prev sha1 to c'tor
        // main folder - in commit or repository ??????



        //createNewObjectFile(mainFolderSha1, repository.getMainFolder().toString());

    }

    private Folder calculateDeltaAndWC(Path currentPath, Folder folderToCompare, String dateModified) {
        List<File> wCFiles = Arrays.asList(currentPath.toFile().listFiles());
        String sha1;
        String fileContent;
        int nameDiff;
        File currentWCFile;
        Folder.Component currentComparedFile;
        Folder currentFolder = new Folder();
        Collections.sort(wCFiles, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        Iterator<File> WCIterator = wCFiles.iterator();
        Iterator<Folder.Component> componentsIterator = folderToCompare.getComponents().iterator();

        while (WCIterator.hasNext() && componentsIterator.hasNext()){ // לבדוק אם צריך לקדם גם את האיטרטורים
            currentWCFile = WCIterator.next();
            currentComparedFile = componentsIterator.next();
            nameDiff = currentWCFile.getName().compareTo(currentComparedFile.getName());
            if(nameDiff == 0){ //if it's the same file
                if(currentWCFile.isFile()){ //if the file is a txt file
                    fileContent = convertTextFileToString(currentWCFile.toString());
                    sha1 = DigestUtils.sha1Hex(fileContent);
                    if(sha1.equals(currentComparedFile.getSha1())){ //if the file didn't change
                        currentFolder.getComponents().add(currentComparedFile);
                    } else{
                        currentFolder.getComponents().add(new Folder.Component(
                                currentWCFile.getName(), sha1, new Blob(fileContent), username, dateModified));
                        //add currentWCFile to updated files list
                    }
                }else{
                    Path subFolderPath = Paths.get(currentWCFile.getPath());
                    FolderComponent subComparedFile = currentComparedFile.getFolderComponent();
                    Folder subFolder = calculateDeltaAndWC(subFolderPath,(Folder)subComparedFile,dateModified);
                    sha1 = subFolder.sha1Folder();
                    if(sha1.equals(currentComparedFile.getSha1())){
                        currentFolder.getComponents().add(currentComparedFile);
                    }else{
                        currentFolder.getComponents().add(new Folder.Component(
                                currentWCFile.getName(), sha1, subFolder, username, dateModified));
                        //add currentWCFile to updated files list
                    }
                }
            }else if (nameDiff < 0){
                //add currentWCFile to new files list
            }else{
                //add currentComparedFile to deleted files list
            }

        }
        while (WCIterator.hasNext()){
            //add all files to new files list
        }
        while (componentsIterator.hasNext()){
            //add all components to deleted files list
        }

        Collections.sort(currentFolder.getComponents());
        return currentFolder;
    }


    public File findFileInDirectory(String fileName, String directoryPath) throws NoSuchFileException {
        File fileToFind = null;
        Path directory = Paths.get(directoryPath);
        File[] files;

        if (directoryIsEmpty(directory)) {
            throw new NoSuchFileException("There is no such file in this directory");
        } else {
            files = directory.toFile().listFiles();
            for (File f : files) {
                if (f.getName().equals(fileName)) {
                    fileToFind = f;
                    break;
                }
            }
            if (fileToFind == null) {
                throw new NoSuchFileException("There is no such file in this directory");
            }
        }

        return fileToFind;
    }

    public File getTextFileFromObjectsFolder(String fileName) {
        File textFile = null;
        File fileToUnzip;
        Path objectsPath = Paths.get(this.repository.getPath().toString() + "/.magit/objects");

        try {
            fileToUnzip = findFileInDirectory(fileName + ".zip", objectsPath.toString()); //לא משווה טוב שמות של קבצים
            unzip(fileToUnzip.getPath(), objectsPath.toString());
            textFile = findFileInDirectory(fileName + ".txt", objectsPath.toString());
        } catch (NoSuchFileException e) {
        } catch (IOException e) {
        }

        return textFile;
    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
            } else {
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[1024];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    private void createNewObjectFile(String Sha1, String content) {//creates a zip for objects
        String fileName = this.repository.getPath().toString() + "/.magit/objects/" + Sha1 + ".txt";
        String zipFileName = this.repository.getPath().toString() + "/.magit/objects/" + Sha1 + ".zip";
        File file = new File(fileName);
        try {
            file.createNewFile();
            BufferedWriter writer = null;
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(content);
            writer.close();

            File zipFile = new File(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
            ZipEntry e = new ZipEntry(Sha1 + ".txt");
            out.putNextEntry(e);

            byte[] data = content.getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();
            out.close();

            file.delete();
        } catch (IOException e) {
        }

    }


    public String convertTextFileToString(String fileName) {
        String returnValue = "";
        FileReader file;
        String line = "";
        try {
            file = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(file);
            try {
                while ((line = reader.readLine()) != null) {
                    returnValue += line + "\r\n";
                }
            } finally {
                reader.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        } catch (IOException e) {
            throw new RuntimeException("IO Error occured");
        }
        return returnValue;
    }


    private String Sha1Directory(Folder currentFolder, Path currentPath, String dateModified) {
        File[] allFileComponents = currentPath.toFile().listFiles();
        String sh1Hex = "";
        String fileContent = "";

        for (File f : allFileComponents) {
            if (!f.getName().equals(".magit")) {
                if (!f.isDirectory()) {
                    fileContent = convertTextFileToString(f.toString());
                    sh1Hex = DigestUtils.sha1Hex(fileContent);
                    currentFolder.getComponents().add(new Folder.FolderComponent(
                            f.getName(), sh1Hex, "BLOB", username, dateModified));
                } else {
                    Folder folder = new Folder();
                    sh1Hex = Sha1Directory(folder, Paths.get(f.getPath()), dateModified);
                    currentFolder.getComponents().add(new Folder.FolderComponent(
                            f.getName(), sh1Hex, "FOLDER", username, dateModified));

                }
            }
        }

        Collections.sort(currentFolder.getComponents());
        return DigestUtils.sha1Hex(currentFolder.toString());
    }
}