package com.xiaopeng.lib.framework.moduleinterface.carcontroller;
/* loaded from: classes.dex */
public interface IMcuController extends ILifeCycle {
    public static final int ACTION_POWER_OFF_COUNTDOWN_CANCLE = 0;
    public static final int ACTION_POWER_OFF_COUNTDOWN_END = 1;
    public static final int COMMAND_LAMP_ACTIVE_AUTO_LIGHT = 1;
    public static final int COMMAND_LAMP_ACTIVE_PARKING_AND_LOW_BEAM = 3;
    public static final int COMMAND_LAMP_ACTIVE_PARKING_LIGHT = 2;
    public static final int COMMAND_LAMP_OFF = 4;
    public static final int COMMAND_REAR_FOG_OFF = 2;
    public static final int COMMAND_REAR_FOG_ON = 1;
    public static final int COMMAND_WINDOWS_AUTO_DOWN = 2;
    public static final int COMMAND_WINDOWS_AUTO_UP = 1;
    public static final int COMMAND_WINDOWS_STOP = 3;
    public static final int COMMAND_WINDOWS_VENTILATE_MODE = 4;
    public static final int DRIVING_STATUS_MODE_COMFORT = 0;
    public static final int DRIVING_STATUS_MODE_ECO = 1;
    public static final int DRIVING_STATUS_MODE_SPORT = 2;
    public static final int MCU_DRIVING_MODE_COMFORT = 1;
    public static final int MCU_DRIVING_MODE_ECO = 2;
    public static final int MCU_DRIVING_MODE_ECO_PLUS = 3;
    public static final int MCU_DRIVING_MODE_SPORT = 4;
    public static final int MCU_VENTILATE_INVALID = 0;
    public static final int MCU_VENTILATE_STATE = 1;
    public static final int NOTICE_POWER_OFF_COUNTDOWN_END = 1;
    public static final int NOTICE_POWER_OFF_COUNTDOWN_START = 0;
    public static final int SEAT_SLOW_MOVE_ANGL_BACKWARD = 6;
    public static final int SEAT_SLOW_MOVE_ANGL_FORWARD = 5;
    public static final int SEAT_SLOW_MOVE_HORI_BACKWARD = 2;
    public static final int SEAT_SLOW_MOVE_HORI_FORWARD = 1;
    public static final int SEAT_SLOW_MOVE_PENDING = 2;
    public static final int SEAT_SLOW_MOVE_START = 1;
    public static final int SEAT_SLOW_MOVE_STOP = 3;
    public static final int SEAT_SLOW_MOVE_VERT_BACKWARD = 4;
    public static final int SEAT_SLOW_MOVE_VERT_FORWARD = 3;
    public static final int VEHICLE_PM_STATUS_DEEP_SLEEP = 3;
    public static final int VEHICLE_PM_STATUS_FAKE_OFF = 1;
    public static final int VEHICLE_PM_STATUS_NORMAL = 0;
    public static final int VEHICLE_PM_STATUS_OFF = 4;
    public static final int VEHICLE_PM_STATUS_SLEEP_OFF = 2;

    /* loaded from: classes.dex */
    public static class AutoPowerOffNoticeEventMsg extends AbstractEventMsg<Integer> {
    }

    /* loaded from: classes.dex */
    public static class AutoPowerOffStateEventMsg extends AbstractEventMsg<Boolean> {
    }

    /* loaded from: classes.dex */
    public static class BurnErrorEventMsg extends AbstractEventMsg<Boolean> {
    }

    /* loaded from: classes.dex */
    public static class BurnFinishEventMsg extends AbstractEventMsg<Boolean> {
    }

    /* loaded from: classes.dex */
    public static class BurnProgressEventMsg extends AbstractEventMsg<Integer> {
    }

    /* loaded from: classes.dex */
    public static class ChargeInfoEventMsg extends AbstractEventMsg<int[]> {
    }

    /* loaded from: classes.dex */
    public static class FactoryDisplayTypeMsgToMcuEventMsg extends AbstractEventMsg<String> {
    }

    /* loaded from: classes.dex */
    public static class FactoryDugReqMsgToMcuEventMsg extends AbstractEventMsg<byte[]> {
    }

    /* loaded from: classes.dex */
    public static class FactoryMcuBmsMsgToMcuEventMsg extends AbstractEventMsg<int[]> {
    }

    /* loaded from: classes.dex */
    public static class FactoryPwrDebugMsgToMcuEventMsg extends AbstractEventMsg<byte[]> {
    }

    /* loaded from: classes.dex */
    public static class FactorySecretKeyToMcuEventMsg extends AbstractEventMsg<byte[]> {
    }

    /* loaded from: classes.dex */
    public static class FactoryTestMsgToMcuEventMsg extends AbstractEventMsg<int[]> {
    }

