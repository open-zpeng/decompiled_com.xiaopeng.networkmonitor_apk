package com.xiaopeng.networkmonitor.netprobe;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.xiaopeng.networkmonitor.netprobe.dns.DnsTaskConfig;
import com.xiaopeng.networkmonitor.netprobe.http.HttpTaskConfig;
import com.xiaopeng.networkmonitor.netprobe.ping.PingTaskConfig;
import com.xiaopeng.networkmonitor.netprobe.tcp.TcpTaskConfig;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
/* loaded from: classes.dex */
public class TaskConfigDeserializer implements JsonDeserializer<ProbeTaskConfig> {
    private static final String TAG = "TaskConfigDeserializer";

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.gson.JsonDeserializer
    public ProbeTaskConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonContext) throws JsonParseException {
        Log.d(TAG, "deserialize()");
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        ProbeTaskConfig taskConfig = null;
        if (jsonObject != null) {
            String ptype = jsonObject.get(ConfigConstants.KEY_TASK_TYPE).getAsString();
            String probeType = !TextUtils.isEmpty(ptype) ? ptype.toUpperCase() : "";
            char c = 65535;
            switch (probeType.hashCode()) {
                case 67849:
                    if (probeType.equals(ProbeType.PROBE_TYPE_DNS)) {
                        c = 3;
                        break;
                    }
                    break;
                case 82881:
                    if (probeType.equals(ProbeType.PROBE_TYPE_TCP)) {
                        c = 1;
                        break;
                    }
                    break;
                case 2228360:
                    if (probeType.equals(ProbeType.PROBE_TYPE_HTTP)) {
                        c = 0;
                        break;
                    }
                    break;
                case 2455922:
                    if (probeType.equals(ProbeType.PROBE_TYPE_PING)) {
                        c = 2;
                        break;
                    }
                    break;
            }
            if (c == 0) {
                taskConfig = new HttpTaskConfig();
            } else if (c == 1) {
                taskConfig = new TcpTaskConfig();
            } else if (c == 2) {
                taskConfig = new PingTaskConfig();
            } else if (c == 3) {
                taskConfig = new DnsTaskConfig();
            } else {
                taskConfig = new ProbeTaskConfig();
            }
            taskConfig.setProbeType(probeType);
            if (jsonObject.get(ConfigConstants.KEY_TASK_ID) != null) {
                taskConfig.setTaskId(jsonObject.get(ConfigConstants.KEY_TASK_ID).getAsInt());
            }
            if (jsonObject.get(ConfigConstants.KEY_TASK_NAME) != null) {
                taskConfig.setTaskName(jsonObject.get(ConfigConstants.KEY_TASK_ID).getAsString());
            }
            if (jsonObject.get(ConfigConstants.KEY_INTERVAL) != null) {
                long interval = jsonObject.get(ConfigConstants.KEY_INTERVAL).getAsLong();
                if (interval < ConfigConstants.DEFAULT_PROBE_INTERVAL) {
                    taskConfig.setProbeInterval(ConfigConstants.DEFAULT_PROBE_INTERVAL);
                } else {
                    taskConfig.setProbeInterval(interval);
                }
            }
            if (jsonObject.get(ConfigConstants.KEY_START_TIME) != null) {
                Date date = (Date) jsonContext.deserialize(jsonObject.get(ConfigConstants.KEY_START_TIME), Date.class);
                taskConfig.setStartTime(date.getTime());
            }
            if (jsonObject.get(ConfigConstants.KEY_END_TIME) != null) {
                Date date2 = (Date) jsonContext.deserialize(jsonObject.get(ConfigConstants.KEY_END_TIME), Date.class);
                taskConfig.setEndTime(date2.getTime());
            }
            if (jsonObject.get(ConfigConstants.KEY_TARGET) != null) {
                taskConfig.setProbeUrl(jsonObject.get(ConfigConstants.KEY_TARGET).getAsString());
            }
            if (jsonObject.get(ConfigConstants.KEY_ALLOW_RESTART_LTE) != null) {
                taskConfig.setAllowDialog(jsonObject.get(ConfigConstants.KEY_ALLOW_RESTART_LTE).getAsBoolean());
            }
            if (jsonObject.get(ConfigConstants.KEY_TASK_PARAM) != null) {
                List<String> params = (List) jsonContext.deserialize(jsonObject.get(ConfigConstants.KEY_TASK_PARAM).getAsJsonArray(), List.class);
                taskConfig.setTaskParams(params);
            }
        }
        Log.d(TAG, "deserialize(): task config = " + taskConfig);
        return taskConfig;
    }
}
