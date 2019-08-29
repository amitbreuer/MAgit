package header.binds;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;

public class IsHeadBranchBind extends BooleanBinding {
    String myName;
    SimpleStringProperty headBranchName;

    public IsHeadBranchBind(String myName, SimpleStringProperty headBranchName) {
        this.myName = myName;
        this.headBranchName = headBranchName;
        bind(headBranchName);
    }

    @Override
    protected boolean computeValue() {
        return myName.equals(headBranchName.getValue());
    }
}
