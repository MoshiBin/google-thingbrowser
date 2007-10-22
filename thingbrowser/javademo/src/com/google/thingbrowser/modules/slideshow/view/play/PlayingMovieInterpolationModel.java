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

package com.google.thingbrowser.modules.slideshow.view.play;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.thingbrowser.modules.sound.Player;

/**
 * A PlayingMovieModel is an optimized data structure supporting [near] realtime
 * playback of a slideshow "movie". Some client sets up the model by
 * adding successive "intervals" of visual and sound track information, The
 * player can then set the current time (relative to the starting point) and
 * query for what the state should be at that time. The PlayingMovieModel takes
 * care of interpolating as necessary, allowing the player to choose its
 * preferred frame rate independently.
 *
 * <p>This class does not fire any propertyChange events. The client is
 * responsible for querying the state as needed.
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class PlayingMovieInterpolationModel {

  private static class Interval {
    public final double duration;
    protected Interval(double duration) {
      this.duration = duration;
    }
  }

  private static class VisualInterval extends Interval {
    public final Image image;
    public final AffineTransform start;
    public final AffineTransform end;
    public VisualInterval(
        double duration,
        Image image,
        AffineTransform start,
        AffineTransform end) {
      super(duration);
      this.image = image;
      this.start = start;
      this.end = end;
    }
  }

  private static class SoundInterval extends Interval {
    public final Player sound;
    public SoundInterval(
        double duration,
        Player sound) {
      super(duration);
      this.sound = sound;
    }
  }

  private double currentTime = 0;
  private final SortedMap<Double, VisualInterval> visualIntervalByEndTime =
    new TreeMap<Double, VisualInterval>();
  private final SortedMap<Double, SoundInterval> soundIntervalByEndTime =
    new TreeMap<Double, SoundInterval>();
  private Image currentImage = null;
  private final AffineTransform currentTransform = new AffineTransform();
  private Player currentSound = null;

  /**
   * @see #getCurrentTime()
   */
  public void setCurrentTime(double currentTime) {
    this.currentTime = currentTime;
    recompute();
  }

  /**
   * The current time, where the starting point is by definition zero.
   */
  public double getCurrentTime() {
    return currentTime;
  }

  /**
   * Append an interval of visual animation of an image. The image is to be
   * animated (interpolated) between a starting and ending transform.
   *
   * @param duration the duration of the interval.
   * @param image the image to animate.
   * @param start the starting transform for the image.
   * @param end the ending transform for the image.
   */
  public void appendVisualInterval(
      double duration,
      Image image,
      AffineTransform start,
      AffineTransform end) {
    appendToMap(
        visualIntervalByEndTime,
        new VisualInterval(duration, image, start, end));
  }

  /**
   * Append an interval of sound playback. The sound is to be played for the
   * specified duration, then stopped. If the supplied Player contains a sound
   * clip shorter than the desired duration, the remainder of the duration will
   * be filled with silence.
   *
   * @param duration the duration of the interval.
   * @param sound a Player that can play the desired sound.
   */
  public void appendSoundInterval(
      double duration,
      Player sound) {
    appendToMap(
        soundIntervalByEndTime,
        new SoundInterval(duration, sound));
  }

  /**
   * Get the currently active image, based on the current time. Returns
   * null if there is no active visual interval at the current time.
   *
   * @see #setCurrentTime(double)
   */
  public Image getCurrentImage() {
    return currentImage;
  }

  /**
   * Get the currently active transform, based on the current time. Returns
   * an identity transform if there is no active visual interval at the current
   * time.
   *
   * <p>The returned object is a pointer to internal state; the client should
   * not modify it.
   *
   * @see #setCurrentTime(double)
   */
  public AffineTransform getCurrentTransform() {
    return currentTransform;
  }

  /**
   * Get the currently active sound clip, based on the current time. Returns
   * null if there is no active sound interval at the current time.
   *
   * <p>The client is responsible for calling play() or stop() on the returned
   * objects as needed.
   */
  public Player getCurrentSound() {
    return currentSound;
  }

  /**
   * Clear all contents of this object.
   */
  public void clear() {
    visualIntervalByEndTime.clear();
    soundIntervalByEndTime.clear();
    currentTime = 0;
    currentSound = null;
    currentImage = null;
    currentTransform.setToIdentity();
  }

  private void recompute() {
    recomputeVisual();
    recomputeSound();
  }

  private void recomputeVisual() {
    SortedMap<Double, VisualInterval> tail =
      visualIntervalByEndTime.tailMap(currentTime);
    if (tail.isEmpty()) {
      currentImage = null;
      currentTransform.setToIdentity();
    } else {
      VisualInterval i = tail.get(tail.firstKey());
      currentImage = i.image;
      interpolateTransform(
          currentTransform,
          i.start,
          i.end,
          0,
          i.duration,
          currentTime - (tail.firstKey() - i.duration));
    }
  }

  private void recomputeSound() {
    SortedMap<Double, SoundInterval> tail =
      soundIntervalByEndTime.tailMap(currentTime);
    if (tail.isEmpty()) {
      currentSound = null;
    } else {
      currentSound = tail.get(tail.firstKey()).sound;
    }
  }

  private static <T extends Interval> void appendToMap(
      SortedMap<Double, T> map,
      T interval) {
    double end = interval.duration;
    if (!map.isEmpty()) end += map.lastKey();
    map.put(end, interval);
  }

  private static void interpolateTransform(
      AffineTransform target,
      AffineTransform t0,
      AffineTransform t1,
      double x0,
      double x1,
      double x) {
    target.setTransform(
        interpolate(t0.getScaleX(), t1.getScaleX(), x0, x1, x),
        interpolate(t0.getShearY(), t1.getShearY(), x0, x1, x),
        interpolate(t0.getShearX(), t1.getShearX(), x0, x1, x),
        interpolate(t0.getScaleY(), t1.getScaleY(), x0, x1, x),
        interpolate(t0.getTranslateX(), t1.getTranslateX(), x0, x1, x),
        interpolate(t0.getTranslateY(), t1.getTranslateY(), x0, x1, x));
  }

  private static double interpolate(
      double y0,
      double y1,
      double x0,
      double x1,
      double x) {
    double theta = ((x - x0) / (x1 - x0) * Math.PI) + Math.PI;
    double factor = (Math.cos(theta) + 1) / 2;
    return y0 + factor * (y1 - y0);
  }
}
