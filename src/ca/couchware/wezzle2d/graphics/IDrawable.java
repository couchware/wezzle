package ca.couchware.wezzle2d.graphics;

import java.awt.Rectangle;

public interface IDrawable
{
	public boolean draw();
    public Rectangle getDrawRect();
    public void resetDrawRect();
    public void setVisible(boolean visible);
    public boolean isVisible();
    public void setDirty(boolean dirty);
    public boolean isDirty();
}
