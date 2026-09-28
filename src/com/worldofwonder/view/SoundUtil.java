package com.worldofwonder.view;

import com.worldofwonder.model.*;
import com.worldofwonder.controller.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Lightweight procedural sound effect synthesizer and player using Java's built-in
 * javax.sound.sampled API. Generates crisp, harmonious chimes, clicks, and fanfares
 * completely in-memory without requiring external audio files.
 */
public final class SoundUtil {

    private static final int SAMPLE_RATE = 44100;
    private static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);

    private static final ExecutorService AUDIO_EXECUTOR = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r, "WOW-Audio-Thread");
        t.setDaemon(true);
        return t;
    });

    private static volatile boolean muted = false;
    private static final ConcurrentHashMap<String, byte[]> SOUND_CACHE = new ConcurrentHashMap<>();

    private SoundUtil() {
    }

    public static boolean isMuted() {
        return muted;
    }

    public static void setMuted(boolean mute) {
        muted = mute;
    }

    public static void toggleMute() {
        muted = !muted;
    }

    /** Short, crisp button click sound */
    public static void playClick() {
        playCached("click", () -> synthesizeClick(0.04, 750, 0.7));
    }

    /** Melodic letter node selection with pitch scaling based on chain index (0..7) */
    public static void playLetterSelect(int chainIndex) {
        // Pentatonic scale starting at C5: C5, D5, E5, G5, A5, C6, D6, E6
        double[] scale = {523.25, 587.33, 659.25, 783.99, 880.00, 1046.50, 1174.66, 1318.51};
        int idx = Math.max(0, Math.min(scale.length - 1, chainIndex));
        double freq = scale[idx];
        String key = "letter_" + idx;
        playCached(key, () -> synthesizeBell(0.10, freq, 0.65));
    }

    /** Satisfying harmonious chime for a correctly spelled word */
    public static void playCorrect() {
        playCached("correct", () -> synthesizeChord(0.35, new double[]{523.25, 659.25, 783.99, 1046.50}, 0.75));
    }

    /** Gentle low double-buzz/boop for wrong guesses or invalid actions */
    public static void playError() {
        playCached("error", () -> synthesizeError(0.18, 160, 0.65));
    }

    /** Magical shimmering upward chime when using a hint or revealing letters */
    public static void playHint() {
        playCached("hint", () -> synthesizeShimmer(0.32, 0.7));
    }

    /** Playful whoosh/shuffle sound */
    public static void playShuffle() {
        playCached("shuffle", () -> synthesizeShuffle(0.20, 0.65));
    }

    /** Triumphant victory fanfare for level / puzzle completion */
    public static void playVictory() {
        playCached("victory", () -> synthesizeVictoryFanfare(0.65, 0.8));
    }

    private static void playCached(String key, SoundGenerator generator) {
        if (muted) {
            return;
        }
        AUDIO_EXECUTOR.submit(() -> {
            try {
                byte[] audioData = SOUND_CACHE.computeIfAbsent(key, k -> generator.generate());
                playPcm(audioData);
            } catch (Throwable ignored) {
                // Silently fallback if audio subsystem is unavailable
            }
        });
    }

    private static void playPcm(byte[] pcmData) {
        if (pcmData == null || pcmData.length == 0 || muted) {
            return;
        }
        try {
            SourceDataLine line = AudioSystem.getSourceDataLine(FORMAT);
            line.open(FORMAT, pcmData.length);
            line.start();
            line.write(pcmData, 0, pcmData.length);
            line.drain();
            line.stop();
            line.close();
        } catch (LineUnavailableException | IllegalArgumentException | SecurityException ignored) {
            // Audio hardware busy or not present
        }
    }

    @FunctionalInterface
    private interface SoundGenerator {
        byte[] generate();
    }

    // ── Waveform Synthesizers ───────────────────────────────────────────

    private static byte[] synthesizeClick(double durationSec, double freq, double volume) {
        int totalSamples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[totalSamples * 2];
        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            // Rapid exponential decay for crisp pop
            double env = Math.exp(-t * 80.0);
            double sample = Math.sin(2.0 * Math.PI * freq * t) * env * volume;
            writeSample(buffer, i, sample);
        }
        return buffer;
    }

    private static byte[] synthesizeBell(double durationSec, double freq, double volume) {
        int totalSamples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[totalSamples * 2];
        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double attack = Math.min(1.0, t / 0.005);
            double decay = Math.exp(-t * 18.0);
            double env = attack * decay;
            // Fundamental + harmonic
            double sample = (Math.sin(2.0 * Math.PI * freq * t) + 0.3 * Math.sin(2.0 * Math.PI * freq * 2.0 * t))
                    * env * volume * 0.75;
            writeSample(buffer, i, sample);
        }
        return buffer;
    }

    private static byte[] synthesizeChord(double durationSec, double[] freqs, double volume) {
        int totalSamples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[totalSamples * 2];
        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double attack = Math.min(1.0, t / 0.01);
            double decay = Math.exp(-t * 6.5);
            double env = attack * decay;

            double sum = 0.0;
            for (int k = 0; k < freqs.length; k++) {
                double noteStart = k * 0.04;
                if (t >= noteStart) {
                    double noteT = t - noteStart;
                    double noteEnv = Math.exp(-noteT * 7.0);
                    sum += Math.sin(2.0 * Math.PI * freqs[k] * noteT) * noteEnv;
                }
            }
            double sample = (sum / freqs.length) * env * volume;
            writeSample(buffer, i, sample);
        }
        return buffer;
    }

    private static byte[] synthesizeError(double durationSec, double freq, double volume) {
        int totalSamples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[totalSamples * 2];
        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            // Two short downward pulses
            double pulse1 = (t < 0.08) ? Math.exp(-t * 25.0) * Math.sin(2.0 * Math.PI * (freq - t * 400.0) * t) : 0;
            double pulse2 = (t >= 0.09 && t < 0.17) ? Math.exp(-(t - 0.09) * 25.0) * Math.sin(2.0 * Math.PI * (freq * 0.85 - (t - 0.09) * 400.0) * (t - 0.09)) : 0;
            double sample = (pulse1 + pulse2) * volume;
            writeSample(buffer, i, sample);
        }
        return buffer;
    }

    private static byte[] synthesizeShimmer(double durationSec, double volume) {
        int totalSamples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[totalSamples * 2];
        double[] notes = {587.33, 739.99, 880.00, 1174.66, 1479.98, 1760.00};
        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double sum = 0.0;
            for (int k = 0; k < notes.length; k++) {
                double start = k * 0.045;
                if (t >= start) {
                    double noteT = t - start;
                    double env = Math.exp(-noteT * 12.0);
                    sum += Math.sin(2.0 * Math.PI * notes[k] * noteT) * env;
                }
            }
            double sample = (sum / notes.length) * volume;
            writeSample(buffer, i, sample);
        }
        return buffer;
    }

    private static byte[] synthesizeShuffle(double durationSec, double volume) {
        int totalSamples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[totalSamples * 2];
        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double f = 300.0 + 800.0 * Math.sin(Math.PI * (t / durationSec));
            double env = Math.sin(Math.PI * (t / durationSec));
            double sample = Math.sin(2.0 * Math.PI * f * t) * env * volume * 0.6;
            writeSample(buffer, i, sample);
        }
        return buffer;
    }

    private static byte[] synthesizeVictoryFanfare(double durationSec, double volume) {
        int totalSamples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[totalSamples * 2];
        // C5, E5, G5, C6 triumphant cascade with rich harmonics
        double[] notes = {523.25, 659.25, 783.99, 1046.50, 1318.51};
        double[] delays = {0.0, 0.10, 0.20, 0.32, 0.42};
        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double sum = 0.0;
            for (int k = 0; k < notes.length; k++) {
                if (t >= delays[k]) {
                    double noteT = t - delays[k];
                    double env = Math.exp(-noteT * 5.0);
                    double f = notes[k];
                    sum += (Math.sin(2.0 * Math.PI * f * noteT) + 0.35 * Math.sin(4.0 * Math.PI * f * noteT)) * env;
                }
            }
            double masterEnv = Math.min(1.0, t / 0.02) * (1.0 - Math.pow(t / durationSec, 3.0));
            double sample = (sum / 3.2) * masterEnv * volume;
            writeSample(buffer, i, sample);
        }
        return buffer;
    }

    private static void writeSample(byte[] buffer, int sampleIndex, double sample) {
        sample = Math.max(-1.0, Math.min(1.0, sample));
        short s = (short) Math.round(sample * 32767.0);
        buffer[sampleIndex * 2] = (byte) (s & 0xFF);
        buffer[sampleIndex * 2 + 1] = (byte) ((s >> 8) & 0xFF);
    }
}
