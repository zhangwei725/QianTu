package com.zw.retrofitlib;

import android.content.Context;
import android.util.Log;

import com.zw.retrofitlib.exception.RetrofitException;

import rx.Subscriber;

/**
 * BaseSubscriber
 * Created by Tamic on 2016-08-03.
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {

    private Context context;

    public BaseSubscriber(Context context) {
        this.context = context;
    }

    @Override
    final public void onError(java.lang.Throwable e) {
        Log.e("RetrofitManager", e.getMessage());
        if(e instanceof RetrofitThrowable){
            Log.e("RetrofitManager", "--> e instanceof RetrofitThrowable");
            onError((RetrofitThrowable)e);
        } else {
            Log.e("RetrofitManager", "e !instanceof RetrofitThrowable");
            onError(new RetrofitThrowable(e, RetrofitException.ERROR.UNKNOWN));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("RetrofitManager", "-->http is start");
        // todo some common as show loadding  and check netWork is NetworkAvailable
        // if  NetworkAvailable no !   must to call onCompleted
    }

    @Override
    public void onCompleted() {
        Log.e("RetrofitManager", "-->http is Complete");
        // todo some common as  dismiss loadding
    }
    public abstract void onError(RetrofitThrowable e);

}
