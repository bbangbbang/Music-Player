import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;


public class MusicApp {
	
	private final int p_width = 300, p_height = 100;
	private final int i_width = 300, i_height = 100;
	private final int l_width = 300, l_height = 300;

	private MouseHandler mouseHandler;
	private PlayFrame playFrame;
	private InformFrame informFrame;
	private ListFrame listFrame;
	private FrameUpdate updateManager;
	
	public MusicApp()
	{
		updateManager = new FrameUpdate();
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension winSize = tk.getScreenSize();
		mouseHandler = new MouseHandler();
		
		
		// 스크린 중앙 위치 구하기
		int xPos = (int)(winSize.getWidth()/2 - p_height/2);
		int yPos = (int)(winSize.getHeight()/3);
		
		MusicPlayer.getMusicPlayer().setUpdateManager(updateManager);
		
		playFrame = new PlayFrame(p_width, p_height,updateManager);
		playFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		playFrame.setUndecorated(true);	// 타이틀 바 제거
		playFrame.setPreferredSize(new Dimension(p_width,p_height));
		playFrame.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, p_width, p_height, 25, 25));
		playFrame.setLocation(xPos, yPos);
		playFrame.addMouseListener(mouseHandler);
		playFrame.addMouseMotionListener(mouseHandler);
	
		playFrame.pack();
	

		informFrame = new InformFrame(i_width, i_height,updateManager);
		informFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		informFrame.setUndecorated(true);
		informFrame.setPreferredSize(new Dimension(p_width,p_height));
		informFrame.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, p_width, p_height, 25, 25));
		informFrame.setLocation(xPos, yPos + p_height);
		
		informFrame.addMouseListener(mouseHandler);
		informFrame.addMouseMotionListener(mouseHandler);
		informFrame.pack();
		
		listFrame = new ListFrame(l_width, l_height,updateManager);
		listFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		listFrame.setUndecorated(true);
		listFrame.setPreferredSize(new Dimension(l_width,l_height));
		listFrame.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, l_width, l_height, 25, 25));
		listFrame.setLocation(xPos, yPos + i_height + p_height);
		
		listFrame.addMouseListener(mouseHandler);
		listFrame.addMouseMotionListener(mouseHandler);
		listFrame.pack();
		
		playFrame.setVisible(true);
		informFrame.setVisible(true);
		listFrame.setVisible(true);
	}

	

}
