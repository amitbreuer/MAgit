package engine;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.iterators.PushbackIterator;

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

    private void createFileInBranches(String fileName, String fileContent) {
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

    private Boolean directoryIsEmpty(Path path) {
        File file = new File(path.toString());
        return file.list().length == 0;
    }

    public void ExcecuteCommit(String message) {
        Path objectsFolderPath = Paths.get(repository.getPath().toString() + "/.magit/objects");
        Folder currentWC;
        Commit newCommit = new Commit(message, username);
        Delta delta = new Delta();
        String currentMainFolderSha1;
        String lastCommitMainFolderSha1 = "";

        if (directoryIsEmpty(Paths.get(objectsFolderPath.toString()))) {//it's the first commit
            currentWC = calculateDeltaAndWC(repository.getPath(), new Folder(), newCommit.getDateCreated(), delta);
        } else {
            lastCommitMainFolderSha1 = repository.getMainFolder().sha1Folder();
            currentWC = calculateDeltaAndWC(repository.getPath(), repository.getMainFolder(), newCommit.getDateCreated(), delta);
        }

        currentMainFolderSha1 = currentWC.sha1Folder();
        if (!lastCommitMainFolderSha1.equals(currentMainFolderSha1)) {//if there are changes since the last commit
            repository.setMainFolder(currentWC);
            newCommit.setMainFolderSh1(currentMainFolderSha1);
            newCommit.setPrevCommitSha1(lastCommitMainFolderSha1);

            createNewObjectFileFromDelta(delta);//create new object files for all new/updated files
            createNewObjectFile(repository.getMainFolder().toString());//create object file that contains the new main folder
            createNewObjectFile(newCommit.toString());//create object file that contains the new commit
        }


        Folder newMainFolder = createMainFolderFromObjectFile(DigestUtils.sha1Hex(newCommit.toString()));

    }

    private void createNewObjectFileFromDelta(Delta delta) {
        for (DeltaComponent dc : delta.getUpdatedFiles()) {
            createNewObjectFile(dc.getFolderComponent().toString());
        }
        for (DeltaComponent dc : delta.getAddedFiles()) {
            createNewObjectFile(dc.getFolderComponent().toString());
        }
    }

    private Folder calculateDeltaAndWC(Path currentPath, Folder folderToCompare, String dateModified, Delta delta) {
        List<File> wCFiles = Arrays.asList(currentPath.toFile().listFiles());
        String sha1;
        String fileContent;
        int nameDiff;
        Blob newBlob;
        File currentWCFile = null;
        Folder.ComponentData currentComparedFile = null;
        Folder currentFolder = new Folder();
        Collections.sort(wCFiles, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        Iterator<File> WCIterator = wCFiles.iterator();
        Iterator<Folder.ComponentData> componentsIterator = folderToCompare.getComponents().iterator();
        if (currentPath.equals(repository.getPath())) {
            WCIterator.next(); // skip ".magit"
        }
        boolean WChasNext = WCIterator.hasNext();
        boolean comparedFileHasNext = componentsIterator.hasNext();
        if (WChasNext) {
            currentWCFile = WCIterator.next();
        }
        if (comparedFileHasNext) {
            currentComparedFile = componentsIterator.next();
        }

        while (WChasNext && comparedFileHasNext) {
            nameDiff = currentWCFile.getName().compareTo(currentComparedFile.getName());
            if (nameDiff == 0) { //if it's the same name
                if (currentWCFile.isFile()) { //if the file is a txt file
                    fileContent = convertTextFileToString(currentWCFile.toString());
                    sha1 = DigestUtils.sha1Hex(fileContent);
                    if (sha1.equals(currentComparedFile.getSha1())) { //if the file didn't change
                        currentFolder.getComponents().add(currentComparedFile);
                    } else {
                        newBlob = new Blob(fileContent);
                        currentFolder.getComponents().add(new Folder.ComponentData(
                                currentWCFile.getName(), sha1, newBlob, username, dateModified));
                        delta.getUpdatedFiles().add(new DeltaComponent(newBlob, currentPath, currentWCFile.getName()));
                    }
                } else {
                    Path subFolderPath = Paths.get(currentWCFile.getPath());
                    FolderComponent subComparedFile = currentComparedFile.getFolderComponent();
                    Folder subFolder = calculateDeltaAndWC(subFolderPath, (Folder) subComparedFile, dateModified, delta);
                    sha1 = subFolder.sha1Folder();
                    if (sha1.equals(currentComparedFile.getSha1())) {
                        currentFolder.getComponents().add(currentComparedFile);
                    } else {
                        currentFolder.getComponents().add(new Folder.ComponentData(
                                currentWCFile.getName(), sha1, subFolder, username, dateModified));
                        delta.getUpdatedFiles().add(new DeltaComponent(subFolder, currentPath, currentWCFile.getName()));
                    }
                }

                WChasNext = WCIterator.hasNext();
                comparedFileHasNext = componentsIterator.hasNext();
                if (WChasNext && comparedFileHasNext) {
                    currentWCFile = WCIterator.next();
                    currentComparedFile = componentsIterator.next();
                }
            } else if (nameDiff < 0) { //file added
                addFileToAddedFilesList(currentWCFile, currentFolder, currentPath, dateModified, delta);
                WChasNext = WCIterator.hasNext();

                if (WChasNext) {
                    currentWCFile = WCIterator.next();
                }

            } else { //file deleted
                addFolderComponentToDeletedFilesList(currentComparedFile, currentPath, delta);
                comparedFileHasNext = componentsIterator.hasNext();
                if (comparedFileHasNext) {
                    currentComparedFile = componentsIterator.next();
                }
            }
        }

        while (WChasNext) {
            addFileToAddedFilesList(currentWCFile, currentFolder, currentPath, dateModified, delta);
            WChasNext = WCIterator.hasNext();
            if (WChasNext) {
                currentWCFile = WCIterator.next();
            }
        }
        while (comparedFileHasNext) {
            addFolderComponentToDeletedFilesList(currentComparedFile, currentPath, delta);
            comparedFileHasNext = componentsIterator.hasNext();
            if (comparedFileHasNext) {
                currentComparedFile = componentsIterator.next();
            }
        }

        Collections.sort(currentFolder.getComponents());
        return currentFolder;
    }

    private void addFileToAddedFilesList(File addedFile, Folder folderToUpdate, Path newFilePath, String dateModified, Delta delta) {
        String fileContent;
        String sha1;
        Folder subFolder;
        Blob newBlob;
        if (addedFile.isFile()) {
            fileContent = convertTextFileToString(addedFile.toString());
            sha1 = DigestUtils.sha1Hex(fileContent);
            newBlob = new Blob(fileContent);
            folderToUpdate.getComponents().add(new Folder.ComponentData(
                    addedFile.getName(), sha1, newBlob, username, dateModified));
            delta.getAddedFiles().add(new DeltaComponent(newBlob, newFilePath, addedFile.getName()));
        } else { // if new file is a directory
            File[] files = addedFile.listFiles();
            subFolder = new Folder();
            for (File f : files) {
                addFileToAddedFilesList(f, subFolder, Paths.get(addedFile.getPath()), dateModified, delta);
                sha1 = subFolder.sha1Folder();
                folderToUpdate.getComponents().add(new Folder.ComponentData(
                        addedFile.getName(), sha1, subFolder, username, dateModified));
            }
            delta.getAddedFiles().add(new DeltaComponent(subFolder, newFilePath, addedFile.getName()));/////////////////new file path or f.getPath?
        }
    }


    private void addFolderComponentToDeletedFilesList(Folder.ComponentData fc, Path path, Delta delta) {

        if (fc.getFolderComponent() instanceof Folder) {
            List<Folder.ComponentData> components = ((Folder) fc.getFolderComponent()).getComponents();
            Path subPath = Paths.get(path.toString() + "/" + fc.getName());
            for (Folder.ComponentData c : components) {
                addFolderComponentToDeletedFilesList(c, subPath, delta);
            }
        }
        delta.getDeletedFiles().add(new DeltaComponent(fc.getFolderComponent(), path, fc.getName()));
    }

    public File getTextFileFromObjectsDirectory(String fileName,String objectsFolderPath) {
        File textFile = null;
        String fileToUnzipPath = objectsFolderPath + "/" + fileName + ".zip";

        try {
            unzip(fileToUnzipPath, objectsFolderPath);
            textFile = new File(objectsFolderPath + "/" + fileName + ".txt");
        } catch (IOException e) {
        }

        return textFile;
    }

    private void unzip(String zipFilePath, String destDirectory) throws IOException {
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

    private void createNewObjectFile(String content) {//creates a zip for objects
        String Sha1 = DigestUtils.sha1Hex(content);
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


    private String convertTextFileToString(String fileName) {
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

    public String getStatus() {
        Folder currentWC;
        Path objectsFolderPath = Paths.get(repository.getPath().toString() + "/.magit/objects");
        Delta delta = new Delta();
        String deltaToString;

        if (directoryIsEmpty(Paths.get(objectsFolderPath.toString()))) {//it's the first commit
            currentWC = calculateDeltaAndWC(repository.getPath(), new Folder(), new Commit("", "").getDateCreated(), delta);
        } else {
            currentWC = calculateDeltaAndWC(repository.getPath(), repository.getMainFolder(), new Commit("", "").getDateCreated(), delta);
        }

        if (delta.isEmpty()) {
            deltaToString = "there are no changes since the last commit";
        } else {
            deltaToString = delta.toString();
        }
        return deltaToString;
    }


    public Folder createMainFolderFromObjectFile(String commitSha1){
        String objectsFolderPath = repository.getPath().toString()+"/.magit/objects";
        String commitTextFileContent;
        String mainFolderSha1;
        List<String> mainFolderTextFileContent = null;
        FolderComponent mainFolder;
        File mainFolderTextFile;
        StringTokenizer tokenizer;
        File commitTextFile;

        commitTextFile = getTextFileFromObjectsDirectory(commitSha1,objectsFolderPath);
        commitTextFileContent = convertTextFileToString(commitTextFile.getPath());
        tokenizer = new StringTokenizer(commitTextFileContent, "\r\n");
        mainFolderSha1 = tokenizer.nextToken(); // get first line of the content - the line of the main folder sha1
        mainFolderTextFile = getTextFileFromObjectsDirectory(mainFolderSha1,objectsFolderPath);
        try {
            mainFolderTextFileContent = Files.readAllLines(mainFolderTextFile.toPath());
        } catch (IOException e) {
        }

        mainFolder = createFolderComponentFromTextFileLines(true,mainFolderTextFileContent,objectsFolderPath);

        commitTextFile.delete();
        mainFolderTextFile.delete();
        return (Folder)mainFolder;
    }

    public FolderComponent createFolderComponentFromTextFileLines(Boolean isFolder,List<String> contentLines,String objectsFolderPath){
        FolderComponent newFolderComponent = null;
        FolderComponent subFolderComonent = null;
        List<String> subComponentContentLines = null;
        Folder.ComponentData newComponentData;
        File subComponentTextFile;
        String contentString;

        if(!isFolder){
            contentString = createStringFromListOfStrings(contentLines);
            Blob newblob = new Blob(contentString);
            newFolderComponent = newblob;
        }else {
            Folder newFolder = new Folder();
            for (String line : contentLines) {
                newComponentData = getComponentDataFromString(line);
                subComponentTextFile = getTextFileFromObjectsDirectory(newComponentData.getSha1(), objectsFolderPath);
                try {
                    subComponentContentLines = Files.readAllLines(subComponentTextFile.toPath());
                } catch (IOException e) {
                }

                subFolderComonent = createFolderComponentFromTextFileLines(newComponentData.getType().equals("Folder"), subComponentContentLines, objectsFolderPath);
                newComponentData.setFolderComponent(subFolderComonent);
                newFolder.getComponents().add(newComponentData);
                subComponentTextFile.delete();
            }
            newFolderComponent = newFolder;
        }

        return newFolderComponent;
    }

    private Folder.ComponentData getComponentDataFromString(String str){
        StringTokenizer tokenizer;
        tokenizer = new StringTokenizer(str,",");
        Folder.ComponentData newComponentData;

        String name = tokenizer.nextToken();
        String sha1 = tokenizer.nextToken();
        String type = tokenizer.nextToken();
        String creator = tokenizer.nextToken();
        String date = tokenizer.nextToken();
        newComponentData = new Folder.ComponentData(name,sha1,type,creator,date);

        return newComponentData;
    }

    private String createStringFromListOfStrings(List<String> stringLines) {
        StringBuilder sb = new StringBuilder();
        for(String s : stringLines){
            sb.append(s);
        }

        return sb.toString();
    }

}

