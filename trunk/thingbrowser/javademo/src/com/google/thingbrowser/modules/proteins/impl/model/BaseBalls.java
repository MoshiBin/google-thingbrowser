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

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

import java.awt.GraphicsConfiguration;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.SpotLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author jregan@google.com (Jeffrey Regan)
 */
public class BaseBalls extends JFrame {

  protected static final Point3d ORIGIN = new Point3d(0.0, 0.0, 0.0);
  protected static final float CENTRAL_SPHERE_RADIUS = 1.0f;
  protected static final double LIGHT_ORBIT_RADIUS = 3 * CENTRAL_SPHERE_RADIUS;

  protected static final int NUM_POLYGONS = 80;
  protected static final float LIGHT_BALL_RADIUS = 0.2f;
  protected static final double BOUNDING_RADIUS = 100.0;

  protected static final int WINDOW_WIDTH_IN_PIXELS = 700;
  protected static final int WINDOW_HEIGHT_IN_PIXELS = 700;
  protected static final String TITLE = "Glowing Balls";

  protected static final Color3f colorRed = new Color3f(1.0f, 0.0f, 0.0f);
  protected static final Color3f colorGreen = new Color3f(0.0f, 1.0f, 0.0f);
  protected static final Color3f colorBlue = new Color3f(0.0f, 0.0f, 1.0f);
  protected static final Color3f colorGray = new Color3f(0.6f, 0.6f, 0.6f);
  protected static final Color3f colorBlack = new Color3f(0.0f, 0.0f, 0.0f);
  protected static final Color3f colorWhite = new Color3f(1.0f, 1.0f, 1.0f);

  protected static final Color3f colorAmbient = new Color3f(0.2f, 0.2f, 0.2f);
  protected static final Color3f colorBackground = new Color3f(0.05f, 0.05f, 0.2f);

  protected static final Point3f lPoint = new Point3f(0.0f, 0.0f, 0.0f);
  protected static final Point3f atten = new Point3f(1.0f, 0.0f, 0.0f);

  protected enum LightSource {
    DIRECTIONAL, POINT, SPOT
  }
  protected LightSource lightType = LightSource.POINT;

  protected AmbientLight makeAmbientLight(BoundingSphere bounds) {
    AmbientLight ambientLight = new AmbientLight(colorAmbient);
    ambientLight.setInfluencingBounds(bounds);
    return ambientLight;
  }

  protected Background makeBackground(BoundingSphere bounds) {
    Background bg = new Background(colorBackground);
    bg.setApplicationBounds(bounds);
    return bg;
  }

  protected Sphere makeCentralSphere() {
    final Color3f ambientColor = colorGray;
    final Color3f emmisiveColor = colorBlack;
    final Color3f diffuseColor = colorGray;
    final Color3f specularColor = colorWhite;
    final float shininess = 100.0f;
    Material material = new Material(ambientColor, emmisiveColor, diffuseColor,
        specularColor, shininess);
    material.setLightingEnable(true);
    Appearance appearance = new Appearance();
    appearance.setMaterial(material);
    Sphere sph = new Sphere(CENTRAL_SPHERE_RADIUS, Sphere.GENERATE_NORMALS,
        NUM_POLYGONS, appearance);
    return sph;
  }

  protected TransformGroup makeMovableLight(BoundingSphere bounds,
      final Vector3d position1, final Color3f color) {
    TransformGroup light1RotTrans = new TransformGroup();
    light1RotTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    TransformGroup transGroup1 = makeNiceLight(bounds, position1, color);
    light1RotTrans.addChild(transGroup1);
    return light1RotTrans;
  }

