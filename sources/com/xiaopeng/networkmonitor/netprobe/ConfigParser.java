package com.xiaopeng.networkmonitor.netprobe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaopeng.networkmonitor.netprobe.model.ProbeConfig;
import java.util.Date;
/* loaded from: classes.dex */
public class ConfigParser {
    public static ProbeConfig parseProbeConfig(String config) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateDeserializer());
        builder.registerTypeAdapter(ProbeTaskConfig.class, new TaskConfigDeserializer());
        Gson gson = builder.create();
        return (ProbeConfig) gson.fromJson(config, (Class<Object>) ProbeConfig.class);
    }
}
