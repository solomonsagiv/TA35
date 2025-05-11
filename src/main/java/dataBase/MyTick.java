package dataBase;

import java.time.LocalDateTime;

public class MyTick {
    public LocalDateTime time;
    public double value;

    public MyTick(LocalDateTime time, double value) {
        this.time = time;
        this.value = value;
    }
}
