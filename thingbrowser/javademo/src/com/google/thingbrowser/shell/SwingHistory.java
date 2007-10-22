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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class SwingHistory {

  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private final List<URL> list = new ArrayList<URL>();
  private int current = -1;

  public SwingHistory() {
    addPropertyChangeListener("current", new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        System.out.println("History@" + System.identityHashCode(this) + ".list:");
        for (int i = 0; i < list.size(); i++)
          System.out.println("  [" + i + "] = " + list.get(i));
      }
    });
  }

  public void go(URL url) {

    if (url == null) return;

    while (list.size() - 1 > current) {
      list.remove(list.size() - 1);
    }

    list.add(url);
    current++;

    pcs.firePropertyChange("current", null, null);
  }

  public void back() {
    if (current <= 0) return;
    current--;
    pcs.firePropertyChange("current", null, null);
  }

  public void forward() {
    if (current == list.size() - 1) return;
    current++;
    pcs.firePropertyChange("current", null, null);
  }

  public URL getCurrent() {
    if (current == -1) return null;
    return list.get(current);
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    pcs.addPropertyChangeListener(l);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
    pcs.addPropertyChangeListener(propertyName, l);
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    pcs.addPropertyChangeListener(l);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
    pcs.addPropertyChangeListener(propertyName, l);
  }
}
