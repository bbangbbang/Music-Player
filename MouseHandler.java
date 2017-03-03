
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

public class MouseHandler implements MouseListener, MouseMotionListener{
	private int xPos = 0, yPos= 0;
	private JFrame f;
	@Override
	// �������� Ŭ���� �� �� ��ũ�� �������� ��ǥ ���
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		f = (JFrame)e.getSource();
		xPos = e.getXOnScreen();
		yPos = e.getYOnScreen();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
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
	// ������ ��ġ �̵�
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

		f.setLocation(f.getLocation().x + e.getXOnScreen() - xPos, f.getLocation().y + e.getYOnScreen() - yPos );
		
		xPos = e.getXOnScreen();
		yPos = e.getYOnScreen();
		
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	
	}
}