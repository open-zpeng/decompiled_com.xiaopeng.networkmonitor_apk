package com.xiaopeng.lib.framework.moduleinterface.carcontroller;
/* loaded from: classes.dex */
public interface IEpsController extends ILifeCycle {
    public static final int EPS_POWER_ASSISTED_SOFT = 1;
    public static final int EPS_POWER_ASSISTED_SPORT = 2;
    public static final int EPS_POWER_ASSISTED_STANDARD = 0;
    public static final int POWER_STATUS_ASSISTED_SOFT = 1;
    public static final int POWER_STATUS_ASSISTED_SPORT = 2;
    public static final int POWER_STATUS_ASSISTED_STANDARD = 0;

    /* loaded from: classes.dex */
    public static class SteeringWheelEPSEventMsg extends AbstractEventMsg<Integer> {
    }

    int getSteeringWheelEPS() throws Exception;

    void setSteeringWheelEPS(int i) throws Exception;
}
