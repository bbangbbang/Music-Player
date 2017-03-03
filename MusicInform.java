
import java.io.File;

public class MusicInform {
	private String path, name;
	private int frameSize;
	private int tagSize = 0;
	public MusicInform(File f) {


		try {		
			MpegHeader mpegHeader = new MpegHeader(f); 
			frameSize = mpegHeader.getFrameSize(); // mpeg ���� ������ ������
			tagSize = mpegHeader.getTagSize();		// mpeg ���� ��� ���� �±� ������
		} catch (Exception e) {}
		path = f.getAbsolutePath(); // ���� Ǯ ����
		int idx = path.lastIndexOf('\\');
		name = path.substring(idx+1); // ���� ����
	}
	
	
	public String getPath() {
		return path;
	}
	
	// ------ Method for listBox in ListFrame -----
	public String toString() {
		return name;
	}
	// --------------------------------------------
	
	public int getFrameSize() {
		return frameSize;
	}
	
	public int getTagSize() {
		return tagSize;
	}
	
	
}