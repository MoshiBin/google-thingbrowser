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

package com.google.thingbrowser.api;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class UrlUtilities {

  /**
   * Decode a URL's text and return a more "readable" form. This is a very
   * simple wrapper that merely hides some exception handling an defaults.
   *
   * @param url a URL.
   *
   * @return the readable (decoded) form of the URL.
   */
  public static String decode(URL url) {
    try {
      return URLDecoder.decode(url.toExternalForm(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // Should not happen -- UTF-8 support is required for Java
      throw new RuntimeException(e);
    }
  }

  /**
   * Given a URL, come up with a short "file name" derived from it. For example,
   * given the URL "http://example.com/foo/bar/baz.html", return "baz.html".
   * The returned string is not guaranteed to follow any precise semantics, but
   * should be good enough for use in user interface.
   *
   * @param url a URL.
   *
   * @return the short portion of the path.
   */
  public static String getShortPath(URL url) {
    String fullUrl = decode(url);
    if (!fullUrl.contains("/")) return fullUrl;
    int index = fullUrl.lastIndexOf("/");
    // TODO(ihab): Handle paths ending with "/" better!!
    if (index == fullUrl.length() - 1) return fullUrl;
    String result = fullUrl.substring(index + 1, fullUrl.length());
    if (result.length() == 0) return fullUrl;
    return result;
  }
}
