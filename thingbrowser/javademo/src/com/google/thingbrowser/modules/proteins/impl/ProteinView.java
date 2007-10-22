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

package com.google.thingbrowser.modules.proteins.impl;

import com.google.thingbrowser.api.AbstractThingView;
import com.google.thingbrowser.api.Thing;
import com.google.thingbrowser.modules.images.ImageFacet;
import com.google.thingbrowser.modules.proteins.ProteinFacet;
import com.google.thingbrowser.modules.proteins.impl.model.GlowingBalls;
import com.google.thingbrowser.modules.proteins.impl.model.VisitableStructure;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The "3D" view of the protein
 *
 * @author jregan@google.com (Jeffrey Regan)
 */
public class ProteinView extends AbstractThingView {

  private GlowingBalls balls;

	public ProteinView(Thing model) {
		super(model);
	}

	public void initialize() {
		super.initialize();

		ProteinFacet proteinFacet = getModel().getFacet(ProteinFacet.class);
		VisitableStructure structure = proteinFacet.getStructure();
    balls = new GlowingBalls(structure);

    getContentPane().setLayout(new GridLayout(1, 1));
		getContentPane().add(balls.getCanvas3D());

    balls.getCanvas3D().addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent e) { refreshImage(); }
    });
	}

  private void refreshImage() {
    ((ProteinImageFacetImpl)getModel().getFacet(ImageFacet.class)).
      setImage(balls.captureImage());
  }

	public void dispose() {
	}
}
