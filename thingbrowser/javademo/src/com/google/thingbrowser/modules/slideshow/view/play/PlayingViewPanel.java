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

import com.google.thingbrowser.api.ThingContext;
import com.google.thingbrowser.modules.slideshow.model.ComposedMovieFacet;
import com.google.thingbrowser.modules.slideshow.model.Keypoint;
import com.google.thingbrowser.modules.slideshow.model.SoundTrackElement;
import com.google.thingbrowser.modules.slideshow.model.VisualTrackElement;
import com.google.thingbrowser.modules.slideshow.util.ViewPanel;
import com.google.thingbrowser.modules.slideshow.view.ViewConstants;
import com.google.thingbrowser.modules.sound.Player;
import com.google.thingbrowser.modules.sound.SoundFacet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class PlayingViewPanel extends ViewPanel<ComposedMovieFacet> {

  private LayoutManager centerLayout = new LayoutManager() {

    public void addLayoutComponent(String name, Component comp) {}

    public void removeLayoutComponent(Component comp) {}

    public void layoutContainer(Container parent) {
      Component child = parent.getComponent(0);
      child.setSize(child.getPreferredSize());
      child.setLocation(
          (parent.getWidth() - child.getWidth()) / 2,
          (parent.getHeight() - child.getHeight()) / 2);
    }

    public Dimension minimumLayoutSize(Container parent) {
      return new Dimension(0, 0);
    }

    public Dimension preferredLayoutSize(Container parent) {
      return minimumLayoutSize(parent);
    }
  };

  private final JComponent moviePane = new JPanel() {

    {
      Dimension size = new Dimension(
          (int)ComposedMovieFacet.DEFAULT_MOVIE_WIDTH,
          (int)ComposedMovieFacet.DEFAULT_MOVIE_HEIGHT);
      setPreferredSize(size);
      setMinimumSize(size);
      setMaximumSize(size);
      setBackground(Color.lightGray);
      setOpaque(true);
    }

    public void paint(Graphics g) {
      if (interpolationModel.getCurrentImage() == null || interpolationModel.getCurrentTransform() == null) {
        super.paint(g);
      } else {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawRenderedImage((BufferedImage)interpolationModel.getCurrentImage(), interpolationModel.getCurrentTransform());
      }
    }
  };

  private final ActionListener timerListener = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      updateFrame();
    }
  };

  private final PlayingMovieInterpolationModel interpolationModel =
    new PlayingMovieInterpolationModel();
  private final JComponent play = makeButton("player_play.png");
  private final JComponent stop = makeButton("player_stop.png");
  private Player currentSound = null;
  private double startTime = 0;
  private final Timer timer = new Timer(
      (int)(1000.0 / ViewConstants.PLAY_FRAMES_PER_SECOND),
      timerListener);

  public PlayingViewPanel(ThingContext thingContext, ComposedMovieFacet model) {
    super(thingContext, model);
  }

  protected void initializeContents() {

    JPanel buttonPanel = new JPanel();
    buttonPanel.setOpaque(false);
    buttonPanel.setLayout(new FlowLayout());
    buttonPanel.add(play);
    buttonPanel.add(stop);

    JPanel displayPanel = new JPanel();
    displayPanel.setOpaque(false);
    displayPanel.setLayout(new BorderLayout());
    displayPanel.add(moviePane, BorderLayout.CENTER);
    displayPanel.add(buttonPanel, BorderLayout.SOUTH);

    play.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        play();
      }
    });

    stop.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        stop();
      }
    });

    setBackground(Color.white);
    setLayout(centerLayout);
    add(displayPanel);
    setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
    setLocation(0, 0);
    doLayout();
  }

  private void play() {
    prepare();
    startTime = System.currentTimeMillis() / 1000.0;
    timer.start();
  }

  private void stop() {
    timer.stop();
    if (currentSound != null) {
      currentSound.stop();
      currentSound = null;
    }
    interpolationModel.clear();
    moviePane.repaint();
  }

  private void updateFrame() {
    interpolationModel.setCurrentTime(System.currentTimeMillis() / 1000.0 - startTime);
    if (currentSound != interpolationModel.getCurrentSound()) {
      if (currentSound != null) {
        currentSound.stop();
      }
      currentSound = interpolationModel.getCurrentSound();
      if (currentSound != null) {
        currentSound.play();
      }
    }
    moviePane.repaint();
  }

  private void prepare() {

    interpolationModel.clear();

    for (VisualTrackElement element : getModel().getVisualTrack().getElements()) {

      List<Keypoint> keypoints = new ArrayList<Keypoint>();
      keypoints.add(element.getFirst());
      keypoints.addAll(element.getIntermediaries());
      keypoints.add(element.getLast());

      for (int i = 1; i < keypoints.size(); i++) {
        interpolationModel.appendVisualInterval(
            keypoints.get(i).getTimeOffset() - keypoints.get(i - 1).getTimeOffset(),
            element.getFilteredImage(),
            getKeypointTransform(keypoints.get(i - 1)),
            getKeypointTransform(keypoints.get(i)));
      }
    }

    for (SoundTrackElement element : getModel().getSoundTrack().getElements()) {
      interpolationModel.appendSoundInterval(
          element.getDuration(),
          element.getSourceThing().getFacet(SoundFacet.class).newPlayer());
    }
  }

  private AffineTransform getKeypointTransform(Keypoint k) {
    AffineTransform t = new AffineTransform();
    double scaleFactorX = ComposedMovieFacet.DEFAULT_MOVIE_WIDTH / k.getBoundsReference().getWidth();
    double scaleFactorY = ComposedMovieFacet.DEFAULT_MOVIE_HEIGHT / k.getBoundsReference().getHeight();
    t.scale(
        scaleFactorX,
        scaleFactorY);
    t.translate(
        - k.getBoundsReference().getMinX(),
        - k.getBoundsReference().getMinY());
    return t;
  }

  public void dispose() {
  }

  private JComponent makeButton(String iconName) {
    URL url = getClass().getResource("icons/" + iconName);
    return new JLabel(new ImageIcon(url));
  }
}
