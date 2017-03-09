package ftpClient.utilities;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ftpClient.Model.MListElement;

public class NvigatorListCellRender implements ListCellRenderer<MListElement> {
	
	private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends MListElement> list, MListElement value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
		        isSelected, cellHasFocus);
		if (value==null) return renderer;
			renderer.setIcon(value.getIcon());
		
		return renderer;
	}
	
}
