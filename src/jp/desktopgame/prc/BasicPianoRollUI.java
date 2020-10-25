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
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * PianoRollUIの基本L&F実装です.
 *
 * @author desktopgame
 */
public class BasicPianoRollUI extends PianoRollUI {

    private PianoRoll p;
    private PropertyChangeHandler propHandler;
    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;
    private PianoRollModelHandler pianoRollModelHandler;
    private OnionSkinHandler onionSkinHandler;
    private NoteDragManager noteDragManager;
    private NoteResizeManager noteResizeManager;
    private RectangleSelectManager rectSelectManager;
    private int cursorX;
    private int cursorY;
    private List<Rectangle> ghostRects;
    private Rectangle highlightKey;

    public BasicPianoRollUI() {
        this.propHandler = new PropertyChangeHandler();
        this.keyHandler = new KeyHandler();
        this.mouseHandler = new MouseHandler();
        this.onionSkinHandler = new OnionSkinHandler();
        this.pianoRollModelHandler = new PianoRollModelHandler();
        this.noteDragManager = createNoteDragManager();
        this.noteResizeManager = createNoteResizeManager();
        this.rectSelectManager = new RectangleSelectManager(this);
        this.ghostRects = new ArrayList<>();
        this.highlightKey = new Rectangle();
    }

    protected NoteDragManager createNoteDragManager() {
        return new DefaultNoteDragManager(this);
    }

    protected NoteResizeManager createNoteResizeManager() {
        return new DefaultNoteResizeManager();
    }

    @Override
    public void installUI(JComponent c) {
        this.p = (PianoRoll) c;
        p.addPropertyChangeListener(propHandler);
        p.addKeyListener(keyHandler);
        p.addMouseMotionListener(mouseHandler);
        p.addMouseListener(mouseHandler);
        p.getModel().addPianoRollModelListener(pianoRollModelHandler);
        updatePrefSize();
    }

    @Override
    public void uninstallUI(JComponent c) {
        p.removePropertyChangeListener(propHandler);
        p.removeKeyListener(keyHandler);
        p.removeMouseMotionListener(mouseHandler);
        p.removeMouseListener(mouseHandler);
        p.getModel().removePianoRollModelListener(pianoRollModelHandler);
        this.p = null;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        paintImpl((Graphics2D) g);
    }

    //
    // 譜面の編集
    //
    @Override
    public int computeWidth() {
        PianoRollModel pModel = p.getModel();
        if (pModel == null) {
            return 0;
        }
        return computeWidth(pModel.getKey(0).getMeasureCount());
    }

    @Override
    public int computeHeight() {
        PianoRollModel pModel = p.getModel();
        if (pModel == null) {
            return 0;
        }
        return computeHeight(pModel.getKeyCount());
    }

    private int computeWidth(int measureCount) {
        PianoRollModel pModel = p.getModel();
        if (pModel == null) {
            return 0;
        }
        int mc = measureCount;
        int bc = pModel.getKey(0).getMeasure(0).getBeatCount();
        int w = (bc * p.getBeatWidth()) * mc;
        return w;
    }

    private int computeHeight(int keyCount) {
        PianoRollModel pModel = p.getModel();
        if (pModel == null) {
            return 0;
        }
        int h = keyCount * p.getBeatHeight();
        return h;
    }

    private void updatePrefSize() {
        p.setPreferredSize(new Dimension(computeWidth(), computeHeight()));
        Container parent = SwingUtilities.getUnwrappedParent(p);
        if (parent != null) {
            parent.revalidate();
        }
    }

    private Optional<Key> getKeyAt(int y) {
        PianoRollModel pModel = p.getModel();
        int i = y / p.getBeatHeight();
        if (i < 0 || i >= pModel.getKeyCount()) {
            return Optional.empty();
        }
        return Optional.of(pModel.getKey(i));
    }

