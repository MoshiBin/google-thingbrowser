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

import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.modules.images.ImageFacet;
import com.google.thingbrowser.modules.images.ImageFilterFacet;
import com.google.thingbrowser.modules.slideshow.util.ModelList;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class VisualTrackElement extends TrackElement {

  private final Keypoint first;
  private final Keypoint last;
  private final List<Keypoint> intermediaries =
    new ModelList<Keypoint>(
        new ArrayList<Keypoint>(),
        "intermediaries",
        getPropertyChangeSupport());
  private Thing filterThing = null;
  private Image filteredImage = null;

  public VisualTrackElement(Track<VisualTrackElement> track, Thing imageThing) {
    super(track, imageThing);

    first = new Keypoint(this) {
      public double getTimeOffset() {
        return 0;
      }
      public void setTimeOffset(double offset) {
        throw new UnsupportedOperationException();
      }
    };

    last = new Keypoint(this) {
      public double getTimeOffset() {
        return getDuration();
      }
      public void setTimeOffset(double offset) {
        VisualTrackElement.this.setDuration(offset);
        firePropertyChange("timeOffset", null, null);
      }
    };

    getSourceThing().getFacet(ImageFacet.class).addPropertyChangeListener("image", new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        filteredImage = null;
        firePropertyChange("filteredImage", null, null);
      }
    });

    addPropertyChangeListener("filterThing", new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        filteredImage = null;
        firePropertyChange("filteredImage", null, null);
      }
    });
  }

  public Thing getFilterThing() {
    return filterThing;
  }

  public void setFilterThing(Thing filter) {
    Object oldValue = this.filterThing;
    this.filterThing = filter;
    firePropertyChange("filterThing", oldValue, filter);
  }

  public Image getFilteredImage() {
    if (filteredImage == null) {
      filteredImage = computeFilteredImage();
    }
    return filteredImage;
  }

  public Keypoint getFirst() {
    return first;
  }

  public Keypoint getLast() {
    return last;
  }

  public List<Keypoint> getIntermediaries() {
   return intermediaries;
  }

  private Image computeFilteredImage() {

    ImageFacet imageFacet = getSourceThing().getFacet(ImageFacet.class);
    if (imageFacet == null) return null;
    Image image = imageFacet.getImage();

    if (filterThing != null) {
      ImageFilterFacet filterFacet = filterThing.getFacet(ImageFilterFacet.class);
      if (filterFacet != null) {
        image = filterFacet.getFilteredImage(image);
      }
    }

    return image;
  }
}
