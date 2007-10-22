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

package com.google.thingbrowser.modules.slideshow.view.editor.timeline;

import com.google.thingbrowser.modules.slideshow.model.Track;
import com.google.thingbrowser.modules.slideshow.model.TrackElement;
import com.google.thingbrowser.modules.slideshow.util.ViewNode;
import com.google.thingbrowser.modules.slideshow.view.SelectionModel;
import com.google.thingbrowser.modules.slideshow.view.ViewConstants;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public abstract class TrackViewNode<T extends TrackElement> extends ViewNode<Track<T>> {

  private final PropertyChangeListener durationListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      layoutChildren();
    }
  };

  private final PropertyChangeListener elementsListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      refresh();
    }
  };

  private final SelectionModel selectionModel;
  private final List<TrackElementViewNode<T>> views = new ArrayList<TrackElementViewNode<T>>();
  private final List<TrackElementDropTargetNode<T>> dropTargets = new ArrayList<TrackElementDropTargetNode<T>>();

  public TrackViewNode(Track<T> model, SelectionModel selectionModel) {
    super(model);
    this.selectionModel = selectionModel;
    model.addPropertyChangeListener("elements", elementsListener);
    setPaint(Color.lightGray);
    refresh();
  }

  protected void layoutChildren() {

    double trackElementHeight = (getHeight() - ViewConstants.TRACK_DROP_GUTTER - ViewConstants.TRACK_STAGGER_GAP) / 2;

    double startLocation = 0;
    int i = 0;

    for ( ; i < views.size(); i++) {

      views.get(i).setBounds(
          getX() + startLocation,
          getY() + (i % 2) * (trackElementHeight + ViewConstants.TRACK_STAGGER_GAP),
          selectionModel.getDurationScaling() * views.get(i).getModel().getDuration(),
          trackElementHeight);

      dropTargets.get(i).setBounds(
          getX() + startLocation,
          getY(),
          0,
          getHeight());

      startLocation += views.get(i).getBoundsReference().getWidth();
    }

    dropTargets.get(i).setBounds(
        getX() + startLocation,
        getY(),
        0,
        getHeight());
  }

  public void dispose() {
    getModel().removePropertyChangeListener(elementsListener);
  }

  private void refresh() {

    for (TrackElementViewNode<T> view : views) {
      view.getModel().removePropertyChangeListener("duration", durationListener);
    }

    while (getChildrenCount() > 0) {
       ViewNode<?> n = (ViewNode<?>)getChild(0);
       removeChild(n);
       n.dispose();
    }

    views.clear();
    dropTargets.clear();

    int i = 0;

    for ( ; i < getModel().getElements().size(); i++) {
      getModel().getElements().get(i).addPropertyChangeListener("duration", durationListener);
      views.add(newTrackElementViewNode(getModel().getElements().get(i), selectionModel));
      dropTargets.add(newTrackElementDropTargetNode(getModel(), i));
    }

    dropTargets.add(newTrackElementDropTargetNode(getModel(), i));

    addChildren(dropTargets);
    addChildren(views);
  }

  protected abstract TrackElementViewNode<T> newTrackElementViewNode(T element, SelectionModel selectionModel);

  protected abstract TrackElementDropTargetNode<T> newTrackElementDropTargetNode(Track<T> model, int index);
}
