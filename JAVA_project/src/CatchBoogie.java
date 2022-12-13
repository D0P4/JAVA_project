import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop.Action;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

class GameFrame extends JFrame{
	JLabel target[] = new JLabel[100];
	JLabel xImage;
	boolean shot = false;
	static int i = 0;
	int targetX = 50, targetY = 50;
	static int targetXSize = 100;
	static int targetYSize = 100;
	static int gameScore = 0;
	int gameLife = 3;
	int delay = 1000;
	int time = 0;
	int targetCount = 0;
	static String playerName;
	
	static ImageIcon image = new ImageIcon("images/상상부기.png");
	static Image img = image.getImage();
	static Image changeImg = img.getScaledInstance(targetXSize, targetYSize,Image.SCALE_SMOOTH);
	static ImageIcon target1 = new ImageIcon(changeImg);
	
	static ImageIcon image2 = new ImageIcon("images/방패부기.png");
	static Image img2 = image2.getImage();
	static Image changeImg2 = img2.getScaledInstance(targetXSize, targetYSize,Image.SCALE_SMOOTH);
	static ImageIcon target2 = new ImageIcon(changeImg2);
	
	static ImageIcon image3 = new ImageIcon("images/cat.png");
	static Image img3 = image3.getImage();
	static Image changeImg3 = img3.getScaledInstance(targetXSize, targetYSize,Image.SCALE_SMOOTH);
	static ImageIcon target3 = new ImageIcon(changeImg3);
	
	static ImageIcon image4 = new ImageIcon("images/방패냥이.png");
	static Image img4 = image4.getImage();
	static Image changeImg4 = img4.getScaledInstance(targetXSize, targetYSize,Image.SCALE_SMOOTH);
	static ImageIcon target4 = new ImageIcon(changeImg4);
	
	static ImageIcon targetIcon = target1;
	static ImageIcon targetIcon2 = target2;
	
	ImageIcon Ximage = new ImageIcon("images/XImage.png");
	Image Ximg = Ximage.getImage();
	Image changeXImg = Ximg.getScaledInstance(30, 30,Image.SCALE_SMOOTH);
	ImageIcon XIcon = new ImageIcon(changeXImg);
	
	private GamePanel gamepanel = new GamePanel();
	private BottomPanel bottompanel = new BottomPanel();
	private TopPanel toppanel = new TopPanel();
	changePanelMainFrame cf;
	
	
	
	GameFrame(changePanelMainFrame win) {
		this.cf = win;
		
		setTitle("부기를 잡아라");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setBackground(Color.WHITE);
		setLayout(new BorderLayout());
		c.add(toppanel,BorderLayout.NORTH);
		c.add(bottompanel,BorderLayout.SOUTH);
		c.addMouseListener(new MyMouseListener());
		setSize(1280,720);
		setVisible(true);
		gameScore = 0;
		
		
	}
	static void selectIcon(int num) {
		if(num == 1) {
			targetIcon = target1;
			targetIcon2 = target2;
		}
		else {
			targetIcon = target3;
			
			targetIcon2 = target4;
		}
	}
	void addScorefile() throws IOException {
		String info = playerName + " | " + gameScore;
		FileWriter fout;
		try{
			fout = new FileWriter("player.txt",true);    //파일명과 같은 파일명이 존재할시 덧붙여쓸여부판단
        	fout.write(info);
        	fout.write("\n");
        	fout.close();        //저장 후 텍스트필드의 값을 가져온 자원들을 해제한다.
		}catch(Exception n){
            System.out.println(n);
        }
	}
	
