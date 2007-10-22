// Copyright (C) 2007 Google Inc.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.google.thingbrowser.modules.slideshow.view.editor;

import com.google.thingbrowser.api.ThingContext;
import com.google.thingbrowser.modules.slideshow.model.ComposedMovieFacet;
import com.google.thingbrowser.modules.slideshow.model.Keypoint;
import com.google.thingbrowser.modules.slideshow.model.SoundTrackElement;
import com.google.thingbrowser.modules.slideshow.model.VisualTrackElement;
import com.google.thingbrowser.modules.slideshow.util.ModelObject;
import com.google.thingbrowser.modules.slideshow.util.ViewPanel;
import com.google.thingbrowser.modules.slideshow.view.SelectionModel;
import com.google.thingbrowser.modules.slideshow.view.ViewConstants;
import com.google.thingbrowser.modules.slideshow.view.editor.display.KeypointEditorViewPanel;
import com.google.thingbrowser.modules.slideshow.view.editor.display.SoundTrackElementViewPanel;
import com.google.thingbrowser.modules.slideshow.view.editor.display.VisualTrackElementViewPanel;
import com.google.thingbrowser.modules.slideshow.view.editor.timeline.TimelineView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class EditingViewPanel extends ViewPanel<ComposedMovieFacet> {

  private class NullPanel extends ViewPanel<Object> {

    public NullPanel(ThingContext thingContext) {
      super(thingContext, new Object());
      setBackground(Color.white);
    }

    protected void initializeContents() {}

    public void dispose() {}
  }

  private final JPanel selectionViewPanel = new JPanel();
  private final SelectionModel selectionModel = new SelectionModel();
  private final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
  private ViewPanel<?> selectionView = null;
  private TimelineView timelineEditor = null;

  public EditingViewPanel(ThingContext thingContext, ComposedMovieFacet model) {
    super(thingContext, model);
  }

  protected void initializeContents() {

    selectionViewPanel.setLayout(new GridLayout(1, 1));
    selectionViewPanel.setPreferredSize(new Dimension(500, 500));
    selectionViewPanel.setBackground(Color.white);

    timelineEditor = new TimelineView(getThingContext(), getModel(), selectionModel);

    selectionModel.addPropertyChangeListener("selection", new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        updateSelection();
      }
    });

    splitPane.setLeftComponent(selectionViewPanel);
    splitPane.setRightComponent(timelineEditor);
    splitPane.setDividerSize(ViewConstants.EDITOR_SPLIT_DIVIDER_SIZE);
    splitPane.setBorder(null);
    splitPane.setResizeWeight(0.75);

    setLayout(new GridLayout(1, 1));
    add(splitPane);

    timelineEditor.initialize();

    updateSelection();
  }

  public void dispose() {
    selectionView.dispose();
    timelineEditor.dispose();
  }

  private void updateSelection() {
    if (selectionView != null) selectionView.dispose();
    selectionViewPanel.removeAll();
    selectionView = newSelectionView();
    selectionViewPanel.add(selectionView);
    selectionView.initialize();
  }

  private ViewPanel<?> newSelectionView() {

    ModelObject selection = selectionModel.getSelection();

    if (selection != null) {
      try {
        return new KeypointEditorViewPanel(getThingContext(), (Keypoint)selection);
      } catch (ClassCastException e) {
        // Fall through to next case
      }

      try {
        return new VisualTrackElementViewPanel(getThingContext(), (VisualTrackElement)selection);
      } catch (ClassCastException e) {
        // Fall through to next case
      }

      try {
        return new SoundTrackElementViewPanel(getThingContext(), (SoundTrackElement)selection);
      } catch (ClassCastException e) {
        // Fall through to next case
      }
    }

    // Null or unrecognized selection. Just render an empty panel.
    return new NullPanel(getThingContext());
  }
}
