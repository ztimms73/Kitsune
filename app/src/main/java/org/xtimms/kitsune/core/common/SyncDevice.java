package org.xtimms.kitsune.core.common;

public class SyncDevice {
    public final int id;
    public final String name;
    public final long created_at;

    public SyncDevice(int id, String name, long created_at) {
        this.id = id;
        this.name = name;
        this.created_at = created_at;
    }
}
