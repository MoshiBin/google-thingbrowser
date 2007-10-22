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

package com.google.thingbrowser.modules.sound.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.google.thingbrowser.api.AbstractThingView;
import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.api.ThingNavigationEvent;
import com.google.thingbrowser.api.ThingNavigationListener;
import com.google.thingbrowser.api.UrlLinkComponent;
import com.google.thingbrowser.modules.sound.PlaylistFacet;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class SimplePlaylistView extends AbstractThingView {

  private final LayoutManager layout = new LayoutManager() {

    public void addLayoutComponent(String name, Component comp) {}

    public void removeLayoutComponent(Component comp) {}

    public void layoutContainer(Container parent) {

      int x = 0;
      int y = 0;
      int w = parent.getWidth();

      if (parent instanceof JComponent) {
        Insets insets = ((JComponent)parent).getBorder().getBorderInsets(parent);
        x += insets.top;
        y += insets.left;
        w -= insets.left + insets.right;
      }

      for (int i = 0; i < parent.getComponentCount(); i++) {
        Component component = parent.getComponent(i);
        component.setLocation(x, y);
        component.setSize(component.getPreferredSize());
        y += (int)component.getPreferredSize().getHeight();
      }
    }

    public Dimension minimumLayoutSize(Container parent) {
      return preferredLayoutSize(parent);
    }

    public Dimension preferredLayoutSize(Container parent) {
      double w = 0;
      double h = 0;

      for (int i = 0; i < parent.getComponentCount(); i++) {
        w = Math.max(w, parent.getComponent(i).getPreferredSize().getWidth());
        h += parent.getComponent(i).getPreferredSize().getHeight();
      }

      if (parent instanceof JComponent) {
        Insets insets = ((JComponent)parent).getBorder().getBorderInsets(parent);
        w += insets.left + insets.right;
        h += insets.top + insets.bottom;
      }

      return new Dimension((int)w, (int)h);
    }
  };

  private final ThingNavigationListener thingNavigationListener = new ThingNavigationListener() {
    public void navigateToUrl(ThingNavigationEvent e) {
      fireNavigateToUrl(e.getUrl());
    }
  };

  public SimplePlaylistView(Thing model) {
    super(model);
  }

  public void initialize() {
    super.initialize();

    PlaylistFacet playlistFacet = getModel().getFacet(PlaylistFacet.class);

    JPanel panel = new JPanel();
    panel.setBackground(Color.white);
    panel.setLayout(layout);
    panel.setBorder(BorderFactory.createLineBorder(Color.white, 3));

    for (Thing thing : playlistFacet.getEntries()) {
      UrlLinkComponent urlLinkComponent = new UrlLinkComponent(this, thing);
      urlLinkComponent.addThingNavigationListener(thingNavigationListener);
      panel.add(urlLinkComponent);
    }

    getContentPane().setLayout(new GridLayout(1, 1));
    getContentPane().add(new JScrollPane(panel));
  }

  public void dispose() {
  }
}
