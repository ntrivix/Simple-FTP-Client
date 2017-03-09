package ftpClient.Model;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

import ftpClient.utilities.ResourceLoader;

public abstract class MListElement {
	protected String name;
	protected ImageIcon icon;// = ResourceLoader.getImageIcon("icon/file.png", 20,20);
	protected String path;
	protected MFtpModel ftpModel;
	
	public MListElement(String name, String path, MFtpModel ftpModel) {
		super();
		this.name = name;
		this.path = path;
		this.ftpModel = ftpModel;
		setIcon();
	}

	protected abstract void setIcon();
	
	public ImageIcon getIcon() {
		return icon;
	}

	public MFtpModel getFtpModel() {
		return ftpModel;
	}

	public abstract void doAction(); //on click do

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}
	
	
	public abstract boolean delete();

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}
	
	
	
}
