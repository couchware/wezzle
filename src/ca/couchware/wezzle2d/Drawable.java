package ca.couchware.wezzle2d;

public interface Drawable
{
	public void draw();
    public void setVisible(boolean visible);
    public boolean isVisible();
    public void setDirty(boolean dirty);
    public boolean isDirty();
}
