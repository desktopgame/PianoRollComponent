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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * RulerUIの基本L&F実装です.
 *
 * @author desktopgame
 */
public class BasicRulerUI extends RulerUI implements RegionUpdateListener, AdjustmentListener {

    private Ruler ruler;
    private MouseHandler mouseHandler;
    private JScrollPane scrollPane;

    public BasicRulerUI() {
        this.mouseHandler = new MouseHandler();
    }

    @Override
    public void installUI(JComponent c) {
        this.ruler = (Ruler) c;
        ruler.getScrollPane().getHorizontalScrollBar().addAdjustmentListener(this);
        ruler.addMouseListener(mouseHandler);
        ruler.addMouseMotionListener(mouseHandler);
        ruler.getPianoRoll().getRegionManager().addRegionUpdateListener(this);
        ruler.setPreferredSize(new Dimension(Short.MAX_VALUE, 30));
    }

    @Override
    public void uninstallUI(JComponent c) {
        ruler.getScrollPane().getHorizontalScrollBar().removeAdjustmentListener(this);
        ruler.removeMouseListener(mouseHandler);
        ruler.removeMouseMotionListener(mouseHandler);
        ruler.getPianoRoll().getRegionManager().removeRegionUpdateListener(this);
        this.ruler = null;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        int kw = ruler.getKeyboard().getWidth();
        Graphics2D g2 = (Graphics2D) g;
        Rectangle clipRect = g2.getClipBounds();
        Rectangle nClipRect = new Rectangle(clipRect);
        nClipRect.x += kw;
        nClipRect.width -= kw;
        g2.setClip(nClipRect);
        Color color = g2.getColor();
        JScrollBar hbar = ruler.getScrollPane().getHorizontalScrollBar();
        g2.setColor(Color.black);
        g2.drawLine(kw, 0, kw, ruler.getHeight());
        g2.drawLine(0, 0, ruler.getWidth(), 0);
        RegionManager regionMan = ruler.getPianoRoll().getRegionManager();
        for (Region region : regionMan.getRegions()) {
            int height = ruler.getHeight();
            Rectangle rect = new Rectangle(region.getStartOffset(), height / 2, region.getLength(), height / 2);
            rect.x += kw + 1 - hbar.getValue();
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
        g2.setClip(clipRect);
    }

    @Override
    public void regionUpdate(RegionUpdateEvent e) {
        int kw = ruler.getKeyboard().getWidth();
        e.getOldValue().map((x) -> toRect(x)).ifPresent(ruler::repaint);
        e.getNewValue().map((x) -> toRect(x)).ifPresent(ruler::repaint);
        ruler.repaint();
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        ruler.repaint();
    }

    private Rectangle toRect(Region region) {
        JScrollBar hbar = ruler.getScrollPane().getHorizontalScrollBar();
        int hOffset = hbar.getValue();
        int kw = ruler.getKeyboard().getWidth();
        Rectangle r = new Rectangle(region.getStartOffset() + (kw - hOffset), 0, region.getLength(), ruler.getHeight() / 2);
        //Rectangle r = region._toRect(kw - hOffset, ruler.getHeight() / 2);
        r.y += ruler.getHeight() / 2;
        return r;
    }

    private class MouseHandler extends MouseAdapter {

        private Region curRegion;
        private int type;
        private Point dragPos;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!ruler.isSeekable()) {
                return;
            }
            if (e.getClickCount() == 1) {
                RegionManager regionMan = ruler.getPianoRoll().getRegionManager();
                List<Region> regions = regionMan.getRegions();
                Optional<Region> regionOpt = regions.stream().filter((x) -> {
                    Rectangle r = toRect(x);
                    return r.contains(e.getPoint());
                }).findFirst();
                if (regionOpt.isPresent()) {
                    Region r = regionOpt.get();
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        r.setLoopCount(r.getLoopCount() + 1);
                    } else {
                        int n = Math.max(1, r.getLoopCount() - 1);
                        r.setLoopCount(n);
                    }
                } else {
                    barMove(e);
                }
            } else if (e.getClickCount() == 2) {
                int kw = ruler.getKeyboard().getWidth();
                JScrollBar hbar = ruler.getScrollPane().getHorizontalScrollBar();
                int hOffset = hbar.getValue();
                RegionManager regionMan = ruler.getPianoRoll().getRegionManager();
                List<Region> regions = regionMan.getRegions();
                // ダブルクリックした場所にリージョンがあるならそれを消す
                Optional<Region> regionOpt = regions.stream().filter((x) -> {
                    Rectangle r = toRect(x);
                    return r.contains(e.getPoint());
                }).findFirst();
                if (regionOpt.isPresent()) {
                    regionMan.removeRegion(regionOpt.get());
                } else {
                    int start = e.getX() + hOffset - kw;
                    regionMan.addRegion(new Region(start, start + 50, 1));
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
            RegionManager regionMan = ruler.getPianoRoll().getRegionManager();
            List<Region> regions = regionMan.getRegions();
            int mx = e.getX();
            int kw = ruler.getKeyboard().getWidth();
            this.curRegion = null;
            JScrollBar hbar = ruler.getScrollPane().getHorizontalScrollBar();
            int hOffset = hbar.getValue();

            // 左端を掴んでいる場合
            Optional<Region> left = regions.stream().filter((x) -> {
                Rectangle r = toRect(x);
                return mx >= r.x && mx < r.x + 5 && inRect(r, e.getY());
            }).findFirst();
            // 右端を掴んでいる場合
            Optional<Region> right = regions.stream().filter((x) -> {
                Rectangle r = toRect(x);
                return mx >= r.x + r.width - 5 && mx < r.x + r.width && inRect(r, e.getY());
            }).findFirst();
            // 真ん中を掴んでいる場合
            Optional<Region> middle = regions.stream().filter((x) -> {
                Rectangle r = toRect(x);
                return r.contains(e.getPoint());
            }).findFirst();
            if (curRegion == null && left.isPresent()) {
                curRegion = left.get();
                type = -1;
            } else if (curRegion == null && right.isPresent()) {
                curRegion = right.get();
                type = 1;
            } else if (curRegion == null && middle.isPresent()) {
                curRegion = middle.get();
                type = 0;
            }
            this.dragPos = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (curRegion != null && e.getX() > ruler.getKeyboard().getWidth()) {
                RegionManager regionMan = ruler.getPianoRoll().getRegionManager();
                int diffX = e.getX() - dragPos.x;
                int startOffset = curRegion.getStartOffset();
                int endOffset = curRegion.getEndOffset();
                if (type == 0) {
                    if (diffX > 0 || curRegion.getStartOffset() != 0) {
                        curRegion.setStartOffset(curRegion.getStartOffset() + diffX);
                        curRegion.setEndOffset(curRegion.getEndOffset() + diffX);
                    }
                } else if (type == 1) {
                    if (curRegion.getLength() > 10 || diffX > 0) {
                        curRegion.setEndOffset(curRegion.getEndOffset() + diffX);
                    }
                } else if (type == -1) {
                    if (curRegion.getLength() > 10 || diffX > 0) {
                        curRegion.setStartOffset(curRegion.getStartOffset() + diffX);
                    }
                }
                if (regionMan.getRegions().stream().filter((x) -> x != curRegion).anyMatch((x) -> {
                    Rectangle a = new Rectangle(x.getStartOffset(), 0, x.getLength(), 10);
                    Rectangle b = new Rectangle(curRegion.getStartOffset(), 0, curRegion.getLength(), 10);
                    return a.intersects(b);
                })) {
                    curRegion.setStartOffset(startOffset);
                    curRegion.setEndOffset(endOffset);
                }
                this.dragPos = e.getPoint();

                return;
            }
            if (!ruler.isSeekable()) {
                return;
            }
            barMove(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            RegionManager regionMan = ruler.getPianoRoll().getRegionManager();
            List<Region> regions = regionMan.getRegions();
            int mx = e.getX();
            int kw = ruler.getKeyboard().getWidth();
            JScrollBar hbar = ruler.getScrollPane().getHorizontalScrollBar();
            int hOffset = hbar.getValue();
            if (regions.stream().map((x) -> toRect(x)).anyMatch((x) -> mx >= x.x && mx < x.x + 5 && inRect(x, e.getY()))) {
                ruler.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            } else if (regions.stream().map((x) -> toRect(x)).anyMatch((x) -> mx >= x.x + x.width - 5 && mx < x.x + x.width && inRect(x, e.getY()))) {
                ruler.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            } else if (regions.stream().map((x) -> toRect(x)).anyMatch((x) -> x.contains(e.getPoint()))) {
                ruler.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                ruler.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        private boolean inRect(Rectangle rect, int y) {
            return y >= rect.y && y < rect.y + rect.height;
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
