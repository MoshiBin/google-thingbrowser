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

import com.google.thingbrowser.api.ThingContext;
import com.google.thingbrowser.modules.images.ImageFilterFacet;

import java.awt.image.Kernel;
import java.net.URL;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class BlurImageFilterThing extends ConvolveImageFilterThing {

  private static final SimpleFilterParam kernelRadiusParam =
    new SimpleFilterParam(
        "radius",
        "Kernel radius",
        "The radius of the blurring image region, in pixels",
        1,
        30);

  public BlurImageFilterThing(ThingContext thingContext, URL url) {
    super(thingContext, url);
    addFacetType(ImageFilterFacet.class);
    setDisplayName("Blur Image Filter");
    setShortDescription("This filter blurs an image");
    addFilterParam(kernelRadiusParam, 5);
    setParamsFromUrl();
  }

  protected Kernel getKernel() {
    int radius = (int)getFacet(ImageFilterFacet.class).getParamValue(kernelRadiusParam);
    int size = ((radius - 1) * 2) + 1;
    float value = 1.0f / (size * size);
    float[] matrix = new float[size * size];
    for (int i = 0; i < matrix.length; i++) matrix[i] = value;
    return new Kernel(size, size, matrix);
  }
}
