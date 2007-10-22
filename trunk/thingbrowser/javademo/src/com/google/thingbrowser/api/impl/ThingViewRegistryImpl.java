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

package com.google.thingbrowser.api.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.thingbrowser.api.Facet;
import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.api.ThingView;
import com.google.thingbrowser.api.ThingViewFactory;
import com.google.thingbrowser.api.ThingViewRegistry;
import com.google.thingbrowser.api.ViewFormat;

/**
 * @author ihab@google.com (Ihab Awad)
 */
public class ThingViewRegistryImpl implements ThingViewRegistry {

  private static class FactorySignature {

    private final ViewFormat format;
    private final Class<? extends Facet> facetType;

    public FactorySignature(ViewFormat format, Class<? extends Facet> facetType) {
      this.format = format;
      this.facetType = facetType;
    }

    public FactorySignature(ThingViewFactory factory) {
      this(factory.getSupportedFormat(), factory.getRequiredFacetType());
    }

    public int hashCode() {
      return facetType.hashCode() + format.hashCode();
    }

    public boolean equals(Object o) {
      try {
        FactorySignature s = (FactorySignature)o;
        return
          format == s.format &&
          facetType == s.facetType;
      } catch (ClassCastException e) {
        return false;
      }
    }

    public boolean isSupertypeOf(FactorySignature s) {
      if (this.format != s.format) return false;
      if (this.facetType.isAssignableFrom(s.facetType)) return true;
      return false;
    }
  }

  private final Map<FactorySignature, Collection<ThingViewFactory>> factoriesBySignature =
      new HashMap<FactorySignature, Collection<ThingViewFactory>>();

  public void registerFactory(ThingViewFactory factory) {
    FactorySignature signature = new FactorySignature(factory);
    Collection<ThingViewFactory> set = factoriesBySignature.get(signature);
    if (set == null) {
      set = new HashSet<ThingViewFactory>();
      factoriesBySignature.put(signature, set);
    }
    set.add(factory);
  }

  public ThingView newView(ViewFormat viewFormat, Thing thing) {
    ThingViewFactory factory = selectFactory(viewFormat, thing);
    return (factory == null) ? null : factory.newView(thing);
  }

  private ThingViewFactory selectFactory(ViewFormat viewFormat, Thing thing) {
    if (viewFormat == null) throw new Error();
    if (thing == null) throw new Error();

    SortedMap<Double, ThingViewFactory> factoriesByPreference =
      new TreeMap<Double, ThingViewFactory>();

    for (Iterator<Class<? extends Facet>> i = thing.getFacetTypes().iterator(); i.hasNext(); ) {
      FactorySignature thingSignature = new FactorySignature(viewFormat, i.next());
      for (Iterator<FactorySignature> j = factoriesBySignature.keySet().iterator(); j.hasNext(); ) {
        FactorySignature candidateSignature = j.next();
        if (candidateSignature.isSupertypeOf(thingSignature)) {
          for (ThingViewFactory f : factoriesBySignature.get(candidateSignature)) {
            factoriesByPreference.put(f.getPreference(), f);
          }
        }
      }
    }

    return factoriesByPreference.isEmpty() ?
      null : factoriesByPreference.get(factoriesByPreference.lastKey());
  }
}
