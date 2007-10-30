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

package com.google.thingbrowser.modules.slideshow.util;

import java.beans.PropertyChangeSupport;
import java.util.AbstractList;
import java.util.List;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class ModelList<T> extends AbstractList<T> {

  private final List<T> backingList;
  private final String propertyName;
  private final PropertyChangeSupport pcs;

  public ModelList(List<T> backingList, String propertyName, PropertyChangeSupport pcs) {
    this.backingList = backingList;
    this.propertyName = propertyName;
    this.pcs = pcs;
  }

  public T get(int index) {
    return backingList.get(index);
  }

  public int size() {
    return backingList.size();
  }

  public T set(int index, T element) {
    T result = backingList.set(index, element);
    pcs.firePropertyChange(propertyName, null, null);
    return result;
  }

  public void add(int index, T element) {
    backingList.add(index, element);
    pcs.firePropertyChange(propertyName, null, null);
  }

  public T remove(int index) {
    T result = backingList.remove(index);
    pcs.firePropertyChange(propertyName, null, null);
    return result;
  }
}