    private OptionalInt getMeasureIndexAt(int x) {
        int i = x / (p.getBeatWidth() * p.getModel().getKey(0).getMeasure(0).getBeatCount());
        if (i < 0 || i >= p.getModel().getKey(0).getMeasureCount()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(i);
    }

    @Override
    public Optional<Measure> getMeasureAt(int x, int y) {
        OptionalInt optI = getMeasureIndexAt(x);
        if (optI.isPresent()) {
            return getKeyAt(y).map((e) -> e.getMeasure(optI.getAsInt()));
        }
        return Optional.empty();
    }

    @Override
    public List<Note> getNotesAt(int x, int y) {
        ArrayList<Note> notes = new ArrayList<>();
        getKeyAt(y).ifPresent((Key e) -> {
            for (int i = 0; i < e.getMeasureCount(); i++) {
                Measure m = e.getMeasure(i);
                int measureOffset = (p.getBeatWidth() * m.getBeatCount()) * i;
                for (int j = 0; j < m.getBeatCount(); j++) {
                    Beat b = m.getBeat(j);
                    int beatOffset = measureOffset + (p.getBeatWidth() * j);
                    for (int k = 0; k < b.getNoteCount(); k++) {
                        Note n = b.getNote(k);
                        int sx = beatOffset + n.getOffset();
                        int ex = sx + n.scaledLength(p.getBeatWidth());
                        if (x >= sx && x < ex) {
                            notes.add(n);
                        }
                    }
                }
            }
        });
        return notes;
    }

    @Override
    public int getRelativeBeatIndex(int x) {
        return (x % (p.getBeatWidth() * p.getModel().getKey(0).getMeasure(0).getBeatCount())) / p.getBeatWidth();
    }

    @Override
    public int measureIndexToXOffset(int i) {
        PianoRollModel pModel = p.getModel();
        return (pModel.getKey(0).getMeasure(0).getBeatCount() * p.getBeatWidth()) * i;
    }

    private int getMeasureWidth() {
        PianoRollModel pModel = p.getModel();
        return (pModel.getKey(0).getMeasure(0).getBeatCount() * p.getBeatWidth());
    }

    private int getKeyWidth() {
        PianoRollModel pModel = p.getModel();
        return (getMeasureWidth() * pModel.getKey(0).getMeasureCount());
    }

    private int relativeBeatIndexToXOffset(int i) {
        return i * p.getBeatWidth();
    }

    private Optional<Note> generateNoteAt(int x, int y, int offset, float length) {
        int section = p.getBeatWidth() / p.getBeatSplitCount();
        for (int i = 0; i < p.getBeatSplitCount(); i++) {
            if (nearEq(section * i, offset, p.getSnapLimit())) {
                offset = section * i;
                break;
            }
        }
        final int offsetA = offset;
        return this.getMeasureAt(x, y).map((Measure e) -> {
            int i = this.getRelativeBeatIndex(x);
            return e.getBeat(i).generateNote(offsetA, length);
        });
    }

    private Optional<Note> generateOrRemoveAt(MouseEvent e, float length) {
        List<Note> notes = BasicPianoRollUI.this.getNotesAt(e.getX(), e.getY());
        if (notes.isEmpty()) {
            OptionalInt optI = BasicPianoRollUI.this.getMeasureIndexAt(e.getX());
            int rbi = BasicPianoRollUI.this.getRelativeBeatIndex(e.getX());
            int xOffset = BasicPianoRollUI.this.measureIndexToXOffset(optI.getAsInt()) + BasicPianoRollUI.this.relativeBeatIndexToXOffset(rbi);
            if ((e.getX() - xOffset) < 5) {
                xOffset = e.getX();
            }
            if (optI.isPresent()) {
                return BasicPianoRollUI.this.generateNoteAt(e.getX(), e.getY(), e.getX() - xOffset, length);
            } else {
                return BasicPianoRollUI.this.generateNoteAt(e.getX(), e.getY(), 0, length);
            }
        } else {
            notes.forEach((child) -> child.removeFromBeat());
        }
        return Optional.empty();
    }

    @Override
    public Rectangle getNoteRect(Note note) {
        return getNoteRect(note, note.getOffset(), note.getLength());
    }

    private Rectangle getNoteRect(Note note, int offset, float length) {
        Beat beat = note.getBeat();
        Measure measure = beat.getMeasure();
        Key key = measure.getKey();
        int xOffset = measureIndexToXOffset(measure.getIndex());
        xOffset += p.getBeatWidth() * beat.getIndex();
        xOffset += offset;
        int yOffset = p.getBeatHeight() * (p.getModel().getKeyCount() - key.getIndex() + 1);
        yOffset = (p.getBeatHeight() * p.getModel().getKeyCount()) - yOffset;
        int width = (int) Math.round(length * (float) p.getBeatWidth());
        int height = p.getBeatHeight();
        return new Rectangle(xOffset, yOffset, width, height);
    }

    @Override
    public PianoRoll getPianoRoll() {
        return p;
    }

    @Override
    public NoteDragManager getNoteDragManager() {
        return noteDragManager;
    }

    @Override
    public NoteResizeManager getNoteResizeManager() {
        return noteResizeManager;
    }

    private boolean isNoteResizablePosLeft(Note note, int x) {
        Rectangle rect = getNoteRect(note);
        int leftSx = rect.x;
        int leftEx = rect.x + 5;
        return (x >= leftSx && x < leftEx);
    }

    private boolean isNoteResizablePosRight(Note note, int x) {
        Rectangle rect = getNoteRect(note);
        int rightSx = rect.x + rect.width - 5;
        int rightEx = rect.x + rect.width;
        return (x >= rightSx && x < rightEx);
    }

    //
    // 描画
    //
    private void paintImpl(Graphics2D g2) {
        PianoRollModel pModel = p.getModel();
        final int BW = p.getBeatWidth();
        final int BH = p.getBeatHeight();
        Rectangle clipRect = g2.getClipBounds();
        int startKey = clipRect.y;
        int endKey = (clipRect.y + clipRect.height);
        int y = 0;
        Color c = g2.getColor();
        int index = 0;
        drawBackground(g2);
        for (int i = pModel.getKeyCount() - 1; i >= 0; i--) {
            if (y < startKey || y >= endKey) {
                int nextY = y + BH;
                y = nextY;
                index++;
                continue;
            }
            int nextY = y + BH;
            drawNotes(g2, pModel.getKey(index), y, nextY, false, null);
            y = nextY;
            index++;
        }
        drawNoteGhost(g2);
        g2.setColor(Color.blue);
        Rectangle selectArea = rectSelectManager.getAreaRect();
        g2.draw(selectArea);
        drawOnionSkin(g2);
        g2.setColor(c);
    }

    private void repaintNote(Note note) {
        Rectangle r = getNoteRect(note);
        r.x -= 1;
        r.y -= 1;
        r.width += 2;
        r.height += 2;
        p.repaint(r);
    }

    private void repaintGhostRects() {
        for (Rectangle rect : ghostRects) {
            p.repaint(rect);
        }
        ghostRects.clear();
    }

    private boolean shouldHeighlightLine(int x) {
        return nearEq(x, cursorX, p.getSnapLimit());
    }

    private boolean nearEq(int a, int b, int limit) {
        return Math.abs(Math.abs(a) - Math.abs(b)) < limit;
    }

    protected void drawBackground(Graphics2D g2) {
        PianoRollModel pModel = p.getModel();
        final int BW = p.getBeatWidth();
        final int BH = p.getBeatHeight();
        final int CW = computeWidth();
        Rectangle clipRect = g2.getClipBounds();
        int startKey = clipRect.y;
        int endKey = (clipRect.y + clipRect.height);
        int index = 0;
        int y = 0;
        for (int i = pModel.getKeyCount() - 1; i >= 0; i--) {
            if (y < startKey || y >= endKey) {
                int nextY = y + BH;
                y = nextY;
                index++;
                continue;
            }
            int nextY = y + BH;
            int indexInBwTable = i % Keyboard.BLACK_WHITE_TABLE.length;
            if (Keyboard.BLACK_WHITE_TABLE[indexInBwTable] == Key.BLACK) {
                g2.setColor(Color.darkGray);
            } else {
                g2.setColor(Color.lightGray);
            }
            if (!noteDragManager.getTargets().isEmpty() && cursorY >= y && cursorY < nextY) {
                g2.setColor(Color.CYAN);
                highlightKey.x = 0;
                highlightKey.y = y;
                highlightKey.width = CW;
                highlightKey.height = nextY - y;
            }
            g2.fillRect(0, y, CW, nextY - y);
            drawKey(g2, pModel.getKey(index), y, nextY);
            g2.setColor(Color.black);
            g2.drawLine(0, y, getKeyWidth(), y);
            y = nextY;
            index++;
        }
        g2.setColor(Color.black);
        g2.drawLine(0, computeHeight(), computeWidth(), computeHeight());
        g2.drawLine(computeWidth(), 0, computeWidth(), computeHeight());
    }

    protected void drawKey(Graphics2D g2, Key key, int topY, int bottomY) {
        final int BW = p.getBeatWidth();
        final int BH = p.getBeatHeight();
        Rectangle clipRect = g2.getClipBounds();
        int startX = clipRect.x;
        int endX = (clipRect.x + clipRect.width);
        int mx = 0;
        for (int j = 0; j < key.getMeasureCount(); j++) {
            Measure m = key.getMeasure(j);
            int bx = (BW * m.getBeatCount()) * j;
            if ((bx + (m.getBeatCount() * p.getBeatWidth()) < startX) || (bx >= endX)) {
                int nextMx = mx + m.getBeatCount() * BW;
                g2.setColor(shouldHeighlightLine(mx) ? Color.magenta : Color.yellow);
                g2.drawLine(mx, topY, mx, bottomY);
                mx = nextMx;
                continue;
            }
            for (int k = 0; k < m.getBeatCount(); k++) {
                int nextBx = bx + BW;
                for (int L = 0; L < p.getBeatSplitCount(); L++) {
                    int lineX = bx + (L * (p.getBeatWidth() / p.getBeatSplitCount()));
                    g2.setColor(shouldHeighlightLine(lineX) ? Color.yellow : new Color(105, 112, 112));
                    g2.drawLine(lineX, topY, lineX, bottomY);
                }
                g2.setColor(shouldHeighlightLine(bx) ? Color.yellow : Color.white);
                g2.drawLine(bx, topY, bx, bottomY);
                bx = nextBx;
            }
            int nextMx = mx + m.getBeatCount() * BW;
            g2.setColor(shouldHeighlightLine(mx) ? Color.magenta : Color.yellow);
            g2.drawLine(mx, topY, mx, bottomY);
            mx = nextMx;
        }
    }

    protected void drawNotes(Graphics2D g2, Key key, int topY, int bottomY, boolean onionSkin, Color onionSkinColor) {
        final int BW = p.getBeatWidth();
        final int BH = p.getBeatHeight();
        for (int j = 0; j < key.getMeasureCount(); j++) {
            Measure m = key.getMeasure(j);
            int bx = (BW * m.getBeatCount()) * j;
            for (int k = 0; k < m.getBeatCount(); k++) {
                Beat beat = m.getBeat(k);
                int nextBx = bx + BW;
                for (int L = 0; L < beat.getNoteCount(); L++) {
                    Note note = beat.getNote(L);
                    drawNote(g2, note, getNoteRect(note), onionSkin, onionSkinColor);
                }
                bx = nextBx;
            }
        }
    }

    protected void drawNote(Graphics2D g2, Note note, Rectangle rect, boolean onionSkin, Color onionSkinColor) {
        if (!onionSkin) {
            g2.setColor(note.isSelected() ? Color.cyan : Color.pink);
        } else {
            g2.setColor(onionSkinColor);
        }
        g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        if (!onionSkin) {
            g2.setColor(Color.gray);
            g2.drawRect(rect.x, rect.y, rect.width, rect.height);
            g2.setColor(Color.black);
            g2.drawRect(rect.x + 1, rect.y + 1, rect.width - 1, rect.height - 1);
        }
    }

    private void drawNoteGhost(Graphics2D g2) {
        int diffX = (noteDragManager.getCurrentX() - noteDragManager.getBaseX());
        int diffY = (noteDragManager.getCurrentY() - noteDragManager.getBaseY());
        ghostRects.clear();
        for (Note note : noteDragManager.getTargets()) {
            Rectangle rect = getNoteRect(note);
            rect.x += diffX;
            rect.y += diffY;
            drawNoteGhost(g2, note, rect);
            ghostRects.add(rect);
        }
    }

    protected void drawNoteGhost(Graphics2D g2, Note note, Rectangle rect) {
        g2.setColor(Color.gray);
        g2.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    private void drawOnionSkin(Graphics2D g2) {
        Optional<PianoRollGroup> groupOpt = p.getGroup();
        if (!groupOpt.isPresent()) {
            return;
        }
        PianoRollGroup group = groupOpt.get();
        for (int i = 0; i < group.getPianoRollCount(); i++) {
            PianoRoll e = group.getPianoRoll(i);
            if (e == this.p) {
                continue;
            }
            drawOnionSkin(g2, group, e);
        }
    }

    private void drawOnionSkin(Graphics2D g2, PianoRollGroup group, PianoRoll otherP) {
        int index = 0;
        int y = 0;
        final int BH = otherP.getBeatHeight();
        PianoRollModel pModel = otherP.getModel();
        PianoRollModel originPModel = p.getModel();
        // モデルの構造が異なると、オニオンスキンを描画できない
        if (pModel.getKeyCount() != originPModel.getKeyCount()) {
            return;
        }
        for (int i = pModel.getKeyCount() - 1; i >= 0; i--) {
            int nextY = y + BH;
            drawNotes(g2, pModel.getKey(index), y, nextY, true, group.getSkinColor(otherP));
            y = nextY;
            index++;
        }
    }

    //
    // イベントハンドラ
    //
    private class PropertyChangeHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String k = evt.getPropertyName();
            Object o = evt.getOldValue();
            Object n = evt.getNewValue();
            if (k.equals("model")) {
                ((PianoRollModel) o).removePianoRollModelListener(pianoRollModelHandler);
                ((PianoRollModel) n).addPianoRollModelListener(pianoRollModelHandler);
                updatePrefSize();
            } else if (k.equals("visibleKeyCount")) {
                updatePrefSize();
            } else if (k.equals("visibleMeasureCount")) {
                updatePrefSize();
            } else if (k.equals("beatWidth") || k.equals("beatHeight") || k.equals("beatSplitCount") || k.equals("snapLimit")) {
                updatePrefSize();
                p.repaint();
            } else if (k.equals("group")) {
                if (o != null) {
                    ((PianoRollGroup) o).removeChangeListener(onionSkinHandler);
                }
                if (n != null) {
                    ((PianoRollGroup) n).addChangeListener(onionSkinHandler);
                }
                p.repaint();
            }
        }
    }

