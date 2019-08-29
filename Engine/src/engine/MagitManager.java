package engine;

import app.AppController;
import exceptions.XmlPathContainsNonRepositoryObjectsException;
import exceptions.XmlRepositoryAlreadyExistsException;
import generated.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MagitManager {
    private AppController controller;
    private String username;
    private Repository repository;
    private XmlManager xmlManager;

    public Repository GetCurrentRepository() {
        return repository;
    }

    public MagitManager() {
        username = "Administrator";
    }

    public void setController(AppController controller) {
        this.controller = controller;
    }

    public void SetUsername(String username) {
        this.username = username;
    }

    public Boolean HasActiveRepository() {
        return this.repository != null;
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

            createTextFile(this.repository.GetBranchesDirPath() + File.separator + "HEAD.txt", "master");
            createTextFile(this.repository.GetBranchesDirPath() + File.separator + "master.txt", "");
            Branch masterBranch = new Branch("master", null);
            this.repository.setHeadBranch(masterBranch);
            this.repository.getBranches().add(masterBranch);
        }
    }

    public void SwitchRepository(String repositoryPath) throws Exception {
        Path path = null;
        try {
            path = Paths.get(repositoryPath);
        } catch (Exception ex) {
            throw new Exception("Error: the path you have entered is not valid");
        }
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
                if (!headBranchContent.equals("")) { // if there is a commit sha1 in head branch file
                    prevCommit = createCommitFromObjectFile(headBranchContent);
                }
                headBranch = new Branch(headBranchName, prevCommit);
                this.repository.setHeadBranch(headBranch);
                this.repository.getBranches().add(headBranch);
                updateRepositoryBranchesList();
            } else {
                throw new Exception("This folder is not a repository in M.A.git");
            }
        }
    }

    private Boolean directoryIsEmpty(Path path) {
        File file = new File(path.toString());
        return file.list().length == 0;
    }

    private void createNewObjectFileFromDelta(Delta delta) throws Exception {
        for (DeltaComponent dc : delta.getUpdatedFiles()) {
            createNewObjectFile(dc.getFolderComponent().toString());
        }
        for (DeltaComponent dc : delta.getAddedFiles()) {
            createNewObjectFile(dc.getFolderComponent().toString());
        }
    }

    private Folder createFolderFromWC(Path currentPath, String currentDate) throws IOException {
        Folder folderToCreate = new Folder();
        List<File> wcFiles = Arrays.asList(currentPath.toFile().listFiles());
        String sha1;
        String fileContent;
        for (File file : wcFiles) {
            if (!file.getName().equals(".magit")) {
                if (file.isFile()) {
                    fileContent = convertTextFileToString(file.getPath());
                    sha1 = DigestUtils.sha1Hex(fileContent);
                    Blob newBlob = new Blob(fileContent);
                    folderToCreate.getComponents().add(new Folder.ComponentData(file.getName(), sha1, newBlob, username, currentDate));
                } else {
                    Path subFolderPath = Paths.get(file.getPath());
                    Folder subFolder = createFolderFromWC(subFolderPath, currentDate);
                    sha1 = subFolder.sha1Folder();
                    folderToCreate.getComponents().add(new Folder.ComponentData(file.getName(), sha1, subFolder, username, currentDate));
                }
            }
        }

        Collections.sort(folderToCreate.getComponents());
        return folderToCreate;
    }

    private void calculateDeltaBetweenTwoFolders(Folder newFolder, Folder oldFolder, String currentPath, Delta delta) throws IOException {
        int nameDiff;
        Folder.ComponentData currentNewComponent = null;
        Folder.ComponentData currentOldComponent = null;
        Iterator<Folder.ComponentData> newIterator = newFolder.getComponents().iterator();
        Iterator<Folder.ComponentData> oldIterator = oldFolder.getComponents().iterator();
        boolean newHasNext = newIterator.hasNext();
        boolean oldHasNext = oldIterator.hasNext();

        if (newHasNext) {
            currentNewComponent = newIterator.next();
        }
        if (oldHasNext) {
            currentOldComponent = oldIterator.next();
        }

        while (newHasNext && oldHasNext) {
            nameDiff = currentNewComponent.getName().compareTo(currentOldComponent.getName());
            if (nameDiff == 0) { //if it's the same name
                if (currentNewComponent.getFolderComponent() instanceof Blob) { //if the file is a txt file
                    if (currentNewComponent.getSha1().equals(currentOldComponent.getSha1())) { //if the file didn't changed
                        currentNewComponent.setLastModifiedDate(currentOldComponent.getLastModifiedDate());
                        currentNewComponent.setLastModifier(currentOldComponent.getLastModifier());
                    } else {
                        delta.getUpdatedFiles().add(new DeltaComponent(currentNewComponent.getFolderComponent(), currentPath, currentNewComponent.getName()));
                    }
                } else {
                    String subFolderPath = currentPath + File.separator + currentNewComponent.getName();
                    Folder subNewFolder = (Folder) currentNewComponent.getFolderComponent();
                    Folder subOldFolder = (Folder) currentOldComponent.getFolderComponent();
                    calculateDeltaBetweenTwoFolders(subNewFolder, subOldFolder, subFolderPath, delta);
                    currentNewComponent.setSha1(subNewFolder.sha1Folder());
                    if (currentNewComponent.getSha1().equals(subOldFolder.sha1Folder())) { //if the folder didn't change
                        currentNewComponent.setLastModifiedDate(currentOldComponent.getLastModifiedDate());
                        currentNewComponent.setLastModifier(currentOldComponent.getLastModifier());
                    } else {
                        delta.getUpdatedFiles().add(new DeltaComponent(subNewFolder, currentPath, currentNewComponent.getName()));
                    }
                }

                newHasNext = newIterator.hasNext();
                oldHasNext = oldIterator.hasNext();

                if (newHasNext) {
                    currentNewComponent = newIterator.next();
                }
                if (oldHasNext) {
                    currentOldComponent = oldIterator.next();
                }
            } else if (nameDiff < 0) { //file added
                addComponentToAddedFilesList(currentNewComponent, currentPath, delta);
                newHasNext = newIterator.hasNext();

                if (newHasNext) {
                    currentNewComponent = newIterator.next();
                }

            } else { //file deleted
                addFolderComponentToDeletedFilesList(currentOldComponent, currentPath, delta);
                oldHasNext = oldIterator.hasNext();
                if (oldHasNext) {
                    currentOldComponent = oldIterator.next();
                }
            }
        }

        while (newHasNext) {
            addComponentToAddedFilesList(currentNewComponent, currentPath, delta);
            newHasNext = newIterator.hasNext();
            if (newHasNext) {
                currentNewComponent = newIterator.next();
            }
        }
        while (oldHasNext) {
            addFolderComponentToDeletedFilesList(currentOldComponent, currentPath, delta);
            oldHasNext = oldIterator.hasNext();
            if (oldHasNext) {
                currentOldComponent = oldIterator.next();
            }
        }
    }

    private void addComponentToAddedFilesList(Folder.ComponentData folderComponentToAdd, String componentPath, Delta delta) throws IOException {
        FolderComponent componentToAdd = folderComponentToAdd.getFolderComponent();
        if (componentToAdd instanceof Folder) {
            String subFolderPath = componentPath + File.separator + folderComponentToAdd.getName();
            List<Folder.ComponentData> components = ((Folder) componentToAdd).getComponents();
            for (Folder.ComponentData cd : components) {
                addComponentToAddedFilesList(cd, subFolderPath, delta);
            }
        }
        delta.getAddedFiles().add(new DeltaComponent(componentToAdd, componentPath, folderComponentToAdd.getName()));
    }

    private void addFolderComponentToDeletedFilesList(Folder.ComponentData componentDataToDelete, String path, Delta delta) {
        FolderComponent componentToDelete = componentDataToDelete.getFolderComponent();
        if (componentToDelete instanceof Folder) {
            List<Folder.ComponentData> components = ((Folder) componentToDelete).getComponents();
            String subPath = path + File.separator + componentDataToDelete.getName();
            for (Folder.ComponentData c : components) {
                addFolderComponentToDeletedFilesList(c, subPath, delta);
            }
        }
        delta.getDeletedFiles().add(new DeltaComponent(componentToDelete, path, componentDataToDelete.getName()));
    }

    private Delta getDelta() throws IOException {
        Folder currentWC;
        Delta delta = new Delta();
        String dateCreated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));
        currentWC = createFolderFromWC(repository.getPath(), dateCreated);
        if (repository.getHeadBranch().getLastCommit() == null) {//it's the first commit
            calculateDeltaBetweenTwoFolders(currentWC, new Folder(), repository.getPath().toString(), delta);
        } else {
            calculateDeltaBetweenTwoFolders(currentWC, repository.getHeadBranch().getLastCommit().getMainFolder(), repository.getPath().toString(), delta);
        }

        return delta;
    }

    public String GetStatus() throws IOException {
        Delta delta = getDelta();
        String deltaToString;

        if (delta.isEmpty()) {
            deltaToString = "There are no changes since the last commit";
        } else {
            deltaToString = delta.toString();
        }
        return deltaToString;
    }

    public void spanWCFromCommit(Commit commitToSpan) throws Exception {
        Path pathOfRepository = this.repository.getPath();
        File repositoryToDelete = pathOfRepository.toFile();
        deleteFileFromWorkingCopy(repositoryToDelete);
        List<Folder.ComponentData> componentDataList = commitToSpan.getMainFolder().getComponents();
        for (Folder.ComponentData fc : componentDataList) {
            addFolderComponentToDirectory(pathOfRepository, fc.getFolderComponent(), fc.getName());
        }
    }

    private void createDirectory(String folderName) {
        File newDirectory = new File(folderName);
        newDirectory.mkdir();
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

    public boolean commitsWereExecuted() {
        return this.repository.getHeadBranch().getLastCommit() != null;
    }

    public List<String> GetDataOfAllFilesOfCurrentCommit() {
        return getComponentsDataInStringList(this.repository.getHeadBranch().getLastCommit().getMainFolder(), this.repository.getPath().toString());
    }

    private List<String> getComponentsDataInStringList(Folder currentFolder, String currentPathString) {
        List<String> allComponentsdata = new ArrayList<>();

        String addedString;
        for (Folder.ComponentData fcd : currentFolder.getComponents()) {

            addedString = "Full name: " + currentPathString + File.separator + fcd.getName() + "\r\n" +
                    "Type: " + fcd.getType() + "\r\n" +
                    "SHA-1: " + fcd.getSha1() + "\r\n" +
                    "Last modifier:" + fcd.getLastModifier() + "\r\n" +
                    "Last modified date:" + fcd.getLastModifiedDate() + "\r\n";
            allComponentsdata.add(addedString);

            if (fcd.getType().equals("Folder")) {
                allComponentsdata.addAll(getComponentsDataInStringList((Folder) fcd.getFolderComponent(), currentPathString + File.separator + fcd.getName()));
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

    private void updateRepositoryBranchesList() throws IOException {
        Path branchesPath = Paths.get(this.repository.GetBranchesDirPath());
        File[] branchesFiles = branchesPath.toFile().listFiles();
        StringTokenizer tokenizer;
        for (File f : branchesFiles) {
            if (!f.getName().equals("HEAD.txt")) {
                tokenizer = new StringTokenizer(f.getName(), ".");
                if (!this.repository.branchExistsInList(tokenizer.nextToken())) {
                    this.repository.getBranches().add(createBranchFromObjectFileSha1(f.getName(), convertTextFileToString(branchesPath + "/" + f.getName())));
                }
            }
        }
    }

    public List<String> GetAllBranchesDetails() throws IOException {
        List<String> allBranchesDetails = new ArrayList<>();
        List<Branch> branches = this.repository.getBranches();
        updateRepositoryBranchesList();
        for (Branch branch : branches) {
            if (this.repository.getHeadBranch().equals(branch)) {
                allBranchesDetails.add("head branch: ");
            }
            allBranchesDetails.add(branch.getDetails());
        }
        return allBranchesDetails;
    }

    private Branch createBranchFromObjectFileSha1(String branchName, String sha1) throws IOException {
        Commit newCommit = createCommitFromObjectFile(sha1);
        StringTokenizer tokenizer = new StringTokenizer(branchName, "."); // to cut the extention ".txt"
        Branch newBranch = new Branch(tokenizer.nextToken(), newCommit);
        return newBranch;
    }

    public void CreateNewBranch(String branchName, Boolean checkoutNewBranch) throws Exception {
        String branchesDirPath = this.repository.GetBranchesDirPath();
        if (Files.exists(Paths.get(branchesDirPath + File.separator + branchName + ".txt"))) {
            throw new Exception("The branch " + branchName + "already exists");
        }
        Branch newBranch = new Branch(branchName, this.repository.getHeadBranch().getLastCommit());
        createTextFile(branchesDirPath + File.separator + branchName + ".txt", newBranch.getLastCommit().Sha1Commit());
        this.repository.getBranches().add(newBranch);
        if (checkoutNewBranch) {
            if (thereAreUncommittedChanges()) {
                throw new Exception("Checkout failed. There are uncommitted changes");
            } else {
                setHeadBranch(newBranch);
            }
        }
    }

    public Boolean thereAreUncommittedChanges() throws IOException {
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
        if (this.repository.getHeadBranch().equals(newHeadBranch)) {
            throw new Exception("This branch is already the head branch");
        }
        this.repository.setHeadBranch(newHeadBranch);
        writeToFile(HEADFilePath, newHeadBranch.getName());
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

    public String GetActiveBranchHistory() throws IOException {
        StringBuilder branchHistory = new StringBuilder();

        if (this.repository.getHeadBranch().getLastCommit() == null) {
            branchHistory.append("There are no commits in current branch");
        } else {
            List<Commit> activeBranchCommits = getAllCommitsOfActiveBrnach();

            branchHistory.append("All the commits in this branch: \r\n");
            for (Commit currentCommit : activeBranchCommits) {
                branchHistory.append("Sha1: ");
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

    List<Commit> getAllCommitsOfActiveBrnach() throws IOException {
        List<Commit> allCommits = new ArrayList<>();
        Commit currentCommit = this.repository.getHeadBranch().getLastCommit();
        String prevCommitSha1;

        if (currentCommit != null) {
            allCommits.add(currentCommit);
            prevCommitSha1 = currentCommit.getPrevCommitSha1();

            while (prevCommitSha1 != null) {
                if (this.repository.getRecentlyUsedCommits().containsKey(prevCommitSha1)) {
                    currentCommit = this.repository.getRecentlyUsedCommits().get(prevCommitSha1);
                } else {
                    currentCommit = createCommitFromObjectFile(prevCommitSha1);
                }
                allCommits.add(currentCommit);
                prevCommitSha1 = currentCommit.getPrevCommitSha1();
            }
        }
        return allCommits;
    }

    private Folder createFolderFromMagitFolder(MagitSingleFolder mf) throws Exception {
        Folder folderToCreate = new Folder();
        Folder subFolderToCreate;
        Blob blobToCreate;

        List<Item> folderItems = mf.getItems().getItem();
        for (Item item : folderItems) {
            if (item.getType().equals("blob")) {
                MagitBlob magitBlob = this.xmlManager.existingBlobs.get(item.getId());
                blobToCreate = createBlobFromMagitSingleBlob(magitBlob);
                folderToCreate.getComponents().add(new Folder.ComponentData(magitBlob.getName(), DigestUtils.sha1Hex(magitBlob.getContent()),
                        blobToCreate, magitBlob.getLastUpdater(), magitBlob.getLastUpdateDate()));
            } else {
                MagitSingleFolder subMagitFolder = this.xmlManager.existingFolders.get(item.getId());
                subFolderToCreate = createFolderFromMagitFolder(subMagitFolder);
                folderToCreate.getComponents().add(new Folder.ComponentData(subMagitFolder.getName(), subFolderToCreate.sha1Folder(),
                        subFolderToCreate, subMagitFolder.getLastUpdater(), subMagitFolder.getLastUpdateDate()));
            }
        }
        Collections.sort(folderToCreate.getComponents());
        createNewObjectFile(folderToCreate.toString());
        return folderToCreate;
    }

    private Blob createBlobFromMagitSingleBlob(MagitBlob magitBlob) throws Exception {
        Blob blobToCreate = new Blob(magitBlob.getContent());
        createNewObjectFile(blobToCreate.toString());
        return blobToCreate;
    }

    public void ExecuteCommit(String message) throws Exception {
        //controller.ShowSingleCommitFilesTree(this.repository.getHeadBranch().getLastCommit().Sha1Commit());

        Folder currentWC;
        Folder lastCommitMainFolder;
        Commit newCommit = new Commit(username, message);
        Delta delta = new Delta();
        String currentMainFolderSha1;
        String lastCommitMainFolderSha1 = "";
        String headBranchFilePath = this.repository.GetBranchesDirPath() + File.separator + repository.getHeadBranch().getName() + ".txt";
        String dateCreated = newCommit.getDateCreated();
        currentWC = createFolderFromWC(repository.getPath(), dateCreated);

        if (this.repository.getHeadBranch().getLastCommit() == null) {//it's the first commit
            calculateDeltaBetweenTwoFolders(currentWC, new Folder(), dateCreated, delta);
        } else {
            lastCommitMainFolder = repository.getHeadBranch().getLastCommit().getMainFolder();
            lastCommitMainFolderSha1 = lastCommitMainFolder.sha1Folder();
            calculateDeltaBetweenTwoFolders(currentWC, lastCommitMainFolder, dateCreated, delta);
        }

        currentMainFolderSha1 = currentWC.sha1Folder();
        if (!lastCommitMainFolderSha1.equals(currentMainFolderSha1)) {//if there are changes since the last commit
            if (repository.getHeadBranch().getLastCommit() != null) { //if it's not the first commit
                newCommit.setPrevCommitSha1(repository.getHeadBranch().getLastCommit().Sha1Commit()); //point the new commit to the previous commit
            }
            repository.getHeadBranch().setLastCommit(newCommit);
            repository.getHeadBranch().getLastCommit().setMainFolder(currentWC);
            repository.getRecentlyUsedCommits().put(newCommit.Sha1Commit(), newCommit);

            createNewObjectFileFromDelta(delta);//create new object files for all new/updated files
            createNewObjectFile(currentWC.toString());//create object file that contains the new app folder
            createNewObjectFile(newCommit.toString());//create object file that contains the new commit
        }

        try {
            writeToFile(headBranchFilePath, newCommit.Sha1Commit());
        } catch (Exception e) {
            ////what if the action didnt success? do we need to delete all created filed?
        }
    }

    private File getTextFileFromObjectsDirectory(String fileName, String objectsFolderPath) {
        File textFile = null;
        String fileToUnzipPath = objectsFolderPath + File.separator + fileName + ".zip";

        try {
            unzip(fileToUnzipPath, objectsFolderPath);
            textFile = new File(objectsFolderPath + File.separator + fileName + ".txt");
        } catch (IOException e) {
            //////////////////////////////// what if it failed?
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

    private void createNewObjectFile(String content) throws Exception { //creates a zip for objects
        String Sha1 = DigestUtils.sha1Hex(content);
        String fileName = this.repository.GetObjectsDirPath() + File.separator + Sha1 + ".txt";
        String zipFileName = this.repository.GetObjectsDirPath() + File.separator + Sha1 + ".zip";
        createTextFile(fileName, content);
        createZipFile(zipFileName, Sha1 + ".txt", content);
        new File(fileName).delete();
    }

    static String convertTextFileToString(String filePath) throws IOException {
        String returnValue = null;
        File fileToRead = new File(filePath);

        List<String> contentLines = Files.readAllLines(fileToRead.toPath());
        returnValue = createStringFromListOfStrings(contentLines);

        return returnValue;
    }

    private void addFolderComponentToDirectory(Path pathOfDirectory, FolderComponent folderComponent, String folderComponentName) throws Exception {
        if (folderComponent instanceof Folder) {
            createDirectory(pathOfDirectory.toString() + File.separator + folderComponentName);
            Path componentPath;
            for (Folder.ComponentData componentData : ((Folder) folderComponent).getComponents()) {
                componentPath = Paths.get(pathOfDirectory.toString() + File.separator + folderComponentName);
                addFolderComponentToDirectory(componentPath, componentData.getFolderComponent(), componentData.getName());
            }
        } else {
            createTextFile(pathOfDirectory.toString() + File.separator + folderComponentName, folderComponent.toString());
        }
    }

    public Commit createCommitFromObjectFile(String commitSha1) throws IOException {
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


        commitTextFile = getTextFileFromObjectsDirectory(commitSha1, objectsFolderPath);
        commitTextFileContent = Files.readAllLines(commitTextFile.toPath());
        prevCommitSha1 = commitTextFileContent.get(0);
        if (prevCommitSha1.equals("null")) {
            prevCommitSha1 = null;
        }
        mainFolderSha1 = commitTextFileContent.get(1);
        dateCreated = commitTextFileContent.get(2);
        creator = commitTextFileContent.get(3);
        message = commitTextFileContent.get(4);
        mainFolderTextFile = getTextFileFromObjectsDirectory(mainFolderSha1, objectsFolderPath);
        mainFolderTextFileContent = Files.readAllLines(mainFolderTextFile.toPath());


        mainFolder = createFolderComponentFromTextFileLines(true, mainFolderTextFileContent, objectsFolderPath);

        newCommit = new Commit(creator, message);
        newCommit.setPrevCommitSha1(prevCommitSha1);
        newCommit.setDateCreated(dateCreated);
        newCommit.setMainFolder((Folder) mainFolder);
        this.repository.getRecentlyUsedCommits().put(newCommit.Sha1Commit(), newCommit);
        commitTextFile.delete();
        mainFolderTextFile.delete();
        return newCommit;
    }

    private FolderComponent createFolderComponentFromTextFileLines(Boolean isFolder, List<String> contentLines, String objectsFolderPath) throws IOException {
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

                subComponentContentLines = Files.readAllLines(subComponentTextFile.toPath());

                subFolderComonent = createFolderComponentFromTextFileLines(newComponentData.getType().equals("Folder"), subComponentContentLines, objectsFolderPath);
                newComponentData.setFolderComponent(subFolderComonent);
                newFolder.getComponents().add(newComponentData);
                subComponentTextFile.delete();
            }
            newFolderComponent = newFolder;
        }

        return newFolderComponent;
    }

    public void ResetHeadBranch(String sha1) throws Exception {
        Commit commitToResetTo = null;

        if (this.repository.getRecentlyUsedCommits().containsKey(sha1)) {
            commitToResetTo = this.repository.getRecentlyUsedCommits().get(sha1);
        } else {
            Path commitTextFilePath = Paths.get(this.repository.GetObjectsDirPath()
                    + File.separator + sha1 + File.separator + ".zip");
            if (!Files.exists(commitTextFilePath)) {
                throw new Exception("There is no commit " + sha1);
            }
            commitToResetTo = createCommitFromObjectFile(sha1);
        }
        this.repository.getHeadBranch().setLastCommit(commitToResetTo);
        String headBranchFilePath = repository.GetBranchesDirPath() + File.separator
                + repository.getHeadBranch().getName() + ".txt";
        writeToFile(headBranchFilePath, commitToResetTo.Sha1Commit());
    }

    public void ValidateAndLoadXMLRepository(String fileFullName) throws Exception {
        this.xmlManager = new XmlManager();
        this.xmlManager.createMagitRepositoryFromXml(fileFullName);
        if (Files.exists(Paths.get(this.xmlManager.getMagitRepository().getLocation()))) {
            if (Files.exists(Paths.get(this.xmlManager.getMagitRepository().getLocation() + File.separator + ".magit"))) {
                throw new XmlRepositoryAlreadyExistsException();
            }
            File file = new File(this.xmlManager.getMagitRepository().getLocation());
            if (!directoryIsEmpty(Paths.get(file.getPath()))) {
                throw new XmlPathContainsNonRepositoryObjectsException("The path you have entered contains files which are not repository");
            }
        }

        createRepositoryFromMagitRepository();
    }

    void deleteDirectory(Path path) throws IOException {
        File file = path.toFile();
        if (file.isDirectory()) {
            for (File f : file.listFiles())
                deleteDirectory(f.toPath());
        }
        file.delete();
    }

    public void createRepositoryFromMagitRepository() throws Exception {
        deleteDirectory(Paths.get(this.xmlManager.getMagitRepository().getLocation()));
        CreateEmptyRepository(this.xmlManager.getMagitRepository().getLocation());
        this.repository.getBranches().clear();
        File masterBranch = new File(this.repository.GetBranchesDirPath() + File.separator + "master.txt");
        masterBranch.delete();
        addMagitRepositoryObjectsToRepository();

        spanWCFromCommit(this.repository.getHeadBranch().getLastCommit());
    }

    private void addMagitRepositoryObjectsToRepository() throws Exception {
        Branch branchToCreate;
        Commit commitToCreate;
        String headBranchName = this.xmlManager.getMagitRepository().getMagitBranches().getHead();
        List<MagitSingleBranch> magitBranches = this.xmlManager.getMagitRepository().getMagitBranches().getMagitSingleBranch();

        for (MagitSingleBranch mb : magitBranches) {
            commitToCreate = createCommitFromMagitCommit(this.xmlManager.existingCommits.get(mb.getPointedCommit().getId()));
            branchToCreate = new Branch(mb.getName(), commitToCreate);
            this.repository.getBranches().add(branchToCreate);
            if (branchToCreate.getName().equals(headBranchName)) {
                this.repository.setHeadBranch(branchToCreate);
                writeToFile(this.repository.GetBranchesDirPath() + File.separator
                        + "HEAD.txt", headBranchName);
            }

            createTextFile(this.repository.GetBranchesDirPath() + File.separator
                    + branchToCreate.getName() + ".txt", commitToCreate.Sha1Commit());
        }
    }



    private Commit createCommitFromMagitCommit(MagitSingleCommit magitCommit) throws Exception {
        Commit commitToCreate;
        Commit previousCommit = null;
        Folder rootFolderToCreate;
        List<PrecedingCommits.PrecedingCommit> precedingCommits = null;
        MagitSingleCommit prevMagitCommit;
        MagitSingleFolder magitFolder = this.xmlManager.existingFolders.get(magitCommit.getRootFolder().getId());
        rootFolderToCreate = createFolderFromMagitFolder(magitFolder);
        commitToCreate = new Commit(magitCommit.getAuthor(), magitCommit.getMessage(), magitCommit.getDateOfCreation());
        commitToCreate.setMainFolder(rootFolderToCreate);
        this.repository.getRecentlyUsedCommits().put(commitToCreate.Sha1Commit(), commitToCreate);
        if (magitCommit.getPrecedingCommits() != null) {
            precedingCommits = magitCommit.getPrecedingCommits().getPrecedingCommit();
        }
        if (precedingCommits != null) {

            if (precedingCommits.size() != 0) {
                prevMagitCommit = this.xmlManager.existingCommits.get(precedingCommits.get(0).getId());
                previousCommit = createCommitFromMagitCommit(prevMagitCommit);
                commitToCreate.setPrevCommitSha1(previousCommit.Sha1Commit());
            }
        }

        createNewObjectFile(commitToCreate.toString());
        return commitToCreate;
    }

    public String getRepositoryName() {
        String absolutePath = repository.getPath().toString();
        return new File(absolutePath).getAbsolutePath();
    }

    public void Merge(String sha1OfTheirsCommit){
        //find aba
        //create three folders - ours,theirs,ancestor
        //create folder of open changes and conflicts
        //resolve conflicts
        //commit
    }
}