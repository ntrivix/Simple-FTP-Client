package ftpClient.Model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import ftpClient.utilities.DirParser;

public class MFtpModel extends AbstractListModel<MListElement> {

	static final int MAX_LEN = 255;

	private Socket socket = null;
	private Socket tSocket;
	private BufferedReader reader;
	// private String command;
	static final String emptystring = "";
	static final String[] commandlist = { "close", "get", "put", "cd", "dir" };

	private Path localSaveLoccation;

	private String curDir;
	private String rootDir = "/";

	private String serverAddr;
	private String username, password;
	private int port = 21;

	private boolean connected;
	private boolean loggedIn = false;

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public MFtpModel(String serverAddr, String username, String password, int port) {
		this.serverAddr = serverAddr;
		this.username = username;
		this.password = password;
		this.port = port;
		this.curDir = "/";
		// uradi konekciju
		if (this.connect()) {
			connected = true;
			login(username, password);
		} else {
			connected = false;
		}
	}

	public MFtpModel(String server, String username, String pass) {
		this(server, username, pass, 21);
	}

	public Path getLocalSaveLoccation() {
		return localSaveLoccation;
	}

	public void setLocalSaveLoccation(Path localSaveLoccation) {
		this.localSaveLoccation = localSaveLoccation;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public String getUsername() {
		return username;
	}

	public ArrayList<MListElement> getElements() {
		return elements;
	}

	public boolean login(String username, String pass) {
		if (!connected) {
			loggedIn = false;
			return false;
		}
		this.username = username;
		this.password = pass;
		String serverOut;
		BufferedReader cmdlReader = new BufferedReader(new InputStreamReader(System.in));

		try {
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
			printWriter.println("USER " + username);
			System.out.println("--> USER " + username);
			String response = reader.readLine();
			System.out.println("<-- " + response);
			if (response.startsWith("501")) {
				return false;
			}
			System.out.println("--> pass " + "*********");
			printWriter.println("pass " + password);
			serverOut = reader.readLine();
		
		
				while (socket.getInputStream().available() > 0) {
					reader.read();
					// System.out.print("-->"+(t.to));
				}
				socket.getInputStream().skip(socket.getInputStream().available());
			
			System.out.println("");

			if (serverOut != "null" && !serverOut.startsWith("530")) {
				System.out.println("<-- " + serverOut);
				loggedIn = true;
				this.dirCommand();
				return true;
			} else {
				loggedIn = false;
				System.out.println("<-- " + serverOut);
				System.out.println("--> Incorrect username. Connection will now close");
				closeConnection();
			}
		} catch (IOException e) {
			errorOutput(825, emptystring, emptystring, 0);
			try {
				socket.close();
			} catch (IOException ex) {
				errorOutput(825, emptystring, emptystring, 0);
			}
			;
		}
		return false;
	}

	// OUTPUT SWITCH FOR ERRORS
	private static void errorOutput(int errorcode, String filename, String hostname, int port) {
		String errorstatement;
		switch (errorcode) {
		case 800:
			errorstatement = "800 Invalid command.";
			break;
		case 801:
			errorstatement = "801 Incorrect number of arguments.";
			break;
		case 802:
			errorstatement = "802 Invalid argument.";
			break;
		case 803:
			errorstatement = "803 Supplied command not expected at this time.";
			break;
		case 810:
			errorstatement = "810 Access to local file " + hostname + " denied.";
			break;
		case 820:
			errorstatement = "820 Control connection to " + hostname + " on port " + port + " failed to open";
			break;
		case 825:
			errorstatement = "825 Control connection I/O error, closing control connection.";
			break;
		case 830:
			errorstatement = "830 Data transfer connection to " + hostname + " on port " + port + " failed to open.";
			break;
		case 835:
			errorstatement = "835 Data transfer connection I/O error, closing data connection.";
			break;
		case 898:
			errorstatement = "898 Input error while reading commands, terminating.";
			break;
		default:
			errorstatement = "899 Processing error. " + filename + ".";
			break;
		}
		System.err.println(errorstatement);
		return;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	// OPEN COMMAND
	private boolean connect() {
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(InetAddress.getByName(serverAddr), port), 30000);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("--> open " + serverAddr);
			System.out.println(reader.readLine());
			return true;
		} catch (IOException e) {
			socket = null;
			errorOutput(820, emptystring, serverAddr, port);
		} catch (IllegalArgumentException e) {
			errorOutput(802, emptystring, emptystring, 0);
		}
		return false;
	};

	public boolean isConnected() {
		return connected;
	}

	// CLOSE COMMAND
	public void closeConnection() {
		try {
			if (socket != null && socket.isConnected()) {
				PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
				printWriter.println("QUIT");
				System.out.println(reader.readLine());
				socket.close();
			} else {
				errorOutput(803, emptystring, emptystring, 0);
			}
			;
		} catch (IOException e) {
			errorOutput(820, emptystring, emptystring, 0);
			return;
		}
		connected = false;
	}

