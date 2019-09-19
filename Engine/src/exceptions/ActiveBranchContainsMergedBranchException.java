package exceptions;

public class ActiveBranchContainsMergedBranchException extends Exception  {
    @Override
    public String getMessage() {
        return "No merge was done";
    }
}
