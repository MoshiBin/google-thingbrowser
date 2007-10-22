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

package com.google.thingbrowser.shell;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class SwingBrowserPanel extends JPanel {

  private final JTabbedPane contentPanel;
  private final SwingLocationBar locationBar;

  public SwingBrowserPanel() {

    contentPanel = new JTabbedPane();
    contentPanel.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        locationBar.setCurrentView(null);
        locationBar.setCurrentView((SwingThingBrowserView)contentPanel.getSelectedComponent());
      }
    });

    locationBar = new SwingLocationBar() {
      protected void newTab() {
        final SwingThingBrowserView view = new SwingThingBrowserView();
        contentPanel.add(view);
        locationBar.setCurrentView(view);

        view.addPropertyChangeListener("displayName", new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent e) {
            contentPanel.setTitleAt(contentPanel.indexOfComponent(view), view.getDisplayName());
          }
        });

        view.addPropertyChangeListener("icon", new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent e) {
            contentPanel.setIconAt(contentPanel.indexOfComponent(view), view.getIcon());
          }
        });

        contentPanel.setTitleAt(contentPanel.indexOfComponent(view), view.getDisplayName());
        contentPanel.setIconAt(contentPanel.indexOfComponent(view), view.getIcon());
      }
    };

    setLayout(new BorderLayout());
    add(locationBar, BorderLayout.NORTH);
    add(contentPanel, BorderLayout.CENTER);
  }
}
