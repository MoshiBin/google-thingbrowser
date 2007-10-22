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

package com.google.thingbrowser.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.thingbrowser.api.AbstractFacet;
import com.google.thingbrowser.api.MimeResourceFacet;
import com.google.thingbrowser.api.Thing;

/**
 * @author ihab@google.com (Ihab Awad)
 */
public class UrlRetrievalMimeResourceFacet extends AbstractFacet
    implements MimeResourceFacet {

  private static final File cacheDirectory =
    new File(System.getProperty("user.home") + File.separator + ".thingbrowser" + File.separator + "cache");

  private String cacheFilename = null;
  private String mimeType = null;

  public UrlRetrievalMimeResourceFacet(Thing thing) {
    super(thing);
  }

  public String getMimeType() {
    updateMimeType();
    return mimeType;
  }

  public InputStream newInputStream() {
    updateDataCache();
    try {
      return new FileInputStream(getCacheFile());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public File getFile() {
    updateDataCache();
    return getCacheFile();
  }

  private synchronized void updateMimeType() {
    if (mimeType != null) return;
    URLConnection connection;
    try {
      connection = getThing().getUrl().openConnection();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    mimeType = connection.getContentType();
  }

  private synchronized void updateDataCache() {
    if (cacheFilename != null) return;

    if (!cacheDirectory.exists()) {
      cacheDirectory.mkdirs();
    }

    cacheFilename = getCacheFilename();

    URLConnection connection;
    try {
      connection = getThing().getUrl().openConnection();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    long lastModified = connection.getLastModified();

    File cacheFile = getCacheFile();

    if (!cacheFile.exists() || lastModified > cacheFile.lastModified()) {
      updateCacheFile(connection, cacheFile, lastModified);
    }
  }

  private File getCacheFile() {
    return new File(cacheDirectory, cacheFilename);
  }

  private String getCacheFilename() {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new Error(e);  // We should always have MD5
    }

    byte[] input = getThing().getUrl().toExternalForm().getBytes();
    byte[] result = digest.digest(input);

    return bytesToHexString(result) + ".cache";
  }

  private void updateCacheFile(URLConnection connection, File cacheFile, long lastModified) {
    if (cacheFile.exists()) {
      cacheFile.delete();
    }

    try {
      InputStream is = connection.getInputStream();
      OutputStream os = new FileOutputStream(cacheFile);
      byte[] buffer = new byte[2048];

      while (true) {
        int n = is.read(buffer);
        if (n < 0) break;
        os.write(buffer, 0, n);
      }

      os.flush();
      os.close();
      is.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    cacheFile.setLastModified(lastModified);
  }

  private static String bytesToHexString(byte[] bytes) {
    StringBuffer sb = new StringBuffer(bytes.length * 2);
    for (int i = 0; i < bytes.length; i++) {
      sb.append(Integer.toHexString(bytes[i]));
    }
    return sb.toString();
  }
}
