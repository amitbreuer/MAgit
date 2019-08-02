package engine;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.SQLOutput;
import java.util.Collections;
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
        Path path = Paths.get(repositoryPath);
        if (Files.exists(path)) {
            System.out.println(path);
            throw new FileAlreadyExistsException("The path you have entered already exists");
        } else {
            new File(repositoryPath).mkdirs();
            new File(repositoryPath + "/.magit").mkdir();
            new File(repositoryPath + "/.magit/objects").mkdir();
            new File(repositoryPath + "/.magit/branches").mkdir();

            createFile("HEAD.txt","master.txt",Paths.get(repositoryPath + "/.magit/branches"));
            createFile("master.txt","",Paths.get(repositoryPath+"/.magit/branches"));
            this.repository = new Repository(repositoryPath);
        }
    }

    public static void createFile(String fileName, String fileContent, Path path) {
        Writer out = null;

        File master = new File(path + "\\" + fileName);
        try {
            out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(master)));
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

    public void switchRepository(String repositoryPath) throws FileNotFoundException {
        Path path = Paths.get(repositoryPath);
        File[] files;
        boolean isRepository = false;

        if(this.repository != null && this.repository.getPath().equals(path)){
            System.out.println("You already working with the repository - " + path.toString());//exception
        }
        else{
            files = path.toFile().listFiles();
            for(File f : files){     //////////////replace with "exists"
                if (f.getName().equals(".magit")){
                    isRepository = true;
                    break;
                }
            }

            if(isRepository){
                this.repository = new Repository(repositoryPath);
            }
            else{
                throw new FileNotFoundException("This folder is not a repository in M.A.git");//throw not repsitory exception
            }
        }
    }

    public Boolean DirectoryIsEmpty(Path path) {
        File file = new File(path.toString());
        return file.list().length == 0;
    }


    public void ExcecuteCommit(String message) {
        String mainFolderSh1;
        String zipFilePath;
        Path objectsFolderPath = Paths.get(repository.getPath().toString() + "/.magit/objects");
        Path branchesFolderPath = Paths.get(repository.getPath().toString() + "/.magit/branches");
        String headBranch = readTextFile(branchesFolderPath.toString()+"/Head");
        String prevCommitSha1 = readTextFile(branchesFolderPath+"/"+headBranch);
        Commit newCommit = new Commit(message);// add prev sh1 to c'tor

        if(prevCommitSha1.equals("")) { //if it's the first commit
            //create empty txt file to compare to WC
        }
        else{
            //compare to txt file that represent main folder of last commit
        }

        mainFolderSh1 = Sh1Directory(this.repository.getMainFolder(),repository.getPath(), newCommit.getDateCreated());
        newCommit.setMainFolderSh1(mainFolderSh1);
        createNewObjectFile(mainFolderSh1, repository.getMainFolder().toString());
        File textFile = getTextFileFromObjectsFolder(mainFolderSh1); //פונקציה פנימית לא מוצאת
        String content = readTextFile(textFile.getName());// לצורך בדיקה אם unzip עבד
    }

    private String Sh1Directory(Folder currentFolder,Path currentPath, String dateModified) {
        File[] allFileComponents = currentPath.toFile().listFiles();
        String sh1Hex = "";
        String fileContent = "";

        for (File f : allFileComponents) {
            if (!f.getName().equals(".magit")) {
                if (!f.isDirectory()) {
                    fileContent = readTextFile(f.toString());
                    sh1Hex = DigestUtils.sha1Hex(fileContent);
                    currentFolder.getComponents().add(new Folder.FolderComponent(
                            f.getName(), sh1Hex, "BLOB", username, dateModified));
                } else {
                    Folder folder = new Folder();
                    sh1Hex = Sh1Directory(folder, Paths.get(f.getPath()), dateModified);
                    currentFolder.getComponents().add(new Folder.FolderComponent(
                            f.getName(), sh1Hex, "FOLDER", username, dateModified));

                }
            }
        }

        Collections.sort(currentFolder.getComponents());
        return DigestUtils.sha1Hex(currentFolder.toString());
    }

    public File findFileInDirectory(String sh1,String pathString) throws NoSuchFileException {
        File fileToFind=null;
        Path directory = Paths.get(pathString);
        File[] files;
        if (DirectoryIsEmpty(directory)) {
            throw new NoSuchFileException("There is no such file in this directory");
        } else {
            files = directory.toFile().listFiles();
            for(File f : files){
                if(f.getName().equals(directory.toString()+"/"+ sh1+".zip")){
                    fileToFind = f;
                    break;
                }
            }
            if(fileToFind == null){
                throw new NoSuchFileException("There is no such file in this directory");
            }
        }

        return fileToFind;
    }

    public File getTextFileFromObjectsFolder(String sh1)
    {
        File textFile = null;
        File fileToUnzip;
        Path objectsPath = Paths.get(this.repository.getPath().toString()+"/.magit/objects");

        try{
            fileToUnzip = findFileInDirectory(sh1+".zip",objectsPath.toString()); //לא משווה טוב שמות של קבצים
            unzip(fileToUnzip.getPath(),objectsPath.toString());
            textFile = findFileInDirectory(sh1,objectsPath.toString());
        } catch (NoSuchFileException e){
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

    private void createNewObjectFile(String Sh1, String content) {//creates a zip for objects
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

/*
    public void ExcecuteCommit(String message) {
        String mainFolderSh1 ="";
        Path objectsFolderPath = Paths.get(repository.getPath().toString() + "/.magit/objects");
        Commit newCommit = new Commit(message, username);

        if (DirectoryIsEmpty(objectsFolderPath)) {//if objects folder is empty - this is the first commit
            //ExcecuteFirstCommit(message);
            mainFolderSh1 = Sh1Directory(this.repository.getMainFolder(),repository.getPath(), newCommit.getDateCreated());
        }else {

        }
        newCommit.setMainFolderSh1(mainFolderSh1);
        createNewObjectFile(mainFolderSh1, repository.getMainFolder().toString());
    }
*/

/*
    private String Sh1Directory(Folder currentFolder ,Path currentPath, String dateModified) {
        File[] allFileComponents = currentPath.toFile().listFiles();
        String sh1Hex = "";
        String fileContent = "";

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
                    if (!Files.exists(Paths.get(currentPath.toString() + f.getName()))){//if a folder with the given sh1 does not exist)
                        Folder folder = new Folder();
                        sh1Hex = Sh1Directory(folder,Paths.get(f.getPath()), dateModified);
                        currentFolder.getComponents().add(new Folder.FolderComponent(
                                f.getName(), sh1Hex, "FOLDER", username, dateModified));
                    }
                }
            }
        }
        Collections.sort(currentFolder.getComponents());
        return DigestUtils.sha1Hex(currentFolder.toString());
    }
*/

