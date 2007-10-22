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

import com.google.thingbrowser.api.ThingContext;

import java.awt.Window;

import javax.swing.JPanel;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public abstract class ViewPanel<T> extends JPanel {

  private final ThingContext thingContext;
  private final T model;

  public ViewPanel(ThingContext thingContext, T model) {
    this.thingContext = thingContext;
    this.model = model;
  }

  public T getModel() {
    return model;
  }

  public ThingContext getThingContext() {
    return thingContext;
  }

  public final void initialize() {
    initializeContents();

    // TODO(ihab): The below is a hack to try to get containers in the hierarchy
    // laid out properly when the contents are updated. Previously, new contents
    // were simply not showing up when added. Need to come up with a cleaner
    // diagnosis/solution.

    if (getTopLevelAncestor() != null && getTopLevelAncestor() instanceof Window) {
      Window window = (Window)getTopLevelAncestor();
      // Need to set preferred size == current size so pack() does not cause a
      // supurious window resize.
      window.setPreferredSize(window.getSize());
      window.pack();
    } else {
      // We may be embedded in some non-standard container that's not a Window.
      // Log it, but we will not fail outright, hoping that either everything
      // else is working okay, or everything looks hoaky in which case someone
      // will look at a logfile somewhere.
      // TODO(ihab): Put this in a proper log, not just System.err.
      System.err.println(
          "Warning: getTopLevelAncestor() returns unexpected object: " +
          getTopLevelAncestor() + "," +
          " in method " + ViewPanel.class.getName() + ".initialize()" +
          " inherited by class " + getClass().getName());
    }
  }

  protected abstract void initializeContents();

  public abstract void dispose();
}
