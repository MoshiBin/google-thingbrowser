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

import java.awt.datatransfer.DataFlavor;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Set;

/**
 * A <code>Thing</code> is a distinct model-layer item reachable from a URL.
 *
 * @author ihab@google.com (Ihab Awad)
 */
public interface Thing {

  /**
   * The DataFlavor to be used for transferring URLs around the Thing Browser
   * system via Java Clipboard and DnD operations. By always supplying and
   * consuming this DataFlavor, Things are able to handle URL transfers
   * with non-Java applications such as native browsers.
   */
  public static final DataFlavor URL_DATA_FLAVOR =
    new DataFlavor(
        "application/x-java-url;class=java.net.URL",
        "Uniform Resource Locator");

  /**
   * @return a short human-readable name for this <code>Thing</code>.
   */
  String getDisplayName();

  /**
   * @return a short human-readable sentence or small paragraph describing
   * this <code>Thing</code>, suitable for display in a tooltip or other
   * small popup aid.
   */
  String getShortDescription();

  /**
   * @return an icon to be used for representing this <code>Thing</code>. The
   * return value should be a byte array containing a supported image format
   * (such as GIF or PNG).
   */
  byte[] getIcon();

  /**
   * @return the URL wihch this <code>Thing</code> represents.
   */
  URL getUrl();

  /**
   * @return an collection containing all the classes of <code>Facet</code>s
   * supported by this <code>Thing</code>.
   */
  Set<Class<? extends Facet>> getFacetTypes();

  /**
   * Return a <code>Facet</code> of this <code>Thing</code> implementing a given interface.
   *
   * @param clazz the class (interface) which must be implemented by the returned <code>Facet</code>.
   *
   * @return the requested <code>Facet</code>, or <code>null</code> if the given <code>Facet</code>
   * is not supported by this <code>Thing</code>.
   */
  <T extends Facet> T getFacet(Class<T> clazz);

  /**
   * Return the ThingContext in which this Thing was created.
   */
  ThingContext getThingContext();

  /**
   * Reload the content of this Thing. This is only valid for some Things.
   */
  void reload();
  
  void addPropertyChangeListener(PropertyChangeListener l);

  void removePropertyChangeListener(PropertyChangeListener l);

  void addPropertyChangeListener(String name, PropertyChangeListener l);

  void removePropertyChangeListener(String name, PropertyChangeListener l);
}
