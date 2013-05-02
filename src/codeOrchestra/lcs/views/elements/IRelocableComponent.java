package codeOrchestra.lcs.views.elements;

public interface IRelocableComponent {
	public void setLocation(int x, int y);
	public int getLocationX();
	public int getLocationY();
	public int getWidth();
	public int getHeight();
}
