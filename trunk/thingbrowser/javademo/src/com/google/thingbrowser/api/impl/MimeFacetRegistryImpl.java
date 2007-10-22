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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.thingbrowser.api.Facet;
import com.google.thingbrowser.api.MimeFacetFactory;
import com.google.thingbrowser.api.MimeFacetRegistry;
import com.google.thingbrowser.api.MimeResourceFacet;
import com.google.thingbrowser.api.Thing;

/**
 * @author ihab@google.com (Ihab Awad)
 */
public class MimeFacetRegistryImpl implements MimeFacetRegistry {

  private Set<MimeFacetFactory<? extends Facet>> NO_FACTORIES =
    new HashSet<MimeFacetFactory<? extends Facet>>();

  private final Map<String, Set<MimeFacetFactory<? extends Facet>>> factoriesByMimeType =
    new HashMap<String, Set<MimeFacetFactory<? extends Facet>>>();

  private final Map<String, Set<MimeFacetFactory<? extends Facet>>> factoriesByUrlExtension =
    new HashMap<String, Set<MimeFacetFactory<? extends Facet>>>();

  public Set<Class<? extends Facet>> getFacetTypes(Thing thing) {
    Set<Class<? extends Facet>> results = new HashSet<Class<? extends Facet>>();

    for (MimeFacetFactory<? extends Facet> factory : getFactoriesForMimeType(thing)) {
      results.add(factory.getFacetClass());
    }

    for (MimeFacetFactory<? extends Facet> factory : getFactoriesForUrlExtension(thing)) {
      results.add(factory.getFacetClass());
    }

    return results;
  }

  @SuppressWarnings(
      value = "unchecked"
  )
  public <T extends Facet> T newFacet(Thing thing, Class<T> clazz) {

    for (MimeFacetFactory<? extends Facet> factory : getFactoriesForMimeType(thing)) {
      if (clazz == factory.getFacetClass()) {
        return (T)factory.createFacet(thing);
      }
    }

    for (MimeFacetFactory<? extends Facet> factory : getFactoriesForUrlExtension(thing)) {
      if (clazz == factory.getFacetClass()) {
        return (T)factory.createFacet(thing);
      }
    }

    return null;
  }

  public void registerFactory(MimeFacetFactory<?> factory) {

    if (factory.getMimeType() != null) {
      Set<MimeFacetFactory<?>> setForMime = factoriesByMimeType.get(factory.getMimeType());
      if (setForMime == null) {
        setForMime = new HashSet<MimeFacetFactory<?>>();
        factoriesByMimeType.put(factory.getMimeType(), setForMime);
      }
      setForMime.add(factory);
    }

    if (factory.getUrlExtension() != null) {
      String lowerCaseExtension = factory.getUrlExtension().toLowerCase();
      Set<MimeFacetFactory<?>> setForExtension = factoriesByUrlExtension.get(lowerCaseExtension);
      if (setForExtension == null) {
        setForExtension = new HashSet<MimeFacetFactory<?>>();
        factoriesByUrlExtension.put(lowerCaseExtension, setForExtension);
      }
      setForExtension.add(factory);
    }
  }

  private Set<MimeFacetFactory<? extends Facet>> getFactoriesForMimeType(Thing thing) {
    String mimeType = getSimpleMimeType(thing);
    if (mimeType == null) return NO_FACTORIES;
    Set<MimeFacetFactory<? extends Facet>> result = factoriesByMimeType.get(mimeType);
    return (result == null) ? NO_FACTORIES : result;
  }

  private Set<MimeFacetFactory<? extends Facet>> getFactoriesForUrlExtension(Thing thing) {
    String extension = getUrlExtension(thing);
    if (extension == null) return NO_FACTORIES;
    Set<MimeFacetFactory<? extends Facet>> result = factoriesByUrlExtension.get(extension);
    return (result == null) ? NO_FACTORIES : result;
  }

  private String getSimpleMimeType(Thing thing) {
    MimeResourceFacet mrf = thing.getFacet(MimeResourceFacet.class);
    if (mrf == null) return null;

    String mimeType = mrf.getMimeType();
    if (mimeType == null) return null;

    if (mimeType.contains(";")) {
      mimeType = mimeType.substring(0, mimeType.indexOf(";"));
    }

    return mimeType;
  }

  private String getUrlExtension(Thing thing) {
    String externalForm = thing.getUrl().toExternalForm();
    int index = externalForm.lastIndexOf('.');
    if (index == -1) return null;
    return externalForm.substring(index + 1, externalForm.length()).toLowerCase();
  }
}
