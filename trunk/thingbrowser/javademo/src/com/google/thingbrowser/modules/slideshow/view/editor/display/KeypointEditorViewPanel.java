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

import com.google.thingbrowser.api.ThingContext;
import com.google.thingbrowser.modules.slideshow.model.Keypoint;
import com.google.thingbrowser.modules.slideshow.util.ViewPanel;

import edu.umd.cs.piccolo.PCanvas;

import java.awt.Color;
import java.awt.GridLayout;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class KeypointEditorViewPanel extends ViewPanel<Keypoint> {

  private final KeypointEditorViewNode node;

  public KeypointEditorViewPanel(ThingContext thingContext, Keypoint model) {
    super(thingContext, model);
    node = new KeypointEditorViewNode(model);
  }

  protected void initializeContents() {

    PCanvas canvas = new PCanvas();
    canvas.getLayer().addChild(node);
    canvas.setBorder(null);
    canvas.setBackground(Color.white);

    setLayout(new GridLayout(1, 1));
    add(canvas);
  }

  public void dispose() {
    node.dispose();
  }
}