	void changeEndPage() throws IOException {
		cf.setVisible(true);
		cf.change("rPanel");
		dispose();
	}
	class TopPanel extends JPanel{
		JLabel timerLabel = new JLabel();
		JLabel targetLabel = new JLabel();
		private TimerThread thread = new TimerThread(timerLabel);
		private targetCountThread TCThread = new targetCountThread(targetLabel);
		TopPanel(){
			setLayout(new BorderLayout());
			setBackground(Color.WHITE);
			setBorder(new LineBorder(Color.PINK, 3, true));
			timerLabel.setFont(new Font("Gothic", Font.BOLD, 30));
			add(timerLabel, BorderLayout.WEST);
			
			targetLabel.setFont(new Font("Gothic", Font.BOLD, 30));
			add(targetLabel, BorderLayout.EAST);
			thread.start();
			TCThread.start();
		}
	}
	class BottomPanel extends JPanel{
		JLabel scoreLabel = new JLabel();
		JLabel lifeLabel = new JLabel();
		private ScoreThread scoreThread = new ScoreThread(scoreLabel);
		private LifeThread lifeThread = new LifeThread(lifeLabel);;
		BottomPanel(){
			setLayout(new BorderLayout());
			setBackground(Color.WHITE);
			setBorder(new LineBorder(Color.PINK, 3, true));

			scoreLabel.setFont(new Font("Gothic", Font.BOLD, 30));
			add(scoreLabel,BorderLayout.WEST);
			
			lifeLabel.setFont(new Font("Gothic", Font.BOLD, 30));
			add(lifeLabel, BorderLayout.EAST);
			
			scoreThread.start();
			lifeThread.start();
		}
	}
	class GamePanel extends JPanel{
		JLabel gameLabel = new JLabel();
		private TargetThread thread = new TargetThread(gameLabel);
		GamePanel(){
			setName("game");
			setBackground(Color.CYAN);
			gameLabel.setFont(new Font("Gothic", Font.BOLD, 30));
			add(gameLabel,BorderLayout.WEST);
			thread.start();
		}
	}
	
	class MyMouseListener extends MouseAdapter{
		public void mousePressed(MouseEvent e) {
			try {
				Clip clip = AudioSystem.getClip();
				File audioFile = new File("sound/error.wav");
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
				clip.open(audioStream);
				clip.start();
			} catch (LineUnavailableException e1) {
				e1.printStackTrace();
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			xImage = new JLabel(XIcon);
			xImage.setSize(30,30);
			xImage.setLocation(e.getX(), e.getY());
			add(xImage);
			revalidate();
			repaint();
			gameLife--;	
		}
		
	}
	
	class TargetThread extends Thread{
		JLabel gameLabel;

		MyMouseListener listener = new MyMouseListener(); 
		TargetThread(JLabel gameLabel){
			this.gameLabel = gameLabel;
		}

