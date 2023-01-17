package com.algamoney.api.exceptionhandler;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Problem {
	
	private Integer status;
	private LocalDateTime timestamp;
	private String type;
	private String title;
	private String detail;
	
	private List<Field> fields = new ArrayList<>();
	
	public Problem() {
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public List<Field> getFields() {
		return fields;
	}
	
	public void setFields(List<Field> list) {
		this.fields = list;
	}
	
	public void addField(Field field) {
		fields.add(field);
	}
}
