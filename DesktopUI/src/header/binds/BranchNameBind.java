package header.binds;

import javafx.beans.binding.StringBinding;

public class BranchNameBind extends StringBinding {
    String myName;
    IsHeadBrancheBind isHeadBranch;

    public BranchNameBind(String branchName, IsHeadBrancheBind isHeadBranch) {
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
