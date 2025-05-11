package arik.dataHandler;

public class DataObject {

    private double value = 0;
    private double pre_value = 0;
    private String name;

    public DataObject(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double new_value) {
        if (new_value != this.value) {
            this.pre_value = this.value;
            this.value = new_value;
        }
    }

    public double getPre_value() {
        return pre_value;
    }

    public String getName() {
        return name;
    }
}
