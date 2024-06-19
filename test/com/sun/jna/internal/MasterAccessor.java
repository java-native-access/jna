package com.sun.jna.internal;

import java.util.Set;

public class MasterAccessor {
    public static synchronized boolean masterIsRunning() { // synchronized is for memory synch not mutex
        return Cleaner.MasterCleaner.INSTANCE != null;
    }

    public static synchronized Set<?> getCleanerImpls() { // synchronized is for memory synch not mutex
        Cleaner.MasterCleaner mc = Cleaner.MasterCleaner.INSTANCE;
        return mc == null ? null : mc.cleaners;
    }
}
