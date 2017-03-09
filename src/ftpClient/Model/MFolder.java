package ftpClient.Model;

import java.io.File;
import java.util.ArrayList;

import ftpClient.utilities.ResourceLoader;

public class MFolder extends MListElement {

	private ArrayList<MListElement> elems;
	
	public MFolder(String name, String path, MFtpModel ftpModel) {
		super(name, path, ftpModel);
		elems = new ArrayList<>();
	}

	@Override
	protected void setIcon() {
		this.icon = ResourceLoader.getImageIcon("folder.png", 20, 20);
	}
	
	public ArrayList<MListElement> getElements(){
		if (elems.isEmpty()){
			//iz ftp-a izlistati dir
		}
		return elems;
	}

	@Override
	public void doAction() {
		ftpModel.cdCommand(this.name);
	}
	
	@Override
	public boolean delete(){
		return false;
	}
	
	public void uploadFile(File file){
		
	}
	
	public void createDir(String name){
		
	}

}
