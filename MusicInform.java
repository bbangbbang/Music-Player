
import java.io.File;

public class MusicInform {
	private String path, name;
	private int frameSize;
	private int tagSize = 0;
	public MusicInform(File f) {


		try {		
			MpegHeader mpegHeader = new MpegHeader(f); 
			frameSize = mpegHeader.getFrameSize(); // mpeg 파일 프레임 사이즈
			tagSize = mpegHeader.getTagSize();		// mpeg 파일 헤더 이전 태그 사이즈
		} catch (Exception e) {}
		path = f.getAbsolutePath(); // 파일 풀 네임
		int idx = path.lastIndexOf('\\');
		name = path.substring(idx+1); // 파일 네임
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