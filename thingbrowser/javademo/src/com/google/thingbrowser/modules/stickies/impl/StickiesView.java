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

package com.google.thingbrowser.modules.stickies.impl;

import com.google.thingbrowser.api.AbstractThingView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class StickiesView extends AbstractThingView {

  private final JLabel cellLabel = new JLabel();

  private final ListModel emptyListModel = new DefaultListModel();

  private final ListModel listModel = new AbstractListModel() {

    public int getSize() {
      return getModel().getStickies().size();
    }

    public Object getElementAt(int i) {
      return getModel().getStickies().get(i);
    }
  };

  private final ListCellRenderer cellRenderer = new ListCellRenderer() {

    public Component getListCellRendererComponent(JList list, Object value, int index,
        boolean isSelected, boolean cellHasFocus) {
      cellLabel.setText(getModel().getStickies().get(index).getDisplayName());
      cellLabel.setIcon(new ImageIcon(getModel().getStickies().get(index).getIcon()));
      return cellLabel;
    }
  };

  private final PropertyChangeListener modelListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      // TODO(ihab): listModel should fire an event instead...
      list.setModel(emptyListModel);
      list.setModel(listModel);
    }
  };

  private final JList list = new JList();

  public StickiesView(final Stickies model) {
    super(model);
  }

  public void initialize() {
    super.initialize();

    getContentPane().setLayout(new GridLayout(1, 1));
    getContentPane().add(list);

    list.setModel(listModel);
    list.setCellRenderer(cellRenderer);

    list.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        int i = list.locationToIndex(e.getPoint());
        if (i == -1) return;
        fireNavigateToUrl(getModel().getStickies().get(i).getUrl());
      }
    });

    cellLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    cellLabel.setFont(cellLabel.getFont().deriveFont(Font.PLAIN));
    cellLabel.setForeground(Color.blue);

    getModel().addPropertyChangeListener(modelListener);
  }

  public Stickies getModel() {
    return (Stickies)super.getModel();
  }

  public void dispose() {
    getModel().removePropertyChangeListener(modelListener);
  }
}
