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

package com.google.thingbrowser.modules.slideshow.view;

import com.google.thingbrowser.api.AbstractThingView;
import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.modules.slideshow.model.ComposedMovieFacet;
import com.google.thingbrowser.modules.slideshow.util.ViewPanel;
import com.google.thingbrowser.modules.slideshow.view.editor.EditingViewPanel;
import com.google.thingbrowser.modules.slideshow.view.play.PlayingViewPanel;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class ComposedMovieThingView extends AbstractThingView {

  private static final String PLAY_FRAGMENT_ID = "play";
  private static final String EDIT_FRAGMENT_ID = "edit";

  private ViewPanel<?> viewPanel;

  public ComposedMovieThingView(Thing model) {
    super(model);
  }

  public void initialize() {
    super.initialize();

    getContentPane().setLayout(new GridLayout(1, 1));

    addPropertyChangeListener("fragmentId", new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        refresh();
      }
    });

    refresh();
  }

  private void refresh() {
    clearLinks();
    getContentPane().removeAll();

    ComposedMovieFacet cmf = getModel().getFacet(ComposedMovieFacet.class);
    boolean playing = PLAY_FRAGMENT_ID.equals(getFragmentId());

    if (playing) {
      viewPanel = new PlayingViewPanel(getModel().getThingContext(), cmf);
      addLink(getViewUrl(EDIT_FRAGMENT_ID), "<html>Edit &raquo;</html>", "Go to edit mode");
    } else /* editing */ {
      viewPanel = new EditingViewPanel(getModel().getThingContext(), cmf);
      addLink(getViewUrl(PLAY_FRAGMENT_ID), "<html>Play &raquo;</html>", "Go to play mode");
    }

    getContentPane().add(viewPanel);
    viewPanel.initialize();
  }

  public void dispose() {
    viewPanel.dispose();
  }

  private URL getViewUrl(String fragment) {
    try {
      return new URL(getModel().getUrl() + "#" + fragment);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
