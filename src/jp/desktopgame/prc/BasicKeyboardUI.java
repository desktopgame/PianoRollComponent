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
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComponentUI;

/**
 * KeyboardUIの基本L&F実装です.
 *
 * @author desktopgame
 */
public class BasicKeyboardUI extends ComponentUI {

    private Keyboard keyboard;
    private JScrollPane scrollPane;
    private PropertyChangeHandler propertyChangeHandler;
    private AdjustmentHandler adjustmentHandler;
    private int offset;

    public BasicKeyboardUI() {
        this.propertyChangeHandler = new PropertyChangeHandler();
        this.adjustmentHandler = new AdjustmentHandler();
    }

    @Override
    public void installUI(JComponent c) {
        this.keyboard = (Keyboard) c;
        this.scrollPane = keyboard.getPianoRollScrollPane();
        keyboard.addPropertyChangeListener(propertyChangeHandler);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(adjustmentHandler);
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
                g2.setColor(Color.darkGray);
            } else {
                g2.setColor(Color.lightGray);
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