	// GET COMMAND
	public void getCommand(MFile file, File saveTo) {

		String filename = file.getName();
		int fileSize = file.getFileSize();
		String streamsize;
		ByteArrayInputStream tReader;
		String pasv;
		FileOutputStream fileOutput;
		int fileByte;
		String regex = "\\((\\d+)\\s\\bbytes\\b";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher;
		int counter = 0;
		int filesize = 0;
		int stringsize;
		String symbols = "____________________";
		String line;

		try {
			System.out.println("--> GET");
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
			printWriter.println("PASV");
			pasv = reader.readLine();

			System.out.println("<-- " + pasv);
			printWriter.println("TYPE I");

			System.out.println("<-- " + reader.readLine());

			createTSocket(pasv); // parse server input of format: "227
									// Entering Passive Mode
									// (h1,h2,h3,h4,p1,p2)"
			// and create new data transfer socket

			printWriter.println("RETR " + filename);
			streamsize = reader.readLine();
			String[] errorcode = streamsize.split(" ");
			if (errorcode[0].equals("550"))
				throw new FileNotFoundException();
			System.out.println("<-- " + streamsize);
			matcher = pattern.matcher(streamsize);

			fileOutput = new FileOutputStream(saveTo);

			// byte[] bytes = new byte[filesize];
			// tSocket.getInputStream().read(bytes, 0, fileSize);
			int downloaded = 0;
			while (true) { // read
							// files
				int av = tSocket.getInputStream().available();
				if (av == 0 && downloaded < fileSize)
					av = 1;
				byte[] bytes = new byte[av];
				tSocket.getInputStream().read(bytes, 0, av);
				downloaded += av;
				try {
					if (bytes.length == 0)
						break;
					for (int i = 0; i < av; i++) {
						if (bytes[i] == -1)
							break;
					}

				} catch (Exception ex) {

				}
				counter++;

				fileOutput.write(bytes);
			}
			// fileOutput.write(bytes);
			fileOutput.close();
			System.out.print("\n");
			tSocket.close();
			System.out.println("<-- " + reader.readLine());

		} catch (UnknownHostException e) {
			errorOutput(899, e.getMessage(), emptystring, 0);
			return;
		} catch (FileNotFoundException e) {
			errorOutput(899, e.getMessage(), emptystring, 0);
			return;
		} catch (IOException e) {
			errorOutput(835, e.getMessage(), emptystring, 0);
			return;
		}

	}

	// PUT COMMAND
	public void putCommand(File file) {
		String filename;
		String pasv;
		String line;
		
		
		ByteArrayOutputStream tWriter = new ByteArrayOutputStream();
		BufferedReader fileReader;
		BufferedReader fReader;
		ByteArrayInputStream byteReader;
		int fileByte;
		
			filename = file.getName();

			try {
				fileReader = new BufferedReader(new FileReader(file));
				String fileString;

				System.out.println("--> PUT");
				PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
				printWriter.println("PASV");
				pasv = reader.readLine();
				System.out.println("<-- " + pasv);
				createTSocket(pasv); // parse server input of format: "227
										// Entering Passive Mode
										// (h1,h2,h3,h4,p1,p2)"
				// and create new data transfer socket
				printWriter.println("TYPE I");
				System.out.println("<-- " + reader.readLine());
				printWriter.println("STOR " + filename);

				String serverOut = reader.readLine();
				System.out.println("<-- " + serverOut);

				FileInputStream fin = new FileInputStream(file);
				
				byte[] fileBytes = new byte[(int)file.length()];
				fin.read(fileBytes);
				 
				tWriter.write(fileBytes, 0, fileBytes.length); // store
																// fileBytes in
																// buffer
				tWriter.writeTo(tSocket.getOutputStream()); // write buffered
															// bytes to socket
															// output stream

				tWriter.close();
				tSocket.close();

				String[] outputSplit = serverOut.split(" ");

				if (outputSplit[0].equals("150")) {
					System.out.println("<-- " + reader.readLine());
				}
				this.dirCommand();
			} catch (UnknownHostException e) {
				errorOutput(899, e.getMessage(), emptystring, 0);
				return;
			} catch (FileNotFoundException e) {
				errorOutput(899, e.getMessage(), emptystring, 0);
				return;
			} catch (IOException e) {
				errorOutput(835, e.getMessage(), emptystring, 0);
				return;
			}
		
	}

