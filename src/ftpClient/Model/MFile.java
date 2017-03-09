package ftpClient.Model;

import java.io.File;

import javax.swing.JFileChooser;

import ftpClient.utilities.ResourceLoader;

public class MFile extends MListElement {

	private int fileSize = 0;
	
	public MFile(String name, String path, MFtpModel ftpModel) {
		super(name, path, ftpModel);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setIcon() {
		this.icon = ResourceLoader.getImageIcon("file.png", 20, 20);		
	}

	
	
	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public void doAction() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");   
		 
		int userSelection = fileChooser.showSaveDialog(null);
		 
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File fileToSave = fileChooser.getSelectedFile();
		    ftpModel.getCommand(this,fileToSave);
		}
		//ftpModel.getCommand(commandparts);
	}

	@Override
	public boolean delete() {
		// TODO Auto-generated method stub
		return false;
	}

}
