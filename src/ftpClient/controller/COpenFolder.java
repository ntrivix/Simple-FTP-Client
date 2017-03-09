package ftpClient.controller;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ftpClient.Model.MFile;
import ftpClient.Model.MFolder;
import ftpClient.Model.MFtpModel;
import ftpClient.Model.MListElement;
import ftpClient.viewer.VList;

public class COpenFolder extends MouseAdapter implements ListSelectionListener, KeyListener, FocusListener {

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		if (e.getClickCount() == 2) {
			VList vlist = (VList) e.getSource();
			MFtpModel ftpmodel = vlist.getModel();
			try {
				MListElement mle = (MListElement) vlist.getSelectedValue();
				mle.doAction();
				vlist.updateUI();
			} catch (Exception ee){
				
			}
		}
	}

}
