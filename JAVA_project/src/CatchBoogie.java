import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

class GameFrame extends JFrame{
	JButton target[] = new JButton[50];
	JLabel hi;
	boolean shot = false;
	static int i = 0;
	int targetX = 50, targetY = 50;
	int gameScore = 0;
	int gameLife = 3;
	String m;
	
	ImageIcon image = new ImageIcon("images/eqypt.jpg");
	Image img = image.getImage();
	Image changeImg = img.getScaledInstance(100, 100,Image.SCALE_SMOOTH);
	ImageIcon targetIcon = new ImageIcon(changeImg);
	private GamePanel gamepanel = new GamePanel();
	private BottomPanel bottompanel = new BottomPanel();
	private TopPanel toppanel = new TopPanel();
	GameFrame() {
		setTitle("부기를 잡아라");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.add(gamepanel);
		setLayout(new BorderLayout());
		c.add(toppanel,BorderLayout.NORTH);
		c.add(bottompanel,BorderLayout.SOUTH);
		c.addMouseListener(new MyMouseListener());
		setSize(1280,720);
		setVisible(true);
		
		
		
	}
	class TopPanel extends JPanel{
		JLabel timerLabel = new JLabel();
		private TimerThread thread = new TimerThread(timerLabel);
		TopPanel(){
			setLayout(new FlowLayout(FlowLayout.LEFT));
			setBackground(Color.WHITE);
			setBorder(new LineBorder(Color.PINK, 3, true));
			timerLabel.setFont(new Font("Gothic", Font.BOLD, 30));
			add(timerLabel);
			thread.start();
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
		
		private TargetThread thread = new TargetThread(targetX, targetY);
		GamePanel(){
			setName("game");
			setFont(new Font("Gothic", Font.ITALIC, 30));
			
			thread.start();
		}
	}

	class TargetThread extends Thread{
		MyActionListener listener = new MyActionListener(); 
		TargetThread(int x, int y){
		}

		void printTarget(JButton target) {
			setLayout(null);
			target = new JButton(targetIcon);
			target.setSize(100, 100);
			target.setLocation(targetX, targetY);
			add(target);
			target.addActionListener(listener);

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
					Thread.sleep(1000);
					getRandomLocation();
					new TargetThread(targetX, targetY);
					printTarget(target[i]);
					i++;
				}
				catch(InterruptedException e){
					return;
					}
				if(i>=50) i=0;
				}
			}	
	}
	class MyActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton)e.getSource();
			if(btn.getIcon()==targetIcon) {
				btn.setVisible(false);
				btn.setEnabled(false);
				gameScore+=100;
				}
		}
	}
	class MyMouseListener extends MouseAdapter{
		public void mouseClicked(MouseEvent e) {
			gameLife--;
		}
	}
	class TimerThread extends Thread{
		JLabel timerLabel;

		TimerThread(JLabel timerLabel){
			this.timerLabel = timerLabel;
		}
		public void run() {
			int n=0;
			while(true) {
				timerLabel.setText("     TIME : " + Integer.toString(n));
				n++;
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
				scoreLabel.setText(m+"     SCORE : " + Integer.toString(gameScore));
				try {
					Thread.sleep(100);
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
					lifeLabel.setText("실패!");
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
public class CatchBoogie {

	public static void main(String[] args) throws InterruptedException {
		new GameFrame();
		
	}
}

