package body.binds;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;

public class ParentIsNullBind extends BooleanBinding {
    private SimpleStringProperty parentSha1;

    public ParentIsNullBind(SimpleStringProperty parentSha1Property) {
        parentSha1 = parentSha1Property;
        bind(parentSha1Property);
    }

    @Override
    protected boolean computeValue() {
        return parentSha1.getValue() == null;
    }
}
