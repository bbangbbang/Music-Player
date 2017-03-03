
import java.util.ArrayList;

public class FrameUpdate {
	private ArrayList obs;
	public FrameUpdate() {
		obs = new ArrayList();
	}
	
	// 리스너로 등록
	public void register(Observer o) {
		obs.add(o);
	}

	// 프레임 활성 여부가 변경되었을 경우.(visible)
	public void setFrameVisible(int v) {
		
		for(int i = 0; i < obs.size(); i ++) {
			Observer observer = (Observer)obs.get(i);
			observer.updateFrameVisible(v);
		}
		
	}
	
	// 노래 상태가 변경된 경우. (스킵이나 노래 변경 등)
	public void setMusicPlayerState() {
		
		for (int i = 0; i < obs.size(); i++) {
			Observer observer = (Observer)obs.get(i);
			observer.updateFrameComponents();
		}
	}
	
	// 프레임이 최소화되거나 최소화 상태에서 활성화 된경우
	public void setFrameState(int s) {
		for (int i = 0; i < obs.size(); i++) {
			Observer observer = (Observer)obs.get(i);
			observer.updateFrameState(s);
		}
		
	}
	
		
	
}
