package com.dynamic.model.project.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class Console {
	/***
	 * ����̨
	 */
	private static MessageConsoleStream console =null;
	/***
	 * �䴦��Ϣ������̨
	 */
	public static void println(String log) {
		if(console==null){
			console = openLogConsole();
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		console.println(df.format(new Date())+ ">"+log);
	}
	/***
	 * ��ÿ���̨
	 * @param domainName
	 * @param stationId
	 * @param applicationId
	 * @return
	 */
	public static MessageConsoleStream openLogConsole() {
		String domainName="Dynamic Model";
		String stationId="Informatiol";
		/*
		 * get console name and create a new console
		 */
		StringBuffer consoleName = new StringBuffer(domainName);
		if (stationId != null) {
			consoleName.append(">" + stationId);
		}

		MessageConsole console = new MessageConsole(consoleName.toString(),null);
		final MessageConsoleStream consoleStream = console.newMessageStream();
		/*
		 * show view
		 */
		IConsoleManager manager =  ConsolePlugin.getDefault().getConsoleManager();
		manager.addConsoles(new IConsole[] { console });
		manager.showConsoleView(console);
		return consoleStream;
	}
}