    /* loaded from: classes.dex */
    public static class Mcu4gErrorEventMsg extends AbstractEventMsg<byte[]> {
    }

    /* loaded from: classes.dex */
    public static class McuAckPwrDebugEventMsg extends AbstractEventMsg<int[]> {
    }

    /* loaded from: classes.dex */
    public static class McuDtcReportEVChangeEvent extends AbstractEventMsg<int[]> {
    }

    /* loaded from: classes.dex */
    public static class McuFaultInfoEventMsg extends AbstractEventMsg<byte[]> {
    }

    /* loaded from: classes.dex */
    public static class McuGpsInfoEventMsg extends AbstractEventMsg<Integer> {
    }

    /* loaded from: classes.dex */
    public static class McuGsensorOffsetEventMsg extends AbstractEventMsg<int[]> {
    }

    /* loaded from: classes.dex */
    public static class McuIgStatusChangeEventMsg extends AbstractEventMsg<int[]> {
    }

    /* loaded from: classes.dex */
    public static class McuOta1EventMsg extends AbstractEventMsg<int[]> {
    }

    /* loaded from: classes.dex */
    public static class McuPmStatusEventMsg extends AbstractEventMsg<int[]> {
    }

    /* loaded from: classes.dex */
    public static class McuPsuOtaEventMsg extends AbstractEventMsg<int[]> {
    }

    /* loaded from: classes.dex */
    public static class McuReset4gEventMsg extends AbstractEventMsg<int[]> {
    }

    /* loaded from: classes.dex */
    public static class McuTheftStateEventMsg extends AbstractEventMsg<int[]> {
    }

    int getCpuTemperature() throws Exception;

    int[] getDtcReportEV() throws Exception;

    String getFactoryDisplayTypeMsgToMcu() throws Exception;

    int[] getFactoryDugReqMsgToMcu() throws Exception;

    int getFactoryMcuBmsMsgToMcu() throws Exception;

    int getFactoryPmSilentMsgToMcu() throws Exception;

    int[] getFactoryPwrDebugMsgToMcu() throws Exception;

    int[] getGSensorOffset() throws Exception;

    String getHardwareCarStage() throws Exception;

    String getHardwareCarType() throws Exception;

    int getHardwareVersion();

    int[] getIgStatus() throws Exception;

    int getMcu4GErrorStatus() throws Exception;

    byte[] getMcuFaultInfo() throws Exception;

    String getMcuHardwareId() throws Exception;

    int getMcuTheftState();

    int getMcuVentilateState() throws Exception;

    int getPmStatus() throws Exception;

    int getPowerOffCountdownNotice() throws Exception;

    String getXpCduType() throws Exception;

    boolean isAutoPowerOff() throws Exception;

    void sendChargeCompleteTime2Mcu(int i) throws Exception;

    void sendDiagnoseMsgToMcu(int[] iArr) throws Exception;

    void sendFactoryDisplayTypeMsgToMcu(int i) throws Exception;

    void sendFactoryDugReqMsgToMcu(int[] iArr) throws Exception;

    void sendFactoryMcuBmsMsgToMcu(int i) throws Exception;

    void sendFactoryPmSilentMsgToMcu(int i) throws Exception;

    void sendFactoryPwrDebugMsgToMcu(int[] iArr) throws Exception;

    void sendFactorySecretKeyToMcu(byte[] bArr) throws Exception;

    void sendFactoryTestMsgToMcu(int[] iArr) throws Exception;

    void sendGpsInfoMsgToMcu(int[] iArr) throws Exception;

    void sendOta1MsgToMcu(int[] iArr) throws Exception;

    void sendPsuOtaMsgToMcu(int[] iArr) throws Exception;

    void sendRequestWakeToMcu(int i) throws Exception;

    void sendReset4gMsgToMcu(int[] iArr) throws Exception;

    void sendResetModemMsgToMcu(int i) throws Exception;

    void setAutoPowerOff(boolean z) throws Exception;

    void setChairSlowlyMove(int[] iArr) throws Exception;

    void setDriveMode(int i) throws Exception;

    void setFlash(boolean z) throws Exception;

    void setHorn(boolean z) throws Exception;

    void setIgHeartBeat() throws Exception;

    void setIgOff() throws Exception;

    void setIgOn() throws Exception;

    void setLampCommand(int i) throws Exception;

    void setMcuIsWakeUpByPhone(boolean z) throws Exception;

    void setMqttLogInfo(String str, String str2, String str3, String str4) throws Exception;

    void setPowerOffCountdownAction(int i) throws Exception;

    void setRearFogCommand(int i) throws Exception;

    void setRepairMode(boolean z) throws Exception;

    void setShockValue2Mcu(int i) throws Exception;

    void setTheftHeartBeatOff() throws Exception;

    void setTheftHeartBeatOn() throws Exception;

    void setWindowsCommand(int i) throws Exception;

    void updateMcu(String str) throws Exception;
}
