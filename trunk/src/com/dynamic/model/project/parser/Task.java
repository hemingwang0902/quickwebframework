package com.dynamic.model.project.parser;

/***
 * task ������
 * @author Binary
 */
public class Task {
	/***
	 * ��������
	 */
	private String key;
	/***
	 * ����Դ�ļ�
	 */
	private String resource;
	/***
	 * ����Ŀ���ļ�
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
