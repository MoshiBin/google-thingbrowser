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


/**
 * A <code>ThingViewFactory</code> is responsible for creating a
 * <code>ThingView</code> for a given <code>Thing</code>.
 *
 * <p>TODO(ihab): Should this be an abstract class, ensuring that the
 * supported formats and required facet types are 'final' and therefore
 * cannot be changed by a buggy or malicious implementation?
 *
 * @author ihab@google.com (Ihab Awad)
 */
public interface ThingViewFactory {

  /**
   * @return the <code>ViewFormat</code> supported by <code>ThingView</code>s
   * created by this factory.
   */
  ViewFormat getSupportedFormat();

  /**
   * @return the set of <code>Facet</code> classes a <code>Thing</code>
   * would have to support in order to be displayed by a view created
   * by this <code>ThingViewFactory</code>.
   */
  Class<? extends Facet> getRequiredFacetType();

  /**
   * The "preference level" for this type of view. This number is used
   * by the framework to decide which view to instantiate for a given Thing out
   * of a number of matching possibilities. A higher number indicates the view
   * is more "preferred". There is no maximum or minimum, but a value of zero
   * is considered "neutral".
   *
   * TODO(ihab): This is a hack; fix it!
   */
  double getPreference();

  /**
   * Obtain a view of a specified <code>Thing</code>.
   *
   * @param thing a <code>Thing</code>.
   *
   * @return a newly created <code>ThingView<code> or <code>null</code>
   * if no such view could be created.
   *
   * TODO(ihab): Improve the interface (especially failure conditions)
   * of this method.
   */
  ThingView newView(Thing thing);
}
