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

package com.google.thingbrowser.modules.proteins.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.google.thingbrowser.api.AbstractFacet;
import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.modules.images.ImageFacet;

/**
 *
 * @author jregan@google.com (Jeffrey Regan)
 */
public class ProteinImageFacetImpl extends AbstractFacet implements ImageFacet {

  private static final int DEFAULT_WIDTH = 640;
  private static final int DEFAULT_HEIGHT = 480;
  private static final Color DEFAULT_COLOR = Color.orange;

	private Image image;

	public ProteinImageFacetImpl(Thing thing) {
		super(thing);
    initializeImage();
	}

	public synchronized Image getImage() {
		return image;
	}

  public synchronized void setImage(Image image) {
    this.image = image;
    firePropertyChange("image", null, null);
  }

  private void initializeImage() {
    image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = (Graphics2D)image.getGraphics();
    graphics.setColor(DEFAULT_COLOR);
    graphics.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    graphics.dispose();
  }
}
