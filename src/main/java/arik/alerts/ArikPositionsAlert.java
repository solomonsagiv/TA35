package arik.alerts;

import threads.MyThread;

import java.util.ArrayList;

public class ArikPositionsAlert extends MyThread implements Runnable {

    public static ArrayList<ArikAlgoAlert> algo_list;

    public ArikPositionsAlert(ArrayList<ArikAlgoAlert> algo_list) {
        super();
        this.algo_list = algo_list;
    }

    @Override
    public void run() {

        while (isRun()) {
            try {
                System.out.println(getName());
                
                // Sleep
                Thread.sleep(10000);

                // Logic
                logic();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void logic() {
        for (ArikAlgoAlert algo : algo_list) {
            algo.go();
        }
    }

    @Override
    public void initRunnable() {
        setRunnable(this);
    }

    @Override
    public String getName() {
        return "Alert service";
    }


}