		void printTarget(JLabel target) {
			target = new JLabel(targetIcon);
			setLayout(null);
			target.setSize(targetXSize, targetYSize);
			target.setLocation(targetX, targetY);
			add(target);
			target.addMouseListener(new MyMouseListener() {
				public void mousePressed(MouseEvent e) {
					JLabel la= (JLabel)e.getSource();
					if(la.getIcon()==targetIcon) {
						try {
							Clip clip = AudioSystem.getClip();
							File audioFile = new File("sound/hit.wav");
							AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
							clip.open(audioStream);
							clip.start();
						} catch (LineUnavailableException e1) {
							e1.printStackTrace();
						} catch (UnsupportedAudioFileException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						Container con = la.getParent();				
						con.remove(la);
						con.revalidate();
						con.repaint();
						gameScore+=100;
						targetCount--;
					}
				}
			});
			
			revalidate();
			repaint();
		}
		void printTarget2(JLabel target) {
			target = new JLabel(targetIcon2);
			setLayout(null);
			target.setSize(targetXSize, targetYSize);
			target.setLocation(targetX, targetY);
			add(target);
			target.addMouseListener(new MyMouseListener() {
				public void mousePressed(MouseEvent e) {
					JLabel la= (JLabel)e.getSource();
					if(la.getIcon()==targetIcon2) {
						if(e.getClickCount()==2) {
							try {
								Clip clip = AudioSystem.getClip();
								File audioFile = new File("sound/hit.wav");
								AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
								clip.open(audioStream);
								clip.start();
							} catch (LineUnavailableException e1) {
								e1.printStackTrace();
							} catch (UnsupportedAudioFileException e1) {
								e1.printStackTrace();
							} catch (IOException e1) {
		
								e1.printStackTrace();
							}
							Container con = la.getParent();				
							con.remove(la);
							con.revalidate();
							con.repaint();
							gameScore+=150;
							targetCount--;
							}
						}
					}
				});
			
			revalidate();
			repaint();
		}
		void getRandomLocation() {
			targetX = (int)(Math.random()*1000) + 80;
			targetY = (int)(Math.random()*550) + 50;
			
		}
		public void run() {
			while(true) {
				try {
					
					Thread.sleep(delay);
					getRandomLocation();
					if(time % 5 == 0)
						printTarget2(target[i]);
					else
						printTarget(target[i]);
					i++;
					targetCount++;
				}
				catch(InterruptedException e){
					return;
				}
				if(i>=100) i=0;
			}
		}	
	}
	class TimerThread extends Thread{
		JLabel timerLabel;
		TimerThread(JLabel timerLabel){
			this.timerLabel = timerLabel;

		}
		public void run() {
			while(true) {
				timerLabel.setText("     TIME : " + Integer.toString(time));
				time++;
				delay-=20;
				try {
					Thread.sleep(1000);
				}
				catch(InterruptedException e) {
					return;
				}
			}
		}
	}
	class ScoreThread extends Thread{
		JLabel scoreLabel;
		ScoreThread(JLabel scoreLabel){
			this.scoreLabel = scoreLabel;
		}
		public void run() {
			while(true) {
				scoreLabel.setText("     SCORE : " + Integer.toString(gameScore));
				try {
					Thread.sleep(100);
				}
				catch(InterruptedException e) {
					return;
				}
			}
		}
	}
	class targetCountThread extends Thread{
		JLabel targetCountLabel;
		targetCountThread(JLabel targetCountLabel){
			this.targetCountLabel = targetCountLabel;
		}
		public void run() {
			while(true) {
				targetCountLabel.setText("     target : " + Integer.toString(targetCount));
				if(targetCount >= 10) {
					try {
						addScorefile();
						changeEndPage();
						break;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(10);
				}
				catch(InterruptedException e) {
					return;
				}
			}
		}
	}
	class LifeThread extends Thread{
		JLabel lifeLabel;
		LifeThread(JLabel lifeLabel){
			this.lifeLabel = lifeLabel;
		}
		public void run() {
			while(true) {
				lifeLabel.setText("LIFE : " + Integer.toString(gameLife) + "     ");
				if(gameLife <= 0) {
					try {
						addScorefile();
						changeEndPage();
						break;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(100);
				}
				catch(InterruptedException e) {
					return;
				}
			}
		}
	}
}
class startPanel extends JPanel {

	private RoundedButton btn;
	private RoundedButton btn2;
	private JLabel textlabel;
	private JLabel img;

	private changePanelMainFrame win;

	public startPanel(changePanelMainFrame win) {
		this.win = win;
		Color color= new Color(232, 217, 244);
		Font font= new Font("Kim jung chul Script Bold", Font.ITALIC, 80);
		ImageIcon bugi = new ImageIcon(("images/상상부기.png"));
		setLayout(null);
		setBackground(color);
		
		textlabel=new JLabel("부기를 잡아라!");
		textlabel.setForeground(Color.WHITE);
		textlabel.setFont(font);
		textlabel.setSize(800,300);
		textlabel.setLocation(440, 0);
		add(textlabel);
		
		img= new JLabel(bugi);
		img.setIcon(bugi);
		img.setSize(300,300);
		img.setLocation(100, 300);
		add(img);
		
		img= new JLabel(bugi);
		img.setIcon(bugi);
		img.setSize(300,300);
		img.setLocation(900, 300);
		add(img);
		
		
		btn = new RoundedButton("게임시작!");
		btn.setSize(300, 40);
		btn.setLocation(500, 450);
		add(btn);
		
	
		btn2= new RoundedButton("캐릭터선택");
		btn2.setSize(300, 40);
		btn2.setLocation(500, 550);
		add(btn2);

		btn.addActionListener(new MyActionListener());
		btn2.addActionListener(new MyActionListener2());
	}



	
	class MyActionListener implements ActionListener { // 버튼 키 눌리면 패널 2번 호출
		@Override
		public void actionPerformed(ActionEvent e) {
			win.change("gPanel");
			//select.change("cPanel");
		}
	}
	
