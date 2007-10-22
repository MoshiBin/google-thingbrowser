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

package com.google.thingbrowser.modules.images.impl;

import com.google.thingbrowser.api.AbstractThingView;
import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.api.UrlLinkComponent;
import com.google.thingbrowser.modules.images.ImageFilterFacet;
import com.google.thingbrowser.modules.images.ImageFilterFacet.FilterParam;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class ImageFilterView extends AbstractThingView {

  // TODO(ihab): Fix this to listen to the ** model ** for filter change
  // events, rather than assuming that we always control the model.

  private final PropertyChangeListener urlListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      permalink.setUrl(getModel().getUrl());
    }
  };

  private final ImageIcon sourceImage =
    new ImageIcon(ImageFilterView.class.getResource("icons/sampleImage.jpg"));
  private final ImageIcon arrowImage =
    new ImageIcon(ImageFilterView.class.getResource("icons/2rightarrow.png"));

  private final ImageIcon resultImage = new ImageIcon();
  private final UrlLinkComponent permalink;

  public ImageFilterView(Thing model) {
    super(model);
    permalink = new UrlLinkComponent(this, model.getUrl(), "Permalink&raquo;", "Navigate to permalink");
  }

  public void initialize() {
    super.initialize();

    addUrlLinkComponent(permalink);

    getModel().addPropertyChangeListener("url", urlListener);

    JLabel sourceImageLabel = new JLabel(sourceImage);
    JLabel arrowImageLabel = new JLabel(arrowImage);
    JLabel resultImageLabel = new JLabel(resultImage);

    JLabel nameLabel = new JLabel("Name:");
    nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
    JLabel descriptionLabel = new JLabel("Description:");
    descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.BOLD));

    JLabel nameValue = new JLabel(getModel().getDisplayName());
    JLabel descriptionValue = new JLabel(getModel().getShortDescription());

    JPanel parametersPanel = createParametersPanel();

    JPanel bottomFiller = new JPanel();
    bottomFiller.setOpaque(false);
    bottomFiller.setPreferredSize(new Dimension(0, Integer.MAX_VALUE));

    JPanel rightFiller = new JPanel();
    rightFiller.setOpaque(false);
    rightFiller.setPreferredSize(new Dimension(Integer.MAX_VALUE, 0));

    getContentPane().setBackground(Color.white);
    getContentPane().setOpaque(true);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);

    layout.setAutoCreateContainerGaps(true);
    layout.setAutoCreateGaps(true);

    GroupLayout.Group hGroup = layout.createSequentialGroup().
        addGroup(layout.createParallelGroup().
            addGroup(layout.createSequentialGroup().
                addComponent(sourceImageLabel).
                addComponent(arrowImageLabel).
                addComponent(resultImageLabel)).
            addGroup(layout.createSequentialGroup().
                addGroup(layout.createParallelGroup().
                    addComponent(nameLabel).
                    addComponent(descriptionLabel)).
                addGroup(layout.createParallelGroup().
                    addComponent(nameValue).
                    addComponent(descriptionValue))).
            addComponent(parametersPanel).
            addComponent(bottomFiller)).
        addComponent(rightFiller);

    layout.setHorizontalGroup(hGroup);

    GroupLayout.Group vGroup = layout.createParallelGroup().
        addGroup(layout.createSequentialGroup().
            addGroup(layout.createParallelGroup(Alignment.CENTER).
                addComponent(sourceImageLabel).
                addComponent(arrowImageLabel).
                addComponent(resultImageLabel)).
            addGroup(layout.createParallelGroup().
                addGroup(layout.createSequentialGroup().
                    addComponent(nameLabel).
                    addComponent(descriptionLabel)).
                addGroup(layout.createSequentialGroup().
                    addComponent(nameValue).
                    addComponent(descriptionValue))).
            addComponent(parametersPanel).
            addComponent(bottomFiller)).
        addComponent(rightFiller);

    layout.setVerticalGroup(vGroup);

    updateImage();
  }

  public void dispose() {
    getModel().removePropertyChangeListener("url", urlListener);
  }

  private JPanel createParametersPanel() {
    JPanel panel = new JPanel();

    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createTitledBorder("Parameters"));

    GroupLayout layout = new GroupLayout(panel);
    panel.setLayout(layout);

    layout.setAutoCreateContainerGaps(true);
    layout.setAutoCreateGaps(true);

    GroupLayout.Group hGroup = layout.createParallelGroup();
    GroupLayout.Group vGroup = layout.createSequentialGroup();

    final ImageFilterFacet facet = getModel().getFacet(ImageFilterFacet.class);

    for (final FilterParam p : facet.getFilterParams()) {

      JLabel name = new JLabel(p.getDisplayName() + ":");
      name.setToolTipText(p.getShortDescription());

      JLabel min = new JLabel(Double.toString(p.getMinValue()));
      JLabel max = new JLabel(Double.toString(p.getMaxValue()));

      final JSlider slider = new JSlider(
          JSlider.HORIZONTAL,
          (int)p.getMinValue(),
          (int)p.getMaxValue(),
          (int)facet.getParamValue(p));

      slider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          facet.setParamValue(p, slider.getValue());
          updateImage();
        }
      });

      hGroup.addGroup(layout.createSequentialGroup().
          addComponent(name).
          addComponent(min).
          addComponent(slider).
          addComponent(max));

      vGroup.addGroup(layout.createParallelGroup().
          addComponent(name).
          addComponent(min).
          addComponent(slider).
          addComponent(max));
    }

    layout.setHorizontalGroup(hGroup);
    layout.setVerticalGroup(vGroup);

    return panel;
  }

  private void updateImage() {
    ImageFilterFacet facet = getModel().getFacet(ImageFilterFacet.class);
    resultImage.setImage(facet.getFilteredImage(sourceImage.getImage()));
    repaint();
  }
}
