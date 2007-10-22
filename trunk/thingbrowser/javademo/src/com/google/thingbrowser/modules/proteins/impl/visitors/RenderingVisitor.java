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

package com.google.thingbrowser.modules.proteins.impl.visitors;

import com.sun.j3d.utils.geometry.Sphere;

import org.biojava.bio.structure.Atom;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

/**
 *
 * @author jregan@google.com (Jeffrey Regan)
 */
public class RenderingVisitor extends AlphaCarbonVisitor {

  private static final float carbonVanDerWallsRadiusInAngstroms = 1.7f;
  private static final int NUM_POLYGONS = 80;
  private static final Appearance APPEARANCE = makeAppearance();

  private static final Color3f colorRed = new Color3f(1.0f, 0.0f, 0.0f);
  private static final Color3f colorGreen = new Color3f(0.0f, 1.0f, 0.0f);
  private static final Color3f colorGray = new Color3f(0.6f, 0.6f, 0.6f);
  private static final Color3f colorBlack = new Color3f(0.0f, 0.0f, 0.0f);
  private static final Color3f colorWhite = new Color3f(1.0f, 1.0f, 1.0f);

  private final Vector3d centerOfMass;

  private double maxDistSquared = 0;

  private final TransformGroup scene;

  public RenderingVisitor(TransformGroup scene, Vector3d centerOfMass) {
    this.scene = scene;
    this.centerOfMass = centerOfMass;
  }

  public double getMaxDistFromCenterOfMass() {
    return Math.sqrt(maxDistSquared);
  }

  @Override
  protected void processAtom(Atom alphaCarbon) {

    double dx = alphaCarbon.getX() - centerOfMass.getX();
    double dy = alphaCarbon.getY() - centerOfMass.getY();
    double dz = alphaCarbon.getZ() - centerOfMass.getZ();

    double distSquared = (dx * dx) + (dy * dy) + (dz * dz);

    if (distSquared > maxDistSquared) {
      maxDistSquared = distSquared;
    }

    Vector3d position = new Vector3d(dx, dy, dz);
    // center it

    Transform3D transform = new Transform3D();
    transform.set(position);
    TransformGroup transGroup = new TransformGroup(transform);
    transGroup.addChild(makeSphere());

    scene.addChild(transGroup);
  }

  private Sphere makeSphere() {
    Sphere sph = new Sphere(carbonVanDerWallsRadiusInAngstroms,
        Sphere.GENERATE_NORMALS, NUM_POLYGONS, makeAppearance());
    return sph;
  }

  private static Appearance makeAppearance() {
    final Color3f ambientColor = colorGray;
    final Color3f emmisiveColor = colorBlack;
    final Color3f diffuseColor = colorGray;
    final Color3f specularColor = colorWhite;
    final float shininess = 100.0f;
    // Material material = new Material(ambientColor, emmisiveColor,
    // diffuseColor,
    // specularColor, shininess);
    Material material = new Material();

    material.setLightingEnable(true);
    Appearance appearance = new Appearance();
    appearance.setMaterial(material);
    return appearance;
  }

}
