package ftpClient.viewer;

import javax.swing.JList;

import ftpClient.Model.MFtpModel;
import ftpClient.controller.COpenFolder;
import ftpClient.utilities.NvigatorListCellRender;


public class VList extends JList {

	private MFtpModel model;
	
	public VList(MFtpModel ftpModel) {
		super(ftpModel);
		this.model = ftpModel;
		this.setModel(model);
		COpenFolder listener = new COpenFolder();
		this.addListSelectionListener(listener);
		this.addMouseListener(listener);
		this.setCellRenderer(new NvigatorListCellRender());
		this.addKeyListener(listener);
		this.addFocusListener(listener);
	}

	public MFtpModel getModel() {
		return model;
	}
	
	
	
}
