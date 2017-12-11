package com.mvp.common.tools;

    /*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.lang.ref.WeakReference;

/**
 * Schedule a countdown until a time in the future, with
 * regular notifications on intervals along the way.
 * <p/>
 * Example of showing a 30 second countdown in a text field:
 * <p/>
 * <p/>
 * new CountDownTimer(30000, 1000) {
 * <p/>
 * public void onTick(long millisUntilFinished) {
 * mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
 * }
 * <p/>
 * public void onFinish() {
 * mTextField.setText("done!");
 * }
 * }.start();
 * <p/>
 * <p/>
 * The calls to {@link #onTick(long)} are synchronized to this object so that
 * one call to {@link #onTick(long)} won't ever occur before the previous
 * callback is complete. This is only relevant when the implementation of
 * {@link #onTick(long)} takes an amount of time to execute that is significant
 * compared to the countdown interval.
 */

/**
 * Created by Allon on 2016/2/17.
 */
public class CountDownTimer {
    /**
     * Millis since epoch when alarm should stop.
     */
    private long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private long mCountdownInterval;

    private long mStopTimeInFuture;

    /**
     * boolean representing if the timer was cancelled
     */
    private boolean mCancelled = true;

    private boolean mCancelledIfPageEnd = false;
    private String mTimerKey;
    //Timer回调
    private TimerListener listener;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimer(String timerKey, long millisInFuture, long countDownInterval, TimerListener listener, boolean mCancelledIfPageEnd) {
        this.mTimerKey = timerKey;
        this.listener = listener;
        this.mCancelledIfPageEnd = mCancelledIfPageEnd;
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    public boolean pageEndIfCancelled() {
        return mCancelledIfPageEnd;
    }

    public boolean isCancelled() {
        return mCancelled;
    }

    public void serTimerListener(TimerListener listener) {
        this.listener = listener;
    }

    public void setTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    /**
     * Cancel the countdown.
     */
    public synchronized final void cancel() {
        mCancelled = true;
        mHandler.removeMessages(MSG);
    }

    /**
     * Cancel the countdown.
     */
    public synchronized final void stop() {
        mCancelled = true;
        mHandler.removeMessages(MSG);
        onFinish();
    }

    /**
     * Start the countdown.
     */
    public synchronized final CountDownTimer start() {
        mCancelled = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }


    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    protected void onTick(long millisUntilFinished) {
        if (listener != null) {
            listener.onTick(mTimerKey, millisUntilFinished / mCountdownInterval);
        }
//        if (textView == null) {
//            return;
//        }
//        textView.setText(String.format(waittingText, millisUntilFinished / mCountdownInterval));
//        textView.setBackgroundResource(R.drawable.button_enable_border);
//        textView.setClickable(false);
    }

    /**
     * Callback fired when the time is up.
     */
    protected void onFinish() {
        if (listener != null) {
            listener.onFinish(mTimerKey);
        }
//        textView.setText(normalText);
//        textView.setBackgroundResource(R.drawable.button_unable_border);
//        textView.setClickable(true);
    }


    private static final int MSG = 1;

    private static class MyHandler extends Handler {
        private final WeakReference<CountDownTimer> countDownTimerWeakReference;

        public MyHandler(CountDownTimer timer) {
            countDownTimerWeakReference = new WeakReference<CountDownTimer>(timer);
        }

        @Override
        public void handleMessage(Message msg) {
            CountDownTimer countDownTimer = countDownTimerWeakReference.get();
            if (countDownTimer == null) {
                return;
            }

            synchronized (CountDownTimer.class) {
                if (countDownTimer.mCancelled) {
                    return;
                }

                final long millisLeft = countDownTimer.mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    countDownTimer.onFinish();
                } else if (millisLeft < countDownTimer.mCountdownInterval) {
                    // no tick, just delay until done
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
                } else {
                    countDownTimer.onTick(millisLeft == countDownTimer.mMillisInFuture ? millisLeft - 1 : millisLeft);
                    long lastTickStart = SystemClock.elapsedRealtime();

                    // take into account user's onTick taking time to execute
                    long delay = lastTickStart + countDownTimer.mCountdownInterval - SystemClock.elapsedRealtime();

                    // special case: user's onTick took more than interval to
                    // complete, skip to next interval
                    while (delay < 0) delay += countDownTimer.mCountdownInterval;

                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    }

    // handles counting down
    private Handler mHandler = new MyHandler(this);
}
