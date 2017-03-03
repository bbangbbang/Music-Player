
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ListFrame extends JFrame implements Observer{
	private int frameWidth, frameHeight;
	private JPanel primary;
	private FrameUpdate updateManager;
	private JList list;
	private JButton addFile, delFile;
	private BtnHandler btnHandler;
	private JScrollPane scroll;
	private ListCellRenderer renderer;
	private MusicPlayer player;
	private WindowHandler winHandler;
	
	public ListFrame(int width, int height, FrameUpdate u) {
		this.updateManager = u;
		player = MusicPlayer.getMusicPlayer();
		updateManager.register(this);
		frameWidth = width;
		frameHeight = height;
		renderer = new ListCellRenderer(0);

		primary = new JPanel();
		winHandler = new WindowHandler();
		btnHandler = new BtnHandler();

		primary = new JPanel();
		primary.setLayout(null);
		primary.setSize(frameWidth, frameHeight);

		list = new JList();
		scroll = new JScrollPane(list);
		scroll.getVerticalScrollBar().setUI(new ListScroll());
		scroll.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

		scroll.setLocation(20,20);
		scroll.setSize(frameWidth - 39,frameHeight - 49);
		list.setBackground(new Color(238,238,238));
		list.setFont(new Font("맑은 고딕",Font.PLAIN,13));
		list.setCellRenderer(renderer);
		scroll.setBorder(null);
		list.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
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
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

				int idx = list.locationToIndex(e.getPoint());
				// 더블 클릭의 경우 노래 실행
				if(e.getClickCount() == 2) {
					player.play(idx);
				}
				else if (e.getClickCount() == 1) {

				}

			}
		});
		
		primary.add(scroll);
		
		addFile = new JButton();
		addFile.setLocation(20, frameHeight-25);
		addFile.setSize(20,20);
		addFile.setFont(new Font("verdana",Font.BOLD,9));
		addFile.setMargin(new Insets(0, 0, 0, 0));

		addFile.setText("+");
		addFile.addActionListener(btnHandler);
		primary.add(addFile);


		delFile = new JButton();
		delFile.setLocation(45, frameHeight-25);
		delFile.setSize(20,20);
		delFile.setFont(new Font("verdana",Font.BOLD,15));
		delFile.setMargin(new Insets(0, 0, 0, 0));
		delFile.addActionListener(btnHandler);
		delFile.setText("-");
		primary.add(delFile);
		addWindowListener(winHandler);
		getContentPane().add(primary);
	}

	public void updateFrameComponents() {
		renderer.setIdx(player.getCurPlayIdx());
		list.repaint();
	}

	public void updateFrameVisible(int v) {
		if(v == FrameVisible.INFORM_FRAME_VISIBLE)
			setVisible(true);
		else if(v == FrameVisible.INFORM_FRAME_INVISIBLE)
			setVisible(false);
	}
	
	private class ListCellRenderer extends DefaultListCellRenderer
	{
		private JLabel item;
		private int idx;

		public ListCellRenderer(int i){
			idx = i;
		}

		public void setIdx(int i) {
			idx = i;
		}
		@Override
		public Component getListCellRendererComponent(JList<?> list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			// TODO Auto-generated method stub

			// 리스트의 각 라벨을 불러옴
			item = (JLabel)super.getListCellRendererComponent(
					list,value,index,isSelected,cellHasFocus);

			// 선택된 라벨을 제외한 라벨에 색깔 부여
			if(!isSelected) {
				if(index % 2== 0) 
					item.setBackground(new Color(252,252,252));
				else 
					item.setBackground(new Color(242,242,235));
			}
			
			// 현재 실행중인 노래를 굵게 표시
			if(index == idx) {
				item.setForeground(Color.black);
				item.setFont(new Font("맑은 고딕",Font.BOLD,13));
			}
			else	
				item.setFont(list.getFont());
			return item; 

		}

	} 
	private class BtnHandler implements ActionListener {

		private JFileChooser fileOpen;
		public BtnHandler() {

			fileOpen = new JFileChooser();
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

			if(e.getSource() == addFile) 
				addMusic();

			if(e.getSource() == delFile)
				delMusic();

		}

		private void delMusic() {

			if(player.getMusicList().size() == 0) 
				return;

			int idx[] = list.getSelectedIndices();

			if(idx.length == 0) return;

			for(int i = idx.length-1; i >= 0; i --) {
				player.delMusic(idx[i]);
			}

			renderer.setIdx(player.getCurPlayIdx());
		
			list.setListData(player.getMusicList());
			list.setSelectedIndex(idx[0]);
			list.repaint();

		}
		private void addMusic(){

			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"mp3 file","mp3");

			fileOpen.setFileFilter(filter);
			fileOpen.setMultiSelectionEnabled(true);


			// case no add music in FileChooser
			if(fileOpen.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
				return;
			}


			File file[] = fileOpen.getSelectedFiles();

			for(int i = 0 ; i< file.length; i ++)
				player.addMusic(file[i]);


			list.setListData(player.getMusicList());

		}

	}

	public void updateFrameState(int state) {

		this.setState(state);
	}	
	private class WindowHandler extends WindowAdapter {

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			//super.windowDeiconified(e);
			updateManager.setFrameState(JFrame.NORMAL);

		}
	}


	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		g.setColor(new Color(200,200, 200));
		g.drawRoundRect(0, 0, frameWidth-1, frameHeight-1, 24, 24);
		g.drawRect(19, 19, frameWidth - 38, frameHeight -48);
	}

	private class ListScroll extends BasicScrollBarUI {

		protected void configureScrollBarColors() {
			trackColor = new Color(225,225,225); // 트랙 색깔 
			thumbColor = new Color(205,205,205); // 덤브 색깔
			thumbDarkShadowColor = new Color(200,200,200); // 덤브 그림자 색상
			thumbHighlightColor = new Color(250,250,250); // 덤브 테두리 색상
		}


		// 덤브 최대 크기 지정
		protected Dimension getMaximumThumbSize() {
			return new Dimension(10, 40);
		}

		// 위 화살표 버튼 안보이게 지정
		protected JButton createDecreaseButton(int orientation) {//1
			JButton button = new JButton();
			button.setBorderPainted(false);
			button.setBackground(new Color(225,225,225));

			return button;

		}

		@Override
		// 아래 화살표 버튼 안보이게 지정
		protected JButton createIncreaseButton(int orientation) {//5
			JButton button = new JButton();
			button.setBorderPainted(false);
			button.setBackground(new Color(225,225,225));
			return button;
		}

	}

}
