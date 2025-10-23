package com.jlibrosa.audio;
import com.jlibrosa.audio.util.Utils;
public class TestUtilsValidAudio {
    public static void main(String[] args) {
        double[] valid = {0.1, -0.5, 0.9};
        double[] invalid = {0.1, Double.NaN, 0.5};
        double[][] stereo = {{0.1, 0.2}, {0.3, 0.4}};

        System.out.println(Utils.validAudio(valid));   // ✅ true
        System.out.println(Utils.validAudio(stereo));  // ✅ true

        try {
            Utils.validAudio(invalid);
        } catch (Utils.ParameterError e) {
            System.out.println("Erro esperado: " + e.getMessage());
        }

        try {
            Utils.validAudio(null);
        } catch (Utils.ParameterError e) {
            System.out.println("Erro esperado: " + e.getMessage());
        }
    }
}

