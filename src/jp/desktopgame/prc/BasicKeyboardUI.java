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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * KeyboardUIの基本L&F実装です.
 *
 * @author desktopgame
 */
public class BasicKeyboardUI extends KeyboardUI {

    private Keyboard keyboard;
    private JScrollPane scrollPane;
    private PropertyChangeHandler propertyChangeHandler;
    private AdjustmentHandler adjustmentHandler;
    private int offset;
    private boolean[] bitmap;

    public BasicKeyboardUI() {
        this.propertyChangeHandler = new PropertyChangeHandler();
        this.adjustmentHandler = new AdjustmentHandler();
        this.bitmap = null;
    }

    @Override
    public void installUI(JComponent c) {
        this.keyboard = (Keyboard) c;
        this.scrollPane = keyboard.getPianoRollScrollPane();
        keyboard.addPropertyChangeListener(propertyChangeHandler);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(adjustmentHandler);
        this.bitmap = null;
    }

    @Override
    public void uninstallUI(JComponent c) {
        this.keyboard = null;
        this.scrollPane = null;
        keyboard.removePropertyChangeListener(propertyChangeHandler);
        scrollPane.getVerticalScrollBar().removeAdjustmentListener(adjustmentHandler);
    }

    @Override
    public void paint(Graphics g, JComponent jc) {
        Graphics2D g2 = (Graphics2D) g;
        PianoRoll p = keyboard.getPianoRoll();
        PianoRollModel pModel = p.getModel();
        final int BW = p.getBeatWidth();
        final int BH = p.getBeatHeight();
        final int LPS = 96;
        int y = -offset;
        Color c = g2.getColor();
        for (int i = pModel.getKeyCount() - 1; i >= 0; i--) {
            Key k = pModel.getKey(i);
            int nextY = y + BH;
            int indexInBwTable = i % Keyboard.BLACK_WHITE_TABLE.length;
            if (Keyboard.BLACK_WHITE_TABLE[indexInBwTable] == Key.BLACK) {
                g2.setColor(!isHighlightKey(i) ? Color.darkGray : Color.ORANGE);
            } else {
                g2.setColor(!isHighlightKey(i) ? Color.lightGray : Color.ORANGE);
            }
            g2.fillRect(0, y, LPS, nextY - y);
            if (Keyboard.KEY_STRING_TABLE[indexInBwTable].equals("C")) {
                g2.setColor(Color.red);
                g2.drawString(String.valueOf((k.getIndex() / 12) - 2), LPS / 2, nextY);
            }
            g2.setColor(Color.green);
            if (keyboard.isUseDrumMap() && keyboard.getDrumMap().containsKey(i)) {
                g2.drawString(keyboard.getDrumMap().get(i), 0, nextY - (p.getBeatHeight() / 2));
            } else {
                g2.drawString(Keyboard.KEY_STRING_TABLE[indexInBwTable], 0, nextY - (p.getBeatHeight() / 2));
            }
            g2.setColor(Color.black);
            g2.drawLine(0, y, LPS, y);
            y = nextY;
        }
    }

    @Override
    public void setHighlight(int height, boolean b) {
        getBitmap()[height] = b;
        keyboard.repaint();
    }

    @Override
    public boolean isHighlight(int height) {
        return getBitmap()[height];
    }

    @Override
    public void resetHighlight() {
        if (getBitmap() != null) {
            Arrays.fill(getBitmap(), false);
            keyboard.repaint();
        }
    }

    private boolean isHighlightKey(int height) {
        boolean[] bm = getBitmap();
        if (height >= bm.length) {
            return false;
        }
        return bm[height];
    }

    private boolean[] getBitmap() {
        PianoRoll p = keyboard.getPianoRoll();
        PianoRollModel pModel = p.getModel();
        if (pModel == null && bitmap == null) {
            this.bitmap = new boolean[12 * 8];
        }
        if (pModel != null && bitmap == null) {
            this.bitmap = new boolean[pModel.getKeyCount()];
        } else {
            if (pModel != null && pModel.getKeyCount() > bitmap.length) {
                boolean[] buf = new boolean[pModel.getKeyCount()];
                System.arraycopy(bitmap, 0, buf, 0, bitmap.length);
                this.bitmap = buf;
            }
        }
        return bitmap;
    }

    private class PropertyChangeHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            Object ov = evt.getOldValue();
            Object nv = evt.getNewValue();
            if (name.equals("drumMap") || name.equals("useDrumMap")) {
                keyboard.repaint();
            }
        }
    }

    private class AdjustmentHandler implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            BasicKeyboardUI.this.offset = e.getValue();
            BasicKeyboardUI.this.keyboard.repaint();
        }
    }
}
