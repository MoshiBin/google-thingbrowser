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

package com.google.thingbrowser.modules.imagefilters.impl;

import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.api.ThingContext;
import com.google.thingbrowser.api.ThingResolver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The Resolver for this plug-in does not extend AbstractThingResolver since
 * it deliberately does not cache previously returned instances. Each resolution
 * returns an independent instance of the referenced Thing. This is to preserve
 * the behavior whereby each resolution returns a *conceptually* "immutable"
 * object identified by the given URL.
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class Resolver implements ThingResolver {

  protected static final URL ROOT_URL;

  static {
    try {
      ROOT_URL = new URL("http://google-thingbrowser.googlecode.com/svn/trunk/javademo/plugins/imagefilters/");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

    public URL getRootUrl() {
    return ROOT_URL;
  }

  public Thing getThing(ThingContext thingContext, URL url) {
    String baseUrl = getBaseUrl(url);
    if (baseUrl.endsWith("/blur"))
      return new BlurImageFilterThing(thingContext, url);
    if (baseUrl.endsWith("/grayscale"))
      return new GrayscaleImageFilterThing(thingContext, url);
    return null;
  }

  private String getBaseUrl(URL url) {
    String text = url.toExternalForm();
    int indexOfQ = text.indexOf('?');
    return indexOfQ == -1 ? text : text.substring(0, indexOfQ);
  }
}
