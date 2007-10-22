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

package com.google.thingbrowser.api;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class UrlLinkComponent extends JLabel {

  private final ThingView parentView;
  private URL url;
  private final List<ThingNavigationListener> thingNavigationListeners =
    new ArrayList<ThingNavigationListener>();

  public UrlLinkComponent(ThingView parentView, final Thing thing) {
    this(parentView, thing.getUrl(), thing.getDisplayName(), thing.getShortDescription());
  }

  public UrlLinkComponent(ThingView parentView, URL url, String displayName, String shortDescription) {
    this.parentView = parentView;
    this.url = url;

    setText("<html><a href=\"" +  url.toExternalForm() + "\">" + displayName + "</a></html>");
    setToolTipText(shortDescription);
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        fireNavigateToUrl();
      }
    });

    final Transferable transferable = new Transferable() {
      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return getUrl();
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

    DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_LINK, dgl);

    setBackground(Color.white);
  }

  public void addThingNavigationListener(ThingNavigationListener l) {
    thingNavigationListeners.add(l);
  }

  public void removeThingNavigationListener(ThingNavigationListener l) {
    thingNavigationListeners.remove(l);
  }

  public URL getUrl() {
    return url;
  }

  public void setUrl(URL url) {
    Object oldValue = this.url;
    this.url = url;
    firePropertyChange("url", oldValue, url);
  }

  protected void fireNavigateToUrl() {
    if (thingNavigationListeners.size() > 0) {
      ThingNavigationEvent tne = new ThingNavigationEvent(parentView, url);
      for (ThingNavigationListener listener : thingNavigationListeners) {
        listener.navigateToUrl(tne);
      }
    }
  }
}
