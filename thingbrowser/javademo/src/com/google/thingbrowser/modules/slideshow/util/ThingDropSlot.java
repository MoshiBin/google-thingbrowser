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

package com.google.thingbrowser.modules.slideshow.util;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.thingbrowser.api.Facet;
import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.api.ThingContext;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class ThingDropSlot extends JPanel {

  private final ThingContext thingContext;
  private final JLabel label = new JLabel();
  private final Class<? extends Facet> requiredFacet;
  private Thing value = null;

  public ThingDropSlot(ThingContext thingContext, Class<? extends Facet> requiredFacet) {
    this.thingContext = thingContext;
    this.requiredFacet = requiredFacet;

    setLayout(new GridLayout(1, 1));
    add(label);

    DropTargetListener dtl = new DropTargetListener() {

      public void dragEnter(DropTargetDragEvent dtde) {
        setDragUnderHighlight(true);
      }

      public void dragOver(DropTargetDragEvent dtde) {
        if (getThingFromTransferable(dtde.getTransferable()) != null) {
          dtde.acceptDrag(DnDConstants.ACTION_LINK);
        } else {
          dtde.rejectDrag();
        }
      }

      public void dropActionChanged(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_LINK);
      }

      public void dragExit(DropTargetEvent dte) {
        setDragUnderHighlight(false);
      }

      public void drop(DropTargetDropEvent dtde) {
        setValue(getThingFromTransferable(dtde.getTransferable()));
        setDragUnderHighlight(false);
      }
    };

    new DropTarget(this, dtl);

    setBackground(Color.white);
    setOpaque(true);
    setDragUnderHighlight(false);
    updateView();
  }

  public Thing getValue() {
    return value;
  }

  public void setValue(Thing value) {
    if (this.value == value) return;
    Object oldValue = this.value;
    this.value = value;
    updateView();
    firePropertyChange("value", oldValue, value);
  }

  private void updateView() {
    label.setIcon(value == null ? null : new ImageIcon(value.getIcon()));
    label.setText(value == null ? "<null>" : value.getDisplayName());
    label.setToolTipText(value == null ? null : value.getShortDescription());
  }

  private void setDragUnderHighlight(boolean state) {
    label.setBorder(state ?
        BorderFactory.createLineBorder(Color.blue, 1) :
        BorderFactory.createLineBorder(Color.white, 1));
  }

  private Thing getThingFromTransferable(Transferable t) {

    URL url;
    try {
      url = (URL)t.getTransferData(Thing.URL_DATA_FLAVOR);
    } catch (UnsupportedFlavorException e) {
      e.printStackTrace(System.err);
      return null;
    } catch (IOException e) {
      e.printStackTrace(System.err);
      return null;
    } catch (ClassCastException e) {
      e.printStackTrace(System.err);
      return null;
    }
    if (url == null) {
      return null;
    }

    Thing thing = thingContext.getThingResolverRegistry().getThing(thingContext, url);
    if (thing == null) {
      return null;
    }

    if (thing.getFacetTypes().contains(requiredFacet)) {
      return thing;
    }

    return null;
  }
}
