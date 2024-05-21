package com.xiaopeng.lib.framework.moduleinterface.accountmodule;
/* loaded from: classes.dex */
public interface ICallback<T, K> {
    void onFail(K k);

    void onSuccess(T t);
}