	class MyActionListener2 implements ActionListener { // 버튼 키 눌리면 패널 2번 호출
		@Override
		public void actionPerformed(ActionEvent e) {
			win.change("cPanel");
		}
	}

}

class characterPanel extends JPanel {
	changePanelMainFrame select = null;
	private JLabel img;
	private JLabel img2;
	
	characterPanel(changePanelMainFrame select) {
		this.select = select;
		setLayout(null);
		setBackground(Color.pink);
		ImageIcon bugi = new ImageIcon(("images/상상부기.png"));
		ImageIcon cat = new ImageIcon(("images/cat.png"));
		
		RoundedButton btn = new RoundedButton("상상부기");
		btn.setSize(300, 40);
		btn.setLocation(200, 450);
		add(btn);

		btn.addActionListener(new MyActionListener());
		
		RoundedButton btn2 = new RoundedButton("한냥이");
		btn2.setSize(300, 40);
		btn2.setLocation(800, 450);
		add(btn2);
		
		btn2.addActionListener(new MyActionListener());
		
		img= new JLabel(bugi);
		img.setIcon(bugi);
		img.setSize(300,300);
		img.setLocation(200, 100);
		add(img);
		
		img2= new JLabel(cat);
		img2.setIcon(cat);
		img2.setSize(300,300);
		img2.setLocation(800, 100);
		add(img2);
		
	}

	class MyActionListener implements ActionListener { // 버튼 키 눌리면 패널 1번 호출
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton b = (JButton)e.getSource();
			if(b.getText().equals("상상부기"))
				GameFrame.selectIcon(1);
			else if(b.getText().equals("한냥이"))
				GameFrame.selectIcon(0);
			select.change("sPanel");
		}
	}
}


class gamePanel extends JPanel {
	Scanner scanner = new Scanner(System.in);
	FileWriter fout = null;
	changePanelMainFrame win = null;
	private JScrollPane scrollPanel;
	private JTextField txtArea;
	private JLabel textlabel;
	private JLabel img;
	GameFrame gf;
	gamePanel(changePanelMainFrame win) {
		this.win = win;
		setLayout(null);
		Color color= new Color(255, 217, 244);
		Font font= new Font("Kim jung chul Script Bold", Font.ITALIC, 80);
		ImageIcon cat = new ImageIcon(("images/cat.png"));
		setBackground(color);
		
		textlabel=new JLabel("이름을 알려줘!");
		textlabel.setForeground(color.WHITE);
		textlabel.setFont(font);
		textlabel.setSize(800,150);
		textlabel.setLocation(100, 0);
		add(textlabel);
		
		img= new JLabel(cat);
		img.setIcon(cat);
		img.setSize(300,300);
		img.setLocation(100, 275);
		add(img);
		
		RoundedButton btn = new RoundedButton("게임시작!");
		btn.setSize(300, 40);
		btn.setLocation(800, 550);
		add(btn);

		txtArea = new JTextField();

		scrollPanel = new JScrollPane(txtArea);
		scrollPanel.setSize(300, 40);
		scrollPanel.setLocation(800, 475);

		add(scrollPanel);
		
		
		btn.addActionListener(new MyActionListener());
	}
	class MyActionListener implements ActionListener { // 버튼 키 눌리면 패널 1번 호출
		@Override
		public void actionPerformed(ActionEvent e) {
			gf = new GameFrame(win);
			gf.setVisible(true);
			win.dispose();
			GameFrame.playerName = txtArea.getText();
			
		}
	}
}

	class resultpanel extends JPanel { //저장 어떻게 하는지 모르겠어
		FileReader fin;
		changePanelMainFrame win = null;
		TextArea ta = new TextArea();
		private JLabel textlabel;
		private JLabel textlabel2;
		private JLabel img;

