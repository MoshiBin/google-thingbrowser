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

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivityScheduler;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class ActivityQueue {

  private final PActivityDelegate delegate = new PActivityDelegate() {

    public void activityFinished(PActivity activity) {
      activity.setDelegate(null);
      activities.remove(activity);
      if (!stopping) start();
    }

    public void activityStarted(PActivity activity) {}

    public void activityStepped(PActivity activity) {}
  };

  private boolean stopping = false;
  private final PActivityScheduler scheduler;
  private final List<PActivity> activities = new ArrayList<PActivity>();

  public ActivityQueue(PActivityScheduler scheduler) {
    this.scheduler = scheduler;
  }

  public void addActivity(PActivity activity) {
    activities.add(activity);
  }

  public void clear() {
    if (activities.size() == 0) return;
    stopping = true;
    activities.get(0).terminate();
    activities.clear();
    stopping = false;
  }

  public void start() {
    if (activities.size() == 0) return;
    if (activities.get(0).isStepping()) return;
    activities.get(0).setDelegate(delegate);
    activities.get(0).setStartTime(System.currentTimeMillis());
    scheduler.addActivity(activities.get(0));
  }
}
