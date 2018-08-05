package com.fnt.sys;

public class RestResponse<T> {

	private T entity;
	private String msg;
	private Integer status;

	public RestResponse() {

	}

	public RestResponse(Integer status, String msg) {
		this.status = status;
		this.entity = null;
		this.msg = msg;
	}

	public RestResponse(Integer status) {
		this.status = status;
		this.entity = null;
		this.msg = null;
	}

	public RestResponse(Integer status, T entity) {
		this.status = status;
		this.entity = entity;
		this.msg = "";
	}

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
