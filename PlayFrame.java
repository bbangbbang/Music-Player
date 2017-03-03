
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PlayFrame extends JFrame implements Observer {

	private int frameWidth, frameHeight;
	private JButton playBtn, stopBtn, nextBtn, prevBtn, pauseBtn;
	private JButton repeatBtn, listFrameVisibleBtn, informFrameVisibleBtn;
	private JButton closeBtn, miniBtn;
	private JPanel primary;

	private int musicBtn_width, musicBtn_height;
	private int sysBtn_width, sysBtn_height;
	private int proper_width, proper_height;
	private ButtonHandler btnHandler;
	private FrameUpdate updateManager;
	private WindowHandler winHandler;
	private MusicPlayer player;
	private int repeatType = RepeatType.NONE;
	private int listFrameVisibleState = FrameVisible.LIST_FRAME_VISIBLE;
	private int informFrameVisibleState = FrameVisible.INFORM_FRAME_VISIBLE;
	private String[] repeatStr = new String[]{"x","1","A"};

	public PlayFrame(int width, int height, FrameUpdate u) {
		this.updateManager = u;
		player = MusicPlayer.getMusicPlayer();
		updateManager.register(this);
		player.setRepeatType(repeatType);

		btnHandler = new ButtonHandler();
		winHandler = new WindowHandler();

		frameWidth = width;
		frameHeight = height;

		primary = new JPanel();
		primary.setLayout(null);
		primary.setSize(frameWidth, frameHeight);

		initSystemButtons();
		initMusicButtons();
		initProperBtn();
		getContentPane().add(primary);
		addWindowListener(winHandler);

	}
	


	private class WindowHandler extends WindowAdapter {

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			// super.windowDeiconified(e);
			updateManager.setFrameState(JFrame.NORMAL);

		}
	}
	public void initProperBtn() {
		proper_width = 20;
		proper_height = 15;
		int repeat_x = frameWidth - 42, repeat_y = frameHeight-20;
		
		repeatBtn = new JButton();
		
		repeatBtn.setSize(proper_width, proper_height);
		repeatBtn.setLocation(repeat_x, repeat_y);
		repeatBtn.addActionListener(btnHandler);
		repeatBtn.setMargin(new Insets(0, 0, 0, 0));
		repeatBtn.setFont(new Font("Dialog",Font.PLAIN,10));
		repeatBtn.setText("x");
		primary.add(repeatBtn);

		listFrameVisibleBtn = new JButton();
		listFrameVisibleBtn.setSize(proper_width, proper_height);
		listFrameVisibleBtn.setLocation(repeat_x - 25, repeat_y);
		listFrameVisibleBtn.addActionListener(btnHandler);
		primary.add(listFrameVisibleBtn);
		
		
		informFrameVisibleBtn = new JButton();
		informFrameVisibleBtn.setSize(proper_width, proper_height);
		informFrameVisibleBtn.setLocation(repeat_x - 50, repeat_y);
		informFrameVisibleBtn.addActionListener(btnHandler);
		primary.add(informFrameVisibleBtn);
		
		
	}
	public void updateFrameState(int state) {
		this.setState(state);
	}
	public void updateFrameComponents() {
		if(player.getState() == MusicState.FINISH) {

			playBtn.setVisible(true);
			pauseBtn.setVisible(false);
		}
		
		else{
			playBtn.setVisible(false);
			pauseBtn.setVisible(true);
		}
	}

	public void updateFrameVisible(int v) {}
	private void initSystemButtons() {
		sysBtn_width = sysBtn_height = 12;

		closeBtn = new JButton("+");
		closeBtn.setSize(sysBtn_width, sysBtn_height);
		closeBtn.setLocation(frameWidth - 25, 5);
		closeBtn.addActionListener(btnHandler);	
		closeBtn.setMargin(new Insets(0, 0, 1, 0));
		closeBtn.setFont(new Font("Dialog",Font.PLAIN,10));
		closeBtn.setText("x");
		primary.add(closeBtn);

		miniBtn = new JButton();
		miniBtn.setSize(sysBtn_width, sysBtn_height);
		miniBtn.setLocation(frameWidth - sysBtn_width - 30, 5);
		miniBtn.addActionListener(btnHandler);
		miniBtn.setMargin(new Insets(0, 0, 5, 0));
		miniBtn.setFont(new Font("Dialog",Font.PLAIN,10));
		miniBtn.setText("_");
		primary.add(miniBtn);

	}

	private void initMusicButtons() {
		musicBtn_width = musicBtn_height = 40;

		int play_xpos = frameWidth / 2 - musicBtn_width / 2 - 27;
		int play_ypos = frameHeight / 2 - musicBtn_height / 2;

		playBtn = new JButton();
		playBtn.setSize(musicBtn_width, musicBtn_height);
		playBtn.setIcon(new ImageIcon("Images/playBtn.jpg"));
		playBtn.setBorderPainted(false);
		playBtn.setLocation(play_xpos, play_ypos);
		playBtn.addActionListener(btnHandler);
		primary.add(playBtn);

		pauseBtn = new JButton();
		pauseBtn.setVisible(false);
		pauseBtn.setSize(musicBtn_width, musicBtn_height);
		pauseBtn.setIcon(new ImageIcon("Images/pauseBtn.jpg"));
		pauseBtn.setBorderPainted(false);
		pauseBtn.setLocation(play_xpos, play_ypos);
		pauseBtn.addActionListener(btnHandler);
		primary.add(pauseBtn);

		stopBtn = new JButton();
		stopBtn.setSize(musicBtn_width, musicBtn_height);
		stopBtn.setIcon(new ImageIcon("Images/stopBtn.jpg"));
		stopBtn.setBorderPainted(false);
		stopBtn.setLocation(play_xpos + musicBtn_height + 10, play_ypos);
		stopBtn.addActionListener(btnHandler);
		primary.add(stopBtn);

		nextBtn = new JButton();
		nextBtn.setSize(musicBtn_width, musicBtn_height);
		nextBtn.setIcon(new ImageIcon("Images/nextBtn.jpg"));
		nextBtn.setBorderPainted(false);
		nextBtn.setLocation(play_xpos + musicBtn_height * 2 + 25, play_ypos);
		nextBtn.addActionListener(btnHandler);
		primary.add(nextBtn);

		prevBtn = new JButton();
		prevBtn.setSize(musicBtn_width, musicBtn_height);
		prevBtn.setIcon(new ImageIcon("Images/prevBtn.jpg"));
		prevBtn.setBorderPainted(false);
		prevBtn.setLocation(play_xpos - musicBtn_height - 15, play_ypos);
		prevBtn.addActionListener(btnHandler);
		primary.add(prevBtn);
	}

	private class ButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Object o = e.getSource();

			// --------------- System button --------------------
			if (o == closeBtn) System.exit(0);
			if (o == miniBtn) updateManager.setFrameState(JFrame.ICONIFIED);
			// -------------------------------------------------

			// ----------------------- Proper Button ---------------------
			// 반복 종류 변경
			if (o == repeatBtn) {
				repeatType = (repeatType + 1) % 3;
				repeatBtn.setText(repeatStr[repeatType]
						);
				player.setRepeatType(repeatType);
			}
			
			// 리스트 프레임 활성 여부
			if (o == listFrameVisibleBtn) {
				
				if(listFrameVisibleState == FrameVisible.LIST_FRAME_VISIBLE) {
					updateManager.setFrameVisible(FrameVisible.LIST_FRAME_INVISIBLE);
					listFrameVisibleState = FrameVisible.LIST_FRAME_INVISIBLE;
				}
				else {
					updateManager.setFrameVisible(FrameVisible.LIST_FRAME_VISIBLE);
					listFrameVisibleState = FrameVisible.LIST_FRAME_VISIBLE;
				}
				
			}
			
			// 인폼 프레임 활성 여부
			if (o == informFrameVisibleBtn) {

				if(informFrameVisibleState == FrameVisible.INFORM_FRAME_VISIBLE) {
					updateManager.setFrameVisible(FrameVisible.INFORM_FRAME_INVISIBLE);
					informFrameVisibleState = FrameVisible.INFORM_FRAME_INVISIBLE;
				}
				else {
					updateManager.setFrameVisible(FrameVisible.INFORM_FRAME_VISIBLE);
					informFrameVisibleState = FrameVisible.INFORM_FRAME_VISIBLE;
				}
			}
			
			// ---------------------------------------------------------------------
			
			//----------------------------- Music Button -------------------------
			
			// 음악 재생버튼
			if (o == playBtn) {
				if(player.play()) {
					playBtn.setVisible(false);
					pauseBtn.setVisible(true);
				}
			}

			// 음악 일시 정지
			if (o == pauseBtn) {
				player.pause();
				pauseBtn.setVisible(false);
				playBtn.setVisible(true);
			}

			// 음악 종료
			if (o == stopBtn) {
				player.stop();
				playBtn.setVisible(true);

			}

			// 이전 노래
			if (o == prevBtn) {
				if(player.play(player.getCurPlayIdx() - 1)) {
					playBtn.setVisible(false);
					pauseBtn.setVisible(true);
				}

				
			}
			// 다음 노래
			if (o == nextBtn) {
				if(player.play(player.getCurPlayIdx() + 1)) {	
					playBtn.setVisible(false);
					pauseBtn.setVisible(true);
				}
			}
			// ----------------------------------------------------------------
		}
	}
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		g.setColor(new Color(200, 200, 200));
		g.drawRoundRect(0, 0, frameWidth - 1, frameHeight - 1, 24, 24);

	}

}