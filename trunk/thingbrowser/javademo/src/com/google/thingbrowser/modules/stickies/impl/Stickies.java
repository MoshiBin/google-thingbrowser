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

package com.google.thingbrowser.modules.stickies.impl;

import com.google.thingbrowser.api.AbstractFacet;
import com.google.thingbrowser.api.AbstractThing;
import com.google.thingbrowser.api.Facet;
import com.google.thingbrowser.api.ThingContext;
import com.google.thingbrowser.api.ThingContextSingleton;
import com.google.thingbrowser.modules.stickies.StickiesFacet;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class Stickies extends AbstractThing {

  private class StickiesFacetImpl extends AbstractFacet implements StickiesFacet {
    public StickiesFacetImpl() {
      super(Stickies.this);
    }
  }

  private static final Stickies instance = new Stickies();

  public static final Stickies getInstance() { return instance; }

  private final Map<URL, Sticky> stickyByUrl = new HashMap<URL, Sticky>();
  private final List<Sticky> stickies = new ArrayList<Sticky>();

  private Stickies() {
    // TODO(ihab): Dependency injection
    super(ThingContextSingleton.getThingContext(), StickyResolver.STICKIES_URL);
    addFacetType(StickiesFacet.class);
    setIcon("icons/stickies.png");
    setDisplayName("Stickies");
    setShortDescription("All my stickies");
  }

  protected Facet newFacet(Class<? extends Facet> clazz) {
    if (clazz == StickiesFacet.class) {
      return new StickiesFacetImpl();
    }
    return null;
  }

  public Sticky getSticky(ThingContext thingContext, URL url) {
    Sticky result = stickyByUrl.get(url);
    if (result == null) {
      result = new Sticky(thingContext, url);
      stickyByUrl.put(url, result);
      stickies.add(result);
      firePropertyChange("stickies", null, null);
    }
    return result;
  }

  public void deleteSticky(URL url) {
    Sticky sticky = stickyByUrl.get(url);
    if (sticky != null) {
      stickyByUrl.remove(url);
      stickies.remove(sticky);
      firePropertyChange("stickies", null, null);
    }
  }

  public List<Sticky> getStickies() {
    return Collections.unmodifiableList(stickies);
  }

  // This method is to fire "some" sort of change to allow aggregate views
  // of the Stickies to update themselves. We should replace this with a more
  // sane pub/sub event mechanism for model to view events at some later date.
  protected void fireChange() {
    firePropertyChange("stickies", null, null);
  }
}
