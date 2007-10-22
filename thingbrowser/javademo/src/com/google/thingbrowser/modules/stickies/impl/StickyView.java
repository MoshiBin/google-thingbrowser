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

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class StickyView extends AbstractThingView {

  private final LayoutManager layout = new LayoutManager() {

    public void addLayoutComponent(String name, Component comp) {}

    public void removeLayoutComponent(Component comp) {}

    public Dimension minimumLayoutSize(Container parent) {
      return new Dimension(0, 0);
    }

    public Dimension preferredLayoutSize(Container parent) {
      return minimumLayoutSize(parent);
    }

    public void layoutContainer(Container parent) {

      double buttonPanelHeight =
        Math.max(buttonPanel.getMinimumSize().getHeight(), infoLabel.getMinimumSize().getHeight());

      double buttonPanelWidth =
        buttonPanel.getMinimumSize().getWidth();

      textArea.setLocation(0, 0);
      textArea.setSize(parent.getWidth(), (int)(parent.getHeight() - buttonPanelHeight));

      infoLabel.setLocation(0, (int)(parent.getHeight() - buttonPanelHeight));
      infoLabel.setSize((int)(parent.getWidth() - buttonPanelWidth), (int)buttonPanelHeight);

      buttonPanel.setLocation((int)(parent.getWidth() - buttonPanelWidth), (int)(parent.getHeight() - buttonPanelHeight));
      buttonPanel.setSize((int)buttonPanelWidth, (int)buttonPanelHeight);
    }
  };

  private final PropertyChangeListener lastModifiedListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      updateLastModified();
    }
  };

  private final PropertyChangeListener colorListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      updateColor();
    }
  };

  private final PropertyChangeListener textListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      if (!listening) return;
      updateText();
    }
  };

  private boolean listening = true;
  private final JTextArea textArea = new JTextArea();
  private final JLabel infoLabel = new JLabel();
  private final JPanel buttonPanel = new JPanel();

  public StickyView(Sticky model) {
    super(model);
  }

  public void initialize() {
    super.initialize();

    infoLabel.setFont(infoLabel.getFont().deriveFont(Font.PLAIN).deriveFont(Font.ITALIC));

    buttonPanel.setLayout(new GridLayout(1, Sticky.StickyColor.values().length));
    ButtonGroup colorGroup = new ButtonGroup();

    for (Sticky.StickyColor color : Sticky.StickyColor.values()) {
      addColorButton(colorGroup, color);
    }

    getContentPane().setLayout(layout);
    getContentPane().add(textArea);
    getContentPane().add(infoLabel);
    getContentPane().add(buttonPanel);

    updateColor();
    updateLastModified();
    updateText();

    textArea.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        handleTextAreaUpdated();
      }
      public void insertUpdate(DocumentEvent e) {
        handleTextAreaUpdated();
      }
      public void removeUpdate(DocumentEvent e) {
        handleTextAreaUpdated();
      }
    });

    textArea.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        highlightUrlCursor(e.getPoint());
      }
    });

    textArea.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        handleUrlClick(e.getPoint());
      }
    });

    getModel().addPropertyChangeListener("color", colorListener);
    getModel().addPropertyChangeListener("lastModified", lastModifiedListener);
    getModel().addPropertyChangeListener("text", textListener);
  }

  public Sticky getModel() {
    return (Sticky)super.getModel();
  }

  public void dispose() {
    getModel().removePropertyChangeListener("color", colorListener);
    getModel().removePropertyChangeListener("lastModified", lastModifiedListener);
    getModel().removePropertyChangeListener("text", textListener);
  }

  private void handleTextAreaUpdated() {
    if (!listening) return;
    listening = false;
    getModel().setText(textArea.getText());
    listening = true;
  }

  private void updateLastModified() {
    infoLabel.setText("Last modified: " + getModel().getLastModified().toString());
  }

  private void updateColor() {
    textArea.setBackground(getModel().getColor().newSwingColor());
  }

  private void updateText() {
    listening = false;
    textArea.setText(getModel().getText());
    listening = true;
  }

  private void addColorButton(ButtonGroup group, final Sticky.StickyColor color) {

    JRadioButton button = new JRadioButton();
    button.setBackground(color.newSwingColor());
    button.setSelected(getModel().getColor() == color);

    buttonPanel.add(button);
    group.add(button);

    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        getModel().setColor(color);
      }
    });
  }

  private void highlightUrlCursor(Point point) {
    textArea.setCursor(tokenIsUrl(getTokenAtPoint(point)) ?
        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
  }

  private void handleUrlClick(Point point) {
    String token = getTokenAtPoint(point);
    if (tokenIsUrl(token)) {
      try {
        this.fireNavigateToUrl(new URL(token));
      } catch (MalformedURLException e) {
        // Do nothing
      }
    }
  }

  private boolean tokenIsUrl(String token) {
    return (token == null) ?
        false : token.matches("http://[a-z0-9\\.]+/[a-z0-9/.]*");
  }

  private String getTokenAtPoint(Point point) {

    int position = textArea.viewToModel(point);
    if (position == -1) return null;

    int start = position;
    int end = position;
    String text = textArea.getText();

    while (end < text.length()) {
      if (Character.isWhitespace(text.charAt(end))) {
        break;
      } else {
        end++;
      }
    }

    while (start > 0 && start < end) {
      if (Character.isWhitespace(text.charAt(start))) {
        start++;
        break;
      } else {
        start--;
      }
    }

    return text.substring(start, end);
  }
}