  protected TransformGroup makeNiceLight(BoundingSphere bounds,
      final Vector3d lightPosition, final Color3f color) {
    Vector3f direction = new Vector3f(lightPosition);
    direction.negate();

    Light light = null;
    switch (lightType) {
    case DIRECTIONAL:
      light = new DirectionalLight(color, direction);
      break;
    case POINT:
      light = new PointLight(color, lPoint, atten);
      break;
    case SPOT:
      light = new SpotLight(color, lPoint, atten, direction,
          25.0f * (float) Math.PI / 180.0f, 10.0f);
      break;
    }

    light.setInfluencingBounds(bounds);
    Transform3D transform = new Transform3D();
    transform.set(lightPosition);
    TransformGroup transGroup = new TransformGroup(transform);
    transGroup.addChild(light);
    transGroup.addChild(makeLightBall(color));
    return transGroup;
  }

  protected Sphere makeLightBall(Color3f color) {
    ColoringAttributes colAttr = new ColoringAttributes();
    colAttr.setColor(color);
    Appearance appL2 = new Appearance();
    appL2.setColoringAttributes(colAttr);
    Sphere sphere2 = new Sphere(LIGHT_BALL_RADIUS, appL2);
    return sphere2;
  }

  protected PositionInterpolator addRotators(TransformGroup vpTrans,
      BoundingSphere bounds, TransformGroup light1RotTrans,
      TransformGroup light2RotTrans) {

    // Create a new Behavior object that will perform the desired
    // operation on the specified transform object and add it into the
    // scene graph.
    Transform3D yAxis = new Transform3D();
    Alpha rotor1Alpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 4000, 0,
        0, 0, 0, 0);
    RotationInterpolator rotator1 = new RotationInterpolator(rotor1Alpha,
        light1RotTrans, yAxis, 0.0f, (float) Math.PI * 2.0f);
    rotator1.setSchedulingBounds(bounds);
    light1RotTrans.addChild(rotator1);

    // Create a new Behavior object that will perform the desired
    // operation on the specified transform object and add it into the
    // scene graph.
    Alpha rotor2Alpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 1000, 0,
        0, 0, 0, 0);
    RotationInterpolator rotator2 = new RotationInterpolator(rotor2Alpha,
        light2RotTrans, yAxis, 0.0f, 0.0f);
    rotator2.setSchedulingBounds(bounds);
    light2RotTrans.addChild(rotator2);

    // Create a position interpolator and attach it to the view
    // platform
    Transform3D axisOfTranslation = new Transform3D();
    Alpha transAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE
        | Alpha.DECREASING_ENABLE, 0, 0, 5000, 0, 0, 5000, 0, 0);
    axisOfTranslation.rotY(-Math.PI / 2.0);

    PositionInterpolator translator = new PositionInterpolator(transAlpha,
        vpTrans, axisOfTranslation, 2.0f, 3.5f);
    translator.setSchedulingBounds(bounds);

    return translator;
  }

  protected void scaleTheScene(TransformGroup scene, double simpleScaling) {
    Transform3D t3d = new Transform3D();
    t3d.setScale(simpleScaling);
    scene.setTransform(t3d);
  }

  protected Canvas3D make3dCanvas() {
    // Get the preferred graphics configuration for the default screen
    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

    // Create a Canvas3D using the preferred configuration
    Canvas3D canvas3d = new Canvas3D(config);
    return canvas3d;
  }

  protected JPanel makePanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new java.awt.BorderLayout());
    panel.setPreferredSize(new java.awt.Dimension(WINDOW_WIDTH_IN_PIXELS,
        WINDOW_HEIGHT_IN_PIXELS));

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle(TITLE);
    getContentPane().add(panel, java.awt.BorderLayout.CENTER);

    pack();
    return panel;
  }

  protected SimpleUniverse makeUniverse(Canvas3D embeddedCanvas) {
    SimpleUniverse univ = new SimpleUniverse(embeddedCanvas);

    // This will move the ViewPlatform back a bit so the
    // objects in the scene can be viewed.
    univ.getViewingPlatform().setNominalViewingTransform();

    // Ensure at least 5 msec per frame (i.e., < 200Hz)
    univ.getViewer().getView().setMinimumFrameCycleTime(5);
    return univ;
  }

}
