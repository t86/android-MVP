package com.mvp.common.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * TimerManager
 * 计时器管理器
 *
 * @author Allon
 * @date 16/4/29 下午5:49
 */
public class TimerManager {
    //计时器计时时长(毫秒)
    private static final long DEFAULT_TIMER_MILLIS_INFUTURE = 120000L;
    //计时器每次跳动的时间间隔(毫秒)
    private static final long DEFAULT_TIMER_COUNT_DOWN_INTERVAL = 1000L;
    //保存所有计时器的容器
    private static Map<String, CountDownTimer> timerMap = new HashMap<>();

    private static TimerManager single = null;

    private TimerManager() {

    }

    public static TimerManager getInstance() {
        if (single == null) {
            synchronized (TimerManager.class) {
                if (single == null) {
                    single = new TimerManager();
                }
            }
        }
        return single;
    }

    /**
     * 创建计时器
     *
     * @param timerTag          计时器tag
     * @param millisInFuture    计时器计时时长(毫秒)
     * @param countDownInterval 计时器每次跳动的时间间隔(毫秒)
     * @param listener          计时器事件监听
     * @param destroyIfPageEnd  是否在页面切换时销毁计时器
     * @return CountDownTimer
     * @author admin
     * @time 16/5/1 下午9:15
     */
    public CountDownTimer createTimer(String timerTag, long millisInFuture, long countDownInterval, TimerListener listener, boolean destroyIfPageEnd) {
        if (timerMap.containsKey(timerTag)) {
            CountDownTimer timer = timerMap.get(timerTag);
            if (timer != null) {
                timer.setTimer(millisInFuture, countDownInterval);
                timer.serTimerListener(listener);
                return timer;
            } else {
                timerMap.remove(timerTag);
            }
        }
        CountDownTimer timer = new CountDownTimer(timerTag, millisInFuture, countDownInterval, listener, destroyIfPageEnd);
        timerMap.put(timerTag, timer);
        return timer;
    }

    /**
     * 创建计时器
     *
     * @param timerTag         计时器tag
     * @param listener         计时器事件监听
     * @param destroyIfPageEnd 是否在页面切换时销毁计时器
     * @return CountDownTimer
     * @author admin
     * @time 16/5/1 下午9:15
     */
    public CountDownTimer createTimer(String timerTag, TimerListener listener, boolean destroyIfPageEnd) {
        return createTimer(timerTag, DEFAULT_TIMER_MILLIS_INFUTURE, DEFAULT_TIMER_COUNT_DOWN_INTERVAL, listener, destroyIfPageEnd);
    }

    /**
     * 创建计时器
     *
     * @param timerTag 计时器tag
     * @param listener 计时器事件监听
     * @return CountDownTimer
     * @author admin
     * @time 16/5/1 下午9:15
     */
    public CountDownTimer createTimer(String timerTag, TimerListener listener) {
        return createTimer(timerTag, listener, false);
    }

    /**
     * 销毁指定的计时器
     *
     * @param timerTag 计时器tag
     * @author admin
     * @time 16/5/1 下午9:12
     */
    public void destroyTimerByTag(String timerTag) {
        if (timerMap != null && timerMap.containsKey(timerTag)) {
            CountDownTimer timer = timerMap.get(timerTag);
            if (timer != null) {
                timer.serTimerListener(null);
                if (timer.pageEndIfCancelled()) {
                    timer.cancel();
                    timerMap.remove(timerTag);
                }
            } else {
                timerMap.remove(timerTag);
            }
        }
    }

    /*
     * 销毁所有的计时器
     *
     * @author Allon
     * @time 16/5/1 下午8:57
     */
    public void destroyTimers() {
        Iterator iterator = timerMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, CountDownTimer> entry = (Map.Entry<String, CountDownTimer>) iterator.next();
            CountDownTimer timer = entry.getValue();
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }
}
