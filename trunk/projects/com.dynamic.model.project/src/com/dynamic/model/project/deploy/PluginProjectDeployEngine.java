package com.dynamic.model.project.deploy;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.core.bundle.BundlePluginModelBase;
import org.eclipse.pde.internal.core.exports.FeatureExportInfo;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.pde.internal.ui.build.PluginExportJob;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.IProgressConstants;

import com.dynamic.model.project.Constants;
import com.dynamic.model.project.parser.BuilderProperty;

@SuppressWarnings("restriction")
public class PluginProjectDeployEngine {
	private StructuredSelection selection;
	private IProject client;
	private ArrayList<BundlePluginModelBase> pluginModelList = new ArrayList();

	private boolean totalExportFlag = false;
	private boolean totalNoExportFlag = false;

	
	/***
	 * 
	 * @param selection dynamic plugin project
	 * @param client dynamic web project
	 */
	public PluginProjectDeployEngine(StructuredSelection selection,
			IProject client) {
		this.selection = selection;
		this.client = client;
	}

	private ArrayList<BundlePluginModelBase> getModleList() {
		for (Iterator it = this.selection.iterator(); it.hasNext();) {
			IProject project = (IProject) it.next();
			IPluginModelBase model = PluginRegistry.findModel(project);
			if (model instanceof BundlePluginModelBase) {
				BundlePluginModelBase pluginModel = (BundlePluginModelBase) model;
				this.pluginModelList.add(pluginModel);
			}
		}
		return this.pluginModelList;
	}

	private String getDestinationDirectory() {
		if (this.client == null){
			return null;
		}
		//创建部署目录
		String launchPath=BuilderProperty.getLaunchPath();
		if(launchPath.endsWith(Constants.JAR_FILE_EXTENSION_NAME)){
			launchPath=launchPath.substring(0,launchPath.lastIndexOf("/"));
		}
		String[] paths =launchPath.substring(0,launchPath.lastIndexOf("/")).split("/");
		String path = getWebContent();
		for (int i = 0; i < paths.length; ++i) {
			if(paths[i].equals("")){
				continue;
			}
			path = path + paths[i] + "/";
			if (this.client.getFolder(path).exists())
				continue;
			try {
				this.client.getFolder(path).create(true, true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		//返回目录
		return this.client.getLocation().makeAbsolute().toOSString() +launchPath.replace('/', '\\');
	}
	/****
	 * web上下文
	 * @param module
	 * @return eg：dynamic.web
	 */
	public String getWebContent() {
		IFile componentFile = this.client.getFile(
				".settings/org.eclipse.wst.common.component");
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(componentFile.getContents());
			Element projecModules = document.getRootElement();
			List<Element> webModules = projecModules.elements("wb-resource");
			for (Element element : webModules) {
				Attribute att = element.attribute("deploy-path");
				if (att.getValue().equals("/")){
					return element.attribute("source-path").getValue().substring(1);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return "WebContent";
	}
	
	private void executeDeploy(BundlePluginModelBase plugin) {
		int flag=0;
		PluginExportJob job;
		FeatureExportInfo info = new FeatureExportInfo();
		info.toDirectory = true;
		info.useJarFormat = true;
		info.exportSource = false;
		info.destinationDirectory = getDestinationDirectory();
		info.zipFileName = null;
		info.items = new Object[] { plugin };
		info.signingInfo = null;
		info.qualifier = null;
		if ((info.items == null) || (info.items.length == 0)) {
			return;
		}
		//导出标识
		if (this.totalNoExportFlag){
			return;
		}
		//包含历史
		if (hasHistoryVersion(plugin)) {
			if (!(this.totalExportFlag)) {
				MessageDialog dialog = new MessageDialog(new Shell(), "确认文件替换",
						null, "该操作将删除SoTower项目 "
								+ this.client.getName()
								+ " 的模块项目 "
								+ plugin.getBundleDescription()
										.getSymbolicName() + " 的旧版本，是否继续？", 3,
						new String[] { "是", "否", "全部是", "全部否" }, 0);
				flag = dialog.open();
			}
		}
		switch (flag) {
		case 0:
			deleteHistoryVersion(plugin);
			job = new PluginExportJob(info);
			job.setUser(true);
			job.schedule();
			job.setProperty(IProgressConstants.ICON_PROPERTY,
					PDEPluginImages.DESC_PLUGIN_OBJ);
			break;
		case 1:
			break;
		case 2:
			this.totalExportFlag = true;
			deleteHistoryVersion(plugin);
			job = new PluginExportJob(info);
			job.setUser(true);
			job.schedule();
			job.setProperty(IProgressConstants.ICON_PROPERTY,
					PDEPluginImages.DESC_PLUGIN_OBJ);
			break;
		case 3:
			this.totalNoExportFlag = true;
		default:
			deleteHistoryVersion(plugin);
			job = new PluginExportJob(info);
			job.setUser(true);
			job.schedule();
			job.setProperty(IProgressConstants.ICON_PROPERTY,
					PDEPluginImages.DESC_PLUGIN_OBJ);
			job = new PluginExportJob(info);
			job.setUser(true);
			job.schedule();
			job.setProperty(IProgressConstants.ICON_PROPERTY,
					PDEPluginImages.DESC_PLUGIN_OBJ);
		}
	}

	private boolean deleteHistoryVersion(BundlePluginModelBase plugin) {
		String bundelPath=BuilderProperty.getLaunchPath();
		String folderPath = this.client.getLocation().makeAbsolute()
				.toOSString()+ bundelPath.substring(0, bundelPath.lastIndexOf("/"));
		File folder = new File(folderPath);
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; ++i) {
				String fileName = files[i].getName();
				String bundleName = plugin.getBundleDescription()
						.getSymbolicName();
				if (isSameBundle(fileName, bundleName)) {
					files[i].delete();
				}
			}
		}
		return true;
	}
	/***
	 * 判断相似资源
	 * @param fileName
	 * @param bundleName
	 * @return
	 */
	private boolean isSameBundle(String fileName, String bundleName) {
		String[] fileParts = fileName.split("_");
		String[] bundleParts = bundleName.split("_");
		if (fileParts.length > bundleName.length() + 1)
			return false;
		for (int i = 0; i < bundleParts.length; ++i) {
			if (!(bundleParts[i].equals(fileParts[i])))
				return false;
		}
		return true;
	}
	/***
	 * 历史版面
	 * @param plugin
	 * @return
	 */
	private boolean hasHistoryVersion(BundlePluginModelBase plugin) {
		String bundelPath=BuilderProperty.getLaunchPath();
		String folderPath = this.client.getLocation().makeAbsolute()
				.toOSString()
				+ bundelPath.substring(0, bundelPath.lastIndexOf("/"));;
		File folder = new File(folderPath);
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; ++i) {
				String fileName = files[i].getName();
				String bundleName = plugin.getBundleDescription()
						.getSymbolicName();
				if (isSameBundle(fileName, bundleName)) {
					return true;
				}
			}
		}
		return false;
	}

	public void executeDeploy() {
		ArrayList<BundlePluginModelBase> modelList = getModleList();
		for (BundlePluginModelBase model : modelList){
			executeDeploy(model);
		}
	}
}
