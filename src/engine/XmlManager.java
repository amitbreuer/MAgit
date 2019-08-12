package engine;

import engine.Exceptions.XmlRepositoryAlreadyExistsException;
import engine.jaxb.schema.generated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlManager {

    private MagitRepository magitRepository;
    Map<String, MagitBlob> existingBlobs;
    Map<String, MagitSingleFolder> existingFolders;
    Map<String,MagitSingleCommit> existingCommits;

    public MagitRepository getMagitRepository() {
        return magitRepository;
    }

    private void setExisitngMaps() throws Exception {
        this.existingBlobs = createExistingBlobsMap();
        this.existingFolders = createExistingFoldersMap();
        this.existingCommits = createExistingCommitsMap();
    }

    void createMagitRepositoryFromXml(String fileFullName) throws Exception {
        validateName(fileFullName);
        InputStream inputStream = new FileInputStream(fileFullName);
        this.magitRepository = deserializeFrom(inputStream);
        setExisitngMaps();
        validateMagitRepository();
    }

    private void validateName(String fileFullName) throws Exception {
        if(!Files.exists(Paths.get(fileFullName))){
            throw new Exception("The file " + fileFullName + " does not exist");
        }
        if(!fileFullName.toLowerCase().endsWith(".xml")){
            throw new Exception("The file " + fileFullName + " is not an xml file");
        }
    }

    private void validateMagitRepository() throws Exception {
        validateBranches(existingCommits);
        validateCommits(existingFolders);
        validateFoldersAndBlobs(existingFolders,existingBlobs);
    }

    private void validateFoldersAndBlobs(Map<String, MagitSingleFolder> existingFolders, Map<String, MagitBlob> existingBlobs) throws Exception {
        List<MagitSingleFolder> folders = this.magitRepository.getMagitFolders().getMagitSingleFolder();
        for(MagitSingleFolder mf : folders){
            List<Item> items = mf.getItems().getItem();
            for(Item item : items){
                if(item.getType().equals("blob")){
                    if(!existingBlobs.containsKey(item.getId())){
                        throw new Exception("The folder with id: " + mf.getId() + " contains blob with id: " + item.getId() + " which does not exist");
                    }
                } else {
                    if(!existingFolders.containsKey(item.getId())) {
                        throw new Exception("The folder with id: " + mf.getId() + " contains folder with id: " + item.getId() + " which does not exist");
                    }
                    if(mf.getId().equals(item.getId())){
                        throw new Exception("The folder with id: " + mf.getId() + " points to itself");
                    }
                }
            }
        }
    }

    private void validateCommits(Map<String, MagitSingleFolder> existingFolders) throws Exception {
        List<MagitSingleCommit> commits = this.magitRepository.getMagitCommits().getMagitSingleCommit();
        for(MagitSingleCommit mc : commits){
            if(!existingFolders.containsKey(mc.getRootFolder().getId())){
                throw new Exception("The root folder pointed by commit " + mc.getId() + " does not exist");
            }
            if(!existingFolders.get(mc.getRootFolder().getId()).isIsRoot()){
                throw new Exception("The root folder pointed by commit " + mc.getId() + " is not a root folder");
            }
        }
    }

    private void validateBranches(Map<String, MagitSingleCommit> existingCommits) throws Exception {
        Boolean headExists = false;
        String headBranchName = this.magitRepository.getMagitBranches().getHead();
        List<MagitSingleBranch> branches = this.magitRepository.getMagitBranches().getMagitSingleBranch();
        for(MagitSingleBranch mb : branches){
            if(mb.getName().equals(headBranchName)){
                headExists = true;
            }
            if(!existingCommits.containsKey(mb.getPointedCommit().getId())){
                throw new Exception("The commit pointed by branch " + mb.getName() + " does not exist");
            }
        }
        if(!headExists){
            throw new Exception("The branch " + headBranchName + " which is pointed by the head, does not exist");
        }
    }

    private Map<String, MagitSingleCommit> createExistingCommitsMap() throws Exception {
        Map<String, MagitSingleCommit> existingCommits = new HashMap<>();
        List<MagitSingleCommit> commits = magitRepository.getMagitCommits().getMagitSingleCommit();
        for (MagitSingleCommit mc : commits) {
            if (existingCommits.containsKey(mc.getId())) {
                throw new Exception("The commit id:" + mc.getId() + "appears more than once in the given repository");
            } else {
                existingCommits.put(mc.getId(), mc);
            }
        }
        return existingCommits;
    }

    private Map<String, MagitSingleFolder> createExistingFoldersMap() throws Exception {
        Map<String, MagitSingleFolder> existingFolders = new HashMap<>();
        List<MagitSingleFolder> folders = this.magitRepository.getMagitFolders().getMagitSingleFolder();
        for (MagitSingleFolder mf : folders) {
            if (existingFolders.containsKey(mf.getId())) {
                throw new Exception("The folder id:" + mf.getId() + "appears more than once in the given repository");
            } else {
                existingFolders.put(mf.getId(), mf);
            }
        }
        return existingFolders;
    }

    private Map<String, MagitBlob> createExistingBlobsMap() throws Exception {
        Map<String, MagitBlob> existingBlobs = new HashMap<>();
        List<MagitBlob> blobs = this.magitRepository.getMagitBlobs().getMagitBlob();
        for (MagitBlob mb : blobs) {
            if (existingBlobs.containsKey(mb.getId())) {
                throw new Exception("The blob id: " + mb.getId() + " appears more than once in the given repository");
            } else {
                existingBlobs.put(mb.getId(), mb);
            }
        }
        return existingBlobs;
    }

    private MagitRepository deserializeFrom(InputStream inputStream) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("engine.jaxb.schema.generated");
        Unmarshaller u = jc.createUnmarshaller();
        return (MagitRepository) u.unmarshal(inputStream);
    }
}

