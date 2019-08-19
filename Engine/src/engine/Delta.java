package engine;

import java.util.ArrayList;
import java.util.List;

public class Delta {
    private List<DeltaComponent> addedFiles;
    private List<DeltaComponent> updatedFiles;
    private List<DeltaComponent> deletedFiles;

    public Delta() {
        addedFiles = new ArrayList<>();
        updatedFiles = new ArrayList<>();
        deletedFiles = new ArrayList<>();
    }

    public List<DeltaComponent> getAddedFiles() {
        return addedFiles;
    }

    public List<DeltaComponent> getUpdatedFiles() {
        return updatedFiles;
    }

    public List<DeltaComponent> getDeletedFiles() {
        return deletedFiles;
    }

    public boolean isEmpty() {
        return addedFiles.isEmpty() && updatedFiles.isEmpty() && deletedFiles.isEmpty();
    }



    @Override
    public String toString() {
        StringBuilder deltaToString = new StringBuilder();
        if(!addedFiles.isEmpty()){
            deltaToString.append("The files that were added:\r\n");
           deltaToString.append(convertDeltaComponentsListToString(addedFiles));
        }
        if(!updatedFiles.isEmpty()){
            deltaToString.append("The files that were updated:\r\n");
            deltaToString.append(convertDeltaComponentsListToString(updatedFiles));
        }
        if(!deletedFiles.isEmpty()){
            deltaToString.append("The files that were deleted:\r\n");
            deltaToString.append(convertDeltaComponentsListToString(deletedFiles));
        }
        return deltaToString.toString();
    }

    public String convertDeltaComponentsListToString(List<DeltaComponent> list){
        StringBuilder sb = new StringBuilder();
        int dcNumber = 1;
        for(DeltaComponent dc : list){
            sb.append(String.format("%d. name: %s: " +"Type:%s  "+"Path: %s\r\n\r\n",dcNumber,dc.getName(),dc.getType(),dc.getPath().toString()));
       dcNumber++;
        }
        return  sb.toString();
    }

}
