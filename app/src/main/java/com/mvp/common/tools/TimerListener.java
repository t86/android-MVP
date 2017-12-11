package com.mvp.common.tools;

/**
 * TimerListener
 * 计时器回调
 *
 * @author Allon
 * @date 16/5/1 下午3:19
 */
public interface TimerListener {
    /**
     * 计时器每次跳动的回调
     *
     * @param leaveTime 当前剩余计时(毫秒)
     * @author admin
     * @time 16/5/1 下午9:24
     */
    void onTick(String timerKey, long leaveTime);

    /**
     * 计时器计时结束的回调
     *
     * @author admin
     * @time 16/5/1 下午9:24
     */
    void onFinish(String timerKey);
}
