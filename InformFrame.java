
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class InformFrame extends JFrame implements Observer{

	private JLabel musicName = null;
	private int frameWidth, frameHeight;
	private FrameUpdate updateManager;
	private MusicPlayer player;
	private WindowHandler winHandler;
	private JPanel primary;
	private JProgressBar progress;
	private int progressWidth;
	private int progressMaxValue = 100;
	private Font font;
	private MouseHandler mouseHandler;
	
	public InformFrame(int width, int height, FrameUpdate u) {
		this.updateManager = u;
		winHandler = new WindowHandler();
		progressWidth= 250;
		font = new Font("맑은 고딕",Font.PLAIN,12);
		player = MusicPlayer.getMusicPlayer();
		updateManager.register(this);
		frameWidth = width;
		frameHeight = height;

		primary = new JPanel();
		primary.setLayout(null);
		primary.setSize(frameWidth, frameHeight);
		
		musicName = new JLabel("Music name");
		musicName.setLocation(20, 10);
		musicName.setSize(frameWidth-50,30);
		primary.add(musicName);
		
		progress = new JProgressBar(0,progressMaxValue);
		progress.setLocation(20,40);
		progress.setSize(progressWidth, 4);
		progress.setBorderPainted(false);
		progress.setForeground(new Color(153, 151, 152));
		progress.setBackground(new Color(213, 211, 212));
		progress.setValue(0);
		mouseHandler = new MouseHandler();
		progress.addMouseListener(mouseHandler);
		progress.addMouseMotionListener(mouseHandler);
		primary.add(progress);
		player.addProgressBar(progress);
		addWindowListener(winHandler);
		getContentPane().add(primary);
	}

	class MouseHandler implements MouseListener, MouseMotionListener {
		int xPos, yPos;
		
		public void mousePressed(MouseEvent e) {
			// 노래가 시작 중이지 않으면 프로그래스바가 움직이면 안됨.
			if(player.getState() != MusicState.NOSTART ) {
				progress.setValue((int) (e.getX()/((float)progressWidth / progressMaxValue)));
				player.setDrawProgressValue(false);
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
			// 노래가 시작 중이지 않으면 프로그래스바가 움직이면 안됨.
			if(player.getState() != MusicState.NOSTART) {
				float x;
				
				if(e.getX()/((float)progressWidth / progressMaxValue) > progressMaxValue) x = progressMaxValue;
				else if(e.getX()/ ((float)progressWidth / progressMaxValue) < 0) x = 0;
				else x = e.getX() / ((float)progressWidth / progressMaxValue);
				
				player.skip(x / progressMaxValue); // Normalize
				player.setDrawProgressValue(true);
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
			@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			if(player.getState() != MusicState.NOSTART) {
				progress.setValue((int) (e.getX()/((float)progressWidth / progressMaxValue)));
				
			}
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	public void updateFrameState(int state) {
		this.setState(state);
	}	
	public void updateFrameComponents() {
		musicName.setFont(font);
		String name = player.getMusicList().get(player.getCurPlayIdx()).toString();
		name = name.substring(0, name.lastIndexOf('.'));
		musicName.setText(name);
		
	}
	public void updateFrameVisible(int v) {
		if(v == FrameVisible.LIST_FRAME_VISIBLE)
			setVisible(true);
		else if(v == FrameVisible.LIST_FRAME_INVISIBLE)
			setVisible(false);
	}
	
	private class WindowHandler extends WindowAdapter {

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			//super.windowDeiconified(e);
			updateManager.setFrameState(JFrame.NORMAL);
		}
	}
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		g.setColor(new Color(200,200, 200));
		g.drawRoundRect(0, 0, frameWidth-1, frameHeight-1, 24, 24);
	}
}
