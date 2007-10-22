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

import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.modules.slideshow.model.ComposedMovieFacet;
import com.google.thingbrowser.modules.slideshow.model.SoundTrackElement;
import com.google.thingbrowser.modules.slideshow.model.Track;
import com.google.thingbrowser.modules.slideshow.view.SelectionModel;
import com.google.thingbrowser.modules.sound.SoundFacet;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class SoundTrackViewNode extends TrackViewNode<SoundTrackElement> {

  private static class SoundDropTargetNode extends TrackElementDropTargetNode<SoundTrackElement> {

    public SoundDropTargetNode(Track<SoundTrackElement> model, int index) {
      super(model, index);
    }

    protected boolean acceptsThing(Thing thing) {
      return thing.getFacetTypes().contains(SoundFacet.class);
    }

    protected void dropThing(Thing thing) {
      SoundTrackElement ste = new SoundTrackElement(getModel(), thing);
      ste.setDuration(ComposedMovieFacet.DEFAULT_ELEMENT_DURATION);
      getModel().getElements().add(getInsertionIndex(), ste);
    }
  };

  public SoundTrackViewNode(Track<SoundTrackElement> model, SelectionModel selectionModel) {
    super(model, selectionModel);
  }

  protected SoundTrackElementViewNode newTrackElementViewNode(SoundTrackElement element, SelectionModel selectionModel) {
   return new SoundTrackElementViewNode(element, selectionModel);
  }

  protected TrackElementDropTargetNode<SoundTrackElement> newTrackElementDropTargetNode(Track<SoundTrackElement> model, int index) {
    return new SoundDropTargetNode(model, index);
  }
}
