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
import com.google.thingbrowser.modules.stickies.StickyFacet;

import java.net.URL;
import java.util.Date;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class Sticky extends AbstractThing {

  private class StickyFacetImpl extends AbstractFacet implements StickyFacet {
    public StickyFacetImpl() {
      super(Sticky.this);
    }
  }

  public enum StickyColor {

    YELLOW(0xff, 0xff, 0x80),
    GREEN(0x80, 0xff, 0xff),
    BLUE(0xb0, 0xb0, 0xff),
    RED(0xff, 0xb0, 0xb0);

    private final int r, g, b;

    StickyColor(int r, int g, int b) {
      this.r = r; this.g = g; this.b = b;
    }

    java.awt.Color newSwingColor() {
      return new java.awt.Color(r, g, b);
    }
  }

  private static final String UNTITLED = "<untitled>";

  private String text = "";
  private Date lastModified = new Date(System.currentTimeMillis());
  private StickyColor color = StickyColor.YELLOW;

  public Sticky(ThingContext thingContext, URL url) {
    super(thingContext, url);
    addFacetType(StickyFacet.class);
    updateThingInfo();
  }

  protected Facet newFacet(Class<? extends Facet> clazz) {
    if (clazz == StickyFacet.class) {
      return new StickyFacetImpl();
    }
    return null;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
    this.lastModified = new Date(System.currentTimeMillis());
    updateThingInfo();
    firePropertyChange("text", null, null);
    firePropertyChange("lastModified", null, null);
  }

  public StickyColor getColor() {
    return color;
  }

  public void setColor(StickyColor color) {
    this.color = color;
    this.lastModified = new Date(System.currentTimeMillis());
    updateThingInfo();
    firePropertyChange("color", null, null);
    firePropertyChange("lastModified", null, null);
  }

  public Date getLastModified() {
    return lastModified;
  }

  private void updateThingInfo() {
    setDisplayName(abbreviate(text, 64));
    setShortDescription(abbreviate(text, 256));
    setIcon("icons/sticky-" + color.name() + ".png");
    Stickies.getInstance().fireChange();
  }

  private String abbreviate(String text, int length) {
    String firstLine = getFirstLine(text);
    if (firstLine.length() == 0) return UNTITLED;
    length = Math.max(length, 3);
    return (firstLine.length() <= length) ?
        firstLine : firstLine.substring(0, length - 3) + "...";
  }

  private String getFirstLine(String text) {

    // For some reason, String.split() returns the trailing newlines, so
    // we must trim() them out "manually" after that operation.
    String[] lines = text.split("\\n");
    for (int k = 0; k < lines.length; k++) lines[k] = lines[k].trim();

    if (lines.length == 0) return "";

    int i = 0;
    while (i < lines.length - 1 && lines[i].length() == 0) i++;
    return lines[i].trim();
  }
}
