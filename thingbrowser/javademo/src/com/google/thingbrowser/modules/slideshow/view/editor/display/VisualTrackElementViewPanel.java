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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.google.thingbrowser.api.ThingContext;
import com.google.thingbrowser.modules.images.ImageFilterFacet;
import com.google.thingbrowser.modules.slideshow.model.VisualTrackElement;
import com.google.thingbrowser.modules.slideshow.util.ThingDropSlot;
import com.google.thingbrowser.modules.slideshow.util.ViewPanel;
import com.google.thingbrowser.modules.slideshow.view.ViewConstants;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class VisualTrackElementViewPanel extends ViewPanel<VisualTrackElement> {

  private final LayoutManager layout = new LayoutManager() {

    public void addLayoutComponent(String name, Component comp) {}

    public void removeLayoutComponent(Component comp) {}

    public void layoutContainer(Container parent) {

      int h = Math.max(
          (int)filterDropSlotLabel.getPreferredSize().getHeight(),
          (int)filterDropSlot.getPreferredSize().getHeight());
      int w = (int)filterDropSlotLabel.getPreferredSize().getWidth();

      filteredImageScrollPane.setLocation(0, 0);
      filteredImageScrollPane.setSize(getWidth(), getHeight() - h);

      filterDropSlotLabel.setLocation(0, getHeight() - h);
      filterDropSlotLabel.setSize(w, h);

      filterDropSlot.setLocation(w, getHeight() - h);
      filterDropSlot.setSize(getWidth() - w, h);
    }

    public Dimension minimumLayoutSize(Container parent) {
      return new Dimension(0, 0);
    }

    public Dimension preferredLayoutSize(Container parent) {
      return minimumLayoutSize(parent);
    }
  };

  private final JLabel filteredImageView = new JLabel();
  private final JScrollPane filteredImageScrollPane = new JScrollPane(filteredImageView);
  private final JLabel filterDropSlotLabel = new JLabel("Filter:");
  private final ThingDropSlot filterDropSlot;

  private final PropertyChangeListener modelFilterThingListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      filterDropSlot.setValue(getModel().getFilterThing());
    }
  };

  private final PropertyChangeListener modelFilteredImageListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      updateView();
    }
  };

  public VisualTrackElementViewPanel(ThingContext thingContext, VisualTrackElement model) {
    super(thingContext, model);
    filterDropSlot = new ThingDropSlot(thingContext, ImageFilterFacet.class);
  }

  protected void initializeContents() {

    setLayout(new GridLayout(1, 1));
    setBackground(Color.white);

    getModel().addPropertyChangeListener("filterThing", modelFilterThingListener);
    getModel().addPropertyChangeListener("filteredImage", modelFilteredImageListener);

    filterDropSlotLabel.setFont(filterDropSlotLabel.getFont().deriveFont(Font.BOLD));
    filterDropSlotLabel.setOpaque(false);
    filterDropSlotLabel.setBorder(BorderFactory.createEmptyBorder(
        ViewConstants.DEFAULT_TABLE_PADDING,
        ViewConstants.DEFAULT_TABLE_PADDING,
        ViewConstants.DEFAULT_TABLE_PADDING,
        ViewConstants.DEFAULT_TABLE_PADDING));

    filterDropSlot.setValue(getModel().getFilterThing());

    filterDropSlot.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        getModel().setFilterThing(filterDropSlot.getValue());
      }
    });

    setLayout(layout);

    add(filteredImageScrollPane);
    add(filterDropSlotLabel);
    add(filterDropSlot);

    updateView();
  }

  public void dispose() {
    getModel().removePropertyChangeListener("filterThing", modelFilterThingListener);
    getModel().removePropertyChangeListener("filteredImage", modelFilteredImageListener);
  }

  private void updateView() {
    Image image = getModel().getFilteredImage();
    filteredImageView.setIcon(image == null ?null : new ImageIcon(image));
  }
}