	// CD COMMAND
	public void cdCommand(String absPath) {
		if (!connected)
			return;
		String foldername;
		BufferedReader tReader;

		foldername = absPath;
		try {
			System.out.println("--> CWD " + absPath);
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
			printWriter.println("CWD " + foldername);
			System.out.println("<-- " + reader.readLine());
			/*
			 * try { //URI u = new URI(curDir + absPath); //this.curDir =
			 * u.getPath(); } catch (URISyntaxException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
			dirCommand();
		} catch (IOException e) {
			errorOutput(899, e.getMessage(), emptystring, 0);
			return;
		}

	}

	private ArrayList<MListElement> elements = new ArrayList<>();

	// DIR COMMAND
	private ArrayList<MListElement> dirCommand() {
		if (!connected)
			return null;
		String pasv;
		BufferedReader tReader;
		String linereader;
		
		try {
			socket.getInputStream().skip(socket.getInputStream().available());
			System.out.println("--> dir");
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
			printWriter.println("PASV");
			pasv = reader.readLine();
			if (pasv.contains("530"))
				throw new IOException();
			System.out.println("<-- " + pasv);
			createTSocket(pasv); // parse server input of format: "227 Entering
									// Passive Mode (h1,h2,h3,h4,p1,p2)""
			tReader = new BufferedReader(new InputStreamReader(tSocket.getInputStream()));
			printWriter.println("LIST");
			System.out.println("<-- " + reader.readLine());
			ArrayList<MListElement> newElements = new ArrayList<>();
			while ((linereader = tReader.readLine()) != null) {
				System.out.println("<-- " + linereader);
				String regex = "^" + // # Start of line
						"(?<dir>[\\-ld])" + // # File size
						"(?<permission>[\\-rwx]{9})" + // # Whitespace
						"\\s+" + // # Whitespace \n
						"(?<filecode>\\d+)" + "\\s+" + // # Whitespace \n
						"(?<owner>\\w+)" + "\\s+" + // # Whitespace \n
						"(?<group>\\w+)" + "\\s+" + // # Whitespace \n
						"(?<size>\\d+)" + "\\s+" + // # Whitespace \n
						"(?<month>\\w{3})" + // # Month (3 letters) \n
						"\\s+" + // # Whitespace \n
						"(?<day>\\d{1,2})" + // # Day (1 or 2 digits) \n
						"\\s+" + // # Whitespace \n
						"(?<timeyear>[\\d:]{4,5})" + // # Time or year
						"\\s+" + // # Whitespace \n
						"(?<filename>(.*))" + // # Filename \n
						"$"; // # End of line

				Pattern sizeAndNamePattern = Pattern.compile("^-.*?(\\d+) \\w{3} \\d{2} \\d{2}:\\d{2} (.*)$",
						Pattern.MULTILINE);

				Matcher matcher = sizeAndNamePattern.matcher(linereader);
				matcher.find();

				try {
					System.out.println(matcher.group(1) + " " + matcher.group(2));
				} catch (Exception e) {
					try {
						ArrayList<String> s = DirParser.parse(linereader);
						// System.out.println(s);
						MListElement t;
						if (s.get(2).equals("<DIR>")) {
							t = new MFolder(s.get(3), this.curDir, this);
						} else {
							t = new MFile(s.get(3), this.curDir, this);
							try {
								((MFile) t).setFileSize(Integer.parseInt(s.get(2)));
							} catch (Exception longparse) {

							}
						}
						newElements.add(t);
					} catch (Exception ee) {
						System.err.println("GRESKA");

					}

				}

			}
			System.out.println("<-- " + reader.readLine());
			if (tSocket != null)
				tSocket.close();
			this.elements = newElements;
		} catch (IOException e) {
			errorOutput(803, emptystring, emptystring, 0);
		}

		return this.elements;
	}

	private void createTSocket(String pasv) {

		String[] parts;
		String hostName;
		int port;
		int p1;
		int p2;

		ByteArrayOutputStream tWriter = null;

		parts = pasv.split(" ");

		if (parts[0].equals("227")) {
			parts[4] = parts[4].replaceAll("[()]", "").replace(".", ""); // remove
																			// "(",
																			// ")",
																			// and
																			// "."
			parts = parts[4].split(","); // split into address and port
											// components
			hostName = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
			p1 = Integer.parseInt(parts[4]);
			p2 = Integer.parseInt(parts[5]);
			port = (p1 * 256) + p2;
			try {
				tSocket = new Socket();
				tSocket.connect(new InetSocketAddress(InetAddress.getByName(hostName), port), 30000);
			} catch (UnknownHostException e) {
				errorOutput(899, e.getMessage(), emptystring, 0);
				return;
			} catch (IOException e) {
				errorOutput(835, e.getMessage(), hostName, port);
				tSocket = null;
				return;

			}
		}
	}

	private ArrayList<ListDataListener> observers = new ArrayList<>();

	@Override
	public void addListDataListener(ListDataListener arg0) {
		observers.add(arg0);

	}

	@Override
	public MListElement getElementAt(int arg0) {
		if (arg0 == 0) {
			return new MFolder("../", "../", this);
		}
		return elements.get(arg0 - 1);
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return elements.size() + 1;
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		observers.remove(l);

	}
}
