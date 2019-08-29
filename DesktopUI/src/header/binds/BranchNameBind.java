package header.binds;

import javafx.beans.binding.StringBinding;

public class BranchNameBind extends StringBinding {
    String myName;
    IsHeadBranchBind isHeadBranch;

    public BranchNameBind(String branchName, IsHeadBranchBind isHeadBranch) {
        this.myName = branchName;
        this.isHeadBranch = isHeadBranch;
        bind(isHeadBranch);
    }

    @Override
    protected String computeValue() {
        String branchString = myName;
        if (isHeadBranch.getValue().equals(Boolean.TRUE)){
            branchString+="  -Head";
        }
        return branchString ;
    }
}
