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

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple abstract implementation of <code>ThingResolver</code>.
 *
 * <p>A subclass must define the root URL, and is then responsible only for
 * creating a <code>Thing</code> given a suffix string added to the root URL
 * by implementing {@link #newThing(String)}. Previously constructed
 * <code>Thing</code>s are weakly cached by this class for subsequent use.
 *
 * @author ihab@google.com (Ihab Awad)
 */
public abstract class AbstractThingResolver implements ThingResolver {

  private final URL rootUrl;
  private final Map<String, WeakReference<Thing>> thingByPrefix =
      new HashMap<String, WeakReference<Thing>>();

  /**
   * Creates a new <code>AbstractThingResolver</code> that handles a URL space
   * rooted at a specified root URL.
   *
   * @param rootUrl the root URL of this <code>ThingResolver</code>.
   */
  protected AbstractThingResolver(URL rootUrl) {
    this.rootUrl = rootUrl;
  }

  public URL getRootUrl() {
    return rootUrl;
  }

  public Thing getThing(ThingContext thingContext, URL url) {

    sweepMap();

    String urlExternalForm = url.toExternalForm();

    WeakReference<Thing> resultReference = thingByPrefix.get(urlExternalForm);

    if (resultReference == null || resultReference.get() == null) {
      Thing result = newThing(thingContext, url);
      if (result == null) return null;
      resultReference = new WeakReference<Thing>(result);
      thingByPrefix.put(urlExternalForm, resultReference);
    }

    return resultReference.get();
  }

  /**
   * Creates a new <code>Thing</code> for the specified URL.
   *
   * @param thingContext the ThingContext of the newly instantiated Thing.
   *
   * @param url the URL of the desired <code>Thing</code>.
   *
   * @return a new <code>Thing</code>, or <code>null</code> if the specified
   * URL does not resolve to any <code>Thing</code>.
   */
  protected abstract Thing newThing(ThingContext thingContext, URL url);

  private void sweepMap() {
    Iterator<Map.Entry<String, WeakReference<Thing>>> i =
      thingByPrefix.entrySet().iterator();
    while (i.hasNext()) {
      if (i.next().getValue().get() == null) {
        i.remove();
      }
    }
  }
}
