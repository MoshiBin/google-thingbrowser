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

import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.api.ThingContext;
import com.google.thingbrowser.api.ThingContextSingleton;
import com.google.thingbrowser.api.ThingNavigationEvent;
import com.google.thingbrowser.api.ThingNavigationListener;
import com.google.thingbrowser.api.ThingView;
import com.google.thingbrowser.api.ViewFormat;

import java.awt.Component;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * A <code>BrowserView</code> is a component that displays one <code>Thing</code>
 * at a time and maintains a "bidirectional" history (for Foward and Back controls)
 * of the Things it has viewed in the past.
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class SwingThingBrowserView extends JPanel {

  private static final String DEFAULT_DISPLAY_NAME = "(Untitled)";
  private static final Icon DEFAULT_ICON = new ImageIcon(SwingThingBrowserView.class.getResource("icons/empty.png"));

  private final ThingNavigationListener viewNavigationListener = new ThingNavigationListener() {
    public void navigateToUrl(ThingNavigationEvent e) {
      getHistory().go(e.getUrl());
    }
  };

  private final PropertyChangeListener thingDisplayNameListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      if (currentThing == null) return;
      setDisplayName(currentThing.getDisplayName());
    }
  };

  private final PropertyChangeListener thingIconListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      if (currentThing == null || currentThing.getIcon() == null) return;
      setIcon(new ImageIcon(currentThing.getIcon()));
    }
  };

  private final PropertyChangeListener fragmentIdListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      if (currentThing == null) return;
      listeningToHistory = false;
      getHistory().go(joinUrlAndFragment(currentThing.getUrl(), currentThingView.getFragmentId()));
      listeningToHistory = true;
    }
  };

  private final SwingHistory history = new SwingHistory();
  private Thing currentThing = null;
  private ThingView currentThingView = null;
  private boolean listeningToHistory = true;
  private final ThingContext thingContext;
  private Icon icon = DEFAULT_ICON;
  private String displayName = DEFAULT_DISPLAY_NAME;

  public SwingThingBrowserView() {
    this.thingContext = ThingContextSingleton.getThingContext(); // TODO(ihab): Dependency injection
    history.addPropertyChangeListener("current", new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        if (listeningToHistory) {
          try {
            newUrl();
          } catch (Throwable t) {
            t.printStackTrace(System.err);
          }
        }
      }
    });
    setLayout(new GridLayout(1, 1));
  }

  public SwingHistory getHistory() {
    return history;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = (displayName == null || displayName.length() == 0) ?
          DEFAULT_DISPLAY_NAME : displayName;
    firePropertyChange("displayName", null, null);
  }

  public Icon getIcon() {
    return icon;
  }

  public void setIcon(Icon icon) {
    this.icon = (icon == null) ?
        DEFAULT_ICON : icon;
    firePropertyChange("icon", null, null);
  }

  private void cleanup() {

    if (currentThingView != null) {

      currentThingView.removeThingNavigationListener(viewNavigationListener);
      currentThingView.removePropertyChangeListener("fragmentId", fragmentIdListener);
      currentThingView.dispose();
      remove((Component)currentThingView);
      currentThingView = null;

      currentThing.removePropertyChangeListener("displayName", thingDisplayNameListener);
      currentThing.removePropertyChangeListener("icon", thingIconListener);
      currentThing = null;
    }
  }

  private URL joinUrlAndFragment(URL url, String fragmentId) {
    if (fragmentId == null || fragmentId.length() == 0) return url;
    try {
      return new URL(url, "#" + fragmentId);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private Object[] splitUrlAndFragment(URL url) {
    String s = url.toExternalForm();
    int idx = s.lastIndexOf('#');
    if (idx == -1) {
      return new Object[] {
        url,
        null,
      };
    } else {
      try {
        return new Object[] {
            new URL(s.substring(0, idx)),
            s.substring(idx + 1, s.length()),
        };
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void newUrl() {

    URL url = history.getCurrent();
    Object[] urlAndFragment = splitUrlAndFragment(url);

    if (currentThingView != null && currentThing != null && urlAndFragment[0].equals(currentThing.getUrl())) {
      currentThingView.setFragmentId((String)urlAndFragment[1]);
      ensureLayout();
      return;
    }

    cleanup();

    currentThing = ThingContextSingleton.getThingContext().getThingResolverRegistry().getThing(thingContext, (URL)urlAndFragment[0]);

    setDisplayName(null);
    if (currentThing != null) {
      setDisplayName(currentThing.getDisplayName());
    }

    setIcon(null);
    if (currentThing != null && currentThing.getIcon() != null) {
      setIcon(new ImageIcon(currentThing.getIcon()));
    }

    if (currentThing == null) return;

    currentThing.addPropertyChangeListener("displayName", thingDisplayNameListener);
    currentThing.addPropertyChangeListener("icon", thingIconListener);

    currentThingView = ThingContextSingleton.getThingContext().getThingViewRegistry().newView(ViewFormat.FULL, currentThing);

    if (currentThingView == null) return;

    currentThingView.setFragmentId((String)urlAndFragment[1]);
    add((Component)currentThingView);

    currentThingView.addThingNavigationListener(viewNavigationListener);
    currentThingView.addPropertyChangeListener("fragmentId", fragmentIdListener);

    currentThingView.initialize();

    ensureLayout();

    firePropertyChange("currentUrl", null, null);
  }

  private void ensureLayout() {
    // The below is a bunch of heuristics, derived by trial and error, to make
    // sure that this panel is updated after a change to the contents.
    setBounds(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    getParent().doLayout();
  }
}
