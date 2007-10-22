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

package com.google.thingbrowser.modules.slideshow.view;

import java.awt.Color;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class ViewConstants {

  public static final int DEFAULT_TABLE_PADDING = 3;

  public static final double PLAY_FRAMES_PER_SECOND = 30;

  public static final double DEFAULT_DURATION_SCALING = 2.0;

  public static final long THROB_MILLISECONDS = 500;

  public static final Color SHADOW_COLOR = Color.lightGray;
  public static final Color BACKGROUND_COLOR = Color.white;

  public static final double ROUNDED_RADIUS = 8;
  public static final double SHADOW_OFFSET = 2;

  public static final double TRACK_EDITOR_TOP_GAP = 5;
  public static final double TRACK_EDITOR_MINIMUM_HEIGHT = 90;

  public static final double TRACK_ELEMENT_HEIGHT = 44;

  public static final Color VISUAL_TRACK_ELEMENT_COLOR = Color.yellow;
  public static final Color SOUND_TRACK_ELEMENT_COLOR = Color.pink;

  public static final double TRACK_ZERO_GAP = 24;
  public static final double TRACK_DROP_GUTTER = 24;
  public static final double TRACK_STAGGER_GAP = 8;

  public static final double TRACK_ELEMENT_DROP_TARGET_WIDTH = 40;
  public static final double TRACK_ELEMENT_INTERNAL_GAP = 6;

  public static final Color HIGHLIGHT_BORDER_COLOR = new Color(0x00, 0xff, 0x00, 0x80);
  public static final double HIGHLIGHT_BORDER_WIDTH = 4;

  public static final double KEYPOINT_THUMBNAIL_SIZE = 32;
  public static final Color KEYPOINT_THUMBNAIL_DEFAULT_FOREGROUND_COLOR = Color.gray;
  public static final Color KEYPOINT_THUMBNAIL_HIGHLIGHT_FOREGROUND_COLOR = Color.black;

  public static final double EDITOR_SPLIT_DIVIDER_LOCATION = 0.80;
  public static final int EDITOR_SPLIT_DIVIDER_SIZE = 3;
}
