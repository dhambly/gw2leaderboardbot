package accounts;

import java.io.Serializable;

public class GW2AccountScores implements Serializable {

    private String id;
    private short value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }
}
