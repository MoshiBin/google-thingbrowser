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

package com.google.thingbrowser.modules.images.impl;

import java.awt.Image;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import com.google.thingbrowser.api.AbstractFacet;
import com.google.thingbrowser.api.MimeResourceFacet;
import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.modules.images.ImageFacet;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class MimeImageFacetImpl extends AbstractFacet implements ImageFacet {

  Image image = null;

  public MimeImageFacetImpl(Thing thing) {
    super(thing);
  }

  public synchronized Image getImage() {
    if (image == null) {
      createImage();
    }

    return image;
  }

  private void createImage() {
    MimeResourceFacet mimeFacet = getThing().getFacet(MimeResourceFacet.class);
    if (mimeFacet == null) return;

    Iterator<?> readers = ImageIO.getImageReadersByMIMEType(mimeFacet.getMimeType());
    if (!readers.hasNext()) return;
    ImageReader reader = (ImageReader)readers.next();

    try {
      reader.setInput(ImageIO.createImageInputStream(mimeFacet.newInputStream()));
      image = reader.read(0);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
