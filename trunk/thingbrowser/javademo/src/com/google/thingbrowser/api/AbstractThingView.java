// Copyright (C) 2006 Google Inc.
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

package com.google.thingbrowser.api;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * An abstract Swing implementation of <code>ThingView</code>.
 *
 * @author ihab@google.com (Ihab Awad)
 */
public abstract class AbstractThingView extends JPanel implements ThingView {

  private String fragmentId;
  private final Thing model;
  private final Set<ThingNavigationListener> thingNavigationListeners =
    new HashSet<ThingNavigationListener>();
  private final JPanel dragSourceBar = new JPanel();
  private final JPanel contentPane = new JPanel();

  /**
   * Creates a new <code>AbstractThingView</code>.
   *
   * @param model the <code>Thing</code> that acts as the model of this view,
   * the identity of which is to remain immutable for the lifetime of this view.
   */
  protected AbstractThingView(Thing model) {
    this.model = model;
  }

  public void initialize() {

    setLayout(new BorderLayout());
    add(contentPane, BorderLayout.CENTER);
    add(dragSourceBar, BorderLayout.NORTH);

    contentPane.setOpaque(false);

    dragSourceBar.setBackground(Color.white);
    dragSourceBar.setLayout(new FlowLayout(FlowLayout.TRAILING));

    final Transferable transferable = new Transferable() {
      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return getModel().getUrl();
      }
      public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {
          Thing.URL_DATA_FLAVOR,
        };
      }
      public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(Thing.URL_DATA_FLAVOR);
      }
    };

    DragGestureListener dgl = new DragGestureListener() {
      public void dragGestureRecognized(DragGestureEvent dge) {
        dge.startDrag(null, transferable);
      }
    };

    DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(dragSourceBar, DnDConstants.ACTION_LINK, dgl);

    clearLinks();
  }

  public Thing getModel() {
    return model;
  }

  public ViewFormat getFormat() {
    return ViewFormat.FULL;
  }

  public void addThingNavigationListener(ThingNavigationListener l) {
    thingNavigationListeners.add(l);
  }

  public void removeThingNavigationListener(ThingNavigationListener l) {
    thingNavigationListeners.remove(l);
  }

  public String getFragmentId() {
    return fragmentId;
  }

  public void setFragmentId(String fragmentId) {
    if (fragmentId == null) fragmentId = "";
    Object oldValue = this.fragmentId;
    this.fragmentId = fragmentId;
    firePropertyChange("fragmentId", oldValue, fragmentId);
  }

  protected JPanel getContentPane() {
    return contentPane;
  }

  protected void fireNavigateToUrl(URL url) {
    if (thingNavigationListeners.size() == 0) return;
    if (url == null) return;
    ThingNavigationEvent e = new ThingNavigationEvent(this, url);
    for (ThingNavigationListener l : thingNavigationListeners)
      l.navigateToUrl(e);
  }

  protected void addLinkSeparator() {
    dragSourceBar.add(new JLabel("|"));
  }

  protected void addLink(Thing thing) {
   addUrlLinkComponent(new UrlLinkComponent(this, thing));
  }

  protected void addLink(URL url, String displayName, String shortDescription) {
    addUrlLinkComponent(new UrlLinkComponent(this, url, displayName, shortDescription));
  }

  protected void addUrlLinkComponent(UrlLinkComponent component) {
    component.addThingNavigationListener(new ThingNavigationListener() {
      public void navigateToUrl(ThingNavigationEvent e) {
        fireNavigateToUrl(e.getUrl());
      }
    });
    dragSourceBar.add(component);
  }

  protected void clearLinks() {
    dragSourceBar.removeAll();
    // If the subclass does not add links to the drag source bar, we still want
    // it to be of a nonzero size. Thus we always add an "invisible" JLabel (one
    // space character) to maintain its height at the proper value.
    dragSourceBar.add(new JLabel(" "));
  }
}
