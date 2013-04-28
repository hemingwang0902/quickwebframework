package com.dynamic.model.project.parser;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dynamic.model.project.builder.DynamicPluginBuilder;
import com.dynamic.model.project.util.Console;

public class BuilderProperty {
	//constant
	private final static String SYNC="sync";
	private final static String LAUNCH="launch";
	// task 文件路径
	private final static String TASK_XML = "/resource/task.xml";
	// task config info
	private static Map<String, List<Task>> taskMap = null;
	public static Map<String, List<Task>> getTaskMap() {
		if(taskMap==null){
			 try {
				parseTask();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return taskMap;
	}
	/***
	 * 解析task文件
	 * 
	 * @return
	 * @throws DocumentException
	 */
	public static void parseTask() throws DocumentException {
		// config file
		InputStream taskInput = BuilderProperty.class.getResourceAsStream(TASK_XML);
		if (taskInput == null) {
			Console.println("读取配置失败：" + TASK_XML);
		} else {
			// Console.println("读取配置："+taskFile.getPath());
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(taskInput);
			Element assignmentElem = document.getRootElement();
			List<Element> tasksList = assignmentElem.elements("tasks");
			if (tasksList == null || tasksList.size() == 0) {
				return;
			}
			taskMap = new HashMap<String, List<Task>>();
			for (Element taskEmem : tasksList) {
				List<Task> list = new ArrayList<Task>();
				String id = taskEmem.attributeValue("id");
				List<Element> tasks = taskEmem.elements("task");
				for (Element task : tasks) {
					Task t = new Task();
					String key = task.attributeValue("key");
					String resource = task.element("resource").getText();
					String destination = task.element("destination").getText();
					t.setKey(key);
					if (resource.startsWith("/")) {
						t.setResource(resource);
					} else {
						t.setResource("/" + resource);
					}
					if (destination.startsWith("/")) {
						t.setDestination(destination);
					} else {
						t.setDestination("/" + destination);
					}
					list.add(t);
				}
				taskMap.put(id, list);
			}
		}
	}
	/****
	 * 获得服务器加载bundel相当路径
	 * @return
	 */
	public static String getLaunchPath() {
		return getDestination(DynamicPluginBuilder.BUILDER_ID,LAUNCH);
	}

	/****
	 * 获得同步bundel相当路径
	 * @return
	 */
	public static String getSyncPath() {
		return getDestination(DynamicPluginBuilder.BUILDER_ID,SYNC);
	}
	/***
	 * 
	 * @param natureId
	 * @param key
	 * @return
	 */
	public static String getDestination(String natureId, String key) {
		List<Task> taskList = getTaskMap().get(natureId);
		for (Task task : taskList) {
			if (task.getKey() == null || task.getKey().equals("")) {
				continue;
			}
			if (task.getKey().trim().equals(key)) {
				return task.getDestination();
			}
		}
		return null;
	}
}
