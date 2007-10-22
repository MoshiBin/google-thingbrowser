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
import com.google.thingbrowser.modules.slideshow.util.ViewNode;
import com.google.thingbrowser.modules.slideshow.view.SelectionModel;
import com.google.thingbrowser.modules.slideshow.view.ViewConstants;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.handles.PHandle;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.util.PBoundsLocator;

import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class KeypointThumbnailViewNode extends ViewNode<Keypoint> {

  private PHandle keypointHandle = new PHandle(PBoundsLocator.createSouthLocator(this)) {

    public void dragHandle(PDimension aLocalDimension, PInputEvent aEvent) {
      localToGlobal(aLocalDimension);
      KeypointThumbnailViewNode.this.globalToLocal(aLocalDimension);
      getModel().setTimeOffset(getModel().getTimeOffset() + aLocalDimension.getWidth() / selectionModel.getDurationScaling());
    }
  };

  private final PropertyChangeListener boundsListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      setClipTransform();
    }
  };

  private final PropertyChangeListener selectionListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      setSelected(selectionModel.getSelection() == getModel());
    }
  };

  private final PropertyChangeListener timeOffsetListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      parentBoundsChanged();
    }
  };

  private final PropertyChangeListener filteredImageListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      imageNode.setImage(getModel().getTrackElement().getFilteredImage());
    }
  };

  private final SelectionModel selectionModel;
  private final PPath highlight = PPath.createRectangle(0, 0, 1, 1);
  private final PClip imageBorder = new PClip();
  private final PImage imageNode = new PImage();

  public KeypointThumbnailViewNode(Keypoint model, final SelectionModel selectionModel, boolean editable) {
    super(model);
    this.selectionModel = selectionModel;

    selectionModel.addPropertyChangeListener("selection", selectionListener);

    addInputEventListener(new PBasicInputEventHandler() {
      public void mouseClicked(PInputEvent e) {
        selectionModel.setSelection(getModel());
        e.setHandled(true);
      }
    });

    highlight.setStroke(new BasicStroke((float)ViewConstants.HIGHLIGHT_BORDER_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    highlight.setStrokePaint(ViewConstants.HIGHLIGHT_BORDER_COLOR);
    highlight.setPaint(null);

    addChild(highlight);
    addChild(imageBorder);
    imageBorder.addChild(imageNode);
    imageNode.setImage(getModel().getTrackElement().getFilteredImage());

    imageBorder.setBounds(0, 0, 1, 1);
    imageBorder.setPathToRectangle(1, 1, 1, 1);
    imageBorder.setPaint(ViewConstants.BACKGROUND_COLOR);

    if (editable) {
      addChild(keypointHandle);
    }

    model.addPropertyChangeListener(timeOffsetListener);
    model.addPropertyChangeListener("bounds", boundsListener);
    model.getTrackElement().addPropertyChangeListener("filteredImage", filteredImageListener);

    setBounds(0, 0, ViewConstants.KEYPOINT_THUMBNAIL_SIZE, ViewConstants.KEYPOINT_THUMBNAIL_SIZE);
    setSelected(false);
  }

  public void setSelected(boolean selected) {
    highlight.setTransparency(selected ? 1.0f : 0.0f);
  }

  protected void layoutChildren() {
    double width = getHeight() * ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
    imageBorder.setBounds(
        getX() - width / 2,
        getY(),
        width,
        getHeight());
    imageNode.setX(0);
    imageNode.setY(0);
    highlight.setBounds(
        imageBorder.getX() - ViewConstants.HIGHLIGHT_BORDER_WIDTH,
        imageBorder.getY() - ViewConstants.HIGHLIGHT_BORDER_WIDTH,
        imageBorder.getWidth() + 2 * ViewConstants.HIGHLIGHT_BORDER_WIDTH,
        imageBorder.getHeight() + 2 * ViewConstants.HIGHLIGHT_BORDER_WIDTH);
    setClipTransform();
    keypointHandle.parentBoundsChanged();
  }

  public void dispose() {
    selectionModel.removePropertyChangeListener("selection", selectionListener);
    getModel().removePropertyChangeListener(timeOffsetListener);
    getModel().removePropertyChangeListener("bounds", boundsListener);
    getModel().getTrackElement().removePropertyChangeListener("filteredImage", filteredImageListener);
  }

  private void setClipTransform() {
    AffineTransform t = new AffineTransform();
    double scaleFactorX = imageBorder.getWidth() / getModel().getBoundsReference().getWidth();
    double scaleFactorY = imageBorder.getHeight() / getModel().getBoundsReference().getHeight();
    t.scale(
        scaleFactorX,
        scaleFactorY);
    t.translate(
        imageBorder.getX() / scaleFactorX - getModel().getBoundsReference().getX(),
        imageBorder.getY() / scaleFactorY - getModel().getBoundsReference().getY());
    imageNode.setTransform(t);
  }
}
