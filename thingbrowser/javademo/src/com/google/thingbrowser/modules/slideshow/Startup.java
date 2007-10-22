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

package com.google.thingbrowser.modules.slideshow;

import com.google.thingbrowser.api.Facet;
import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.api.ThingContextSingleton;
import com.google.thingbrowser.api.ThingView;
import com.google.thingbrowser.api.ThingViewFactory;
import com.google.thingbrowser.api.ViewFormat;
import com.google.thingbrowser.modules.slideshow.model.ComposedMovieFacet;
import com.google.thingbrowser.modules.slideshow.model.ComposedMovieThing;
import com.google.thingbrowser.modules.slideshow.util.MovieResolver;
import com.google.thingbrowser.modules.slideshow.view.ComposedMovieThingView;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class Startup implements Runnable {

  public void run() {
    // TODO(ihab): Dependency injection

    ThingContextSingleton.getThingContext().getThingResolverRegistry().registerResolver(new MovieResolver());

    ThingContextSingleton.getThingContext().getThingViewRegistry().registerFactory(new ThingViewFactory() {
      public Class<? extends Facet> getRequiredFacetType() { return ComposedMovieFacet.class; }
      public ViewFormat getSupportedFormat() { return ViewFormat.FULL; }
      public ThingView newView(Thing thing) { return new ComposedMovieThingView((ComposedMovieThing)thing); }
      public double getPreference() { return 10; }
    });
  }
}
