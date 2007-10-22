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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.net.URL;

import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.api.ThingContextSingleton;
import com.google.thingbrowser.modules.slideshow.model.Track;
import com.google.thingbrowser.modules.slideshow.model.TrackElement;
import com.google.thingbrowser.modules.slideshow.util.ViewNode;
import com.google.thingbrowser.modules.slideshow.view.ViewConstants;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public abstract class TrackElementDropTargetNode<T extends TrackElement> extends ViewNode<Track<T>> implements DropTargetListener {

  private boolean throbbing = false;
  private PActivity colorActivityLeft = null;
  private PActivity colorActivityRight = null;
  private PNode leftNode = new PNode();
  private PNode rightNode = new PNode();
  private final int insertionIndex;

  public TrackElementDropTargetNode(Track<T> model, int insertionIndex) {
    super(model);
    this.insertionIndex = insertionIndex;

    setPaint(Color.red);

    addChild(leftNode);
    addChild(rightNode);

    leftNode.setTransparency(0);
    rightNode.setTransparency(0);

    setWidth(ViewConstants.TRACK_ELEMENT_DROP_TARGET_WIDTH);
  }

  public void dragEnter(DropTargetDragEvent dtde) {
  }

  public void dragExit(DropTargetEvent dte) {
    endThrob();
  }

  public int getInsertionIndex() {
    return insertionIndex;
  }

  public void dragOver(DropTargetDragEvent dtde) {
    if (getThingFromTransferable(dtde.getTransferable()) != null) {
      startThrob();
      dtde.acceptDrag(DnDConstants.ACTION_LINK);
    } else {
      dtde.rejectDrag();
    }
  }

  public void drop(DropTargetDropEvent dtde) {
    try {
      dropThing(getThingFromTransferable(dtde.getTransferable()));
    } finally {
      endThrob();
    }
  }

  public void dropActionChanged(DropTargetDragEvent dtde) {
    dtde.acceptDrag(DnDConstants.ACTION_LINK);
  }

  protected abstract boolean acceptsThing(Thing thing);

  protected abstract void dropThing(Thing thing);

  private Thing getThingFromTransferable(Transferable t) {

    URL url;
    try {
      url = (URL)t.getTransferData(Thing.URL_DATA_FLAVOR);
    } catch (UnsupportedFlavorException e) {
      e.printStackTrace(System.err);
      return null;
    } catch (IOException e) {
      e.printStackTrace(System.err);
      return null;
    } catch (ClassCastException e) {
      e.printStackTrace(System.err);
      return null;
    }
    if (url == null) {
      return null;
    }

    Thing thing =
      ThingContextSingleton.getThingContext().getThingResolverRegistry().getThing(
          ThingContextSingleton.getThingContext(),
          url);
    if (thing == null) {
      return null;
    }

    if (acceptsThing(thing)) {
      return thing;
    }

    return null;
  }

  private void startThrob() {
    if (throbbing) return;

    leftNode.setPaint(
        new GradientPaint(
            (int)leftNode.getBoundsReference().getMaxX(), 0, ViewConstants.HIGHLIGHT_BORDER_COLOR,
            (int)leftNode.getBoundsReference().getMinX(), 0, ViewConstants.BACKGROUND_COLOR));

    rightNode.setPaint(
        new GradientPaint(
            (int)rightNode.getBoundsReference().getMinX(), 0, ViewConstants.HIGHLIGHT_BORDER_COLOR,
            (int)rightNode.getBoundsReference().getMaxX(), 0, ViewConstants.BACKGROUND_COLOR));

    colorActivityLeft = leftNode.animateToTransparency(1, ViewConstants.THROB_MILLISECONDS);
    colorActivityRight = rightNode.animateToTransparency(1, ViewConstants.THROB_MILLISECONDS);

    throbbing = true;
  }

  private void endThrob() {
    if (!throbbing) return;

    colorActivityLeft.terminate();
    colorActivityRight.terminate();

    colorActivityLeft = null;
    colorActivityRight = null;

    leftNode.setTransparency(0);
    rightNode.setTransparency(0);

    throbbing = false;
  }

  protected void layoutChildren() {
    leftNode.setBounds(
        getX() - ViewConstants.TRACK_ELEMENT_DROP_TARGET_WIDTH / 2,
        getY(),
        ViewConstants.TRACK_ELEMENT_DROP_TARGET_WIDTH / 2,
        getHeight());
    rightNode.setBounds(
        getX(),
        getY(),
        ViewConstants.TRACK_ELEMENT_DROP_TARGET_WIDTH / 2,
        getHeight());
  }

  public void dispose() {
  }
}
