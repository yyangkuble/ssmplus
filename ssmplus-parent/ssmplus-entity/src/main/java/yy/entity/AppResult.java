package yy.entity;

import java.io.Serializable;

public class AppResult implements Serializable {
	Boolean status=true;
	Object data;
	public AppResult() {
		// TODO Auto-generated constructor stub
	}
	public AppResult(Object data) {
		// TODO Auto-generated constructor stub
		this.data=data;
	}
	public AppResult(Boolean status,Object data) {
		// TODO Auto-generated constructor stub
		this.data=data;
		this.status=status;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
}
