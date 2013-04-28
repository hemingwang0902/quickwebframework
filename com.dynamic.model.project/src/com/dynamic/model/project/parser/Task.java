package com.dynamic.model.project.parser;

/***
 * task 任务项
 * @author Binary
 */
public class Task {
	/***
	 * 任务主键
	 */
	private String key;
	/***
	 * 任务源文件
	 */
	private String resource;
	/***
	 * 任务目标文件
	 */
	private String destination;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
}
