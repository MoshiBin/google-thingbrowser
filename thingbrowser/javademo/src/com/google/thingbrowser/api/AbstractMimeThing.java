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

package com.google.thingbrowser.api;

import java.net.URL;
import java.util.Set;

/**
 * An AbstractMimeThing is a superclass for all Things which wish to represent
 * themselves as a MIME-typed stream of data. This class ensures that all Facets
 * that can be provided by the MimeFacetRegistry are correctly instantiated.
 *
 * <p>Concrete subclasses must implement the newMimeResourceFacet().
 *
 * <p>The reason for this class is that not all Things representable as MIME
 * typed streams are obtained by direct retrieval from a URL, e.g., by retrieving
 * the data using HTTP GET and HEAD commands. Some programmatic Things which get
 * their data in other ways may nevertheless want to appear to the system as
 * streams, thus taking advantage of the existing machinery. For example, a Thing
 * may represent itself as a "text/html" stream, thereby providing itself with a
 * simple user interface without having to write any UI code.
 *
 * @author ihab@google.com (Ihab Awad)
 */
public abstract class AbstractMimeThing extends AbstractThing {

  private boolean facetTypesUpdated = false;
  private boolean computingFacetTypes = false;

  public AbstractMimeThing(ThingContext thingContext, URL url) {
    super(thingContext, url);
    addFacetType(MimeResourceFacet.class);
  }

  public Set<Class<? extends Facet>> getFacetTypes() {
    if (computingFacetTypes || facetTypesUpdated) {
      return super.getFacetTypes();
    }

    if (!facetTypesUpdated) {
      computingFacetTypes = true;
      // TODO(ihab): Should the following be reloaded whenever the MimeFacetRegistry
      // is updated? How may we do this?
      for (Class<? extends Facet> clazz : getThingContext().getMimeFacetRegistry().getFacetTypes(this)) {
        addFacetType(clazz);
      }
      facetTypesUpdated = true;
      computingFacetTypes = false;
    }

    return super.getFacetTypes();
  }

  protected Facet newFacet(Class<? extends Facet> clazz) {
    if (clazz == MimeResourceFacet.class) {
      return newMimeResourceFacet();
    }
    return getThingContext().getMimeFacetRegistry().newFacet(this, clazz);
  }

  /**
   * Create and return a MimeResourceFacet for this Thing. This Facet
   * provides access to the MIME type of the data, and the data itself.
   *
   * @return a new MimeResourceFacet for this Thing.
   */
  protected abstract MimeResourceFacet newMimeResourceFacet();
}
