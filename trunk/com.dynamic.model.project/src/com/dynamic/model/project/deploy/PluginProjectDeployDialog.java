package com.dynamic.model.project.deploy;

import java.util.ArrayList;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.dynamic.model.project.Constants;
import com.dynamic.model.project.ProjectActivator;
import com.dynamic.model.project.util.ProjectUtil;

public class PluginProjectDeployDialog extends TitleAreaDialog {
	
	public PluginProjectDeployDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	private StructuredSelection selection;
	private Button[] buttonGroup = null;

	private int selectIndex = 0;
	private ArrayList<IProject> clientList;
	/***
	 * ���캯��
	 * @param shell
	 * @param selection
	 */
	public PluginProjectDeployDialog(Shell shell, StructuredSelection selection) {
		super(shell);
		this.selection = selection;
	}

	public static GridData createCompositeGridData() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = 4;
		return gridData;
	}
	/***
	 * ������������
	 */
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		if ((this.clientList == null) || (this.clientList.size() == 0)) {
			setErrorMessage("��ǰ�����ռ�û��Dynamic Model��Ŀ�����ȴ���Dynamic Model��Ŀ");
			//getOKButton().setEnabled(false);
			getButton(OK).setEnabled(false);
		}

		return control;
	}
	/***
	 * ����Dialog��ʾ����
	 */
	protected Control createDialogArea(Composite parent) {
		setTitle("����ģ����Ŀ");
		setMessage("����ѡ��Dynamic Model��Ŀ��Ȼ����OK��ť", 0);

		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);

		createClientsTable(composite);

		return super.createDialogArea(parent);
	}
	/***
	 * Clients Table Data
	 * @param parent
	 */
	void createClientsTable(Composite parent) {
		Label NameLabel = new Label(parent, 0);
		NameLabel.setText("��ѡ��һ��Dynamic Web��Ŀ:");

		ScrolledComposite sc = new ScrolledComposite(parent, 2816);

		sc.setLayoutData(new GridData(500, 100));
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		Composite buttonComp = new Composite(sc, 0);
		buttonComp.setLayout(new GridLayout(1, true));
		buttonComp.setBackground(new Color(null, 255, 255, 255));

		this.clientList = getClientList();
		String selectName = ProjectActivator.getDefault().getPreferenceStore().getString(Constants.hostWeb);

		for (int i = 0; i < this.clientList.size(); ++i) {
			if (((IProject) this.clientList.get(i)).getName()
					.equals(selectName)) {
				this.selectIndex = i;
				break;
			}
		}

		this.buttonGroup = new Button[this.clientList.size()];
		for (int i = 0; i < this.buttonGroup.length; i++) {
			this.buttonGroup[i] = new Button(buttonComp, 16);
			this.buttonGroup[i].setText(((IProject) this.clientList.get(i))
					.getName());
			this.buttonGroup[i].setBackground(new Color(null, 255, 255, 255));
			if (i == this.selectIndex)
				this.buttonGroup[i].setSelection(true);
			else
				this.buttonGroup[i].setSelection(false);
		}
		sc.setContent(buttonComp);
		sc.setMinSize(buttonComp.computeSize(-1, -1));
	}

	public void setErrorMessage(String newErrorMessage) {
		super.setErrorMessage(newErrorMessage);
	}

	public void setMessage(String newMessage) {
		super.setMessage(newMessage);
	}

	@SuppressWarnings("deprecation")
	protected void okPressed() {
		getSelectedButton();
		//getCancelButton().setEnabled(false);
		getButton(CANCEL).setEnabled(false);
		//getOKButton().setEnabled(false);
		getButton(OK).setEnabled(false);
		//����clientName����
		ProjectActivator.getDefault().getPreferenceStore()
				.putValue(Constants.hostWeb,((IProject) this.clientList.get(this.selectIndex)).getName());
		//ProjectActivator.getDefault().savePluginPreferences();
		PluginProjectDeployEngine exportEngine = new PluginProjectDeployEngine(
				this.selection,
				(IProject) this.clientList.get(this.selectIndex));
		exportEngine.executeDeploy();
		super.okPressed();
		refreshSelectedClient();
	}
	/***
	 * ˢ��Selected Dynamic Web Model Project
	 */
	private void refreshSelectedClient() {
		try {
			((IProject) getClientList().get(this.selectIndex)).refreshLocal(2,
					null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	/***
	 * ���Dynamic Model Project
	 * @return
	 */
	private ArrayList<IProject> getClientList() {
		ArrayList clientsList = new ArrayList();
		IProject[] projectList = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		if (projectList != null) {
			for (int i = 0; i < projectList.length; ++i) {
				IProject p = projectList[i];
				if (ProjectUtil.isClientProject(p)) {
					clientsList.add(p);
				}
			}
		}
		return clientsList;
	}
	/***
	 * ���ѡ��ť
	 * @return
	 */
	private int getSelectedButton() {
		for (int i = 0; i < this.buttonGroup.length; ++i) {
			if (this.buttonGroup[i].getSelection()) {
				this.selectIndex = i;
				return i;
			}
		}
		return 0;
	}
}
