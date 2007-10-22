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

package com.google.thingbrowser.modules.sound.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.thingbrowser.api.AbstractFacet;
import com.google.thingbrowser.api.MimeResourceFacet;
import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.api.ThingContextSingleton;
import com.google.thingbrowser.modules.sound.PlaylistFacet;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class M3uPlaylistFacetImpl extends AbstractFacet implements PlaylistFacet {

  private final List<Thing> entries = new ArrayList<Thing>();

  public M3uPlaylistFacetImpl(Thing thing) {
    super(thing);
    initializeEntries();
  }

  public List<Thing> getEntries() {
    return Collections.unmodifiableList(entries);
  }

  private void initializeEntries() {
    BufferedReader br = new BufferedReader(new InputStreamReader(getThing().getFacet(MimeResourceFacet.class).newInputStream()));

    while (true) {
      String line;
      try {
        line = br.readLine();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      if (line == null) {
        break;
      }

      if (line.length() == 0 || line.startsWith("#")) {
        continue;
      }

      URL url;
      try {
        url = new URL(line);
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }

      Thing thing = ThingContextSingleton.getThingContext().getThingResolverRegistry().getThing(ThingContextSingleton.getThingContext(), url);

      if (thing != null) {
        entries.add(thing);
      }
    }
  }
}
