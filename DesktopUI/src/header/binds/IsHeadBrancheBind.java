package header.binds;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;

public class IsHeadBrancheBind extends BooleanBinding {
    String myName;
    SimpleStringProperty headBranchName;

    public IsHeadBrancheBind(String myName, SimpleStringProperty headBranchName) {
        this.myName = myName;
        this.headBranchName = headBranchName;
        bind(headBranchName);
    }

    @Override

    protected boolean computeValue() {
        return myName.equals(headBranchName.getValue());
    }
}
