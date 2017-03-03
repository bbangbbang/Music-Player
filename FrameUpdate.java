
import java.util.ArrayList;

public class FrameUpdate {
	private ArrayList obs;
	public FrameUpdate() {
		obs = new ArrayList();
	}
	
	// �����ʷ� ���
	public void register(Observer o) {
		obs.add(o);
	}

	// ������ Ȱ�� ���ΰ� ����Ǿ��� ���.(visible)
	public void setFrameVisible(int v) {
		
		for(int i = 0; i < obs.size(); i ++) {
			Observer observer = (Observer)obs.get(i);
			observer.updateFrameVisible(v);
		}
		
	}
	
	// �뷡 ���°� ����� ���. (��ŵ�̳� �뷡 ���� ��)
	public void setMusicPlayerState() {
		
		for (int i = 0; i < obs.size(); i++) {
			Observer observer = (Observer)obs.get(i);
			observer.updateFrameComponents();
		}
	}
	
	// �������� �ּ�ȭ�ǰų� �ּ�ȭ ���¿��� Ȱ��ȭ �Ȱ��
	public void setFrameState(int s) {
		for (int i = 0; i < obs.size(); i++) {
			Observer observer = (Observer)obs.get(i);
			observer.updateFrameState(s);
		}
		
	}
	
		
	
}
