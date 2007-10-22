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

import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

import com.google.thingbrowser.modules.sound.Player;

/**
 *
 * @author ihab@google.com (Ihab Awad)
 */
public class MP3PlayerImpl implements Player {

  private static enum State {
    READY,
    PLAYING,
    STOPPING,
    DONE;
  };

  private State state = State.READY;
  private Bitstream bitstream;
  private Decoder decoder;
  private AudioDevice audioDevice;

  public MP3PlayerImpl(InputStream inputStream) throws JavaLayerException {
    bitstream = new Bitstream(inputStream);
    decoder = new Decoder();
    audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();
    audioDevice.open(decoder);
  }

  public synchronized void play() {
    if (state != State.READY) return;
    new Thread(new Runnable() {
      public void run() {
        runPlayLoop();
      }
    }).start();
    state = State.PLAYING;
  }

  public synchronized void stop() {
    if (state != State.PLAYING) return;
    state = State.STOPPING;
  }

  private void runPlayLoop() {

    synchronized (this) {
      // Enter an empty critical section to ensure that the play() method
      // has completed, so state == State.PLAYING. After this point, this
      // thread can proceed without synchronizing.
    }

    try {
      while (true) {
        Header header = bitstream.readFrame();
        if (header == null) break;
        SampleBuffer output = (SampleBuffer)decoder.decodeFrame(header, bitstream);
        audioDevice.write(output.getBuffer(), 0, output.getBufferLength());
        bitstream.closeFrame();
        if (state == State.STOPPING) break;
      }
    } catch (JavaLayerException e) {
      // Since we have no easy way to propagate this exception to the calling
      // thread(s), we just fall off the end and stop.
    }

    try {
      bitstream.close();
    } catch (JavaLayerException e) {
      // Oh well, we tried our best -- ignore the exception.
    }

    state = State.DONE;
  }
}
