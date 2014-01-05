package com.simonmonk.home;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Beeper {

  // Number of samples per second of audio
  private final static int SAMPLE_RATE = 16000;

  // Number of samples to use for encoding bits
  private final static int BIT_SAMPLES = 1000;
  private final static int ZERO_SAMPLES = 10;
  private final static int ONE_SAMPLES = 200;
  private final static int HEADER_SAMPLES = 500;
  
  // Tone properties
  private final static float FREQ = 1000.0f;
  private final static float AMPLITUDE = 32000.0f;

  // Emit a series of tone pulses, encoding `word`,
  // with long beeps for 1 and short beeps for 0.
  public void beep(int word) {
    AudioTrack at;
    int total_samples = BIT_SAMPLES * 16;
    int bit;

    // Precalculate the buffers
    short[] header_buffer = pulse_buffer(BIT_SAMPLES, HEADER_SAMPLES);
    short[] one_buffer = pulse_buffer(BIT_SAMPLES, ONE_SAMPLES);
    short[] zero_buffer = pulse_buffer(BIT_SAMPLES, ZERO_SAMPLES);

    // Create a streaming AudioTrack
    at = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
        AudioFormat.CHANNEL_CONFIGURATION_MONO,
        AudioFormat.ENCODING_PCM_16BIT, total_samples*2,
        AudioTrack.MODE_STREAM);
    at.setStereoVolume(1.0f, 1.0f);

    // Begin streaming
    at.play();
    
    // Write a dummy header, in case the first pulse doesn't make it through
    at.write(header_buffer, 0, BIT_SAMPLES);
    
    // Write pulses for each bit in the word, starting with
    // the most significant bit
    for (int i = 15; i >= 0; i--) {
      bit = ((word >> i) & 1);
      if (bit == 0) {
        at.write(zero_buffer, 0, BIT_SAMPLES);
      }
      else {
        at.write(one_buffer, 0, BIT_SAMPLES);
      }
    }

    // Stop streaming
    at.stop();
  }
  
  // Build an audio buffer representing an audio pulse, having `total_samples` in all,
  // with the first `pulse_samples` being a sine wave, and the remaining samples being silence.
  public short[] pulse_buffer(int total_samples, int pulse_samples) {
    short[] buffer = new short[total_samples];
    double omega = (float) (2.0 * Math.PI * FREQ);
    double t = 0.0;
    double dt = 1.0 / SAMPLE_RATE;

    for (int i = 0; i < total_samples; i++) {
      if (i < pulse_samples) {
        buffer[i] = (short) (AMPLITUDE * Math.sin(omega * t));
      }
      else {
        buffer[i] = 0;
      }
      t += dt;
    }
    return buffer;
  }

}
