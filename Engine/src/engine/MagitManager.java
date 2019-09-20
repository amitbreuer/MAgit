package engine;

import app.AppController;
import exceptions.ActiveBranchContainsMergedBranchException;
import exceptions.XmlPathContainsNonRepositoryObjectsException;
import exceptions.XmlRepositoryAlreadyExistsException;
import generated.*;
import header.binds.BranchNameBind;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import puk.team.course.magit.ancestor.finder.AncestorFinder;
import puk.team.course.magit.ancestor.finder.CommitRepresentative;
import tasks.LoadRepositoryFromXmlTask;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MagitManager {
    private AppController controller;
    private String username;
    private Repository repository;
    private XmlManager xmlManager;
    private String repositoryName;

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

    public void CreateEmptyRepository(String repositoryPath, String repositoryName) throws Exception {
        if (Files.exists(Paths.get(repositoryPath))) {
            throw new FileAlreadyExistsException("The path you have entered already exists");
        } else {
            this.repository = new Repository(repositoryPath);
            this.repositoryName = repositoryName;

            new File(repositoryPath).mkdirs();
            new File(repositoryPath + File.separator + ".magit").mkdir();
            new File(this.repository.GetBranchesDirPath()).mkdir();
            new File(this.repository.GetObjectsDirPath()).mkdir();

            createTextFile(repositoryPath + File.separator + ".magit" + File.separator + "repositoryName.txt", repositoryName);
            createTextFile(this.repository.GetBranchesDirPath() + File.separator + "HEAD.txt", "master");
            createTextFile(this.repository.GetBranchesDirPath() + File.separator + "master.txt", "");
            Branch masterBranch = new Branch("master", null);
            this.repository.setHeadBranch(masterBranch);
            this.repository.getBranches().put(masterBranch.getName(), masterBranch);
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
        Path remoteRepositoryPath;
        if (this.repository != null && this.repository.getPath().equals(path)) {
            throw new Exception("You already working with the repository - " + path.toString());
        } else {
            isRepository = Files.exists(Paths.get(repositoryPath + File.separator + ".magit"));

            if (isRepository) {
                this.repository = new Repository(repositoryPath);
                this.repositoryName = convertTextFileToString(this.repository.getPath().toString() + File.separator + ".magit" + File.separator + "repositoryName.txt");
                headBranchName = this.repository.getHeadBranchNameFromBranchesDir();
                headBranchContent = convertTextFileToString(this.repository.GetBranchesDirPath() + File.separator + headBranchName + ".txt");
                if (!headBranchContent.equals("")) { // if there is a commit sha1 in head branch file
                    prevCommit = createCommitFromObjectFile(headBranchContent, repository.GetObjectsDirPath());
                }
                headBranch = new Branch(headBranchName, prevCommit);
                this.repository.setHeadBranch(headBranch);
                this.repository.getBranches().put(headBranch.getName(), headBranch);

                updateRepositoryBranchesList();
                remoteRepositoryPath = Paths.get(this.repository.getPath().toString() + File.separator + ".magit" + File.separator + "remoteRepositoryPath.txt");

                if (Files.exists(remoteRepositoryPath)) {
                    this.repository.setRemoteRepositoryPath(Paths.get(convertTextFileToString(remoteRepositoryPath.toString())));
                    this.repository.setRemoteRepositoryname(convertTextFileToString(repository.getRemoteRepositoryPath() + File.separator + ".magit" + File.separator + "repositoryName.txt"));
                }
            } else {
                throw new Exception("This folder is not a repository in M.A.git");
            }
        }
    }

    public static Boolean directoryIsEmpty(Path path) {
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

    public Delta GetWCDelta() throws IOException {
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

    public String GetWCStatusAsString() throws IOException {
        Delta delta = GetWCDelta();
        String deltaToString;

        if (delta.isEmpty()) {
            deltaToString = "There are no open changes";
        } else {
            deltaToString = delta.toString();
        }
        return deltaToString;
    }

    private void spanWCFromCommit(Commit commitToSpan) throws Exception {
        Folder mainFolder = commitToSpan.getMainFolder();
        spanWCFromFolder(mainFolder);
    }

    private void spanWCFromFolder(Folder mainFolder) throws Exception {
        Path pathOfRepository = this.repository.getPath();
        File repositoryToDelete = pathOfRepository.toFile();
        deleteFileFromWorkingCopy(repositoryToDelete);
        List<Folder.ComponentData> componentDataList = mainFolder.getComponents();
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
        String RRName = null;
        Map<String, Branch> remoteBranches = new HashMap<>();

        for (File f : branchesFiles) {
            if (f.isDirectory()) {
                RRName = f.getName();
                File[] remoteBranchesFiles = Paths.get(this.repository.GetBranchesDirPath() + File.separator + RRName).toFile().listFiles();
                for (File rf : remoteBranchesFiles) {
                    tokenizer = new StringTokenizer(rf.getName(), ".");
                    if (!this.repository.branchExistsInList(RRName + "\\" + tokenizer.nextToken())) {
                        Branch remoteBranch = createBranchFromObjectFileSha1(RRName + "\\" + rf.getName(),
                                convertTextFileToString(branchesPath + File.separator + RRName + File.separator + rf.getName()));
                        remoteBranch.setIsRB(true);
                        this.repository.getBranches().put(remoteBranch.getName(), remoteBranch);
                        remoteBranches.put(remoteBranch.getName(), remoteBranch);
                    }
                }
            } else if (!f.getName().equals("HEAD.txt")) {
                tokenizer = new StringTokenizer(f.getName(), ".");
                if (!this.repository.branchExistsInList(tokenizer.nextToken())) {
                    Branch branch = createBranchFromObjectFileSha1(f.getName(), convertTextFileToString(branchesPath + File.separator + f.getName()));
                    this.repository.getBranches().put(branch.getName(), branch);
                }
            }
        }

        if (!remoteBranches.isEmpty()) {
            setRTBs(remoteBranches, RRName);
        }
    }

    private void setRTBs(Map<String, Branch> remoteBranches, String RRName) {
        Map<String, Branch> branches = repository.getBranches();
        for (Map.Entry<String, Branch> entry : branches.entrySet()) {
            if (remoteBranches.containsKey(RRName + "/" + entry.getValue().getName()) && !entry.getValue().getIsRB()) {
                entry.getValue().setIsRTB(true);
            }
        }
    }

    public List<String> GetAllBranchesDetails() throws IOException {
        List<String> allBranchesDetails = new ArrayList<>();
        Map<String, Branch> branches = this.repository.getBranches();
        updateRepositoryBranchesList();
        for (Map.Entry<String, Branch> entry : branches.entrySet()) {
            if (this.repository.getHeadBranch().equals(entry.getValue())) {
                allBranchesDetails.add("head branch: ");
            }
            allBranchesDetails.add(entry.getValue().getDetails());
        }
        return allBranchesDetails;
    }

    private Branch createBranchFromObjectFileSha1(String branchName, String sha1) throws IOException {
        Commit newCommit = createCommitFromObjectFile(sha1, repository.GetObjectsDirPath());
        StringTokenizer tokenizer = new StringTokenizer(branchName, "."); // to cut the extension ".txt"
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
        this.repository.getBranches().put(newBranch.getName(), newBranch);
        if (checkoutNewBranch) {
            if (thereAreUncommittedChanges()) {
                throw new Exception("Checkout failed. There are uncommitted changes");
            } else {
                setHeadBranch(newBranch);
            }
        }
    }

    public void CreateNewBranchForCommit(String branchName, String commitSha1) throws Exception {
        String branchesDirPath = this.repository.GetBranchesDirPath();
        if (repository.FindBranchByName(branchName) != null) {
            throw new Exception("The branch \"branchName\" already exists");
        }
        Commit commit = CreateCommitFromSha1(commitSha1, this.repository.GetObjectsDirPath());
        Branch newBranch = new Branch(branchName, commit);
        createTextFile(branchesDirPath + File.separator + branchName + ".txt", commitSha1);
        repository.getBranches().put(newBranch.getName(), newBranch);
    }

    public Boolean thereAreUncommittedChanges() throws IOException {
        Delta delta = GetWCDelta();
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
                this.repository.getBranches().put(newHeadBranch.getName(), newHeadBranch);
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
            List<Commit> activeBranchCommits = getAllCommitsOfActiveBranch();

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

    private List<Commit> getAllCommitsOfActiveBranch() throws IOException {
        List<Commit> allCommits = new ArrayList<>();
        Commit currentCommit = this.repository.getHeadBranch().getLastCommit();
        String prevCommitSha1;

        if (currentCommit != null) {
            allCommits.add(currentCommit);
            prevCommitSha1 = currentCommit.getPrevCommitSha1();

            while (prevCommitSha1 != null) {
                currentCommit = CreateCommitFromSha1(prevCommitSha1, this.repository.GetObjectsDirPath());
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

    public void ExecuteCommit(String message, String mergedBranchLastCommitSha1) throws Exception {
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
                if (mergedBranchLastCommitSha1 != null) {
                    newCommit.setSecondPrevCommitSha1(mergedBranchLastCommitSha1);
                }
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
            e.printStackTrace();
        }
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

    private void writeToFile(String filePath, String content) throws IOException {
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

    public Commit createCommitFromObjectFile(String commitSha1, String objectsFolderPath) throws IOException {
        List<String> commitTextFileContent;
        FolderComponent mainFolder;
        List<String> mainFolderTextFileContent = null;
        File mainFolderTextFile = null;
        File commitTextFile = null;
        String prevCommitSha1 = null;
        String anotherPrevSha1 = null;
        String mainFolderSha1 = null;
        String dateCreated = null;
        String creator = null;
        String message = null;
        Commit newCommit;


        commitTextFile = getTextFileFromObjectsDirectory(commitSha1, objectsFolderPath);
        commitTextFileContent = Files.readAllLines(commitTextFile.toPath());
        prevCommitSha1 = commitTextFileContent.get(0);


        anotherPrevSha1 = commitTextFileContent.get(1);


        mainFolderSha1 = commitTextFileContent.get(2);
        dateCreated = commitTextFileContent.get(3);
        creator = commitTextFileContent.get(4);
        message = commitTextFileContent.get(5);
        mainFolderTextFile = getTextFileFromObjectsDirectory(mainFolderSha1, objectsFolderPath);
        mainFolderTextFileContent = Files.readAllLines(mainFolderTextFile.toPath());

        mainFolder = createFolderComponentFromTextFileLines(true, mainFolderTextFileContent, objectsFolderPath);

        newCommit = new Commit(creator, message);
        newCommit.setPrevCommitSha1(prevCommitSha1);
        newCommit.setSecondPrevCommitSha1(anotherPrevSha1);
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
                throw new Exception("There is no commit with the sha1: " + sha1);
            }
            commitToResetTo = CreateCommitFromSha1(sha1, this.repository.GetObjectsDirPath());
        }
        this.repository.getHeadBranch().setLastCommit(commitToResetTo);
        String headBranchFilePath = repository.GetBranchesDirPath() + File.separator
                + repository.getHeadBranch().getName() + ".txt";
        writeToFile(headBranchFilePath, commitToResetTo.Sha1Commit());
        spanWCFromFolder(commitToResetTo.getMainFolder());
    }


    public void LoadRepositoryFromXML(String fileFullName, Consumer<String> errorNotifier, Runnable runIfPathContainsRepository, Runnable runIfFinishedProperly) {
        LoadRepositoryFromXmlTask loadRepositoryFromXmlTask = new LoadRepositoryFromXmlTask(this, fileFullName, errorNotifier, runIfPathContainsRepository,runIfFinishedProperly);
        new Thread(loadRepositoryFromXmlTask).start();

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
                throw new XmlPathContainsNonRepositoryObjectsException();
            }
        }

        createRepositoryFromMagitRepository();
    }

    private void deleteDirectory(Path path) {
        File file = path.toFile();
        if (file.isDirectory()) {
            for (File f : file.listFiles())
                deleteDirectory(f.toPath());
        }
        file.delete();
    }

    public void createRepositoryFromMagitRepository() throws Exception {
        deleteDirectory(Paths.get(this.xmlManager.getMagitRepository().getLocation()));
        CreateEmptyRepository(this.xmlManager.getMagitRepository().getLocation(), this.xmlManager.getMagitRepository().getName());
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
            branchToCreate.setIsRB(mb.isIsRemote());
            branchToCreate.setIsRTB(mb.isTracking());
            this.repository.getBranches().put(branchToCreate.getName(), branchToCreate);
            if (branchToCreate.getName().equals(headBranchName)) {
                this.repository.setHeadBranch(branchToCreate);
                writeToFile(this.repository.GetBranchesDirPath() + File.separator
                        + "HEAD.txt", headBranchName);
            }

            if (branchToCreate.getIsRB()) {
                StringTokenizer tokenizer = new StringTokenizer(branchToCreate.getName(), "\\");
                String RRName = tokenizer.nextToken();
                if (!Files.exists(Paths.get(this.repository.GetBranchesDirPath() + File.separator + RRName))) {
                    new File(this.repository.GetBranchesDirPath() + File.separator + RRName).mkdirs();
                }
                createTextFile(this.repository.GetBranchesDirPath() + File.separator
                        + RRName + File.separator + tokenizer.nextToken() + ".txt", commitToCreate.Sha1Commit());
            } else {
                createTextFile(this.repository.GetBranchesDirPath() + File.separator
                        + branchToCreate.getName() + ".txt", commitToCreate.Sha1Commit());
            }
        }
    }

    private Commit createCommitFromMagitCommit(MagitSingleCommit magitCommit) throws Exception {
        Commit commitToCreate;
        Commit previousCommit = null;
        Commit anotherPreviousCommit = null;
        String commitToCreateSha1;
        Folder rootFolderToCreate;
        List<PrecedingCommits.PrecedingCommit> precedingCommits = null;
        MagitSingleCommit prevMagitCommit;
        MagitSingleCommit anotherPrevMagitCommit;
        MagitSingleFolder magitFolder = this.xmlManager.existingFolders.get(magitCommit.getRootFolder().getId());
        rootFolderToCreate = createFolderFromMagitFolder(magitFolder);
        commitToCreate = new Commit(magitCommit.getAuthor(), magitCommit.getMessage(), magitCommit.getDateOfCreation());
        commitToCreate.setMainFolder(rootFolderToCreate);

        if (magitCommit.getPrecedingCommits() != null) {
            precedingCommits = magitCommit.getPrecedingCommits().getPrecedingCommit();
        }
        if (precedingCommits != null) {
            if (precedingCommits.size() != 0) {
                prevMagitCommit = this.xmlManager.existingCommits.get(precedingCommits.get(0).getId());
                previousCommit = createCommitFromMagitCommit(prevMagitCommit);
                commitToCreate.setPrevCommitSha1(previousCommit.Sha1Commit());
                if (precedingCommits.size() == 2) {
                    anotherPrevMagitCommit = this.xmlManager.existingCommits.get(precedingCommits.get(1).getId());
                    anotherPreviousCommit = createCommitFromMagitCommit(anotherPrevMagitCommit);
                    commitToCreate.setSecondPrevCommitSha1(anotherPreviousCommit.getSha1());
                }
            }
        }

        commitToCreateSha1 = commitToCreate.Sha1Commit();
        if (!this.repository.getRecentlyUsedCommits().containsKey(commitToCreateSha1)) {
            this.repository.getRecentlyUsedCommits().put(commitToCreateSha1, commitToCreate);
        }

        createNewObjectFile(commitToCreate.toString());
        return commitToCreate;
    }

    public String getRepositoryName() {
        return this.repositoryName;
    }

    public String getRepositoryPath() {
        return this.repository.getPath().toString();
    }

    public Folder CreateMergedFolderAndFindConflicts(String branchToMergeName, Conflicts conflicts) throws Exception {
        try {
            if (thereAreUncommittedChanges()) {
                throw new Exception("Merge failed. There are open changes");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Branch branchToMerge = repository.FindBranchByName(branchToMergeName);
        String theirsCommitSha1 = branchToMerge.getLastCommit().getSha1();
        //find ancestor
        Function<String, CommitRepresentative> function = new Sha1ToCommitFunction(this);
        AncestorFinder ancestorFinder = new AncestorFinder(function);
        String oursCommitSha1 = this.repository.getHeadBranch().getLastCommit().Sha1Commit();
        String ancestorCommitSha1 = ancestorFinder.traceAncestor(oursCommitSha1, theirsCommitSha1);
        String objectsDirPath = this.repository.GetObjectsDirPath();

        // Fast Forward
        if (oursCommitSha1.equals(ancestorCommitSha1)) {
            Commit theirsCommit = CreateCommitFromSha1(theirsCommitSha1, this.repository.GetObjectsDirPath());
            this.repository.getHeadBranch().setLastCommit(theirsCommit);
            return theirsCommit.getMainFolder();
        }
        if (theirsCommitSha1.equals(ancestorCommitSha1)) {
            throw new ActiveBranchContainsMergedBranchException();
        }

        //create three folders - ours,theirs,ancestor
        Folder oursFolder = CreateCommitFromSha1(oursCommitSha1, objectsDirPath).getMainFolder();
        Folder theirsFolder = CreateCommitFromSha1(theirsCommitSha1, objectsDirPath).getMainFolder();
        Folder ancestorsFolder = CreateCommitFromSha1(ancestorCommitSha1, objectsDirPath).getMainFolder();

        return addFilesToMergedFolderAndConflicts(oursFolder, theirsFolder, ancestorsFolder, conflicts, repository.getPath().toString(), this.username);
    }

    private void createObjectsFilesFromFolder(Folder folder) throws Exception {
        List<Folder.ComponentData> components = folder.getComponents();
        String objectsDirPath = repository.GetObjectsDirPath();
        for (Folder.ComponentData cd : components) {
            if (cd.getFolderComponent() instanceof Blob) {
                if (!Files.exists(Paths.get(objectsDirPath + File.separator + cd.getSha1() + ".zip"))) {
                    createNewObjectFile(cd.getFolderComponent().toString());
                }
            } else {
                if (!Files.exists(Paths.get(objectsDirPath + File.separator + cd.getSha1() + ".zip"))) {
                    createObjectsFilesFromFolder((Folder) cd.getFolderComponent());
                }
            }
        }
        if (!Files.exists(Paths.get(objectsDirPath + File.separator + folder.sha1Folder() + ".zip"))) {
            createNewObjectFile(folder.toString());
        }
    }

    public static Folder addFilesToMergedFolderAndConflicts(Folder oursFolder, Folder theirsFolder, Folder ancestorsFolder, Conflicts conflicts, String path, String updaterName) {
        Folder mergedFolder = new Folder();
        String minName;
        int oursIndex = 0;
        int theirsIndex = 0;
        int ancestorsIndex = 0;
        String oursFileName;
        String theirsFileName;
        String ancestorsFileName;
        Folder.ComponentData oursComponent = null;
        Folder.ComponentData theirsComponent = null;
        Folder.ComponentData ancestorsComponent = null;
        SingleFileMerger fileMerger;
        List<Folder.ComponentData> oursComponents = oursFolder.getComponents();
        List<Folder.ComponentData> theirsComponents = theirsFolder.getComponents();
        List<Folder.ComponentData> ancestorsComponents = ancestorsFolder.getComponents();
        int oursSize = oursComponents.size();
        int theirsSize = theirsComponents.size();
        int ancestorsSize = ancestorsComponents.size();

        ArrayList<Integer> indicesArray = new ArrayList<>();
        indicesArray.add(0, oursIndex);
        indicesArray.add(1, theirsIndex);
        indicesArray.add(2, ancestorsIndex);

        while (oursIndex < oursSize || theirsIndex < theirsSize || ancestorsIndex < ancestorsSize) {
            oursComponent = oursIndex < oursSize ? oursComponents.get(oursIndex) : null;
            theirsComponent = theirsIndex < theirsSize ? theirsComponents.get(theirsIndex) : null;
            ancestorsComponent = ancestorsIndex < ancestorsSize ? ancestorsComponents.get(ancestorsIndex) : null;

            oursFileName = oursComponent != null ? oursComponent.getName() : null;
            theirsFileName = theirsComponent != null ? theirsComponent.getName() : null;
            ancestorsFileName = ancestorsComponent != null ? ancestorsComponent.getName() : null;

            minName = getLowestLexicographicFileName(oursFileName, theirsFileName, ancestorsFileName);

            fileMerger = getSingleFileMerger(oursComponent, theirsComponent, ancestorsComponent, minName);
            fileMerger.mergeFiles(oursComponent, theirsComponent, ancestorsComponent, mergedFolder, conflicts, path, updaterName);

            advanceMergeIndicesByEnumValue(fileMerger, indicesArray);

            oursIndex = indicesArray.get(0);
            theirsIndex = indicesArray.get(1);
            ancestorsIndex = indicesArray.get(2);
        }
        Collections.sort(mergedFolder.getComponents());
        return mergedFolder;
    }

    private static void advanceMergeIndicesByEnumValue(SingleFileMerger fileMerger, ArrayList<Integer> indices) {
        switch (fileMerger) {
            case DELETEDBYOURS:
                indices.set(1, indices.get(1) + 1);
                indices.set(2, indices.get(2) + 1);
                break;
            case DELETEDBYTHEIRS:
                indices.set(0, indices.get(0) + 1);
                indices.set(2, indices.get(2) + 1);
                break;
            case DELETEDBYBOTH:
                indices.set(2, indices.get(2) + 1);
                break;
            case NOCHANGE:
                indices.set(0, indices.get(0) + 1);
                indices.set(1, indices.get(1) + 1);
                indices.set(2, indices.get(2) + 1);
                break;
            case ADDEDBYOURS:
                indices.set(0, indices.get(0) + 1);
                break;
            case ADDEDBYTHEIRS:
                indices.set(1, indices.get(1) + 1);
                break;
            case ADDEDBYBOTH:
                indices.set(0, indices.get(0) + 1);
                indices.set(1, indices.get(1) + 1);
                break;
            case UPDATEDBYOURS:
            case UPDATEDBYBOTH:
            case UPDATEDBYTHEIRS:
                indices.set(0, indices.get(0) + 1);
                indices.set(1, indices.get(1) + 1);
                indices.set(2, indices.get(2) + 1);
                break;

            case OURSDELETEDTHEIRSUPDATEDCONFLICT:
                indices.set(1, indices.get(1) + 1);
                indices.set(2, indices.get(2) + 1);
                break;

            case OURSUPDATEDTHEIRSDELETEDCONFLICT:
                indices.set(0, indices.get(0) + 1);
                indices.set(2, indices.get(2) + 1);
                break;

            case OURSADDEDTHEIRSADDEDIFFERENTLYDCONFLICT:
                indices.set(0, indices.get(0) + 1);
                indices.set(1, indices.get(1) + 1);
                break;

            case OURSUPDATEDTHEIRSUPDATEDDIFFERENTLYCONFLICT:
                indices.set(0, indices.get(0) + 1);
                indices.set(1, indices.get(1) + 1);
                indices.set(2, indices.get(2) + 1);
                break;
        }
    }

    private static SingleFileMerger getSingleFileMerger(Folder.ComponentData oursComponent, Folder.ComponentData theirsComponent, Folder.ComponentData ancestorComponent, String minName) {
        boolean existsInOurs;
        boolean existsInTheirs;
        boolean existsInAncestors;
        boolean oursEqualsTheirs;
        boolean oursEqualsAncestors;
        boolean theirsEqualsAncestors;
        String oursFileName;
        String theirsFileName;
        String ancestorFileName;

        if (oursComponent == null) {
            existsInOurs = false;
            oursEqualsTheirs = false;
            oursEqualsAncestors = false;
        } else {
            oursFileName = oursComponent.getName();
            existsInOurs = oursFileName.equals(minName);
        }
        if (theirsComponent == null) {
            existsInTheirs = false;
            oursEqualsTheirs = false;
            theirsEqualsAncestors = false;
        } else {
            theirsFileName = theirsComponent.getName();
            existsInTheirs = theirsFileName.equals(minName);
        }
        if (ancestorComponent == null) {
            existsInAncestors = false;
            oursEqualsAncestors = false;
            theirsEqualsAncestors = false;
        } else {
            ancestorFileName = ancestorComponent.getName();
            existsInAncestors = ancestorFileName.equals(minName);
        }

        oursEqualsTheirs = existsInOurs && existsInTheirs && oursComponent.getSha1().equals(theirsComponent.getSha1());
        oursEqualsAncestors = existsInOurs && existsInAncestors && oursComponent.getSha1().equals(ancestorComponent.getSha1());
        theirsEqualsAncestors = existsInTheirs && existsInAncestors && theirsComponent.getSha1().equals(ancestorComponent.getSha1());

        SingleFileMerger fileMerger = SingleFileMerger.GetMerger(existsInOurs, existsInTheirs, existsInAncestors, oursEqualsTheirs, oursEqualsAncestors, theirsEqualsAncestors);

        return fileMerger;
    }

    private static String getLowestLexicographicFileName(String str1, String str2, String str3) {
        String min = "";
        if (str1 != null) {
            min = str1;
        }
        if (str2 != null) {
            if (min == "") {
                min = str2;
            } else {
                min = str2.compareTo(min) < 0 ? str2 : min;
            }
        }
        if (str3 != null) {
            if (min == "") {
                min = str3;
            } else {
                min = str3.compareTo(min) < 0 ? str3 : min;
            }
        }
        return min;
    }

    public Map<String, Commit> GetAllCommitsMap() {
        Map<String, Commit> commitsMap = new HashMap<>();
        Map<String, Branch> branches = this.repository.getBranches();
        for (Map.Entry<String, Branch> entry : branches.entrySet()) {
            if (entry.getValue().getLastCommit() != null) {
                addCommitToCommitsMap(entry.getValue().getLastCommit(), commitsMap);
            }
        }

        return commitsMap;
    }

    private void addCommitToCommitsMap(Commit commitToAdd, Map<String, Commit> commitsMap) {
        if (!commitsMap.containsKey(commitToAdd.getSha1())) {
            commitsMap.put(commitToAdd.getSha1(), commitToAdd);
        }
        String prevCommitSha1 = commitToAdd.getPrevCommitSha1();
        String anotherPrevCommitSha1 = commitToAdd.getSecondPrecedingSha1();
        String objectsDirPath = this.repository.GetObjectsDirPath();
        if (!prevCommitSha1.equals("")) {
            Commit prevCommit = CreateCommitFromSha1(prevCommitSha1, objectsDirPath);
            addCommitToCommitsMap(prevCommit, commitsMap);
        }

        if (!anotherPrevCommitSha1.equals("")) {
            Commit anotherPrevCommit = CreateCommitFromSha1(anotherPrevCommitSha1, objectsDirPath);
            addCommitToCommitsMap(anotherPrevCommit, commitsMap);
        }
    }

    public Delta GetDeltaBetweenTwoCommitSha1s(String commit1Sha1, String commit2Sha1) throws IOException {
        Delta delta = new Delta();
        String objectsDirPath = this.repository.GetObjectsDirPath();
        Commit commit1 = CreateCommitFromSha1(commit1Sha1, objectsDirPath);
        Commit commit2 = CreateCommitFromSha1(commit2Sha1, objectsDirPath);
        calculateDeltaBetweenTwoFolders(commit1.getMainFolder(), commit2.getMainFolder(), repository.getPath().toString(), delta);

        return delta;
    }

    public Commit CreateCommitFromSha1(String commitSha1, String objectsDirPath) {
        Commit commitToReturn = null;
        if (repository.getRecentlyUsedCommits().containsKey(commitSha1)) {
            commitToReturn = repository.getRecentlyUsedCommits().get(commitSha1);
        } else {
            try {
                commitToReturn = createCommitFromObjectFile(commitSha1, objectsDirPath);
                repository.getRecentlyUsedCommits().put(commitSha1, commitToReturn);
            } catch (IOException e) {
            }
        }

        return commitToReturn;
    }

    public void CommitMerge(Folder mergedFolder, String message, String theirsBranchName) throws Exception {
        spanWCFromFolder(mergedFolder);
        String theirsLastCommitSha1 = repository.FindBranchByName(theirsBranchName).getLastCommit().getSha1();
        ExecuteCommit(message, theirsLastCommitSha1);
        createObjectsFilesFromFolder(mergedFolder);
    }

    public void ImplementConflictsSolutions(Conflicts conflicts) {
        List<ConflictComponent> conflictComponentList = conflicts.getConflictFiles();
        for (ConflictComponent cc : conflictComponentList) {
            cc.updateContainingFolder();
        }
    }

    public void CloneRepository(String RRPath, String LRPath, String LRName) {
        String RRName;
        try {
            RRName = convertTextFileToString(RRPath + File.separator + ".magit" + File.separator + "repositoryName.txt");

            //make a copy of the RR in the given LR path
            FileUtils.copyDirectory(new File(RRPath), new File(LRPath), false);
            createTextFile(LRPath + File.separator + ".magit" + File.separator + "repositoryName.txt", LRName);
            createTextFile(LRPath + File.separator + ".magit" + File.separator + "remoteRepositoryPath.txt", RRPath);
            SwitchRepository(LRPath);
            this.repository.setRemoteRepositoryPath(Paths.get(RRPath));
            this.repository.setRemoteRepositoryname(convertTextFileToString(RRPath + File.separator + ".magit" + File.separator + "repositoryName.txt"));
            convertRepositoryBranchesToRemoteBranches(RRName);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*The method is being called after a clone request is made.
      after all RR files were copied to LR, the method takes all the branches inside LR,
      and define them as remote branches
    */
    private void convertRepositoryBranchesToRemoteBranches(String RRName) {
        try {
            updateRepositoryBranchesList();
            new File(this.repository.GetBranchesDirPath() + File.separator + RRName).mkdirs();
            Map<String, Branch> branches = this.repository.getBranches();
            String branchesDirPath = this.repository.GetBranchesDirPath();
            Branch RBHeadBranch = null;
            for (Map.Entry<String, Branch> entry : branches.entrySet()) {
                if (entry.getValue().getName().equals(this.repository.getHeadBranch().getName())) {
                    entry.getValue().setIsRTB(true);
                    writeToFile(branchesDirPath + File.separator + RRName + File.separator + entry.getValue().getName() + ".txt", entry.getValue().getLastCommit().Sha1Commit());
                    RBHeadBranch = new Branch(RRName + "\\" + entry.getValue().getName(), entry.getValue().getLastCommit());
                    RBHeadBranch.setIsRB(true);

                } else {
                    entry.getValue().setIsRB(true);
                    File fileToMove = new File(branchesDirPath + File.separator + entry.getValue().getName() + ".txt");
                    fileToMove.renameTo(new File(branchesDirPath + File.separator + RRName + File.separator + entry.getValue().getName() + ".txt"));
                }
            }
            this.repository.getBranches().put(RBHeadBranch.getName(), RBHeadBranch);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void Fetch() {
        List<Branch> RRBranches = getRRBranches();
        for (Branch branch : RRBranches) {
            fetchSingleBranch(branch);
        }
    }

    private void fetchSingleBranch(Branch RRBranch) {
        String RRBranchName = RRBranch.getName();
        String RRPath = this.repository.getRemoteRepositoryPath().toString();
        String RRBranchPath = RRPath + File.separator + ".magit" + File.separator + "branches" + File.separator + RRBranchName + ".txt";
        String lastCommitSha1 = null;

        try {
            lastCommitSha1 = convertTextFileToString(RRBranchPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!lastCommitSha1.equals(null)) {
            //if the branch contains at least one commit, it updates LRBranches, and adds it to branches directory if needed
            updateBranchesAfterSingleBranchFetch(RRBranch);

            //fetchs all the object files that belong to the RRBranch, and are not in Objects directory of our current repository
            addRRCommitsObjectFilesToLRObjectsDirectory(lastCommitSha1);
        }

           /* while (lastCommitSha1 != null && !lastCommitSha1.equals("")) {
                if (Files.exists(Paths.get(LRObjectsPath + File.separator +
                        lastCommitSha1 + File.separator + ".zip"))) {
                    break;
                }
                lastCommit = createCommitFromObjectFile(lastCommitSha1, RRObjectsDirPath);
                createNewObjectFile(lastCommit.toString());
                createObjectsFilesFromFolder(lastCommit.getMainFolder());
                lastCommitSha1 = lastCommit.getPrevCommitSha1();
            }*/
    }


    /*the method copys all the object files of a given remote repository's commit, and it's previous commits, to the current repository's object directory*/
    private void addRRCommitsObjectFilesToLRObjectsDirectory(String commitSha1) {
        Commit commit;
        String RRObjectsDirPath = this.repository.getRemoteRepositoryPath() + File.separator + ".magit" + File.separator + "objects";

        if (Files.exists(Paths.get(this.repository.GetObjectsDirPath() + File.separator +
                commitSha1 + File.separator + ".zip"))) {
            return;
        }
        try {
            commit = createCommitFromObjectFile(commitSha1, RRObjectsDirPath);
            createNewObjectFile(commit.toString());
            createObjectsFilesFromFolder(commit.getMainFolder());

            if (!commit.getPrevCommitSha1().equals("")) {
                addRRCommitsObjectFilesToLRObjectsDirectory(commit.getPrevCommitSha1());
            }
            if (!commit.getSecondPrecedingSha1().equals("")) {
                addRRCommitsObjectFilesToLRObjectsDirectory(commit.getSecondPrecedingSha1());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateBranchesAfterSingleBranchFetch(Branch RRBranch) {
        Map<String, Branch> LRBranches = this.repository.getBranches();
        String RRName = this.repository.getRemoteRepositoryname();
        Path RRpath = this.repository.getRemoteRepositoryPath();
        String LRBranchName = RRName + '\\' + RRBranch.getName();
        String LRpath = this.repository.getPath().toString();

        if (LRBranches.containsKey(LRBranchName)) {
            LRBranches.get(LRBranchName).setLastCommit(RRBranch.getLastCommit());
        } else {
            Branch RBRanch = new Branch(LRBranchName, RRBranch.getLastCommit());
            RBRanch.setIsRB(true);
            LRBranches.put(LRBranchName, RBRanch);
            try {
                createTextFile(this.repository.GetBranchesDirPath()
                        + File.separator + RRName + File.separator + RRBranch.getName(), RBRanch.getLastCommit().Sha1Commit());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private List<Branch> getRRBranches() {
        List<Branch> RRbranches = new ArrayList<>();
        String RRBObjectsDirPath = this.repository.getRemoteRepositoryPath() + File.separator + ".magit" + File.separator + "objects";
        File RRbranchesFolder = new File(this.repository.getRemoteRepositoryPath()
                + File.separator + ".magit" + File.separator + "branches");
        List<File> branchesFiles = Arrays.asList(RRbranchesFolder.listFiles());
        String fileName;
        for (File file : branchesFiles) {
            StringTokenizer tokenizer = new StringTokenizer(file.getName(), "."); // to cut the extension ".txt"
            fileName = tokenizer.nextToken();
            if (!fileName.equals("HEAD")) {
                try {
                    String lastCommitSha1 = convertTextFileToString(file.getPath());
                    Commit lastCommit = CreateCommitFromSha1(lastCommitSha1, RRBObjectsDirPath);
                    RRbranches.add(new Branch(fileName, lastCommit));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return RRbranches;
    }

    public String GetObjectsDirPath() {
        return this.repository.GetObjectsDirPath();
    }

    public String GetHeadBranchName() {
        return this.repository.getHeadBranch().getName();
    }
}