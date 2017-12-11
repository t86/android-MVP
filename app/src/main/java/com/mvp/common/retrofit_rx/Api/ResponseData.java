/**
 *
 */
package com.mvp.common.retrofit_rx.Api;

import com.mvp.common.retrofit_rx.http.resmodel.OptResult;

/**
 * @功能: request请求返回的数据
 * @项目名:kyloanServer
 * @作者:wangjz
 * @日期:2016年3月21日下午2:26:34
 */
public class ResponseData<T> {
    /**
     * 请求结果
     */
    private OptResult reqResult;
    /**
     * 数据
     */
    private T data;

    /**
     * @构造方法
     */
    public ResponseData() {
        super();
    }

    /**
     * @param reqResult
     * @构造方法
     */
    public ResponseData(OptResult reqResult) {
        super();
        this.reqResult = reqResult;
    }

    /**
     * @param reqResult
     * @param data
     * @构造方法
     */
    public ResponseData(OptResult reqResult, T data) {
        super();
        this.reqResult = reqResult;
        this.data = data;
    }

    /**
     * @取得 请求结果
     */
    public OptResult getReqResult() {
        return reqResult;
    }

    /**
     * @设置 请求结果
     */
    public void setReqResult(OptResult reqResult) {
        this.reqResult = reqResult;
    }

    /**
     * @取得 数据
     */
    public T getData() {
        return data;
    }

    /**
     * @设置 数据
     */
    public void setData(T data) {
        this.data = data;
    }
}
