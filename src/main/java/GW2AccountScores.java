import java.io.Serializable;

public class GW2AccountScores implements Serializable {

    private String id;
    private int value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
