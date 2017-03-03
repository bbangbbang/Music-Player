
import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import javax.swing.JProgressBar;

import javazoom.jl.player.Player;

public class MusicPlayer {
	private Object lock = null;
	private static MusicPlayer instance = null;
	private int state = MusicState.NOSTART;
	private Vector musicList = null; 
	private int curPlayIdx = 0;
	private PlayThread playThread = null;
	private int repeatType = RepeatType.NONE;
	private FrameUpdate updateManager;
	private JProgressBar progress = null;
	private int progressMaxValue = 0;
	private boolean bDrawProgressValue = true;
	private MusicPlayer() {
		lock = new Object();
		musicList = new Vector();
	}

	// ------ Method for MusicPlayer control -------
	public boolean play() {

		if(musicList.size() > 0) {
			// ------ resume Music ------
			if(state == MusicState.PAUSE) {
				synchronized (lock) {
					state = MusicState.PLAYING;
					lock.notifyAll();
				}
			}
			// ------ play new Music -----
			else {
				if(state == MusicState.PLAYING) stop();
				state = MusicState.PLAYING;
				playThread = new PlayThread();
				playThread.setDaemon(true); 
				playThread.start();
				updateManager.setMusicPlayerState();
			}
			return true;
		}
		return false;
	}

	public boolean play(int idx) {
		setCurPlayIdx(idx);
		if(state != MusicState.NOSTART) stop();
		return play();
	}

	public void pause()  {
		state = MusicState.PAUSE;
	}

	public void skip(float pos) {
		synchronized (lock) {	
			playThread.skip(pos);
		}
	}

	public void stop() {
		
		synchronized(lock) {
			try {
				// ��ȭ�� state�� ���¸� playThread�� �������� �ʰ� �����ϸ�
				// �� stop �޼ҵ尡 play(int idx)�� ȣ����� ���
				// �뷡�� ���������� �������� ����.
				// play() ���� state�� ���� �ٷ� play�� �ٲ�� ����
				if(state == MusicState.PLAYING){
					state = MusicState.NOSTART;
					lock.wait();
				}

				if (state == MusicState.PAUSE) {
					state = MusicState.NOSTART;
					lock.notifyAll(); // pause�� ��� playThread�� wait �����̱� ������ ���� ��������
					lock.wait();	  
				}
			}
			catch (Exception e) {}
		}				
	}

	// -----------------------------------------------

	private class PlayThread extends Thread {
		private FileInputStream fis;
		private MusicInform m;
		private Player player;
		private int framePos, frameSize;
		private File f;
		private int totalFrameSize;
		private int totalFrameCnt;

		@Override
		public void run() {
			try {
				m = (MusicInform) musicList.get(curPlayIdx);
				frameSize = m.getFrameSize();
				f= new File(m.getPath());
				fis = new FileInputStream(f);
				fis.skip(m.getTagSize());
				totalFrameSize = fis.available();
				totalFrameCnt = totalFrameSize/frameSize;
				player = new Player(fis);
				framePos = 0;

				for(;state != MusicState.FINISH;) {
					try {			

						player.play(1); // ������ ������ �뷡 ����
						
						if(progress != null) {
							// �� ������� ���α׷����� ���� ����
							if(bDrawProgressValue)
								progress.setValue( (int) (((float)framePos / totalFrameCnt) * progress.getMaximum()));
						}
						synchronized (lock) {
							framePos ++;
							if(fis.available() <= 0) {
								state = MusicState.FINISH;
								break;
							}
							if (state == MusicState.PAUSE) {
								player.close(); // �ٷ� ���� ������ �뷡�� �ٷ� ������ �ȵ�.
								lock.wait();
								reOpenMusic();	
							}
							if(state == MusicState.NOSTART) {
								player.close();
								lock.notifyAll();
								break;
							}
						} // synchronized
					} // try
					catch(Exception e) {
						// �����ӿ� ������ ���� ��� ���� ������ ���
						framePos ++;
						reOpenMusic();
						
					}
				} // for

				progress.setValue(0);
				if(state == MusicState.FINISH) {
					// �Ѱ� ���� ����� ��� �ٽ� ���
					if(repeatType == RepeatType.ONE_RERPEAT)
						play();
					// ��� �뷡 ����� ��� ���� �뷡 ���
					else if(repeatType == RepeatType.ALL_REPEAT) {
						play(curPlayIdx + 1);
					} 
					// ��ư�� ���¸� play button ���� �ٲ�
					else {
						updateManager.setMusicPlayerState();
					}

				}

			} // try
			catch(Exception e) {

			}
		}// run

		public void skip(float pos) {
			framePos = (int) (totalFrameCnt * pos);
			reOpenMusic();
		}

		// ���� �����
		private void reOpenMusic() {
			try {
				player.close();
				f= new File(m.getPath());
				fis = new FileInputStream(f);
				fis.skip((framePos)*frameSize + m.getTagSize()); // ������  ������� ������ ��ġ�� ���ϸ� �̹� �ǳʶ� �κ��� ����� �� �� ����
				player = new Player(fis);
			} catch (Exception e) {}
		}
	}// PlayThread

	// ------ Method for musicList --------
	public void addMusic(File f) {
		MusicInform m = new MusicInform(f);
		musicList.add(m);
	}

	public void delMusic(int idx) {
		musicList.remove(idx);
		if(idx < curPlayIdx) curPlayIdx --;
	}

	public final Vector getMusicList() {
		return musicList;
	}

	// ------------------------------------

	// ----------- singleton --------------
	public static MusicPlayer getMusicPlayer() {
		if(instance == null)
			instance = new MusicPlayer();
		return instance;
	}
	//-------------------------------------

	public int getCurPlayIdx() { return curPlayIdx; }
	private void setCurPlayIdx(int idx) {
		try { curPlayIdx = (musicList.size() + idx) % musicList.size() ; }
		catch(Exception e) {}// divided by zero
	}

	public int getState() {	return state; }
	public void setState(int s) { state = s; }

	public void setRepeatType(int repeat) { repeatType = repeat;}
	public void setUpdateManager(FrameUpdate u) {
		updateManager = u;
	}

	public void addProgressBar(JProgressBar bar) {
		progress = bar;
	}
	
	// �÷��̾ ���α׷������� ���¸� �׸� ������ ����
	// ����ڰ� ���α׷����� ��ġ�� �巡�� �ϰ� ���� ��� false �ؾ� ��
	// �ȱ׷��� ���� �����忡 ���� �巡�� �߿��� ���α׷��� ���� ���°� ����
	public void setDrawProgressValue(boolean d) {
		bDrawProgressValue = d;
	}
}
