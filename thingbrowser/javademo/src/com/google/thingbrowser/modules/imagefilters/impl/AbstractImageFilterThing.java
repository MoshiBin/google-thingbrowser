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

import com.google.thingbrowser.api.AbstractFacet;
import com.google.thingbrowser.api.AbstractThing;
import com.google.thingbrowser.api.Facet;
import com.google.thingbrowser.api.ThingContext;
import com.google.thingbrowser.modules.images.ImageFilterFacet;
import com.google.thingbrowser.modules.images.ImageFilterFacet.FilterParam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public abstract class AbstractImageFilterThing extends AbstractThing {

  protected static final class SimpleFilterParam implements FilterParam {

    private final String name;
    private final String displayName;
    private final String shortDescription;
    private final double minValue;
    private final double maxValue;

    public SimpleFilterParam(String name, String displayName, String shortDescription, double minValue, double maxValue) {
      if (name == null) throw new NullPointerException();
      if (shortDescription == null) throw new NullPointerException();
      this.name = name;
      this.displayName = displayName;
      this.shortDescription = shortDescription;
      this.minValue = minValue;
      this.maxValue = maxValue;
    }

    public String getName() {
      return name;
    }

    public String getDisplayName() {
      return displayName;
    }

    public String getShortDescription() {
      return shortDescription;
    }

    public double getMinValue() {
      return minValue;
    }

    public double getMaxValue() {
      return maxValue;
    }

    public int hashCode() {
      return name.hashCode();
    }

    public boolean equals(Object o) {
      try {
        SimpleFilterParam p = (SimpleFilterParam)o;
        return name.equals(p.name);
      } catch (ClassCastException e) {
        return false;
      }
    }
  };

  private class LocalImageFilterFacet extends AbstractFacet implements ImageFilterFacet {

    public LocalImageFilterFacet() {
      super(AbstractImageFilterThing.this);
    }

    public Image getFilteredImage(Image source) {
      return AbstractImageFilterThing.this.getFilteredImage(getBufferedImage(source));
    }

    public Set<FilterParam> getFilterParams() {
      return filterParamValueByParam.keySet();
    }

    public double getParamValue(FilterParam param) {
      Double result = filterParamValueByParam.get(param);
      if (result == null) throw new RuntimeException();
      return result;
    }

    public void setParamValue(FilterParam param, double value) {
      if (!filterParamValueByParam.containsKey(param)) throw new RuntimeException();
      filterParamValueByParam.put(param, value);
      setUrlFromParams();
    }
  };

  private final Map<String, FilterParam> filterParamByName = new HashMap<String, FilterParam>();
  private final Map<FilterParam, Double> filterParamValueByParam = new HashMap<FilterParam, Double>();

  protected AbstractImageFilterThing(ThingContext thingContext, URL url) {
    super(thingContext, url);
    addFacetType(ImageFilterFacet.class);
    setIcon(getClass().getResource("icons/filter.png"));
  }

  protected void addFilterParam(SimpleFilterParam param, double defaultValue) {
    if (filterParamValueByParam.containsKey(param)) throw new RuntimeException();
    filterParamValueByParam.put(param, defaultValue);
    filterParamByName.put(param.getName(), param);
  }

  protected Facet newFacet(Class<? extends Facet> clazz) {
    if (clazz == ImageFilterFacet.class) return new LocalImageFilterFacet();
    return null;
  }

  protected void setParamsFromUrl() {
    String params = getUrl().toExternalForm();

    int indexOfQ = params.indexOf('?');
    if (indexOfQ == -1) {
      return;
    }

    params = params.substring(indexOfQ + 1, params.length());
    String[] keyValuePairs = params.split("&");

    for (String kvp : keyValuePairs) {
      String[] keyValue = kvp.split("=");
      String key = keyValue[0];

      double value;
      try {
        value = Double.parseDouble(keyValue[1]);
      } catch (NumberFormatException e) {
        continue;
      }

      FilterParam p = filterParamByName.get(key);
      if (p == null) continue;

      filterParamValueByParam.put(p, value);
    }
  }

  protected void setUrlFromParams() {
    String base = getUrl().toExternalForm();

    int indexOfQ = base.indexOf('?');
    if (indexOfQ != -1) {
      base = base.substring(0, indexOfQ);
    }

    List<String> params = new ArrayList<String>();

    for (FilterParam p : filterParamValueByParam.keySet()) {
      params.add(p.getName() + '=' + Double.toString(filterParamValueByParam.get(p)));
    }

    StringBuffer paramsString = new StringBuffer();

    for (int i = 0; i < params.size(); i++) {
      paramsString.append(params.get(i));
      if (i < params.size() - 1) paramsString.append('&');
    }

    String result = base + '?' + paramsString;

    try {
      setUrl(new URL(result));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  protected abstract Image getFilteredImage(BufferedImage source);

  private static BufferedImage getBufferedImage(Image source) {
    // Note that this method *always* creates a new source image. Not doing
    // so, when the original source is a BufferedImage anyway, caused spurious
    // errors with ConvolveImageOp, which were not debuggable since that called
    // to a native method in a Sun "ImageLib" class for which source was not
    // supplied. The error message was nonspecific.
    //
    // TODO(ihab): Debug this problem better to avoid creating a new source
    // when the source is already a BufferedImage.
    BufferedImage bufferedSource = new BufferedImage(
        source.getWidth(null),
        source.getHeight(null),
        BufferedImage.TYPE_INT_RGB);
    Graphics2D g = bufferedSource.createGraphics();
    g.setColor(Color.white);
    g.fillRect(
        0,
        0,
        bufferedSource.getWidth(),
        bufferedSource.getHeight());
    g.drawImage(
        source,
        0,
        0,
        bufferedSource.getWidth(),
        bufferedSource.getHeight(),
        null);
    g.dispose();
    return bufferedSource;
  }
}
