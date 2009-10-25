/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.ITextLabel;

/**
 *
 * @author kgrad
 */
public class ScrollerRow
{
    private Button button;
    private ITextLabel label;
    public ScrollerRow(Button b, ITextLabel l)
    {
        this.button = b;
        this.label = l;
    }

    public Button getButton()
    {
        return button;
    }

    public ITextLabel getLabel()
    {
        return label;
    }
}
