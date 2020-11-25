package sirs.api.hospital;

import java.util.concurrent.atomic.AtomicBoolean;

public class OperationMode {
    public static AtomicBoolean pandemicMode = new AtomicBoolean(false);

    public static String getPandemicMode() {
        if(pandemicMode.get())
            return "pandemic";
        else
            return "normal";
    }
}
