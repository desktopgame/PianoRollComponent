/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.Optional;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * PianoRollはMIDIシーケンサーのノート打ち込みのために使用できるコンポーネントです.
 * <br>
 * ピアノロールの概要<br>
 * このコンポーネントはMIDIの実際のデータ構造と一致するようなモデルを持っています。<br>
 * 格子状のUIを表示しますが、モデルは単なる二次元配列ではなくキー/小節/拍によって構成されています。<br>
 * 再生中のシーケンサーの位置を表すバーについてはこのクラスには実装されていません。<br>
 * PianoRollModelLayerUIを参照してください。<br>
 * <br>
 * ノートの作成、または削除<br>
 * コンポーネントをダブルクリックした時、その場所にノートがなければ作成し、<br>
 * あれば対象のノートを削除します。<br>
 * ノートは拍に紐づいていますが、必ずしも拍の境界に配置されるとは限りません。<br>
 * 拍を基準位置として任意のオフセットを持つことができます。<br>
 * <br>
 * ノートの選択<br>
 * ノートをShiftクリックするか、矩形選択を使用してノートを囲むことでノートが選択状態になります。<br>
 * 選択された全てのノートに対して同時にリサイズ、ドラッグによる移動が実行できます。<br>
 * 選択状態を解除するにはノートを生成するか、Shiftクリックで選択状態のノートをクリックする必要があります。<br>
 * <br>
 * ノートの移動<br>
 * ノートにカーソルを合わせたままドラッグドロップすることで、ノートを任意の場所に移動させることができます。<br>
 * キーや小節をまたいで任意の場所に再配置できます。<br>
 * <br>
 * ノートのリサイズ<br>
 * ノートの右端か左端にカーソルを合わせたままドラッグするとノートの大きさを変えることができます。<br>
 * 左端からリサイズした場合には必要に応じてオフセットも更新されます。<br>
 * <br>
 * Undo/Redoのサポート<br>
 * ノートに対する編集操作はほとんどUndo/Redoに対応しています。<br>
 * 正し、のちに述べるような基準単位の変更が行われた後では正常にUndo/Redoできることが保証されません。<br>
 * このような変更が行われた場合にはUndoManager#discardAllEditsで変更履歴を破棄するべきです。<br>
 * <br>
 * 基準単位の変更<br>
 * このコンポーネントだけでなく、他の多くのMIDIシーケンサーに言えることですが、<br>
 * 基本的に小節や拍の分割単位を一部分だけ変更することはできません。<br>
 * このコンポーネントでは後から分割単位を変更することも可能ですが、必ず全体に対して同じような変更を加える必要があります。<br>
 * また、分割単位が縮小された場合にはいくつかのノートや拍が削除されますが、<br>
 * このような分割単位の変更による削除については変更履歴が保存されません。<br>
 * <br>
 * シーケンサーとの同期<br>
 * UpdateRate, PianoRollLayerUIクラスを参照してください。<br>
 * <br>
 *
 * @author desktopgame
 */
public class PianoRoll extends JComponent implements Scrollable {

    private PianoRollModel model;
    private int beatWidth;
    private int beatHeight;
    private int beatSplitCount;
    private int clickSnapLimit;
    private int dragSnapLimit;
    private boolean editable;
    private Phrase clipboard;
    private Optional<PianoRollGroup> group;

    private static final String uiClassID = "PianoRollUI";

    public PianoRoll() {
        this.group = Optional.empty();
        setEditable(true);
        setBeatSplitCount(4);
        setBeatWidth(96);
        setBeatHeight(24);
        setClickSnapLimit(3);
        setDragSnapLimit(5);
        setModel(new DefaultPianoRollModel(12 * 11, 4, 4));
        updateUI();
    }

    public void setUI(BasicPianoRollUI ui) {
        super.setUI(ui);
    }

    public PianoRollUI getUI() {
        return (PianoRollUI) ui;
    }

