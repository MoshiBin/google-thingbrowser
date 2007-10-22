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

package com.google.thingbrowser.modules.slideshow.util;

import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PPickPath;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class DnDCanvas extends PCanvas {

  public DnDCanvas() {

    DropTargetListener dtl = new DropTargetListener() {

      private DropTargetListener currentTargetNode = null;

      public void dragEnter(DropTargetDragEvent dtde) {
        pickNode(dtde);
      }

      public void dragExit(DropTargetEvent dte) {
        if (currentTargetNode != null) {
          currentTargetNode.dragExit(dte);
          currentTargetNode = null;
        }
      }

      public void dragOver(DropTargetDragEvent dtde) {
        pickNode(dtde);

        if (currentTargetNode != null) {
          currentTargetNode.dragOver(dtde);
        } else {
          dtde.rejectDrag();
        }
      }

      public void drop(DropTargetDropEvent dtde) {
        if (currentTargetNode != null) {
          currentTargetNode.drop(dtde);
          currentTargetNode = null;
        }
      }

      public void dropActionChanged(DropTargetDragEvent dtde) {
        if (currentTargetNode != null) {
          currentTargetNode.dropActionChanged(dtde);
        } else {
          dtde.rejectDrag();
        }
      }

      private void pickNode(DropTargetDragEvent dtde) {

        PPickPath path = getCamera().pick(dtde.getLocation().getX(), dtde.getLocation().getY(), 1);
        DropTargetListener pickedNode = null;

        for (Object o : path.getNodeStackReference()) {
          try {
            pickedNode = (DropTargetListener)o;
            break;
          } catch (ClassCastException e) {
            continue;
          }
        }

        if (pickedNode == currentTargetNode) return;

        if (currentTargetNode != null) {
          currentTargetNode.dragExit(dtde);
        }

        currentTargetNode = pickedNode;

        if (currentTargetNode != null) {
          currentTargetNode.dragEnter(dtde);
        }
      }
    };

    new DropTarget(this, dtl);
  }
}
