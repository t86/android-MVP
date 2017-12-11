/**
 * 
 */
package com.mvp.common.retrofit_rx.http.resmodel;

/**
 * @功能:操作结果
 * @项目名:kyloanServer
 * @作者:wangjz
 * @日期:2016年3月21日下午2:30:42
 */
public class OptResult {
	/** 消息代码,大于等于0表示正确，小于表示错误 */
	private Integer code;
	/** 消息内容 */
	private String msg;
	/** 临时参数 */
	private Object temp;

	/**
	 * 构造方法
	 */
	public OptResult() {
		super();
	}

	/**
	 * 构造方法
	 * 
	 * @param code
	 * @param msg
	 */
	public OptResult(Integer code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	/**
	 * 构造方法
	 * 
	 * @param code
	 * @param msg
	 * @param temp
	 */
	public OptResult(Integer code, String msg, Object temp) {
		super();
		this.code = code;
		this.msg = msg;
		this.temp = temp;
	}

	/**
	 * @取得 消息代码大于等于0表示正确，小于表示错误
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @设置 消息代码大于等于0表示正确，小于表示错误
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @取得 消息内容
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @设置 消息内容
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @取得 临时参数
	 */
	public Object getTemp() {
		return temp;
	}

	/**
	 * @设置 临时参数
	 */
	public void setTemp(Object temp) {
		this.temp = temp;
	}

}
