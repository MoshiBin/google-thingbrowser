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

import com.google.thingbrowser.modules.slideshow.model.TrackElement;
import com.google.thingbrowser.modules.slideshow.util.ViewNode;
import com.google.thingbrowser.modules.slideshow.view.SelectionModel;
import com.google.thingbrowser.modules.slideshow.view.ViewConstants;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public abstract class TrackElementViewNode<T extends TrackElement> extends ViewNode<T> {

  private final PropertyChangeListener durationListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      layoutChildren();
    }
  };

  private final PropertyChangeListener selectionListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      setSelected(selectionModel.getSelection() == getModel());
    }
  };

  private final SelectionModel selectionModel;
  private final PNode background;
  private final PNode shadow;
  private final PNode highlight;

  protected TrackElementViewNode(T model, final SelectionModel selectionModel, Color backgroundColor) {

    super(model);
    this.selectionModel = selectionModel;

    selectionModel.addPropertyChangeListener("selection", selectionListener);

    shadow = new RoundedRectangleNode();
    shadow.setPaint(ViewConstants.SHADOW_COLOR);
    addChild(shadow);

    highlight = new RoundedRectangleNode();
    highlight.setPaint(ViewConstants.HIGHLIGHT_BORDER_COLOR);
    addChild(highlight);

    background = new RoundedRectangleNode();
    background.setPaint(backgroundColor);
    addChild(background);

    model.addPropertyChangeListener("duration", durationListener);

    addInputEventListener(new PBasicInputEventHandler() {
      public void mouseClicked(PInputEvent e) {
        if (e.getClickCount() == 1) {
          selectionModel.setSelection(getModel());
          e.setHandled(true);
        }
      }
    });

    setSelected(false);
  }

  protected void layoutChildren() {
    double endcapExtension = computeEndcapExtension();
    background.setBounds(
        getX() - endcapExtension,
        getY(),
        getWidth() + endcapExtension * 2,
        getHeight());
    shadow.setBounds(
        background.getX() + ViewConstants.SHADOW_OFFSET,
        background.getY() + ViewConstants.SHADOW_OFFSET,
        background.getWidth(),
        background.getHeight());
    highlight.setBounds(
        background.getX() - ViewConstants.HIGHLIGHT_BORDER_WIDTH,
        background.getY() - ViewConstants.HIGHLIGHT_BORDER_WIDTH,
        background.getWidth() + 2 * ViewConstants.HIGHLIGHT_BORDER_WIDTH,
        background.getHeight() + 2 * ViewConstants.HIGHLIGHT_BORDER_WIDTH);
  }

  protected SelectionModel getSelectionModel() {
    return selectionModel;
  }

  private void setSelected(boolean selected) {
    highlight.setTransparency(selected ? 1.0f : 0.0f);
  }

  public void dispose() {
    selectionModel.removePropertyChangeListener("selection", selectionListener);
    getModel().removePropertyChangeListener("duration", durationListener);
    removeAllChildren();
  }

  protected abstract double computeEndcapExtension();
}
