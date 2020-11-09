/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

/**
 * RulerUIの基本L&F実装です.
 *
 * @author desktopgame
 */
public class BasicRulerUI extends RulerUI {

    private Ruler ruler;
    private MouseHandler mouseHandler;

    public BasicRulerUI() {
        this.mouseHandler = new MouseHandler();
    }

    @Override
    public void installUI(JComponent c) {
        this.ruler = (Ruler) c;
        ruler.addMouseListener(mouseHandler);
        ruler.addMouseMotionListener(mouseHandler);
        ruler.setPreferredSize(new Dimension(Short.MAX_VALUE, 30));
    }

    @Override
    public void uninstallUI(JComponent c) {
        ruler.removeMouseListener(mouseHandler);
        ruler.removeMouseMotionListener(mouseHandler);
        this.ruler = null;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        Color color = g2.getColor();
        int kw = ruler.getKeyboard().getWidth();
        g2.setColor(Color.black);
        g2.drawLine(kw, 0, kw, ruler.getHeight());
        g2.drawLine(0, 0, ruler.getWidth(), 0);
        RegionManager regionMan = ruler.getPianoRoll().getRegionManager();
        for (Region region : regionMan.getRegions()) {
            int height = ruler.getHeight();
            Rectangle rect = new Rectangle(region.getStartOffset(), height / 2, region.getLength(), height / 2);
            rect.x += kw + 1;
            g2.setColor(Color.orange);
            g2.fill(rect);
            g2.setColor(Color.black);
            g2.draw(rect);

            String str = String.format("x%d", region.getLoopCount());
            FontMetrics fm = g2.getFontMetrics();
            int strWidth = fm.stringWidth(str);
            int strHeight = fm.getHeight();
            int strX = rect.x + ((rect.width - strWidth) / 2);
            int strY = rect.y + ((rect.height - strHeight) / 2) + fm.getAscent();
            g2.drawString(str, strX, strY);
        }
        g2.setColor(color);
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!ruler.isSeekable()) {
                return;
            }
            barMove(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!ruler.isSeekable()) {
                return;
            }
            barMove(e);
        }

        private void barMove(MouseEvent e) {
            int kw = ruler.getKeyboard().getWidth();
            int pw = ruler.getPianoRoll().getUI().computeWidth();
            int xpos = e.getX() - kw;
            if (xpos < 0) {
                xpos = 0;
            }
            if (xpos >= pw) {
                xpos = pw;
            }
            int offset = ruler.getScrollPane().getHorizontalScrollBar().getValue();
            int newpos = xpos + offset;
            PianoRollLayerUI lui = ruler.getPianoRollLayerUI();
            lui.setSequencePosition(newpos);
        }
    }
}
