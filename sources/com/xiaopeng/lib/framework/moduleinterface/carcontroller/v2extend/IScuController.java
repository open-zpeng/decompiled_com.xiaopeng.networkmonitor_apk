package com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend;

import com.xiaopeng.lib.framework.moduleinterface.carcontroller.AbstractEventMsg;
/* loaded from: classes.dex */
public interface IScuController {

    /* loaded from: classes.dex */
    public static class EnvCharacterInfoEventMsg extends AbstractEventMsg<Object[]> {
    }

    /* loaded from: classes.dex */
    public static class ParkSlotInfoEventMsg extends AbstractEventMsg<Object[]> {
    }

    /* loaded from: classes.dex */
    public static class PositionInfoEventMsg extends AbstractEventMsg<Object[]> {
    }

    /* loaded from: classes.dex */
    public static class RadarDataInfoEventMsg extends AbstractEventMsg<Object[]> {
    }

    float[] getCarPositionInfo() throws Exception;

    float[] getEnvCharacterInfo() throws Exception;

    float[] getParkSlotInfo() throws Exception;

    float[] getRadarDataInfo() throws Exception;
}