		resultpanel(changePanelMainFrame win) throws IOException {
			this.win = win;
			setLayout(null);
			Color color= new Color(255, 255, 244);
			Font font= new Font("Kim jung chul Script Bold", Font.ITALIC, 80);
			Font font2= new Font("Kim jung chul Script Bold", Font.PLAIN, 40);
			ImageIcon wink = new ImageIcon(("images/wink.png"));
			setBackground(color);
			
			textlabel=new JLabel("Game Over");
			textlabel.setForeground(color.darkGray);
			textlabel.setFont(font);
			textlabel.setSize(800,150);
			textlabel.setLocation(100, 0);
			add(textlabel);
			
			String info = GameFrame.playerName + " | " + GameFrame.gameScore;
			textlabel2=new JLabel(info);
			textlabel2.setForeground(color.darkGray);
			textlabel2.setFont(font2);
			textlabel2.setSize(300,80);
			textlabel2.setLocation(700,40);
			add(textlabel2);
			
			img= new JLabel(wink);
			img.setIcon(wink);
			img.setSize(300,300);
			img.setLocation(100, 275);
			add(img);
			
			RoundedButton btn = new RoundedButton("다시하기");
			btn.setSize(300, 40);
			btn.setLocation(800, 580);
			add(btn);

			String str;
            BufferedReader br = new BufferedReader(new FileReader("player.txt"));   // 불러올 파일이름
            while((str=br.readLine())!=null)
             ta.append(str+"\n"); 
            ta.setFont(font2);
            ta.setSize(300, 400);
    		ta.setLocation(700, 150);
    		add(ta);
    		br.close();

			btn.addActionListener(new MyActionListener());
		}
		

	class MyActionListener implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent e) {
			win.change("sPanel");
		}
	}
}
class changePanelMainFrame extends JFrame {
	public startPanel sPanel = null;
	public characterPanel cPanel = null;
	public gamePanel gPanel = null;
	public resultpanel rPanel = null;
	public GameFrame gameFrame = null;
	public changePanelMainFrame() throws IOException {
		this.setTitle("Panel Change");
		sPanel = new startPanel(this);
		cPanel = new characterPanel(this);
		gPanel = new gamePanel(this);
		

		add(this.sPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1280, 700);
		setVisible(true);
	}

	public void change(String panelName) { 
		if (panelName.equals("sPanel")) {
			getContentPane().removeAll();
			getContentPane().add(sPanel);
			revalidate();
			repaint();
		}
		else if(panelName.equals("cPanel")) {
			getContentPane().removeAll();
			getContentPane().add(cPanel);
			revalidate();
			repaint();
		}
		else if(panelName.equals("rPanel")) {
			try {
				rPanel = new resultpanel(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getContentPane().removeAll();
			getContentPane().add(rPanel);
			revalidate();
			repaint();
		}
		else {
			getContentPane().removeAll();
			getContentPane().add(gPanel);
			revalidate();
			repaint();
		}
	}
}
class RoundedButton extends JButton{
	 public RoundedButton() {
	        super();
	        decorate();
	    }

	    public RoundedButton(String text) {
	        super(text);
	        decorate();
	    }

	    public RoundedButton(Action action) {
	        super();
	        decorate();
	    }

	    public RoundedButton(Icon icon) {
	        super(icon);
	        decorate();
	    }

	    public RoundedButton(String text, Icon icon) {
	        super(text, icon);
	        decorate();
	    }

	    protected void decorate() {
	        setBorderPainted(false);
	        setOpaque(false);
	    }
	    @Override
	    protected void paintComponent(Graphics g) {
	        int width = getWidth();
	        int height = getHeight();

	        Graphics2D graphics = (Graphics2D) g;

	        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	        if (getModel().isArmed()) {
	            graphics.setColor(getBackground().darker());
	        } else if (getModel().isRollover()) {
	            graphics.setColor(getBackground().brighter());
	        } else {
	            graphics.setColor(getBackground());
	        }

	        graphics.fillRoundRect(0, 0, width, height, 10, 10);

	        FontMetrics fontMetrics = graphics.getFontMetrics();
	        Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds();

	        int textX = (width - stringBounds.width) / 2;
	        int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent();
	        
	        Font font= new Font("Kim jung chul Script Bold", Font.PLAIN, 20);
	        graphics.setColor(getForeground());
	        graphics.setFont(font);
	        graphics.drawString(getText(), textX, textY);
	        graphics.dispose();

	        super.paintComponent(g);
	    }
}

public class CatchBoogie {
	
	public static void main(String[] args) throws InterruptedException, IOException {
		new changePanelMainFrame();
		
	}
}

