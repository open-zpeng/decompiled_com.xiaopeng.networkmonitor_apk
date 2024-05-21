package com.xiaopeng.networkmonitor.netprobe.model;
/* loaded from: classes.dex */
public final class StatsEntry {
    public int tag;
    public int uid;

    public StatsEntry(int uid, int tag) {
        this.uid = uid;
        this.tag = tag;
    }

    public String toString() {
        return "StatsEntry {uid=" + this.uid + ",tag=" + String.format("0x%x", Integer.valueOf(this.tag)) + "}";
    }
}
