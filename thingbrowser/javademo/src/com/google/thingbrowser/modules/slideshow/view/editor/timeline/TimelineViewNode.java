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
import com.google.thingbrowser.modules.slideshow.util.ViewNode;
import com.google.thingbrowser.modules.slideshow.view.SelectionModel;
import com.google.thingbrowser.modules.slideshow.view.ViewConstants;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class TimelineViewNode extends ViewNode<ComposedMovieFacet> {

  private final VisualTrackViewNode visualNode;
  private final SoundTrackViewNode soundNode;

  public TimelineViewNode(ComposedMovieFacet model, SelectionModel selectionModel) {
    super(model);
    visualNode = new VisualTrackViewNode(model.getVisualTrack(), selectionModel);
    soundNode = new SoundTrackViewNode(model.getSoundTrack(), selectionModel);
    addChild(visualNode);
    addChild(soundNode);
  }

  protected void layoutChildren() {
    double visualNodeHeight = Math.max(
        ViewConstants.TRACK_EDITOR_MINIMUM_HEIGHT,
        getBoundsReference().getHeight() - ViewConstants.TRACK_EDITOR_MINIMUM_HEIGHT - ViewConstants.TRACK_EDITOR_TOP_GAP);

    visualNode.setBounds(
        getBoundsReference().getX() + ViewConstants.TRACK_ZERO_GAP,
        ViewConstants.TRACK_EDITOR_TOP_GAP,
        0,
        visualNodeHeight);
    soundNode.setBounds(
        getBoundsReference().getX() + ViewConstants.TRACK_ZERO_GAP,
        visualNodeHeight + ViewConstants.TRACK_EDITOR_TOP_GAP,
        0,
        ViewConstants.TRACK_EDITOR_MINIMUM_HEIGHT);
  }

  public void dispose() {
    visualNode.dispose();
    soundNode.dispose();
  }
}
