package com.mvp.common.tools;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by tanglin on 2017/7/3.
 */

public class UserSessionManager {

    BehaviorSubject<Integer> sessionTimeCheckSubject = null;
    Disposable disposable = null;
    public interface RunForReachInterval {
        void timeup();
    };

    private static UserSessionManager single = null;

    private UserSessionManager() {

    }

    public static UserSessionManager getInstance() {
        if (single == null) {
            synchronized (UserSessionManager.class) {
                if (single == null) {
                    single = new UserSessionManager();
                }
            }
        }
        return single;
    }

    //called method after login
    public void start(int interval, final RunForReachInterval reachInterval){
        if (sessionTimeCheckSubject !=null) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
                sessionTimeCheckSubject = null;
            }
        }

        sessionTimeCheckSubject = BehaviorSubject.create();
        disposable = sessionTimeCheckSubject.delay(interval * 60, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        reachInterval.timeup();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
        sessionTimeCheckSubject.onNext(0);
    }

    public void cancel(){
        sessionTimeCheckSubject.onComplete();
        disposable.dispose();
        sessionTimeCheckSubject = null;
        disposable = null;
    }
}
