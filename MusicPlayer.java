
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
				// 변화된 state의 상태를 playThread에 적용하지 않고 진행하면
				// 이 stop 메소드가 play(int idx)에 호출됫을 경우
				// 노래가 정상적으로 정지되지 않음.
				// play() 에서 state의 값이 바로 play로 바뀌기 때문
				if(state == MusicState.PLAYING){
					state = MusicState.NOSTART;
					lock.wait();
				}

				if (state == MusicState.PAUSE) {
					state = MusicState.NOSTART;
					lock.notifyAll(); // pause일 경우 playThread가 wait 상태이기 떄문에 먼저 깨워야함
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

						player.play(1); // 프레임 단위로 노래 실행
						
						if(progress != null) {
							// 매 재생마다 프로그래스바 상태 변경
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
								player.close(); // 바로 닫지 않으면 노래가 바로 중지가 안됨.
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
						// 프레임에 오류가 있을 경우 다음 프레임 재생
						framePos ++;
						reOpenMusic();
						
					}
				} // for

				progress.setValue(0);
				if(state == MusicState.FINISH) {
					// 한곡 무한 재생일 경우 다시 재생
					if(repeatType == RepeatType.ONE_RERPEAT)
						play();
					// 모든 노래 재생일 경우 다음 노래 재생
					else if(repeatType == RepeatType.ALL_REPEAT) {
						play(curPlayIdx + 1);
					} 
					// 버튼의 상태를 play button 으로 바꿈
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

		// 파일 재오픈
		private void reOpenMusic() {
			try {
				player.close();
				f= new File(m.getPath());
				fis = new FileInputStream(f);
				fis.skip((framePos)*frameSize + m.getTagSize()); // 프레임  사이즈와 프레임 위치를 곱하면 이미 건너뛴 부분의 사이즈를 알 수 있음
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
	
	// 플레이어가 프로그래스바의 상태를 그릴 것인지 결정
	// 사용자가 프로그래스의 위치를 드래그 하고 있을 경우 false 해야 함
	// 안그러면 현재 스레드에 의해 드래그 중에도 프로그래스 바의 상태가 변함
	public void setDrawProgressValue(boolean d) {
		bDrawProgressValue = d;
	}
}
