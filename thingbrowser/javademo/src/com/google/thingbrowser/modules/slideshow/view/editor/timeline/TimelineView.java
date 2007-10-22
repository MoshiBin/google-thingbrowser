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

import com.google.thingbrowser.api.ThingContext;
import com.google.thingbrowser.modules.slideshow.model.ComposedMovieFacet;
import com.google.thingbrowser.modules.slideshow.util.DnDCanvas;
import com.google.thingbrowser.modules.slideshow.util.ViewPanel;
import com.google.thingbrowser.modules.slideshow.view.SelectionModel;
import com.google.thingbrowser.modules.slideshow.view.ViewConstants;

import edu.umd.cs.piccolo.PCanvas;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class TimelineView extends ViewPanel<ComposedMovieFacet> {

  private final PCanvas canvas;
  private final TimelineViewNode node;

  public TimelineView(ThingContext thingContext, ComposedMovieFacet model, SelectionModel selectionModel) {
    super(thingContext, model);
    node = new TimelineViewNode(model, selectionModel);
    canvas = new DnDCanvas();
  }

  protected void initializeContents() {

    canvas.getLayer().addChild(node);
    canvas.removeInputEventListener(canvas.getPanEventHandler());
    canvas.removeInputEventListener(canvas.getZoomEventHandler());

    canvas.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        adjustNode();
      }
    });

    setPreferredSize(new Dimension(1, (int)(ViewConstants.TRACK_EDITOR_MINIMUM_HEIGHT * 2)));

    setLayout(new GridLayout(1, 1));
    add(canvas);
    setLocation(0, 0);
    canvas.doLayout();
    doLayout();
  }

  public void dispose() {
    node.dispose();
  }

  private void adjustNode() {
    node.setBounds(
        0,
        0,
        canvas.getWidth(),
        canvas.getHeight());
    node.layoutChildren();
  }
}
