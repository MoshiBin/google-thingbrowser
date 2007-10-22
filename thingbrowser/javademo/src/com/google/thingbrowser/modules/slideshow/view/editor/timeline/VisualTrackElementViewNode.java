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

import com.google.thingbrowser.modules.slideshow.model.ComposedMovieFacet;
import com.google.thingbrowser.modules.slideshow.model.Keypoint;
import com.google.thingbrowser.modules.slideshow.model.VisualTrackElement;
import com.google.thingbrowser.modules.slideshow.view.SelectionModel;
import com.google.thingbrowser.modules.slideshow.view.ViewConstants;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class VisualTrackElementViewNode extends TrackElementViewNode<VisualTrackElement> {

  private final PropertyChangeListener intermediariesListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      refresh();
    }
  };

  private final PropertyChangeListener timeOffsetListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      layoutChildren();
    }
  };

  private final Collection<KeypointThumbnailViewNode> thumbnails = new ArrayList<KeypointThumbnailViewNode>();

  public VisualTrackElementViewNode(VisualTrackElement model, final SelectionModel selectionModel) {
    super(model, selectionModel, ViewConstants.VISUAL_TRACK_ELEMENT_COLOR);

    addInputEventListener(new PBasicInputEventHandler() {
      public void mouseClicked(PInputEvent e) {
        if (e.getClickCount() == 2) {
          insertKeypoint(e.getPosition());
          e.setHandled(true);
        }
      }
    });

    for (Keypoint k : model.getIntermediaries()) {
      k.addPropertyChangeListener("timeOffset", timeOffsetListener);
    }

    model.addPropertyChangeListener("intermediaries", intermediariesListener);

    refresh();
  }

  protected void layoutChildren() {
    super.layoutChildren();

    for (KeypointThumbnailViewNode thumbnail : thumbnails) {
      thumbnail.setBounds(
          getX() + thumbnail.getModel().getTimeOffset() * getSelectionModel().getDurationScaling(),
          getY() + ViewConstants.TRACK_ELEMENT_INTERNAL_GAP,
          0,
          getHeight() - 2 * ViewConstants.TRACK_ELEMENT_INTERNAL_GAP);
    }
  }

  protected void refresh() {

    for (KeypointThumbnailViewNode ktv : thumbnails) {
      removeChild(ktv);
      ktv.dispose();
    }

    thumbnails.clear();

    thumbnails.add(new KeypointThumbnailViewNode(getModel().getFirst(), getSelectionModel(), false));

    for (Keypoint k : getModel().getIntermediaries()) {
      thumbnails.add(new KeypointThumbnailViewNode(k, getSelectionModel(), true));
    }

    thumbnails.add(new KeypointThumbnailViewNode(getModel().getLast(), getSelectionModel(), true));

    for (PNode p : thumbnails) {
      addChild(p);
    }
  }

  private void insertKeypoint(Point2D position) {
    Keypoint k = new Keypoint(getModel());
    k.setTimeOffset((position.getX() - getBoundsReference().getX()) / getSelectionModel().getDurationScaling());
    getModel().getIntermediaries().add(k);
    k.addPropertyChangeListener("timeOffset", timeOffsetListener);
    layoutChildren();
  }

  protected double computeEndcapExtension() {
    double extension = getHeight();
    extension -= 2 * ViewConstants.TRACK_ELEMENT_INTERNAL_GAP;
    extension /= 2;
    extension *= ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
    extension += ViewConstants.TRACK_ELEMENT_INTERNAL_GAP;
    return extension;
  }

  public void dispose() {
    for (KeypointThumbnailViewNode ktv : thumbnails) {
      ktv.dispose();
    }

    thumbnails.clear();

    for (Keypoint k : getModel().getIntermediaries()) {
      k.removePropertyChangeListener("timeOffset", timeOffsetListener);
    }

    getModel().removePropertyChangeListener("intermediaries", intermediariesListener);

    super.dispose();
  }
}
