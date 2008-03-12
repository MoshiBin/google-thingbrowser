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

import com.google.thingbrowser.modules.slideshow.model.SoundTrackElement;
import com.google.thingbrowser.modules.slideshow.view.SelectionModel;
import com.google.thingbrowser.modules.slideshow.view.ViewConstants;

import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.handles.PHandle;
import edu.umd.cs.piccolox.util.PBoundsLocator;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class SoundTrackElementViewNode extends TrackElementViewNode<SoundTrackElement> {

  private PHandle durationHandle = new PHandle(PBoundsLocator.createEastLocator(this)) {

    public void dragHandle(PDimension aLocalDimension, PInputEvent aEvent) {
      localToGlobal(aLocalDimension);
      SoundTrackElementViewNode.this.globalToLocal(aLocalDimension);
      getModel().setDuration(getModel().getDuration() + aLocalDimension.getWidth() / getSelectionModel().getDurationScaling());
    }
  };

  public SoundTrackElementViewNode(SoundTrackElement model, final SelectionModel selectionModel) {
    super(model, selectionModel, ViewConstants.SOUND_TRACK_ELEMENT_COLOR);
    addChild(durationHandle);
  }

  protected double computeEndcapExtension() {
    return ViewConstants.TRACK_ELEMENT_INTERNAL_GAP;
  }
}