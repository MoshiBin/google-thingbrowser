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

package com.google.thingbrowser.modules.slideshow.view.editor.display;

import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.google.thingbrowser.modules.slideshow.model.ComposedMovieFacet;
import com.google.thingbrowser.modules.slideshow.model.Keypoint;
import com.google.thingbrowser.modules.slideshow.util.ViewNode;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.handles.PHandle;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.util.PBoundsLocator;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class RegionSelectorNode extends ViewNode<Keypoint> {

  private abstract class ImageEditingHandle extends PHandle {

    protected ImageEditingHandle(PBoundsLocator locator) {
      super(locator);
      setStroke(new PFixedWidthStroke(1));
    }

    protected void paint(PPaintContext pc) {
      double size = DEFAULT_HANDLE_SIZE / pc.getScale();
      setBounds(
          getBoundsReference().getCenterX() - (size / 2),
          getBoundsReference().getCenterY() - (size / 2),
          size,
          size);
      super.paint(pc);
    }

    public void dragHandle(PDimension aLocalDimension, PInputEvent aEvent) {
      Point2D mousePosition = aEvent.getPosition();
      clipRegion.localToGlobal(mousePosition);
      globalToLocal(mousePosition);
      dragHandle(mousePosition);
    }

    protected abstract void dragHandle(Point2D mousePosition);
  }

  private final PropertyChangeListener keypointBoundsListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      setClipRegionBoundsFromModel();
    }
  };

  private final PropertyChangeListener filteredImageListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      imageNode.setImage(getModel().getTrackElement().getFilteredImage());
    }
  };


  private final PNode clipRegion = new PNode();
  private final PClip clipPath = new PClip();
  private final PImage imageNode = new PImage();
  private final Point2D dragOffset = new Point2D.Double();

	public RegionSelectorNode(Keypoint keypoint) {
	  super(keypoint);

    // Lay out the source image node

    imageNode.setImage(getModel().getTrackElement().getFilteredImage());
    imageNode.setX(0);
    imageNode.setY(0);

    // Fix our bounds equal to the source image

    setBounds(imageNode.getBoundsReference());

    // Create a clip path and use it to clip the source image. Set the
    // clip path bounds equal to the source image bounds for the time being.

    clipPath.setStroke(new PFixedWidthStroke(1));
    clipPath.setPathToRectangle(0, 0, 1, 1);
    clipPath.setBounds(imageNode.getBoundsReference());
    clipPath.addChild(imageNode);
    addChild(clipPath);

    // Create a clip region which we will use to adjust and keep track of the
    // clipping. It will be transparent but will be decorated with handles
    // and event listeners for moving and resizing. Again, fix its bound equal
    // to the source image bounds for the time being.

    clipRegion.setBounds(imageNode.getBoundsReference());
    addChild(clipRegion);

    // Now bind the clip region node and clip path node bounds together.

    clipRegion.addPropertyChangeListener("bounds", new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        clipPath.setBounds(clipRegion.getBoundsReference());
      }
    });

    // Bind the keypoint bounds and clip region bounds together.

    getModel().addPropertyChangeListener("bounds", keypointBoundsListener);

    // Create handles that will edit the clip region. These handles command
    // the model (Keypoint) which in turn fires events that modify the bounds
    // of the clip region.

    clipRegion.addChild(new ImageEditingHandle(PBoundsLocator.createNorthEastLocator(clipRegion)) {
      protected void dragHandle(Point2D mousePosition) {
        double deltaXA = mousePosition.getX() - getModel().getBoundsReference().getMaxX();
        double deltaXB = (getModel().getBoundsReference().getMinY() - mousePosition.getY()) / ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
        double deltaX = Math.max(deltaXA, deltaXB);
        double nodeWidth = getModel().getBoundsReference().getWidth() + deltaX;
        double nodeHeight = nodeWidth / ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
        double nodeX = getModel().getBoundsReference().getX();
        double nodeY = getModel().getBoundsReference().getMaxY() - nodeHeight;
        getModel().setBounds(nodeX, nodeY, nodeWidth, nodeHeight);
      }
    });

    clipRegion.addChild(new ImageEditingHandle(PBoundsLocator.createNorthWestLocator(clipRegion)) {
      protected void dragHandle(Point2D mousePosition) {
        double deltaXA = getModel().getBoundsReference().getMinX() - mousePosition.getX();
        double deltaXB = (getModel().getBoundsReference().getMinY() - mousePosition.getY()) / ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
        double deltaX = Math.max(deltaXA, deltaXB);
        double nodeWidth = getModel().getBoundsReference().getWidth() + deltaX;
        double nodeHeight = nodeWidth / ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
        double nodeX = getModel().getBoundsReference().getMaxX() - nodeWidth;
        double nodeY = getModel().getBoundsReference().getMaxY() - nodeHeight;
        getModel().setBounds(nodeX, nodeY, nodeWidth, nodeHeight);
      }
    });

    clipRegion.addChild(new ImageEditingHandle(PBoundsLocator.createSouthEastLocator(clipRegion)) {
      protected void dragHandle(Point2D mousePosition) {
        double deltaXA = mousePosition.getX() - getModel().getBoundsReference().getMaxX();
        double deltaXB = (mousePosition.getY() - getModel().getBoundsReference().getMaxY()) / ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
        double deltaX = Math.max(deltaXA, deltaXB);
        double nodeWidth = getModel().getBoundsReference().getWidth() + deltaX;
        double nodeHeight = nodeWidth / ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
        double nodeX = getModel().getBoundsReference().getX();
        double nodeY = getModel().getBoundsReference().getY();
        getModel().setBounds(nodeX, nodeY, nodeWidth, nodeHeight);
      }
    });

    clipRegion.addChild(new ImageEditingHandle(PBoundsLocator.createSouthWestLocator(clipRegion)) {
      protected void dragHandle(Point2D mousePosition) {
        double deltaXA = getModel().getBoundsReference().getMinX() - mousePosition.getX();
        double deltaXB = (mousePosition.getY() - getModel().getBoundsReference().getMaxY()) / ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
        double deltaX = Math.max(deltaXA, deltaXB);
        double nodeWidth = getModel().getBoundsReference().getWidth() + deltaX;
        double nodeHeight = nodeWidth / ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
        double nodeX = getModel().getBoundsReference().getMaxX() - nodeWidth;
        double nodeY = getModel().getBoundsReference().getMinY();
        getModel().setBounds(nodeX, nodeY, nodeWidth, nodeHeight);
      }
    });

    // Add an event handler to the clip region allowing it to be moved around
    // by dragging anywhere within it.

    PDragSequenceEventHandler clipRegionDragger = new PDragSequenceEventHandler() {

      public void startDrag(PInputEvent aEvent) {
        super.startDrag(aEvent);
        Point2D mousePosition = aEvent.getPosition();
        dragOffset.setLocation(
            mousePosition.getX() - clipRegion.getBoundsReference().getX(),
            mousePosition.getY() - clipRegion.getBoundsReference().getY());
      }

      public void drag(PInputEvent aEvent) {
        super.drag(aEvent);
        Point2D mousePosition = aEvent.getPosition();
        double deltaX = mousePosition.getX() - dragOffset.getX() - getModel().getBoundsReference().getX();
        double deltaY = mousePosition.getY() - dragOffset.getY() - getModel().getBoundsReference().getY();
        getModel().setBounds(
            getModel().getBoundsReference().getX() + deltaX,
            getModel().getBoundsReference().getY() + deltaY,
            getModel().getBoundsReference().getWidth(),
            getModel().getBoundsReference().getHeight());
      }

      public void endDrag(PInputEvent aEvent) {
        super.endDrag(aEvent);
      }
    };

    clipRegionDragger.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
    clipRegionDragger.getEventFilter().setMarksAcceptedEventsAsHandled(true);
    clipRegionDragger.getEventFilter().setAcceptsMouseEntered(false);
    clipRegionDragger.getEventFilter().setAcceptsMouseExited(false);
    clipRegionDragger.getEventFilter().setAcceptsMouseMoved(false);

    clipRegion.addInputEventListener(clipRegionDragger);

    // Initialize the clip region bounds to be equal to the current values
    // from the model (the keypoint).

    setClipRegionBoundsFromModel();

    // Add a listener for image changes

    getModel().getTrackElement().addPropertyChangeListener("filteredImage", filteredImageListener);
  }

  public void dispose() {
    getModel().removePropertyChangeListener("bounds", keypointBoundsListener);
    getModel().getTrackElement().removePropertyChangeListener("filteredImage", filteredImageListener);
  }

  private void setClipRegionBoundsFromModel() {
    clipRegion.setBounds(
        imageNode.getBoundsReference().getX() + getModel().getBounds().getX(),
        imageNode.getBoundsReference().getY() + getModel().getBounds().getY(),
        getModel().getBounds().getWidth(),
        getModel().getBounds().getHeight());
  }
}
