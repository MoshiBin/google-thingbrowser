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

package com.google.thingbrowser.modules.html.impl;

import com.google.thingbrowser.api.AbstractThingView;
import com.google.thingbrowser.api.Thing;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * @author ihab@google.com (Ihab Awad)
 */
public class HtmlThingView extends AbstractThingView {

  public HtmlThingView(Thing model) {
    super(model);
  }

  public void initialize() {
    super.initialize();

    JTextPane htmlComponent = new JTextPane();
    getContentPane().setLayout(new GridLayout(1, 1));
    getContentPane().add(new JScrollPane(htmlComponent));

    htmlComponent.setEditable(false);

    htmlComponent.addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
          HtmlThingView.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
          HtmlThingView.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          HtmlThingView.this.fireNavigateToUrl(e.getURL());
        }
      }
    });

    // Note we use setPage(...) rather than setText() -- because, otherwise,
    // the page would not know its own base URL and could thus not load stuff
    // like images with a base URL. This loses generality.
    try {
      htmlComponent.setPage(getModel().getUrl());
    } catch (IOException e) {
      throw new Error(e);
    }
  }

  public void dispose() {
  }
}
