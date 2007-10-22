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

package com.google.thingbrowser.modules.sound.impl;

import com.google.thingbrowser.api.AbstractThingView;
import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.modules.sound.Player;
import com.google.thingbrowser.modules.sound.SoundFacet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class SimpleSoundPlayerView extends AbstractThingView {

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

  private Player player = null;
  private final JComponent play = makeButton("player_play");
  private final JComponent stop = makeButton("player_stop");
  private final JProgressBar progress = new JProgressBar();

  public SimpleSoundPlayerView(Thing model) {
    super(model);
  }

  public void initialize() {
    super.initialize();

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

    progress.getModel().setMinimum(0);
    progress.getModel().setMaximum(100);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setOpaque(false);
    buttonPanel.setLayout(new FlowLayout());
    buttonPanel.add(play);
    buttonPanel.add(stop);

    JPanel displayPanel = new JPanel();
    displayPanel.setOpaque(false);
    displayPanel.setLayout(new BorderLayout());
    displayPanel.add(progress, BorderLayout.CENTER);
    displayPanel.add(buttonPanel, BorderLayout.SOUTH);

    getContentPane().setBackground(Color.white);
    getContentPane().setOpaque(true);
    getContentPane().setLayout(centerLayout);
    getContentPane().add(displayPanel);
  }

  private void play() {
    if (player != null) return;
    player = getModel().getFacet(SoundFacet.class).newPlayer();
    player.play();
    progress.setIndeterminate(true);
  }

  private void stop() {
    if (player == null) return;
    player.stop();
    player = null;
    progress.setIndeterminate(false);
  }

  public void dispose() {
    stop();
  }

  private JComponent makeButton(String iconName) {
    URL url = getClass().getResource("icons/" + iconName + ".png");
    return new JLabel(new ImageIcon(url));
  }
}