    @Override
    public void updateUI() {
        setUI(new BasicPianoRollUI());
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    private void copy(boolean remove) {
        List<Note> selected = getModel().getSelectedNotes();
        this.clipboard = Phrase.createFromNotes(selected);
        if (remove) {
            selected.forEach(Note::removeFromBeat);
        }
    }

    public void cut() {
        copy(true);
    }

    public void copy() {
        copy(false);
    }

    public void copy(Phrase phrase) {
        this.clipboard = new Phrase(phrase.getVirtualNotes());
    }

    public void paste(int offset) {
        if (clipboard == null) {
            return;
        }
        int beatInMeasure = getModel().getKey(0).getMeasure(0).getBeatCount();
        clipboard.expand(getModel(), offset, beatInMeasure, getBeatWidth());
        getModel().clearAllSelection();
        this.clipboard = null;
    }

    /**
     * ピアノロールを指定のモデルに関連づけます.
     *
     * @param model
     */
    public void setModel(PianoRollModel model) {
        PianoRollModel m = this.model;
        this.model = model;
        this.firePropertyChange("model", m, model);
    }

    /**
     * ピアノロールに関連したモデルを取り出します.
     *
     * @return
     */
    public PianoRollModel getModel() {
        return model;
    }

    /**
     * 一拍あたりの幅を設定します.
     *
     * @param beatWidth
     */
    public void setBeatWidth(int beatWidth) {
        int bw = this.beatWidth;
        this.beatWidth = beatWidth;
        this.firePropertyChange("beatWidth", bw, beatWidth);
    }

    /**
     * 一拍あたりの幅を返します.
     *
     * @return
     */
    public int getBeatWidth() {
        return beatWidth;
    }

    /**
     * 一拍あたりの高さを設定します.
     *
     * @param beatHeight
     */
    public void setBeatHeight(int beatHeight) {
        int bh = this.beatHeight;
        this.beatHeight = beatHeight;
        this.firePropertyChange("beatHeight", bh, beatHeight);
    }

    /**
     * 一拍あたりの高さを返します.
     *
     * @return
     */
    public int getBeatHeight() {
        return beatHeight;
    }

    /**
     * 一拍あたりの分割数を設定します.
     *
     * @param beatSplitCount
     */
    public void setBeatSplitCount(int beatSplitCount) {
        int bh = this.beatSplitCount;
        this.beatSplitCount = beatSplitCount;
        this.firePropertyChange("beatSplitCount", bh, beatHeight);
    }

    /**
     * 一拍あたりの分割数を返します.
     *
     * @return
     */
    public int getBeatSplitCount() {
        return beatSplitCount;
    }

    /**
     * スナップが起動する範囲を設定します.
     *
     * @param clickSnapLimit
     */
    public void setClickSnapLimit(int clickSnapLimit) {
        int sl = this.clickSnapLimit;
        this.clickSnapLimit = clickSnapLimit;
        this.firePropertyChange("clickSnapLimit", sl, this.clickSnapLimit);
    }

    /**
     * スナップが起動する範囲を返します.
     *
     * @return
     */
    public int getClickSnapLimit() {
        return this.clickSnapLimit;
    }

    /**
     * スナップが起動する範囲を設定します.
     * @param dragSnapLimit
     */
    public void setDragSnapLimit(int dragSnapLimit) {
        int a = this.dragSnapLimit;
        this.dragSnapLimit = dragSnapLimit;
        firePropertyChange("dragSnapLimit", a, dragSnapLimit);
    }

    /**
     * スナップが起動する範囲を設定します.
     * @return
     */
    public int getDragSnapLimit() {
        return dragSnapLimit;
    }

    /**
     * 編集可能であるかどうかを設定します.
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        boolean a = this.editable;
        this.editable = editable;
        firePropertyChange("editable", a, editable);
    }

    /**
     * 編集可能であるかどうかを返します.
     *
     * @return
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * このピアノロールが所属するグループを設定します. グループに含まれているピアノロールは互いにオニオンスキンを表示できます。
     *
     * @param group
     */
    public void setGroup(PianoRollGroup group) {
        Optional<PianoRollGroup> g = this.group;
        this.group = Optional.ofNullable(group);
        if (g.isPresent()) {
            firePropertyChange("group", g.get(), group);
        } else {
            firePropertyChange("group", null, group);
        }
    }

    /**
     * このピアノロールが所属するグループを返します.
     *
     * @return
     */
    public Optional<PianoRollGroup> getGroup() {
        return group;
    }

    //
    // Scrollable
    //
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return this.getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        switch (orientation) {
            case SwingConstants.VERTICAL:
                return this.getBeatHeight();
            case SwingConstants.HORIZONTAL:
                return this.getBeatWidth();
            default:
                throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        switch (orientation) {
            case SwingConstants.VERTICAL:
                return visibleRect.height;
            case SwingConstants.HORIZONTAL:
                return visibleRect.width;
            default:
                throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        Container parent = SwingUtilities.getUnwrappedParent(this);
        if (parent instanceof JViewport) {
            return parent.getWidth() > getPreferredSize().width;
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        Container parent = SwingUtilities.getUnwrappedParent(this);
        if (parent instanceof JViewport) {
            return parent.getHeight() > getPreferredSize().height;
        }
        return false;
    }

}
