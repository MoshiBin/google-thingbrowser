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

package com.google.thingbrowser.api;

import java.beans.PropertyChangeListener;

/**
 * A view widget that displays exactly one <code>Thing</code>.
 *
 * @author ihab@google.com (Ihab Awad)
 */
public interface ThingView {

  /**
   * Initialize the child components of this ThingView. Must be called
   * by the client after the ThingView has been constructed and has been
   * added, as a widget, to the widget hierarchy in which it will reside.
   */
  void initialize();

  /**
   * @return the <code>Thing</code> that forms the model (in a model-view sense)
   * of this <code>ThingView</code>.
   */
  Thing getModel();

  /**
   * @return the <code>VieWFormat</code> of this <code>ThingView</code>.
   */
  ViewFormat getFormat();

  /**
   * The URL fragment identifier assigned to this Thing View. The fragment
   * identifier is a way for the view to save information in the URL about the
   * state of the view.
   */
  String getFragmentId();

  /**
   * @see #getFragmentId()
   */
  void setFragmentId(String fragmentId);

  void dispose();

  void addThingNavigationListener(ThingNavigationListener l);

  void removeThingNavigationListener(ThingNavigationListener l);

  void addPropertyChangeListener(PropertyChangeListener l);

  void removePropertyChangeListener(PropertyChangeListener l);

  void addPropertyChangeListener(String propertyName, PropertyChangeListener l);

  void removePropertyChangeListener(String propertyName, PropertyChangeListener l);
}
