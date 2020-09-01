package networkSpeed;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Main {
	
	static int i = 0;
	static String NetworkSpeed = 0 + " Ko/s";
	static JDialog fen = new JDialog();
	static int red = 0, green = 255, blue = 0;
	static int align = SwingConstants.LEFT;
	static int Flowalign = FlowLayout.LEFT;
	static String alignement = "Left";
	static int posHeight = 0;
	static int posWidth = 0;
	static int screenSize[] = {0,0};
	static String pingCmd ="";
	static BufferedReader in;

	public static void main(String[] args) {
		Locale local = Locale.getDefault();
		String lang = local.getDisplayLanguage();
		
		screenSize[0] = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		screenSize[1] = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		
		readPref();
		savePref();
		
		fen.setTitle("Network Speed");
		fen.setSize(screenSize[0], 50);
		fen.setUndecorated(true);
		fen.setBackground(new Color(0, 0, 0, 0));
		fen.setLocation(posWidth,posHeight);
		fen.setAlwaysOnTop(true);
		fen.setResizable(false);
		fen.setType( Window.Type.UTILITY);
		NotificationPopup();
		if(lang != "français" && lang != "English"){
			NetworkSpeed = "System language is not supported. Go to the System Tray to exit";
		}else {
		
			fen.setContentPane(contentPane());
			fen.setVisible(true);
			
			if(lang == "français") {
				pingCmd = "typeperf \"\\Interface réseau(*)\\Octets reçus/s\"";
			}
			if(lang == "English") {
				pingCmd = "typeperf \"\\Network Interface(*)\\Bytes received/sec\"";
			}
			GetSpeed();
		}
	}

	private static void GetSpeed() {
		//String pingCmd = "typeperf \"\\\\LAPTOP-FIFI\\Interface réseau(Qualcomm Atheros QCA61x4A Wireless Network Adapter)\\Octets reçus/s\"";
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);

            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                Display(inputLine);
            }
            in.close();

        } catch (IOException e) {
            System.out.println(e);
        }
	}

	private static void Display(String texte) {
		float speed = 0;
		if(texte.indexOf(",") != -1) {
			i++;
			texte = texte.substring(texte.indexOf(","));
			texte = texte.substring(1);
			String value[] = texte.split(",");
			if(i != 1) {
				for(int i = 0; i<= value.length-1; i++) {
					value[i] = value[i].substring(1);
					value[i] = value[i].substring(0,value[i].length()-1);
					speed += Float.parseFloat(value[i]);
				}
			}
			float KO = (float)speed/1024;
			
			KO = (float)Math.round(KO*100)/100;
			if( KO > 1024) {
				float MO = (float)KO/1024;
				MO = (float)Math.round(MO*100)/100;
				NetworkSpeed = MO + " Mo/s";
			}else {
				NetworkSpeed = KO + " Ko/s";
			}
			System.out.println(NetworkSpeed);
			fen.setContentPane(contentPane());
			fen.validate();
			fen.repaint();
		}
		
	}

	private static JPanel contentPane() {
		JPanel pane = new JPanel(new FlowLayout(Flowalign));
		pane.setOpaque(false);
		JLabel label = new JLabel(NetworkSpeed,align);
		label.setFont(new Font("Courier",Font.BOLD,20));
		label.setForeground(new Color(red, green, blue));
		pane.add(label);
		
		return pane;
	}
	
	private static void NotificationPopup() {
		if(!SystemTray.isSupported()) {
			System.out.println("Le systeme ne supporte pas SystemTray");
			System.exit(1);
		}
		PopupMenu popup = new PopupMenu();
		TrayIcon trayIcon = new TrayIcon(createImage("images/icon2.png","tray icon"));
		SystemTray tray = SystemTray.getSystemTray();
		
		Menu position = new Menu("Position");
		MenuItem leftalign = new MenuItem("Up Left");
		MenuItem centeralign = new MenuItem("Up Centered");
		MenuItem rightalign = new MenuItem("Up Right");
		position.add(leftalign);
		position.add(centeralign);
		position.add(rightalign);
		
		Menu colorMenu = new Menu("Color");
		MenuItem redm = new MenuItem("Red");
		MenuItem greenm = new MenuItem("Green");
		MenuItem bluem = new MenuItem("Blue");
		MenuItem perso = new MenuItem("Customize");
		colorMenu.add(redm);
		colorMenu.add(greenm);
		colorMenu.add(bluem);
		colorMenu.addSeparator();
		colorMenu.add(perso);
		
		MenuItem reload = new MenuItem("Reload");
		MenuItem exit = new MenuItem("Exit");
		
		popup.add(position);
		popup.add(colorMenu);
		popup.addSeparator();
		popup.add(reload);
		popup.add(exit);
		
		trayIcon.setPopupMenu(popup);
		trayIcon.setToolTip("Network Speed");
		
		try {
			tray.add(trayIcon);
		}catch (AWTException e) {
			System.out.println("nullllllle");
			System.exit(1);
		}
		
		trayIcon.setImageAutoSize(true);
		
		leftalign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				align("Left");
			}
		});
		
		centeralign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				align("Center");
			}
		});
		
		rightalign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				align("Right");
			}
		});
		
		redm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				red = 255; green = 0; blue = 0;
				savePref();
			}
		});
		
		greenm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				red = 0; green = 255; blue = 0;
				savePref();
			}
		});
		
		bluem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				red = 0; green = 0; blue = 255;
				savePref();
			}
		});
		
		perso.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color newcolor = JColorChooser.showDialog(new JFrame(), "Selection de couleur" , new Color(red, green, blue));
				red = newcolor.getRed(); green = newcolor.getGreen(); blue = newcolor.getBlue();
				savePref();
			}
		});
		
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tray.remove(trayIcon);
				try {
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		
		reload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String javaBin = System.getProperty("java.home")+File.separator+"bin"+File.separator+"java";
				File currentJar = null;
				try {
					currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.exit(0);
				}
				
				if(!currentJar.getName().endsWith(".jar")) {
					return;
				}
				
				ArrayList<String> command = new ArrayList<String>();
				command.add(javaBin);
				command.add("-jar");
				command.add(currentJar.getPath());
				
				ProcessBuilder builder = new ProcessBuilder(command);
				try {
					builder.start();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				tray.remove(trayIcon);
				try {
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
	}

	protected static void align(String Salignement) {
		if(Salignement.contains("Left")) {
			align = SwingConstants.LEFT;
			Flowalign = FlowLayout.LEFT;
		}else if (Salignement.contains("Center")) {
			align = SwingConstants.CENTER;
			Flowalign = FlowLayout.CENTER;
		}else if (Salignement.contains("Right")) {
			align = SwingConstants.RIGHT;
			Flowalign = FlowLayout.RIGHT;
		}else {
			System.out.println("Invalid position");
			System.exit(1);
		}
		alignement = Salignement;
		savePref();
	}

	private static Image createImage(String path, String description) {
		URL imageURL = Main.class.getResource(path);
		return(new ImageIcon(imageURL, description)).getImage();
	}
	
	private static void savePref() {
		try {
			PrintWriter writer = new PrintWriter("Config.txt");
			writer.println("#This config file is for the Network Speed app");
			writer.println("");
			writer.println("#Color (R, G and B take value beetween 0 and 255) :");
			writer.println("Red = " + red);
			writer.println("Green = " + green);
			writer.println("Blue = " + blue);
			writer.println("");
			writer.println("# Position (Left, Center or Right) :");
			writer.println("Position = " + alignement);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void readPref() {
		File file = new File("Config.txt");
		try {
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while((line = buffReader.readLine()) != null) {
				if(line.startsWith("Red = ")) {
					line = line.substring(6);
					red = Integer.parseInt(line);
				}
				if(line.startsWith("Green = ")) {
					line = line.substring(8);
					green = Integer.parseInt(line);
				}
				if(line.startsWith("Blue = ")) {
					line = line.substring(7);
					blue = Integer.parseInt(line);
				}
				if(line.startsWith("Position = ")) {
					line = line.substring(11);
					align(line);
				}
			}
			buffReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
}