    private class KeyHandler extends KeyAdapter {

        @Override
        public void keyPressed(java.awt.event.KeyEvent e) {
        }

        @Override
        public void keyReleased(java.awt.event.KeyEvent e) {

        }
    }

    private class MouseHandler extends MouseAdapter {

        private DefaultNoteResizeManager.Type resizeType;
        private boolean rectSelect;

        public MouseHandler() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!p.isEditable()) {
                return;
            }
            if (e.getClickCount() == 2) {
                generateOrRemoveAt(e, 0.25f);
                p.getModel().clearAllSelection();
            } else {
                List<Note> notes = BasicPianoRollUI.this.getNotesAt(e.getX(), e.getY());
                boolean shiftMask = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
                if (shiftMask) {
                    p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    notes.stream().forEach((n) -> n.setSelected(!n.isSelected()));
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e
        ) {
            if (!p.isEditable()) {
                return;
            }
            if (e.getClickCount() > 1) {
                return;
            }
            List<Note> notes = BasicPianoRollUI.this.getNotesAt(e.getX(), e.getY());
            boolean shiftMask = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
            if (shiftMask || rectSelect) {
                if (!rectSelect && noteDragManager.getTargets().isEmpty() && noteResizeManager.getTargets().isEmpty()) {
                    rectSelectManager.start(e.getX(), e.getY());
                    rectSelect = true;
                }
                return;
            }
            if (rectSelect) {
                return;
            }

            if (notes.stream().anyMatch((n) -> n.isSelected())) {
                if (noteResizeManager.getTargets().isEmpty()) {

                    noteDragManager.touch(p.getModel().getSelectedNotes());
                    noteDragManager.start(e.getX(), e.getY());
                } else {
                    noteResizeManager.touch(p.getModel().getSelectedNotes());
                    noteResizeManager.start(resizeType, e.getX());
                }
            } else {
                if (noteResizeManager.getTargets().isEmpty()) {
                    noteDragManager.touch(BasicPianoRollUI.this.getNotesAt(e.getX(), e.getY()));
                    noteDragManager.start(e.getX(), e.getY());
                } else {
                    noteResizeManager.start(resizeType, e.getX());
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e
        ) {
            if (!p.isEditable()) {
                return;
            }
            if (rectSelect) {
                repaintSelectArea();
                rectSelectManager.stop();
                rectSelect = false;
                return;
            }
            if (noteResizeManager.hasFocus()) {
                noteResizeManager.stop();
                p.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            if (noteDragManager.hasFocus()) {
                p.repaint(highlightKey);
                highlightKey = new Rectangle();
                noteDragManager.stop();
                p.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e
        ) {
        }

        @Override
        public void mouseExited(MouseEvent e
        ) {
        }

        private void repaintSelectArea() {
            Rectangle rect = rectSelectManager.getAreaRect();
            rect.x -= 1;
            rect.y -= 1;
            rect.width += 2;
            rect.height += 2;
            p.repaint(rect);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!p.isEditable()) {
                return;
            }
            if (rectSelect) {
                repaintSelectArea();
                rectSelectManager.move(e.getX(), e.getY());
                repaintSelectArea();
                return;
            }
            if (noteDragManager.hasFocus()) {
                noteDragManager.move(e.getX(), e.getY());
                repaintGhostRects();
                p.repaint(highlightKey);
            } else if (noteResizeManager.hasFocus()) {
                noteResizeManager.resize(e.getX(), p.getBeatWidth());
            }
            updateCursorPos(e);
        }

        @Override
        public void mouseMoved(MouseEvent e
        ) {
            if (!p.isEditable()) {
                return;
            }
            if (noteDragManager.hasFocus()) {
                noteDragManager.move(e.getX(), e.getY());
                p.repaint();
            } else if (noteResizeManager.hasFocus()) {
                noteResizeManager.resize(e.getX(), p.getBeatWidth());
            } else {
                updateCursor(e);
            }
            updateCursorPos(e);
        }

        private int oldCursorX;
        private int oldCursorY;

        private void updateCursorPos(MouseEvent e) {
            p.repaint(new Rectangle(oldCursorX - 5, 0, 10, computeHeight()));
            cursorX = e.getX();
            cursorY = e.getY();
            p.repaint(new Rectangle(cursorX - 5, 0, 10, computeHeight()));
            this.oldCursorX = cursorX;
            this.oldCursorY = cursorY;
        }

        private void updateCursor(MouseEvent e) {
            if (noteResizeManager.hasFocus()) {
                return;
            }
            List<Note> notes = BasicPianoRollUI.this.getNotesAt(e.getX(), e.getY());
            boolean selected = notes.stream().anyMatch((n) -> n.isSelected());
            noteResizeManager.clear();
            if (!notes.isEmpty()) {
                if (notes.stream().anyMatch((n) -> isNoteResizablePosLeft(n, e.getX()))) {
                    this.resizeType = DefaultNoteResizeManager.Type.Move;
                    p.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                    noteResizeManager.touch(notes.stream().filter((n) -> isNoteResizablePosLeft(n, e.getX())).collect(Collectors.toList()));
                } else if (notes.stream().anyMatch((n) -> isNoteResizablePosRight(n, e.getX()))) {
                    this.resizeType = DefaultNoteResizeManager.Type.Resize;
                    p.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                    noteResizeManager.touch(notes.stream().filter((n) -> isNoteResizablePosRight(n, e.getX())).collect(Collectors.toList()));
                } else {
                    p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            } else {
                p.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    private class PianoRollModelHandler implements PianoRollModelListener {

        @Override
        public void pianoRollModelUpdate(PianoRollModelEvent e) {
            Optional<NoteEvent> neOpt = e.getNoteEvent();
            Optional<BeatEvent> beOpt = e.getBeatEvent();
            Optional<MeasureEvent> meOpt = e.getMeasureEvent();
            Optional<KeyEvent> keOpt = e.getInnerEvent();
            if (neOpt.isPresent()) {
                NoteEvent ne = neOpt.get();
                Note note = ne.getSource();
                if (ne.getType() == NoteEventType.OFFSET_CHANGE) {
                    p.repaint(getNoteRect(note, (int) ne.getOldValue().get(), note.getLength()));
                } else if (ne.getType() == NoteEventType.LENGTH_CHANGE) {
                    p.repaint(getNoteRect(note, note.getOffset(), (float) ne.getOldValue().get()));
                }
                repaintNote(note);
                repaintGhostRects();
            } else if (beOpt.isPresent()) {
                BeatEvent be = beOpt.get();
                if (be.getBeatEventType() == BeatEventType.NOTE_CREATED) {
                    repaintNote(be.getNote());
                    repaintGhostRects();
                } else {
                    p.repaint();
                }
            } else if (meOpt.isPresent()) {
                MeasureEvent me = meOpt.get();
                if (me.getType() == MeasureEventType.BEAT_CREATED || me.getType() == MeasureEventType.BEAT_REMOVED) {
                    updatePrefSize();
                    p.repaint();
                }
            } else if (keOpt.isPresent()) {
                KeyEvent ke = keOpt.get();
                if (ke.getType() == KeyEventType.MEASURE_CREATED || ke.getType() == KeyEventType.MEASURE_REMOVED) {
                    updatePrefSize();
                    p.repaint();
                }
            } else {
                if (e.getType() == PianoRollModelEventType.KEY_CREATED || e.getType() == PianoRollModelEventType.KEY_REMOVED) {
                    updatePrefSize();
                    p.repaint();
                } else {
                    p.repaint();
                }
            }
        }
    }

    private class OnionSkinHandler implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            p.repaint();
        }
    }
}
