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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public abstract class SwingLocationBar extends JPanel {

  private final PropertyChangeListener currentUrlListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      setTextFromUrl(currentView.getHistory().getCurrent());
    }
  };

  private final JToolBar toolbar = new JToolBar();
  private final JTextField urlField = new JTextField();
  private SwingThingBrowserView currentView = null;

  public SwingLocationBar() {

    setLayout(new GridLayout(1, 1));
    add(toolbar);

    toolbar.setFloatable(false);

    toolbar.add(new AbstractAction("New window", getIcon("new-window")) {
      public void actionPerformed(ActionEvent e) {
        newWindow();
      }
    });

    toolbar.add(new AbstractAction("New tab", getIcon("new-tab")) {
      public void actionPerformed(ActionEvent e) {
        newTab();
      }
    });

    toolbar.add(new AbstractAction("Back", getIcon("back")) {
      public void actionPerformed(ActionEvent e) {
        back();
      }
    });

    toolbar.add(new AbstractAction("Forward", getIcon("forward")) {
      public void actionPerformed(ActionEvent e) {
        forward();
      }
    });

    toolbar.add(new AbstractAction("Reload", getIcon("reload")) {
      public void actionPerformed(ActionEvent e) {
        reload();
      }
    });

    toolbar.add(new AbstractAction("Stop", getIcon("stop")) {
      public void actionPerformed(ActionEvent e) {
        stop();
      }
    });

    toolbar.add(new AbstractAction("Home", getIcon("home")) {
      public void actionPerformed(ActionEvent e) {
        home();
      }
    });

    toolbar.add(urlField);

    urlField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        navigateToUrl(urlField.getText());
      }
    });
  }

  public void setCurrentView(SwingThingBrowserView currentView) {

    if (this.currentView != null) {
      this.currentView.getHistory().removePropertyChangeListener("current", currentUrlListener);
      this.currentView = null;
    }

    if (currentView == null) return;

    this.currentView = currentView;
    currentView.getHistory().addPropertyChangeListener("current", currentUrlListener);

    setTextFromUrl(currentView == null ? null : currentView.getHistory().getCurrent());
  }

  public SwingThingBrowserView getCurrentView() {
    return currentView;
  }

  private Icon getIcon(String name) {
    return new ImageIcon(getClass().getResource("icons/" + name + ".png"));
  }

  private void newWindow() {
    SwingBrowserFrame.newFrame();
  }

  protected abstract void newTab();

  private void back() {
    if (currentView == null) return;
    currentView.getHistory().back();
  }

  private void forward() {
    if (currentView == null) return;
    currentView.getHistory().forward();
  }

  private void reload() {
    if (currentView == null) return;
    currentView.getHistory().go(currentView.getHistory().getCurrent());
  }

  private void stop() {
    // Nothing to do
  }

  private void home() {
    navigateToUrl(getHomeUrl());
  }

  private void navigateToUrl(String text) {

    if (currentView == null) return;

    URL url;

    try {
      url = new URL(text);
    } catch (MalformedURLException e) {
      e.printStackTrace(System.err);
      return;
    }

    urlField.setText(text);
    currentView.getHistory().go(url);
  }

  private void setTextFromUrl(URL url) {
    urlField.setText(url == null ? "" : url.toExternalForm());
  }

  private String getHomeUrl() {
    return "http://google-thingbrowser.googlecode.com/svn/trunk/javademo/plugins/index.html";
  }
}
