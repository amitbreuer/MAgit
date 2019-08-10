package engine;

import org.apache.commons.codec.digest.DigestUtils;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Manager {
    private String username;
    private Repository repository;

    public Manager() {
        username = "Administrator";
    }

    public void SetUsername(String username) {
        this.username = username;
    }

    public void CreateEmptyRepository(String repositoryPath) throws Exception {
        if (Files.exists(Paths.get(repositoryPath))) {
            throw new FileAlreadyExistsException("The path you have entered already exists");
        } else {
            this.repository = new Repository(repositoryPath);

            new File(repositoryPath).mkdirs();
            new File(repositoryPath + "/.magit").mkdir();
            new File(this.repository.GetBranchesDirPath()).mkdir();
            new File(this.repository.GetObjectsDirPath()).mkdir();

            createTextFile(this.repository.GetBranchesDirPath() + File.separator +"HEAD.txt", "master");
            createTextFile(this.repository.GetBranchesDirPath() + File.separator +"master.txt", "");
            Branch masterBranch = new Branch("master", null);
            this.repository.setHeadBranch(masterBranch);
            this.repository.getBranches().add(masterBranch);
        }
    }

    public void SwitchRepository(String repositoryPath) throws Exception {
        Path path = Paths.get(repositoryPath);
        boolean isRepository;
        String headBranchContent;
        String headBranchName;
        Branch headBranch;
        Commit prevCommit = null;

        if (this.repository != null && this.repository.getPath().equals(path)) {
            throw new Exception("You already working with the repository - " + path.toString());
        } else {
            isRepository = Files.exists(Paths.get(repositoryPath + "/.magit"));

            if (isRepository) {
                this.repository = new Repository(repositoryPath);
                headBranchName = this.repository.getHeadBranchNameFromBranchesDir();
                headBranchContent = convertTextFileToString(this.repository.GetBranchesDirPath() + File.separator + headBranchName + ".txt");
                if (!headBranchContent.equals("")) {
                    prevCommit = createCommitFromObjectFile(headBranchContent);
                }
                headBranch = new Branch(headBranchName, prevCommit);
                this.repository.setHeadBranch(headBranch);
                this.repository.getBranches().add(headBranch);
            } else {
                throw new Exception("This folder is not a repository in M.A.git");
            }
        }
    }

    private Boolean directoryIsEmpty(Path path) {
        File file = new File(path.toString());
        return file.list().length == 0;
    }

    public void ExecuteCommit(String message) {
        Folder currentWC;
        Commit newCommit = new Commit(username, message);
        Delta delta = new Delta();
        String currentMainFolderSha1;
        String lastCommitMainFolderSha1 = "";
        String headBranchFilePath = this.repository.GetBranchesDirPath() + File.separator + repository.getHeadBranch().getName() + ".txt";

        if (this.repository.getHeadBranch().getLastCommit() == null) {//it's the first commit
            currentWC = calculateDeltaAndWC(repository.getPath(), new Folder(), newCommit.getDateCreated(), delta);
        } else {
            lastCommitMainFolderSha1 = repository.getHeadBranch().getLastCommit().getMainFolder().sha1Folder();
            currentWC = calculateDeltaAndWC(repository.getPath(), repository.getHeadBranch().getLastCommit().getMainFolder(), newCommit.getDateCreated(), delta);
        }

        currentMainFolderSha1 = currentWC.sha1Folder();
        if (!lastCommitMainFolderSha1.equals(currentMainFolderSha1)) {//if there are changes since the last commit
            if (repository.getHeadBranch().getLastCommit() != null) { //if it's not the first commit
                newCommit.setPrevCommitSha1(repository.getHeadBranch().getLastCommit().Sha1Commit()); //point the new commit to the previous commit
            }
            repository.getHeadBranch().setLastCommit(newCommit);
            repository.getHeadBranch().getLastCommit().setMainFolder(currentWC);
            repository.getRecentlyUsedCommits().put(newCommit.Sha1Commit(),newCommit);

            createNewObjectFileFromDelta(delta);//create new object files for all new/updated files
            createNewObjectFile(currentWC.toString());//create object file that contains the new main folder
            createNewObjectFile(newCommit.toString());//create object file that contains the new commit
        }

        try {
            writeToFile(headBranchFilePath, newCommit.Sha1Commit());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Collections.sort(wCFiles, (o1, o2) -> o1.getName().compareTo(o2.getName()));
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

                if (WChasNext) {
                    currentWCFile = WCIterator.next();
                }
                if (comparedFileHasNext) {
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
            delta.getAddedFiles().add(new DeltaComponent(subFolder, newFilePath, addedFile.getName()));
        }
    }

    private void addFolderComponentToDeletedFilesList(Folder.ComponentData fc, Path path, Delta delta)  {

        if (fc.getFolderComponent() instanceof Folder) {
            List<Folder.ComponentData> components = ((Folder) fc.getFolderComponent()).getComponents();
            Path subPath = Paths.get(path.toString() + File.separator + fc.getName());
            for (Folder.ComponentData c : components) {
                addFolderComponentToDeletedFilesList(c, subPath, delta);
            }
        }
        delta.getDeletedFiles().add(new DeltaComponent(fc.getFolderComponent(), path, fc.getName()));
    }

    private File getTextFileFromObjectsDirectory(String fileName, String objectsFolderPath) {
        File textFile = null;
        String fileToUnzipPath = objectsFolderPath + File.separator + fileName + ".zip";

        try {
            unzip(fileToUnzipPath, objectsFolderPath);
            textFile = new File(objectsFolderPath + File.separator + fileName + ".txt");
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

    private void createTextFile(String filePath, String content) throws Exception {
        File newFile = new File(filePath);
        newFile.createNewFile();
        writeToFile(filePath, content);
    }

    private void writeToFile(String filePath, String content) throws Exception {
        File fileToEdit = new File(filePath);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileToEdit));
        writer.write("");
        writer.flush();
        writer.write(content);
        writer.close();
    }

    private void createZipFile(String zipFileName, String entryName, String content) throws Exception {
        File zipFile = new File(zipFileName);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        ZipEntry entry = new ZipEntry(entryName);
        out.putNextEntry(entry);
        byte[] data = content.getBytes();
        out.write(data, 0, data.length);
        out.closeEntry();
        out.close();
    }

    private void createNewObjectFile(String content) {//creates a zip for objects
        String Sha1 = DigestUtils.sha1Hex(content);
        String fileName = this.repository.GetObjectsDirPath() + File.separator + Sha1 + ".txt";
        String zipFileName = this.repository.GetObjectsDirPath() + File.separator + Sha1 + ".zip";
        try {
            createTextFile(fileName, content);
            createZipFile(zipFileName, Sha1 + ".txt", content);
            new File(fileName).delete();
        } catch (Exception e) {
        }
    }

    static String convertTextFileToString(String filePath) {
        String returnValue = null;
        File fileToRead = new File(filePath);
        try {
            List<String> contentLines = Files.readAllLines(fileToRead.toPath());
            returnValue = createStringFromListOfStrings(contentLines);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    private Delta getDelta(){
        Folder currentWC;
        Path objectsDirPath = Paths.get(repository.GetObjectsDirPath());
        Delta delta = new Delta();

        if (directoryIsEmpty(Paths.get(objectsDirPath.toString()))) {//it's the first commit
            currentWC = calculateDeltaAndWC(repository.getPath(), new Folder(), new Commit("", "").getDateCreated(), delta);
        } else {
            currentWC = calculateDeltaAndWC(repository.getPath(), repository.getHeadBranch().getLastCommit().getMainFolder(), new Commit("", "").getDateCreated(), delta);
        }

        return delta;
    }

    public String GetStatus() {
        Delta delta = getDelta();
        String deltaToString;

        if (delta.isEmpty()) {
            deltaToString = "There are no changes since the last commit";
        } else {
            deltaToString = delta.toString();
        }
        return deltaToString;
    }

    public void spanWCFromCommit(Commit commitToSpan) {
        Path pathOfRepository = this.repository.getPath();
        File repositoryToDelete = pathOfRepository.toFile();
        deleteFileFromWorkingCopy(repositoryToDelete);
        List<Folder.ComponentData> componentDataList = commitToSpan.getMainFolder().getComponents();
        for (Folder.ComponentData fc : componentDataList) {
            addFolderComponentToDirectory(pathOfRepository, fc.getFolderComponent(), fc.getName());
        }
    }

    private void addFolderComponentToDirectory(Path pathOfDirectory, FolderComponent folderComponent, String folderComponentName) {
        if (folderComponent instanceof Folder) {
            createDirectory(pathOfDirectory.toString() + File.separator + folderComponentName);
            Path componentPath;
            for (Folder.ComponentData componentData : ((Folder) folderComponent).getComponents()) {
                componentPath = Paths.get(pathOfDirectory.toString() + File.separator + folderComponentName);
                addFolderComponentToDirectory(componentPath, componentData.getFolderComponent(), componentData.getName());
            }
        } else {
            try {
                createTextFile(pathOfDirectory.toString() + File.separator + folderComponentName, folderComponent.toString());
            } catch (Exception ex) {
            }
        }
    }

    private void createDirectory(String folderName) {
        File newDirectory = new File(folderName);
        newDirectory.mkdir();
    }

    private Commit createCommitFromObjectFile(String commitSha1) {
        String objectsFolderPath = repository.GetObjectsDirPath();
        List<String> commitTextFileContent;
        FolderComponent mainFolder;
        List<String> mainFolderTextFileContent = null;
        File mainFolderTextFile = null;
        File commitTextFile = null;
        String prevCommitSha1 = null;
        String mainFolderSha1 = null;
        String dateCreated = null;
        String creator = null;
        String message = null;
        Commit newCommit;

        try {
            commitTextFile = getTextFileFromObjectsDirectory(commitSha1, objectsFolderPath);
            commitTextFileContent = Files.readAllLines(commitTextFile.toPath());
            prevCommitSha1 = commitTextFileContent.get(0);
            if(prevCommitSha1.equals("null")){
                prevCommitSha1 = null;
            }
            mainFolderSha1 = commitTextFileContent.get(1);
            dateCreated = commitTextFileContent.get(2);
            creator = commitTextFileContent.get(3);
            message = commitTextFileContent.get(4);
            mainFolderTextFile = getTextFileFromObjectsDirectory(mainFolderSha1, objectsFolderPath);
            mainFolderTextFileContent = Files.readAllLines(mainFolderTextFile.toPath());
        } catch (IOException e) {
        }

        mainFolder = createFolderComponentFromTextFileLines(true, mainFolderTextFileContent, objectsFolderPath);

        newCommit = new Commit(creator, message);
        newCommit.setPrevCommitSha1(prevCommitSha1);
        newCommit.setDateCreated(dateCreated);
        newCommit.setMainFolder((Folder)mainFolder);

        commitTextFile.delete();
        mainFolderTextFile.delete();
        return newCommit;
    }

    private FolderComponent createFolderComponentFromTextFileLines(Boolean isFolder, List<String> contentLines, String objectsFolderPath) {
        FolderComponent newFolderComponent = null;
        FolderComponent subFolderComonent = null;
        List<String> subComponentContentLines = null;
        Folder.ComponentData newComponentData;
        File subComponentTextFile;
        String contentString;

        if (!isFolder) {
            contentString = createStringFromListOfStrings(contentLines);
            Blob newblob = new Blob(contentString);
            newFolderComponent = newblob;
        } else {
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

    private Folder.ComponentData getComponentDataFromString(String str) {
        StringTokenizer tokenizer;
        tokenizer = new StringTokenizer(str, ",");
        Folder.ComponentData newComponentData;

        String name = tokenizer.nextToken();
        String sha1 = tokenizer.nextToken();
        String type = tokenizer.nextToken();
        String creator = tokenizer.nextToken();
        String date = tokenizer.nextToken();
        newComponentData = new Folder.ComponentData(name, sha1, type, creator, date);

        return newComponentData;
    }

    private void deleteFileFromWorkingCopy(File fileToDelete) { // "clear WC" instead of "deleteFileFromDirectory"?
        if (!fileToDelete.isFile()) {
            List<File> allFolderFiles = Arrays.asList(fileToDelete.listFiles());
            for (File file : allFolderFiles) {
                if (!file.getName().equals(".magit")) {
                    deleteFileFromWorkingCopy(file);
                }
            }
        }
        fileToDelete.delete();
    }

    private static String createStringFromListOfStrings(List<String> stringLines) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (; i < stringLines.size() - 1; i++) {
            sb.append(stringLines.get(i));
            sb.append("\r\n");
        }
        if (stringLines.size() > 0) {
            sb.append(stringLines.get(i));
        }

        return sb.toString();
    }

    public boolean commitsWereExecuted(){
        return this.repository.getHeadBranch().getLastCommit() != null;
    }

    public List<String> GetDataOfAllFilesOfCurrentCommit(){
        return getComponentsDataInStringList(this.repository.getHeadBranch().getLastCommit().getMainFolder(), this.repository.getPath().toString());
    }

    private List<String> getComponentsDataInStringList(Folder currentFolder, String currentPathString) {
        List<String> allComponentsdata = new ArrayList<>();

        String addedString;
        for (Folder.ComponentData fcd : currentFolder.getComponents()) {

            addedString = "Full name: " + currentPathString + "/" + fcd.getName() + "\r\n" +
                    "Type: " + fcd.getType() + "\r\n" +
                    "SHA-1: " + fcd.getSha1() + "\r\n" +
                    "Last modifier:" + fcd.getLastModifier() + "\r\n" +
                    "Last modified date:" + fcd.getLastModifiedDate() + "\r\n";
            allComponentsdata.add(addedString);

            if (fcd.getType().equals("Folder")) {
                allComponentsdata.addAll(getComponentsDataInStringList((Folder) fcd.getFolderComponent(), currentPathString + "/" + fcd.getName()));
            }
        }
        return allComponentsdata;
    }

    public String GetRepositoryDetails() {
        String repositoryDetails = new String();
        repositoryDetails += "Repository's full name: " + this.repository.getPath() + "\r\n" +
                "Active user: " + username;
        return repositoryDetails;
    }

    private void updateRepositoryBranchesList() {
        Path branchesPath = Paths.get(this.repository.GetBranchesDirPath());
        File[] branchesFiles = branchesPath.toFile().listFiles();
        StringTokenizer tokenizer;
        for (File f : branchesFiles) {
            if (!f.getName().equals("HEAD.txt")) {
                tokenizer = new StringTokenizer(f.getName(),".");
                if (!this.repository.branchExistsInList(tokenizer.nextToken())) {
                    this.repository.getBranches().add(createBranchFromObjectFileSha1(f.getName(), convertTextFileToString(branchesPath + "/" + f.getName())));
                }
            }
        }
    }

    public List<String> GetAllBranchesDetails() {
        List<String> allBranchesDetails = new ArrayList<>();
        List<Branch> branches = this.repository.getBranches();
        updateRepositoryBranchesList();
        for (Branch branch : branches) {
            if (this.repository.getHeadBranch().equals(branch)){
                allBranchesDetails.add("head branch: ");
            }
            allBranchesDetails.add(branch.getDetails());
        }
        return allBranchesDetails;
    }

    private Branch createBranchFromObjectFileSha1(String branchName, String sha1) {
        Commit newCommit = createCommitFromObjectFile(sha1);
        StringTokenizer tokenizer = new StringTokenizer(branchName,"."); // to cut the extention ".txt"
        Branch newBranch = new Branch(tokenizer.nextToken(), newCommit);
        return newBranch;
    }

    public void CreateNewBranch(String branchName,Boolean checkoutNewBranch) throws Exception {
        String branchesDirPath = this.repository.GetBranchesDirPath();
        if(Files.exists(Paths.get(branchesDirPath + File.separator + branchName + ".txt"))){
            throw new Exception("The branch " + branchName + "already exists");
        }
        Branch newBranch = new Branch(branchName,this.repository.getHeadBranch().getLastCommit());
        createTextFile(branchesDirPath + File.separator + branchName + ".txt",newBranch.getLastCommit().Sha1Commit());
        this.repository.getBranches().add(newBranch);
        if(checkoutNewBranch) {
            if (thereAreUncommittedChanges()) {
                throw new Exception("Checkout failed. There are uncommitted changes");
            } else {
                setHeadBranch(newBranch);
            }
        }
    }

    public Boolean thereAreUncommittedChanges() {
        Delta delta = getDelta();
        return !delta.isEmpty();
    }

    public void DeleteBranch(String branchName) throws Exception {
        String branchToDeleteFilePath = this.repository.GetBranchesDirPath() + File.separator + branchName + ".txt";
        if (this.repository.getHeadBranch().getName().equals(branchName)) { // if this branch is the head branch
            throw new Exception("This is the head branch\r\nPlease checkout another branch before deleting this one");
        }
        if (!Files.exists(Paths.get(branchToDeleteFilePath))) {
            throw new Exception("There is no branch with the name " + branchName);
        }
        if (this.repository.branchExistsInList(branchName)) {
            Branch branchToDelete = this.repository.FindBranchByName(branchName);
            this.repository.getBranches().remove(branchToDelete);
        }
        Files.delete(Paths.get(branchToDeleteFilePath));
    }

    private void setHeadBranch(Branch newHeadBranch) throws Exception {
        String HEADFilePath = this.repository.getPath().toString() + "/.magit/branches/HEAD.txt";
        if(this.repository.getHeadBranch().equals(newHeadBranch)){
            throw new Exception("This branch is already the head branch");
        }
        this.repository.setHeadBranch(newHeadBranch);
        writeToFile(HEADFilePath,newHeadBranch.getName());
    }

    public void CheckOut(String branchToCheckout) throws Exception {
        Branch newHeadBranch;
        String branchToCheckoutFilePath = this.repository.GetBranchesDirPath() + File.separator + branchToCheckout + ".txt";
        if (!this.repository.branchExistsInList(branchToCheckout)) {
            if (!Files.exists(Paths.get(branchToCheckoutFilePath))) {
                throw new Exception("There is no branch with the name " + branchToCheckout);
            } else {
                String branchToCheckoutCommitSha1 = convertTextFileToString(branchToCheckoutFilePath);
                newHeadBranch = createBranchFromObjectFileSha1(branchToCheckout, branchToCheckoutCommitSha1);
                this.repository.getBranches().add(newHeadBranch);
            }
        } else {
            newHeadBranch = this.repository.FindBranchByName(branchToCheckout);
        }

        if (!this.repository.getHeadBranch().getLastCommit().equals(newHeadBranch.getLastCommit())) { // if the branch points to different commit
            spanWCFromCommit(newHeadBranch.getLastCommit());
        }
        setHeadBranch(newHeadBranch);
    }

    public String GetActiveBranchHistory() {
        StringBuilder branchHistory = new StringBuilder();

        if (this.repository.getHeadBranch().getLastCommit() == null) {
            branchHistory.append("There are no commits in current branch");
        } else {
            List<Commit> activeBranchCommits = getAllCommitsOfActiveBrnach();

            branchHistory.append("All the commits in this branch: \r\n");
            for (Commit currentCommit : activeBranchCommits) {
                branchHistory.append( "Sha1: ");
                branchHistory.append(currentCommit.Sha1Commit());
                branchHistory.append("\r\n");

                branchHistory.append("message: ");
                branchHistory.append(currentCommit.getMessage());
                branchHistory.append("\r\n");

                branchHistory.append("Date created: ");
                branchHistory.append(currentCommit.getDateCreated());
                branchHistory.append("\r\n");

                branchHistory.append("Creator: ");
                branchHistory.append(currentCommit.getCreator());
                branchHistory.append("\r\n\r\n");
            }
        }
        return branchHistory.toString();
    }

    List<Commit> getAllCommitsOfActiveBrnach() {
        List<Commit> allCommits = new ArrayList<>();
        Commit currentCommit = this.repository.getHeadBranch().getLastCommit();
        String prevCommitSha1;

        if (currentCommit != null) {
            allCommits.add(currentCommit);
            prevCommitSha1 = currentCommit.getPrevCommitSha1();

            while (prevCommitSha1 != null) {
                if (this.repository.getRecentlyUsedCommits().containsKey(prevCommitSha1)) {
                    currentCommit =this.repository.getRecentlyUsedCommits().get(prevCommitSha1);
                } else {
                    currentCommit =createCommitFromObjectFile(prevCommitSha1);
                    this.repository.getRecentlyUsedCommits().put(prevCommitSha1,currentCommit);
                }
                allCommits.add(currentCommit);
                prevCommitSha1 = currentCommit.getPrevCommitSha1();
            }
        }
        return allCommits;
    }
}