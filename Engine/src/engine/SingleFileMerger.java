package engine;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public enum SingleFileMerger {
    DELETEDBYBOTH(false, false, true, false, false, false),
    DELETEDBYOURS(false, true, true, false, false, true),
    DELETEDBYTHEIRS(true, false, true, false, true, false),

    ADDEDBYOURS(true, false, false, false, false, false),
    ADDEDBYBOTH(true, true, false, true, false, false),
    UPDATEDBYOURS(true, true, true, false, false, true),
    UPDATEDBYBOTH(true, true, true, true, false, false),
    NOCHANGE(true, true, true, true, true, true),

    ADDEDBYTHEIRS(false, true, false, false, false, false),
    UPDATEDBYTHEIRS(true, true, true, false, true, false),

    OURSDELETEDTHEIRSUPDATEDCONFLICT(false, true, true, false, false, false),
    OURSUPDATEDTHEIRSDELETEDCONFLICT(true, false, true, false, false, false),
    OURSADDEDTHEIRSADDEDIFFERENTLYDCONFLICT(true, true, false, false, false, false),
    OURSUPDATEDTHEIRSUPDATEDDIFFERENTLYCONFLICT(true, true, true, false, false, false);


    private boolean existsInOurs;
    private boolean existsInTheirs;
    private boolean existsInAncestors;
    private boolean oursEqualsTheirs;
    private boolean oursEqualsAncestors;
    private boolean theirsEqualsAncestors;

    public static SingleFileMerger GetMerger(boolean existsInOurs, boolean existsInTheirs, boolean existsInAncestors, boolean oursEqulesTheirs, boolean oursEqulesAncestors, boolean theirsEqulesAncestors) {
        SingleFileMerger returnValue = null;
        for (SingleFileMerger sfm : SingleFileMerger.values()) {
            if (sfm.existsInOurs == existsInOurs &&
                    sfm.existsInTheirs == existsInTheirs &&
                    sfm.existsInAncestors == existsInAncestors &&
                    sfm.oursEqualsTheirs == oursEqulesTheirs &&
                    sfm.oursEqualsAncestors == oursEqulesAncestors &&
                    sfm.theirsEqualsAncestors == theirsEqulesAncestors) {
                returnValue = sfm;
                break;
            }
        }
        return returnValue;
    }

    void mergeFiles(Folder.ComponentData ours, Folder.ComponentData theirs, Folder.ComponentData ancestors, Folder containingFolder, Conflicts conflicts, String path, String updaterName) {
        switch (this) {
            case DELETEDBYBOTH:
            case DELETEDBYOURS:
            case DELETEDBYTHEIRS:
                break;
            case NOCHANGE:
            case ADDEDBYOURS:
            case ADDEDBYBOTH:
            case UPDATEDBYBOTH:
                containingFolder.getComponents().add(ours);
                break;

            case UPDATEDBYOURS:
                if (ours.getFolderComponent() instanceof Blob) {
                    containingFolder.getComponents().add(ours);
                } else {
                    String subFolderPath = path + File.separator + ours.getName();
                    Folder mergedSubFolder = MagitManager.addFilesToMergedFolderAndConflicts((Folder) ours.getFolderComponent(), (Folder) theirs.getFolderComponent(), (Folder) ancestors.getFolderComponent(), conflicts,subFolderPath,updaterName);
                    containingFolder.getComponents().add(new Folder.ComponentData(ours.getName(),
                            mergedSubFolder.sha1Folder(), mergedSubFolder, ours.getLastModifier(), ours.getLastModifiedDate()));
                }
                break;
            case ADDEDBYTHEIRS:
                containingFolder.getComponents().add(theirs);
                break;
            case UPDATEDBYTHEIRS:
                if (theirs.getFolderComponent() instanceof Blob) {
                    containingFolder.getComponents().add(theirs);
                } else {
                    String subFolderPath = path + File.separator + ours.getName();
                    Folder mergedSubFolder = MagitManager.addFilesToMergedFolderAndConflicts((Folder) ours.getFolderComponent(), (Folder) theirs.getFolderComponent(), (Folder) ancestors.getFolderComponent(), conflicts,subFolderPath,updaterName);
                    containingFolder.getComponents().add(new Folder.ComponentData(theirs.getName(),
                            mergedSubFolder.sha1Folder(), mergedSubFolder, theirs.getLastModifier(), theirs.getLastModifiedDate()));
                }
                break;
            case OURSDELETEDTHEIRSUPDATEDCONFLICT:
                if (theirs.getFolderComponent() instanceof Blob) {
                    conflicts.AddConflictComponent(new ConflictComponent(null,
                            theirs, ancestors, containingFolder,path,updaterName,getDate()));
                } else {
                    String subFolderPath = path + File.separator + theirs.getName();
                    MagitManager.addFilesToMergedFolderAndConflicts(new Folder(), (Folder) theirs.getFolderComponent(),
                            (Folder) ancestors.getFolderComponent(), conflicts,subFolderPath,updaterName);
                }
                break;

            case OURSUPDATEDTHEIRSDELETEDCONFLICT:
                if (ours.getFolderComponent() instanceof Blob) {
                    conflicts.AddConflictComponent(new ConflictComponent(ours,
                            null, ancestors, containingFolder,path,updaterName,getDate()));
                }else {
                    String subFolderPath = path + File.separator + ours.getName();
                    MagitManager.addFilesToMergedFolderAndConflicts((Folder)ours.getFolderComponent(),new Folder(),
                            (Folder)ancestors.getFolderComponent(),conflicts,subFolderPath,updaterName);
                }
                break;

            case OURSADDEDTHEIRSADDEDIFFERENTLYDCONFLICT:
                if(ours.getFolderComponent() instanceof Blob) {
                    conflicts.AddConflictComponent(new ConflictComponent(ours,
                            theirs, null, containingFolder,path,updaterName,getDate()));
                }else {
                    String subFolderPath = path + File.separator + ours.getName();
                    Folder mergedSubFolder = MagitManager.addFilesToMergedFolderAndConflicts((Folder)ours.getFolderComponent(),(Folder)theirs.getFolderComponent(),new Folder(),conflicts,subFolderPath,updaterName);
                    containingFolder.getComponents().add(new Folder.ComponentData(ours.getName(),mergedSubFolder.sha1Folder(),mergedSubFolder,updaterName,getDate()) );
                }

                break;
            case OURSUPDATEDTHEIRSUPDATEDDIFFERENTLYCONFLICT:
                if(ours.getFolderComponent() instanceof Blob) {
                    conflicts.AddConflictComponent(new ConflictComponent(ours,
                            theirs, ancestors, containingFolder,path,updaterName,getDate()));
                }else {
                    String subFolderPath = path + File.separator + ours.getName();
                    Folder mergedSubFolder = MagitManager.addFilesToMergedFolderAndConflicts((Folder)ours.getFolderComponent(),(Folder)theirs.getFolderComponent(),(Folder)ancestors.getFolderComponent(),conflicts,subFolderPath,updaterName);
                    containingFolder.getComponents().add(new Folder.ComponentData(ours.getName(),mergedSubFolder.sha1Folder(),mergedSubFolder,updaterName,getDate()) );
                }
                break;
        }
    }

    private String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    SingleFileMerger(boolean b1, boolean b2, boolean b3, boolean b4, boolean b5, boolean b6) {
        existsInOurs = b1;
        existsInTheirs = b2;
        existsInAncestors = b3;
        oursEqualsTheirs = b4;
        oursEqualsAncestors = b5;
        theirsEqualsAncestors = b6;
    }

}
