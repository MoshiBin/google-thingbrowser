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

/**
 * A MimeFacetFactory is an object provided by a Thing Browser plug-in that
 * promises to decorate some Things, derived from a MIME resource, with a
 * certain kind of Facet. For example, a Thing containing data of MIME type
 * "text/html" may be decorated with an HtmlDomFacet.
 *
 * @author ihab@google.com (Ihab Awad)
 */
public interface MimeFacetFactory <T extends Facet> {

  /**
   * The MIME type, such as "text/html", that this factory requires.
   * This takes precedence over {@link #getUrlExtension()}. This property
   * should be null if the factory is not interested in using it.
   */
  String getMimeType();

  /**
   * The URL extension, such as "html", that this factory requires. This should
   * NOT be prepended by a dot; an extension of "html" will automatically match
   * URLs ending in "*.html", "pdf" matches "*.pdf", etc. This property should
   * be null if the factory is not interested in using it.
   */
  String getUrlExtension();

  /**
   * The class of Facets which this factory can create.
   */
  Class<? extends Facet> getFacetClass();

  /**
   * Create a Facet, as promised, for the specified Thing.
   *
   * @param thing a Thing.
   *
   * @return the newly constructed Facet.
   */
  T createFacet(Thing thing);
}
