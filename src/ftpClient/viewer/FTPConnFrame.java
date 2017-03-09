package ftpClient.viewer;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import ftpClient.Model.MFtpModel;

public class FTPConnFrame extends JFrame {

	private VList vlist;
	private MFtpModel ftpModel;

	public FTPConnFrame(MFtpModel model) throws HeadlessException {
		super("FTP Client");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		JScrollPane consoleOut = getConsoleOut();
		ftpModel = model;
		vlist = new VList(ftpModel);
		// vlist.add(new MFile("afs", "sfdfsa"));
		JScrollPane listScroll = new JScrollPane(vlist);

		
		JButton upload = new JButton("Upload");
		upload.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Specify a file to save");   
				 
				int userSelection = fileChooser.showOpenDialog(null);
				 
				if (userSelection == JFileChooser.APPROVE_OPTION) {
				    File fileToSave = fileChooser.getSelectedFile();
				    ftpModel.putCommand(fileToSave);
				}
				
			}
		});
		JSplitPane vertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScroll, upload);
		vertical.setResizeWeight(0.7);

		this.add(vertical);
		this.setVisible(true);
	}

	private JScrollPane getConsoleOut() {
		JTextArea textArea = new JTextArea(50, 5);
		PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));

		//System.setOut(printStream);
		//System.setErr(printStream);

		return new JScrollPane(textArea);
	}

	private class CustomOutputStream extends OutputStream {
		private JTextArea textArea;

		public CustomOutputStream(JTextArea textArea) {
			this.textArea = textArea;
		}

		@Override
		public void write(byte[] buffer, int offset, int length) throws IOException {
			final String text = new String(buffer, offset, length);

			textArea.append(text);

		}

		@Override
		public void write(int b) throws IOException {
			write(new byte[] { (byte) b }, 0, 1);
		}

	}

}
