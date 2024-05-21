package com.xiaopeng.lib.framework.moduleinterface.carcontroller;
/* loaded from: classes.dex */
public interface IEspController extends ILifeCycle {

    /* loaded from: classes.dex */
    public static class AVHEventMsg extends AbstractEventMsg<Boolean> {
    }

    /* loaded from: classes.dex */
    public static class AVHFaultEventMsg extends AbstractEventMsg<Boolean> {
    }

    /* loaded from: classes.dex */
    public static class ESPEventMsg extends AbstractEventMsg<Boolean> {
    }

    /* loaded from: classes.dex */
    public static class ESPFaultEventMsg extends AbstractEventMsg<Boolean> {
    }

    /* loaded from: classes.dex */
    public static class EpbWarningLampOnEventMsg extends AbstractEventMsg<Boolean> {
    }

    /* loaded from: classes.dex */
    public static class EpsWarningLampOnEventMsg extends AbstractEventMsg<Boolean> {
    }

    /* loaded from: classes.dex */
    public static class HDCEventMsg extends AbstractEventMsg<Boolean> {
    }

    /* loaded from: classes.dex */
    public static class HDCFaultEventMsg extends AbstractEventMsg<Boolean> {
    }

    boolean getAVH() throws Exception;

    boolean getAvhFault() throws Exception;

    boolean getESP() throws Exception;

    boolean getEspFault() throws Exception;

    boolean getHDC() throws Exception;

    boolean getHdcFault() throws Exception;

    boolean isEpbWarningLampOn() throws Exception;

    boolean isEpsWarningLampOn() throws Exception;

    void setAVH(boolean z) throws Exception;

    void setESP(boolean z) throws Exception;

    void setHDC(boolean z) throws Exception;
}
