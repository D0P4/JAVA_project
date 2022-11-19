import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class GameFrame extends JFrame{
	JButton target[] = new JButton[50];
	JLabel hi;
	boolean shot = false;
	static int i = 0;
	int targetX = 50, targetY = 50;
	
	ImageIcon image = new ImageIcon("images/eqypt.jpg");
	Image img = image.getImage();
	Image changeImg = img.getScaledInstance(100, 100,Image.SCALE_SMOOTH);
	ImageIcon targetIcon = new ImageIcon(changeImg);

	
	GameFrame() {
		setTitle("부기를 잡아라");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(new GamePanel());
		setSize(1280,720);
		setVisible(true);
		
		
		
	}
	class GamePanel extends JPanel{
		private TargetThread thread = new TargetThread(targetX, targetY);
		GamePanel(){
			setLayout(null);
			setBackground(Color.CYAN);
			thread.start();
		}
	}

	class TargetThread extends Thread{
		MyActionListener listener = new MyActionListener(); 
		TargetThread(int x, int y){
		}

		void printTarget(JButton target) {
			target = new JButton(targetIcon);
			target.setSize(100, 100);
			target.setLocation(targetX, targetY);
			add(target);
			target.addActionListener(listener);
			revalidate();
			repaint();
		}
		void getRandomLocation() {
			targetX = (int)(Math.random()*1170) + 50;
			targetY = (int)(Math.random()*670) + 50;
			
		}
		public void run() {
			while(true) {
				getRandomLocation();
				new TargetThread(targetX, targetY);
				printTarget(target[i]);
				
				try {
					Thread.sleep(500);
					i++;
				}
				catch(InterruptedException e){
					return;
					}
				if(i>=10) i=0;
				}
			}	
	}
	class MyActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton)e.getSource();
			if(btn.getIcon()==targetIcon) {
				btn.setVisible(false);
				btn.setEnabled(false);
				}
		}
	}
}
public class CatchBoogie {

	public static void main(String[] args) throws InterruptedException {
		new GameFrame();
		
	}
}

