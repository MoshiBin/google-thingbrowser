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

import com.google.thingbrowser.modules.proteins.impl.model.VisitableAmino;
import com.google.thingbrowser.modules.proteins.impl.model.VisitableAtom;
import com.google.thingbrowser.modules.proteins.impl.model.VisitableChain;
import com.google.thingbrowser.modules.proteins.impl.model.VisitableChainList;
import com.google.thingbrowser.modules.proteins.impl.model.VisitableHeteroGroup;
import com.google.thingbrowser.modules.proteins.impl.model.Visitor;

import org.biojava.bio.structure.AminoAcid;
import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.Structure;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jregan@google.com (Jeffrey Regan)
 */
class StructureVisitor implements Visitor {

  private final boolean doAllModels = false;
  private final boolean doAllChains = false;

  private static final int UNSEEN = -1;

  private int modelNumber = UNSEEN;

  private int chainNumber = UNSEEN;

  private int position = UNSEEN;

  public int getChainNumber() {
    return chainNumber;
  }

  public int getModelNumber() {
    return modelNumber;
  }

  public int getPosition() {
    return position;
  }

  public void visit(Structure structure) {
    int numModels = structure.nrModels();
    if (doAllModels) {
      for (int index = 0; index < numModels; index++) {
        doModel(structure, index);
      }
    } else {
      doModel(structure, 0);
    }
  }

  private void doModel(Structure structure, int index) {
    modelNumber = index;
    VisitableChainList chainList = new VisitableChainList(structure
        .getModel(modelNumber));
    chainList.accept(this);
  }

  public void visit(List<Chain> chainList) {
    if (doAllChains) {
      for (int index = 0; index < chainList.size(); index++) {
        doChain(chainList, index);
      }
    } else {
      doChain(chainList, 0);
    }
  }

  private void doChain(List<Chain> chainList, int index) {
    chainNumber = index;
    VisitableChain chain = new VisitableChain(chainList.get(chainNumber));
    chain.accept(this);
  }

  public void visit(Chain chain) {
    for (position = 0; position < chain.getLength(); position++) {
      Group group = chain.getGroup(position);
      if (group.getType().equals("amino")) {
        VisitableAmino amino = new VisitableAmino((AminoAcid) group);
        amino.accept(this);
      } else if (group.getType().equals("hetatom")) {
        VisitableHeteroGroup heteroGroup = new VisitableHeteroGroup(group);
        heteroGroup.accept(this);
      } else if (group.getType().equals("nucleotide")) {
        // not ready for dna yet.
      } else {
        // throw exception?
      }
    }
  }

  public void visit(VisitableHeteroGroup heteroGroup) {
    // do nothing - could loop over the atoms or something.
  }

  public void visit(AminoAcid aminoAcid) {
    Iterator iterator = aminoAcid.iterator();
    for (Iterator iter = aminoAcid.iterator(); iter.hasNext();) {
      VisitableAtom atom = new VisitableAtom((Atom) iter.next());
      atom.accept(this);
    }
  }

  /**
   * Feel free to override
   */
  public void visit(Atom atom) {
    System.out.printf("%4d%4d%4d  \"%s\"\n", getModelNumber(),
        getChainNumber(), getPosition(), atom);
  }

}
