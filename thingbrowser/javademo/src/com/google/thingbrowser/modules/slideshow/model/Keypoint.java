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

package com.google.thingbrowser.modules.slideshow.model;

import java.awt.Rectangle;

import com.google.thingbrowser.modules.slideshow.util.ModelObject;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class Keypoint extends ModelObject {

  private double timeOffset = 0.0;
  private final VisualTrackElement trackElement;
  private final Rectangle bounds = new Rectangle();

  public Keypoint(VisualTrackElement trackElement) {
    this.trackElement = trackElement;
    // TODO(ihab): The below assumes that the Image has been loaded and
    // the width and height are available without registering an ImageObserver.
    // This will not always be true.
    setBounds(
        0,
        0,
        trackElement.getFilteredImage().getWidth(null),
        trackElement.getFilteredImage().getHeight(null));
    trimBoundsToContent();
  }

  public VisualTrackElement getTrackElement() {
    return trackElement;
  }

  public Rectangle getBoundsReference() {
    return bounds;
  }

  public Rectangle getBounds() {
    return new Rectangle(bounds);
  }

  public void setBounds(Rectangle bounds) {
    this.bounds.setRect(bounds);
    firePropertyChange("bounds", null, null);
  }

  public void setBounds(double x, double y, double w, double h) {
   bounds.setRect(x, y, w, h);
   firePropertyChange("bounds", null, null);
  }

  public double getTimeOffset() {
    return timeOffset;
  }

  public void setTimeOffset(double timeOffset) {
    this.timeOffset = Math.min(timeOffset, trackElement.getDuration());
    firePropertyChange("timeOffset", null, null);
  }

  private void trimBoundsToContent() {
    if ((bounds.getWidth() / bounds.getHeight()) < ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO) {
      // Bounds are too tall and skinny. Trim the height to fit.
      double height = bounds.getWidth() / ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
      bounds.setRect(
          bounds.getX(),
          bounds.getCenterY() - (height / 2),
          bounds.getWidth(),
          height);
    } else {
      // Bounds are too squat and plump. Trim the width to fit.
      double width = bounds.getHeight() * ComposedMovieFacet.DEFAULT_MOVIE_ASPECT_RATIO;
      bounds.setRect(
          bounds.getCenterX() - (width / 2),
          bounds.getY(),
          width,
          bounds.getHeight());
    }
  }
}
