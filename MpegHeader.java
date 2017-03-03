
import java.io.File;
import java.io.RandomAccessFile;

public class MpegHeader {

	private int tagSize;
	private byte[] ID3v2Tag = new byte[10];
	private byte[] header = new byte[4]; 
	private int version;
	private int layer;
	private int errorProtection;
	private int bitRate;
	private int frequency;
	private int padBit;
	private int privBit;
	private int mode;
	private int modeExtension;
	private int copy;
	private int origina;
	private int emphasis;
	private int frameSize;
	private int samplePerFrame;
	
	// col : mpeg , row : layer
	private int smaplePerFrameTable[][] = new int[][] {
		{0, 0, 0, 0},
		{576, 0, 576, 1152},
		{1152, 0, 1152, 1152},
		{384, 0, 384, 384},
	};
	
	// row : sampling rate index, col : mpeg
	private int[][] samplingRatesTable = new int [][]{
			{11025,	 0, 22050,	44100},
			{12000,	 0, 24000,	48000},
			{8000,	 0, 16000,	32000},
			{0, 	 0, 0	 ,		0}
			
	};
	
	// mpeg, bitrate Index, layer
	private int[][][] bitRateTable = new int[][][]
	{
			{}, // mpeg version 2.5
			{}, // reserved
			{
				{0,0, 0, 0},
				{0,8,32, 32},
				{0,16, 48, 40},
				{0,24, 56, 48},
				{0,32, 64, 56},
				{0,40, 80, 64},
				{0,48, 96, 80},
				{0,56, 112, 96},
				{0,64, 128, 112},
				{0,80, 144, 128},
				{0,96, 160, 160},
				{0,112, 176, 192},
				{0,128, 192, 224},			
				{0,144, 224, 256},
				{0,160, 256, 320},
				{-1,-1,-1,-1}
			}, // mpeg 2
			{
				{0, 0, 0, 0},
				{0, 32, 32, 32},
				{0, 40, 48, 64},
				{0, 48, 56, 96},
				{0, 56, 64, 128},
				{0, 64, 80, 160},
				{0, 80, 96, 192},
				{0, 96, 112, 224},
				{0, 112, 128, 256},
				{0, 128, 160, 288},
				{0, 160, 192, 320},
				{0, 192, 224, 352},
				{0, 224, 256, 384},
				{0, 256, 320, 416},
				{0, 320, 384, 448},
				{-1,-1,-1,-1}
			} // mpeg 1
			
	};

	public MpegHeader(File f) {
		try {
			RandomAccessFile ris = new RandomAccessFile(f, "r");
			ris.read(ID3v2Tag,0,3);
			tagSize = 0;
			
			// 헤더 앞에 태그가 존재하는 경우 스킵
			if(ID3v2Tag[0] == 'I' && ID3v2Tag[1] == 'D' && ID3v2Tag[2] == '3')
			{
				ris.read(ID3v2Tag,3,7);
				tagSize = ID3v2Tag[9] | (ID3v2Tag[8] << 7) | (ID3v2Tag[7] << 14) | (ID3v2Tag[6] << 21);
				ris.skipBytes(tagSize);
			}
			
			else
			{
				ris.seek(0);
			}
			
			ris.read(header,0,4);
			int col = ((header[1] & (1 << 4)) >> 3) | ((header[1] & (1 << 3)) >> 3); // mpeg
			int row = ((header[2] & (1 << 3)) >> 2) | ((header[2] & (1 << 2)) >> 2); // sampling Rate Index
			
			frequency = samplingRatesTable[row][col];
			
			col = ((header[1] & (1 << 2)) >> 1) | ((header[1] & (1 << 1)) >> 1); //layer 
			row = ((header[2] & (1 << 7)) >> 4) | ((header[2] & (1 << 6)) >> 4)
					| ((header[2] & (1 << 5)) >> 4) | ((header[2] & (1 << 4)) >> 4); // bitrate Index
			
			int mpeg = ((header[1] & (1 << 4)) >> 3) | ((header[1] & (1 << 3)) >> 3);
			
			bitRate = bitRateTable[mpeg][row][col] * 1000;
			col = ((header[1] & (1 << 4)) >> 3) | ((header[1] & (1 << 3)) >> 3); // mpeg
			row = ((header[1] & (1 << 2)) >> 1) | ((header[1] & (1 << 1)) >> 1); // layer
			samplePerFrame = smaplePerFrameTable[row][col];
			frameSize = (samplePerFrame / 8 * bitRate) / frequency;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getFrameSize() {
		return frameSize;
	}
	
	public int getBitRate() {
		return bitRate;
	}
	public int getFrequency() {
		return frequency;
	}
	
	public int getTagSize() {
		return tagSize;
	}
}

/* http://creon.tistory.com/203
 * http://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header
 * http://died-young.tistory.com/89
 * http://www.mp3-tech.org/programmer/frame_header.html
 * 
 */ 
 
 
