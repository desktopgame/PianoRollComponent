/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.LayerUI;

/**
 *
 * @author desktopgame
 */
public class PianoRollLayerUI extends LayerUI<PianoRoll> {

    private JLayer<PianoRoll> self;
    private PianoRoll p;
    private int barPosition;
    private int barWidth;
    private Color barColor;
    private BarStyle barStyle;
    private Timer timer;
    private UpdateRate updateRate;
    private EventListenerList listenerList;
    private List<Note> cachedNotes;
    private JScrollPane scrollPane;
    private boolean syncScrollPane;

    public enum BarStyle {
        Loop,
        PlayOneShot
    }

    public PianoRollLayerUI() {
        this.barPosition = 0;
        this.barWidth = 1;
        this.barColor = Color.GRAY;
        this.barStyle = BarStyle.PlayOneShot;
        this.timer = new Timer(0, this::onTime);
        this.listenerList = new EventListenerList();
        this.cachedNotes = new ArrayList<>();
    }

    private void onTime(ActionEvent e) {
        self.repaint(new Rectangle(barPosition, 0, barWidth, p.getUI().computeHeight()));
        updateBarPosition(barPosition + 1);
        fireSequenceEvent(new SequenceEvent(this, barPosition - 1, barPosition));
        int index = p.getUI().getRelativeBeatIndex(barPosition);
        List<Note> allNote = new ArrayList<>();
        for (int y = 0; y < p.getUI().computeHeight(); y += p.getBeatHeight()) {
            List<Note> sub = p.getUI().getNotesAt(barPosition, y);
            allNote.addAll(sub);
        }
        List<Note> notes = allNote.stream().distinct().collect(Collectors.toList());
        // ノートを再生
        for (Note note : notes) {
            if (!cachedNotes.contains(note)) {
                fireNotePlay(note, NotePlayEventType.NOTE_ON);
            }
        }
        // ノートを停止
        List<Note> noteOffList = cachedNotes.stream().filter((n) -> !notes.contains(n)).collect(Collectors.toList());
        for (Note note : noteOffList) {
            fireNotePlay(note, NotePlayEventType.NOTE_OFF);
        }
        cachedNotes.removeIf((n) -> !notes.contains(n));
        cachedNotes.addAll(notes);
        cachedNotes = new ArrayList<>(cachedNotes.stream().distinct().collect(Collectors.toList()));
        self.repaint(new Rectangle(barPosition, 0, barWidth, p.getUI().computeHeight()));
        if (barPosition < p.getUI().computeWidth()) {
            return;
        }
        if (this.barStyle == BarStyle.Loop) {
            updateBarPosition(0);
            fireSequenceEvent(new SequenceEvent(this, barPosition, 0));
        } else {
            self.repaint(new Rectangle(barPosition, 0, barWidth, p.getUI().computeHeight()));
            updateBarPosition(0);
            fireSequenceEvent(new SequenceEvent(this, barPosition, 0));
            timer.stop();
            self.repaint(new Rectangle(barPosition, 0, barWidth, p.getUI().computeHeight()));
        }
    }

    private void updateBarPosition(int newBarPosition) {
        this.barPosition = newBarPosition;
        if (scrollPane == null || !isSyncScrollPane()) {
            return;
        }
        JViewport vp = scrollPane.getViewport();
        Rectangle viewRect = vp.getViewRect();
        Point vpos = vp.getViewPosition();
        vpos.x = Math.max(newBarPosition - (viewRect.width / 2), 0);
        vp.setViewPosition(vpos);
    }

    private void fireNotePlay(Note note, NotePlayEventType type) {
        NotePlayEvent e = new NotePlayEvent(this, note, type);
        for (NotePlayListener listener : listenerList.getListeners(NotePlayListener.class)) {
            listener.notePlay(e);
        }
    }

    public void addNotePlayListener(NotePlayListener listener) {
        listenerList.add(NotePlayListener.class, listener);
    }

    public void removeNotePlayListener(NotePlayListener listener) {
        listenerList.remove(NotePlayListener.class, listener);
    }

    public void addSequenceListener(SequenceListener listener) {
        listenerList.add(SequenceListener.class, listener);
    }

    public void removeSequenceListener(SequenceListener listener) {
        listenerList.remove(SequenceListener.class, listener);
    }

    private void fireSequenceEvent(SequenceEvent e) {
        for (SequenceListener listener : listenerList.getListeners(SequenceListener.class)) {
            listener.sequenceUpdate(e);
        }
    }

    /**
     * シーケンスの位置を設定します.
     *
     * @param pos
     */
    public void setSequencePosition(int pos) {
        self.repaint(new Rectangle(barPosition, 0, barWidth, p.getUI().computeHeight()));
        int a = this.barPosition;
        updateBarPosition(pos);
        fireSequenceEvent(new SequenceEvent(this, a, pos));
        self.repaint(new Rectangle(barPosition, 0, barWidth, p.getUI().computeHeight()));
    }

    /**
     * シーケンスの位置を返します.
     *
     * @return
     */
    public int getSequencePosition() {
        return this.barPosition;
    }

    /**
     * シーケンスの更新レートを設定します.
     *
     * @param updateRate
     */
    public void setSequenceUpdateRate(UpdateRate updateRate) {
        int bw = p.getBeatWidth();
        int delay = updateRate.computeTimerDelay(bw);
        timer.setInitialDelay(0);
        timer.setDelay(delay);
        this.updateRate = updateRate;
    }

    /**
     * シーケンスの更新レートを返します.
     *
     * @return
     */
    public UpdateRate getSequenceUpdateRate() {
        return updateRate;
    }

    /**
     * シーケンスの再生を開始します.
     */
    public void playSequence() {
        timer.setRepeats(true);
        timer.setInitialDelay(0);
        timer.start();
    }

    /**
     * シーケンスを停止します.
     */
    public void stopSequence() {
        timer.stop();
    }

    public PianoRoll getPianoRoll() {
        return p;
    }

    public void setBarStyle(BarStyle barStyle) {
        this.barStyle = barStyle;
    }

    public BarStyle getBarStyle() {
        return barStyle;
    }

    /**
     * シーケンサの移動と同期するスクロールペインを設定します.
     *
     * @param scrollPane
     */
    protected void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    /**
     * シーケンサの移動と同期するスクロールペインを返します.
     *
     * @return
     */
    protected JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setSyncScrollPane(boolean syncScrollPane) {
        this.syncScrollPane = syncScrollPane;
    }

    public boolean isSyncScrollPane() {
        return syncScrollPane;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        Graphics2D g2 = (Graphics2D) g;
        Color color = g2.getColor();
        g2.setColor(Color.red);
        g2.fillRect(barPosition, 0, barWidth, p.getUI().computeHeight());
        g2.setColor(color);
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        ((JLayer) c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK);
        this.p = (this.self = ((JLayer<PianoRoll>) c)).getView();
        setSequenceUpdateRate(UpdateRate.bpmToUpdateRate(480, 60));
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        ((JLayer) c).setLayerEventMask(0);
        this.p = null;
    }
}
