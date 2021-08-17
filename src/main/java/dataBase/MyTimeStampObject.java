package dataBase;

import java.time.Instant;

public class MyTimeStampObject {

    Instant instant;
    double value;

    public MyTimeStampObject(Instant instant, double value) {
        this.instant = instant;
        this.value = value;
    }

    public Instant getInstant() {
        return instant;
    }

    public double getValue() {
        return value;
    }
}
