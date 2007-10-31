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
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple abstract implementation of <code>Thing</code>.
 *
 * <p>Subclasses of this class must supply a URL upon construction, and
 * should call setter methods (such as {@link #setDisplayName(String)}) to
 * set other important information and keep it up to date.
 *
 * @author ihab@google.com (Ihab Awad)
 */
public abstract class AbstractThing implements Thing {

  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private URL url;
  private String displayName = null;
  private String shortDescription = null;
  private byte[] icon = null;
  private final ThingContext thingContext;
  private final Set<Class<? extends Facet>> facetTypes =
    new HashSet<Class<? extends Facet>>();
  private final Map<Class<? extends Facet>, Facet> facets =
    new HashMap<Class<? extends Facet>, Facet>();

  protected AbstractThing(ThingContext thingContext, URL url) {
    this.thingContext = thingContext;
    this.url = url;
  }

  public URL getUrl() {
    return url;
  }

  protected void setUrl(URL url) {
    if (url == null) throw new NullPointerException();
    Object oldValue = this.url;
    this.url = url;
    pcs.firePropertyChange("url", oldValue, url);
  }

  public String getDisplayName() {
    return displayName;
  }

  public ThingContext getThingContext() {
    return thingContext;
  }

  protected void setDisplayName(String displayName) {
    Object oldValue = this.displayName;
    this.displayName = displayName;
    pcs.firePropertyChange("displayName", oldValue, displayName);
  }

  public String getShortDescription() {
    return shortDescription;
  }

  protected void setShortDescription(String shortDescription) {
    Object oldValue = this.shortDescription;
    this.shortDescription = shortDescription;
    pcs.firePropertyChange("shortDescription", oldValue, shortDescription);
  }

  public byte[] getIcon() {
    return icon;
  }

  protected void addFacetType(Class<? extends Facet> clazz) {
    facetTypes.add(clazz);
  }

  protected void setIcon(byte[] icon) {
    Object oldValue = this.icon;
    this.icon = icon;
    try {
      pcs.firePropertyChange("icon", oldValue, icon);
    } catch (Throwable t) {
      t.printStackTrace(System.err);
    }
  }

  protected void setIcon(String iconResourcePath) {
    InputStream is = getClass().getResourceAsStream(iconResourcePath);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      for (int b = is.read(); b != -1; b = is.read()) baos.write(b);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    setIcon(baos.toByteArray());
  }

  protected void setIcon(URL url) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      InputStream is = url.openStream();
      for (int b = is.read(); b != -1; b = is.read()) baos.write(b);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    setIcon(baos.toByteArray());
  }

  public Set<Class<? extends Facet>> getFacetTypes() {
    return Collections.unmodifiableSet(facetTypes);
  }

  @SuppressWarnings(
      value = "unchecked"
  )
  public final <T extends Facet> T getFacet(Class<T> clazz) {
    Facet facet = facets.get(clazz);

    if (facet == null) {

      if (!getFacetTypes().contains(clazz)) {
        return null;
      }

      facet = newFacet(clazz);

      if (facet == null) {
        String msg =
            "Thing " + this +
            " declared Facet class " + clazz +
            " but failed to create an instance.";
        throw new RuntimeException(msg);
      }

      facets.put(clazz, facet);
    }

    return (T)facet;
  }
  
  public void reload() {
    // This is a no-op by default.
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    pcs.addPropertyChangeListener(l);
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    pcs.removePropertyChangeListener(l);
  }

  public void addPropertyChangeListener(String name, PropertyChangeListener l) {
    pcs.addPropertyChangeListener(name, l);
  }

  public void removePropertyChangeListener(String name, PropertyChangeListener l) {
    pcs.removePropertyChangeListener(name, l);
  }

  protected void firePropertyChange(String name, Object oldValue, Object newValue) {
    pcs.firePropertyChange(name, oldValue, newValue);
  }

  protected abstract Facet newFacet(Class<? extends Facet> clazz);
}
