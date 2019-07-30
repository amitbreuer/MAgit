package engine;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Manager {
    String username = new String();
    Repository repository;
    Commit headBranch;

    public void UpdateUserName(String userName) {
        this.username = userName;
    }

    public void CreateEmptyRepository(String repositoryPath) throws FileAlreadyExistsException {
        Path path = Paths.get(repositoryPath);
        if (Files.exists(path)) {
            throw new FileAlreadyExistsException("The path you have entered already exists");
        } else {
            new File(repositoryPath).mkdirs();
            new File(repositoryPath + "/.magit").mkdir();
            new File(repositoryPath + "/.magit/objects").mkdir();
            new File(repositoryPath + "/.magit/branches").mkdir();

            try {
                new File(repositoryPath + "/.magit/branches/HEAD").createNewFile();
            } catch (IOException e) {
            }

            this.repository = new Repository(repositoryPath);
        }
    }

    public Boolean DirectoryIsEmpty(Path path) {
        File file = new File(path.toString());
        return file.list().length == 0;
    }

    public void ExcecuteCommit(String message) {
        String mainFolderSh1 ="";
        Path objectsFolderPath = Paths.get(repository.getPath().toString() + "/.magit/objects");
        Commit newCommit = new Commit(message, username);

        if (DirectoryIsEmpty(objectsFolderPath)) {//if objects folder is empty - this is the first commit
            ExcecuteFirstCommit(message);
            mainFolderSh1 = Sh1Directory(repository.getMainFolder(), repository.getPath(), newCommit.getDateCreated());

        } else {


        }
        newCommit.setMainFolderSh1(mainFolderSh1);
        createNewObjectFile(mainFolderSh1, repository.getMainFolder().toString());

    }


    private void createNewObjectFile(String Sh1, String content) {
        String fileName = this.repository.getPath().toString() + "/.magit/objects/" + Sh1 + ".txt";
        String zipFileName = this.repository.getPath().toString() + "/.magit/objects/" + Sh1 + ".zip";
        File file = new File(fileName);
        try {
            file.createNewFile();
            BufferedWriter writer = null;
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(content);
            writer.close();

            File zipFile = new File(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
            ZipEntry e = new ZipEntry(Sh1 + ".txt");
            out.putNextEntry(e);

            byte[] data = content.getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();
            out.close();

            file.delete();
        } catch (IOException e) {
        }

    }

    private String Sh1Directory(Path currentPath, String dateModified) {
        File[] allFileComponents = currentPath.toFile().listFiles();
        String sh1Hex = "";
        String fileContent = "";
        Folder currentFolder = new Folder();

        for (File f : allFileComponents) {
            if (!f.getName().equals(".magit")) {
                if (!f.isDirectory()) {
                    fileContent = readTextFile(f.toString());
                    sh1Hex = DigestUtils.sha1Hex(fileContent);
                    if (!Files.exists(Paths.get(currentPath.toString() + f.getName()))) {//if a file with the given sh1 does not exist
                        currentFolder.getComponents().add(new Folder.FolderComponent(
                                f.getName(), sh1Hex, "BLOB", username, dateModified));
                    }
                } else {
                    if (!Files.exists(Paths.get(currentPath.toString() + f.getName()))){//if a file with the given sh1 does not exist)
                        Folder folder = new Folder();
                        sh1Hex = Sh1Directory(folder, Paths.get(f.getPath()), dateModified);
                        currentFolder.getComponents().add(new Folder.FolderComponent(
                                f.getName(), sh1Hex, "FOLDER", username, dateModified));
                    }
                }
            }
        }
        Collections.sort(currentFolder.getComponents());
        return DigestUtils.sha1Hex(currentFolder.toString());
    }


    public String readTextFile(String fileName) {
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

}





