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

import java.io.File;
import java.io.InputStream;

/**
 * A <code>Facet</code> representing a <code>Thing</code> as
 * a MIME typed stream.
 *
 * @author ihab@google.com (Ihab Awad)
 */
public interface MimeResourceFacet extends Facet {

  /**
   * @return the MIME type for this resource.
   */
  String getMimeType();

  /**
   * Create and return a stream from which the information in
   * this MIME resource may be read.
   *
   * @return a stream, which the client should <code>close()</code> when done.
   */
  InputStream newInputStream();

  /**
   * Return the path to a File containing the contents of this MIME resource.
   * The file may or may not be the actual path from which the MIME resource
   * was loaded. This is provided merely to support clients who, in order to
   * comply with some legacy API, must obtain their data from a File. New code
   * is *very* strongly discouraged from deliberately relying on this feature.
   *
   * @return a File from which the contents of this MIME resource may be read.
   */
  File getFile();
}
