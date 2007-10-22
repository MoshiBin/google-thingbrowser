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

package com.google.thingbrowser.modules.proteins.impl.model;

import com.google.thingbrowser.modules.proteins.impl.visitors.CenterOfMassVisitor;
import com.google.thingbrowser.modules.proteins.impl.visitors.RenderingVisitor;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.PDBFileReader;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.Raster;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

/**
 *
 * @author jregan@google.com (Jeffrey Regan)
 */
public class GlowingBalls extends BaseBalls {

	private static final boolean DO_PROTEIN = true;

	private final Canvas3D canvas3d;

	private static final double MAGIC_SCALE = 0.05;

	// The smaller this is, either we are further away or the universe is
	// smaller.
	// Nobody knows what it means...

	public Canvas3D getCanvas3D() {
		return canvas3d;
	}

	public BufferedImage captureImage() {
		Rectangle rectangle = getCanvas3D().getBounds();

		GraphicsContext3D ctx = getCanvas3D().getGraphicsContext3D();

		Raster ras = new Raster(new Point3f(-1.0f, -1.0f, -1.0f),
				Raster.RASTER_COLOR, 0, 0, rectangle.width, rectangle.height,
				new ImageComponent2D(ImageComponent.FORMAT_RGB,
						new BufferedImage(rectangle.width, rectangle.height,
								BufferedImage.TYPE_INT_RGB)), null);

		ctx.readRaster(ras);

		ImageComponent2D imageComponent2D = ras.getImage();
		return imageComponent2D.getImage();
	}

	public GlowingBalls(VisitableStructure structure) {
		canvas3d = make3dCanvas();
		JPanel panel = makePanel();
		panel.add(canvas3d, java.awt.BorderLayout.CENTER);

		SimpleUniverse universe = makeUniverse(canvas3d);

		ViewingPlatform platform = universe.getViewingPlatform();

		BranchGroup rootObject = new BranchGroup();
		OrbitBehavior orbit = new OrbitBehavior(canvas3d,
				OrbitBehavior.REVERSE_ALL);

		TransformGroup vpTrans = platform.getViewPlatformTransform();
		TransformGroup scene;
		if (DO_PROTEIN) {
			scene = createProteinScene(structure,platform, orbit);
		} else {
			scene = createOrbitingScene(vpTrans);
		}

		scaleTheScene(scene, MAGIC_SCALE);

		rootObject.addChild(scene);

		rootObject.compile();
		// presumably optimizes

		universe.addBranchGraph(rootObject);
	}

	private TransformGroup createProteinScene(VisitableStructure structure,ViewingPlatform platform,
			OrbitBehavior orbit) {

		TransformGroup scene = new TransformGroup();
		// Holds everything in scene;

		CenterOfMassVisitor comVisitor = new CenterOfMassVisitor();
		structure.accept(comVisitor);
		Vector3d centerOfMass = comVisitor.getCenterOfMass();
		System.out.println("COM = " + centerOfMass);

		RenderingVisitor visitor = new RenderingVisitor(scene, centerOfMass);
		structure.accept(visitor);

		double maxDist = visitor.getMaxDistFromCenterOfMass();
		double radius = maxDist + (maxDist / 20.0);

		BoundingSphere bounds = new BoundingSphere(ORIGIN, radius);
		orbit.setSchedulingBounds(bounds);
		platform.setViewPlatformBehavior(orbit);

		Vector3d lightPos1 = new Vector3d(0.0, 0.0, maxDist);
		Vector3d lightPos2 = new Vector3d(0.0, 0.0, 0.0);
		Vector3d lightPos3 = new Vector3d(0.0, 0.0, -maxDist);

		TransformGroup light1 = makeMovableLight(bounds, lightPos1, colorRed);
		TransformGroup light2 = makeMovableLight(bounds, lightPos2, colorGreen);
		TransformGroup light3 = makeMovableLight(bounds, lightPos3, colorBlue);

		scene.addChild(light1);
		scene.addChild(light2);
		scene.addChild(light3);

		// PositionInterpolator sceneShifter = addRotators(platform
		// .getViewPlatformTransform(), bounds, light1, light2);
		// scene.addChild(sceneShifter);

		scene.addChild(makeBackground(bounds));
		scene.addChild(makeAmbientLight(bounds));

		return scene;
	}

	public TransformGroup createOrbitingScene(TransformGroup vpTrans) {

		TransformGroup scene = new TransformGroup();
		// Holds everything in scene;

		// Create a bounds for everything
		BoundingSphere bounds = new BoundingSphere(ORIGIN, BOUNDING_RADIUS);

		// Create the transform group node for the each light and initialize
		// it to the identity. Enable the TRANSFORM_WRITE capability so that
		// our behavior code can modify it at runtime. Add them to the root
		// of the subgraph.
		Vector3d lightPos1 = new Vector3d(0.0, 0.0, LIGHT_ORBIT_RADIUS);
		Vector3d lightPos2 = new Vector3d(LIGHT_ORBIT_RADIUS / 5,
				LIGHT_ORBIT_RADIUS / 4, LIGHT_ORBIT_RADIUS / 2);

		TransformGroup light1 = makeMovableLight(bounds, lightPos1, colorRed);
		TransformGroup light2 = makeMovableLight(bounds, lightPos2, colorGreen);

		scene.addChild(light1);
		scene.addChild(light2);

		PositionInterpolator sceneShifter = addRotators(vpTrans, bounds,
				light1, light2);
		scene.addChild(sceneShifter);

		scene.addChild(makeBackground(bounds));
		scene.addChild(makeCentralSphere());
		scene.addChild(makeAmbientLight(bounds));

		return scene;
	}
}
