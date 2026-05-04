package com.jlibrosa.audio;
import org.apache.commons.math3.complex.Complex;
import com.jlibrosa.audio.core.IntervalFrequencies;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;


public class TestIntervalFrequencies {
    public static void main(String[] args) {
        try {
           double[] freqs = IntervalFrequencies.intervalFrequencies(
                24,
                440.0,
                "pythagorean",
                12,
                0.0,
                true
        );

        System.out.println(Arrays.toString(freqs));
        
        } catch (Exception e) {
            System.err.println("Erro geral: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

