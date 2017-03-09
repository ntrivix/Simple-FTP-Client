package ftpClient.viewer;

import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import ftpClient.Model.MFtpModel;

public class AppFrame extends JFrame {

	private JTextField server, port, user;
	private JPasswordField pass;
	private JButton submit;

	public AppFrame() throws HeadlessException {
		super("FTP Client");
		// new FTPConnFrame();

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(300, 200);
		this.setLocationRelativeTo(null);

		this.setLayout(new GridLayout(5, 2));

		this.add(new JLabel("Server"));
		server = new JTextField();
		this.add(server);
		port = new JTextField("21");
		this.add(new JLabel("Port"));
		this.add(port);
		user = new JTextField();
		this.add(new JLabel("User"));
		this.add(user);
		pass = new JPasswordField();
		this.add(new JLabel("Password"));
		this.add(pass);

		submit = new JButton("Connect");
		this.add(submit);
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					MFtpModel model = new MFtpModel(server.getText(), user.getText(), pass.getText(),
							Integer.parseInt(port.getText()));
					if (model.isConnected() && model.isLoggedIn()) {
						new FTPConnFrame(model);
					} else {
						//greska pri konekciji
					}
				} catch (Exception ee) {

				}
			}
		});

		this.setVisible(true);
	}

}
