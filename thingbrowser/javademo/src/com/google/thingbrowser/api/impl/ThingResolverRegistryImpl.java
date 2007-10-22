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

package com.google.thingbrowser.api.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.api.ThingContext;
import com.google.thingbrowser.api.ThingResolver;
import com.google.thingbrowser.api.ThingResolverRegistry;
import com.google.thingbrowser.api.ThingViewFactory;

/**
 * @author ihab@google.com (Ihab Awad)
 */
public final class ThingResolverRegistryImpl implements ThingResolverRegistry {

  private final ThingResolver mimeThingResolver = new UrlRetrievalMimeThingResolver();

  private final Map<URL, ThingResolver> resolverByUrl =
      new HashMap<URL, ThingResolver>();

  public ThingResolverRegistryImpl() {}

  public void registerResolver(ThingResolver resolver) {
    resolverByUrl.put(resolver.getRootUrl(), resolver);
  }

  public Thing getThing(ThingContext thingContext, URL url) {
    ThingResolver resolver = findResolver(url);
    return (resolver == null) ? null : resolver.getThing(thingContext, url);
  }

  private ThingResolver findResolver(URL requestUrl) {

    // TODO(ihab): This simplistic algorithm does not consider overlapping resolver
    // root URLs. Either we should handle them cleanly, or we should disallow them
    // by throwing an exception in the registerResolver(ThingResolver) method.

    String requestUrlString = requestUrl.toExternalForm();

    for (URL resolverUrl : resolverByUrl.keySet()) {
      String resolverUrlString = resolverUrl.toExternalForm();
      if (requestUrlString.startsWith(resolverUrlString)) {
        return resolverByUrl.get(resolverUrl);
      }
    }

    return mimeThingResolver;
  }
}
