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

package com.google.thingbrowser.modules.html.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ObjectView;

import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.api.ThingContextSingleton;
import com.google.thingbrowser.api.ThingNavigationEvent;
import com.google.thingbrowser.api.ThingNavigationListener;
import com.google.thingbrowser.api.ThingView;
import com.google.thingbrowser.api.ViewFormat;

/**
 * A JEditorPane that accepts Thing embeddings of the form --
 * <pre>
 *   &lt;object
 *     classid="com.google.thingbrowser.modules.html.impl.EmbeddedThing"
 *     thingurl=" ... thing url here ..."&gt;
 *   &lt;/object&gt;
 * </pre>
 *
 * <p>Based on a blog post by Amy Fowler at
 * http://weblogs.java.net/blog/aim/archive/2007/07/embedding_swing.html
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class EmbeddedThingHtmlPane extends JEditorPane {
  private static final String THINGURL_ATTR = "thingurl";
  private static final String THINGWIDTH_ATTR = "thingwidth";
  private static final String THINGHEIGHT_ATTR = "thingheight";

  private class EmbeddedThingEditorKit extends HTMLEditorKit {
    public ViewFactory getViewFactory() {
      return new EmbeddedThingViewFactory();
    }       
  }

  private class EmbeddedThingViewFactory extends HTMLEditorKit.HTMLFactory {
    public View create(Element element) {
      AttributeSet attrs = element.getAttributes();
      Object elementName = attrs.getAttribute(
          AbstractDocument.ElementNameAttribute);
      Object o = (elementName != null) ?
          null : attrs.getAttribute(StyleConstants.NameAttribute);
      if (o instanceof HTML.Tag) {       
        if ((HTML.Tag) o == HTML.Tag.OBJECT) {
          return new EmbeddedThingView(element); 
        }
      }
      return super.create(element);
    }
  }

  protected class EmbeddedThingView extends ObjectView {
    public EmbeddedThingView(Element element) {
      super(element);
    }

    protected Component createComponent() {
      Thing thing = ThingContextSingleton.getThingContext().
          getThingResolverRegistry().
          getThing(ThingContextSingleton.getThingContext(), getThingUrl());
      ThingView thingView = ThingContextSingleton.getThingContext().
          getThingViewRegistry().newView(ViewFormat.FULL, thing);
      thingView.addThingNavigationListener(new ThingNavigationListener() {
        public void navigateToUrl(ThingNavigationEvent e) {
          fireHyperlinkUpdate(
              new HyperlinkEvent(this,
                                 HyperlinkEvent.EventType.ACTIVATED,
                                 e.getUrl()));
        }
      });
      thingView.initialize();
      ((Component)thingView).setPreferredSize(getEmbeddedSize());
      return (Component)thingView;
    }

    private URL getThingUrl() {
      Object attributeValue =
        getElement().getAttributes().getAttribute(THINGURL_ATTR);
      if (attributeValue instanceof String) {
        try {
          return new URL((String)attributeValue);
        } catch (MalformedURLException e) {
          throw new RuntimeException(e);
        }
      } else if (attributeValue instanceof URL) {
        return (URL)attributeValue;
      } else {
        return null;
      }
    }
    
    private Dimension getEmbeddedSize() {
      return new Dimension(
          Integer.parseInt((String)getElement().getAttributes().
              getAttribute(THINGWIDTH_ATTR)),
          Integer.parseInt((String)getElement().getAttributes().
              getAttribute(THINGHEIGHT_ATTR)));
    }
  }

  public EmbeddedThingHtmlPane() {       
    setContentType("text/html");
    setEditorKit(new EmbeddedThingEditorKit());
  }
}
