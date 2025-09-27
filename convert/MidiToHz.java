public class MidiToHz {

    /**
     * Converte um número MIDI (inteiro ou fracionário) em frequência (Hz).
     * @param note número MIDI (pode ser double)
     * @return frequência em Hz
     */
    public static double midiToHz(double note) {
        return 440.0 * Math.pow(2.0, (note - 69.0) / 12.0);
    }

    /**
     * Converte um array de números MIDI em frequências (Hz).
     * @param notes array de números MIDI (pode conter doubles)
     * @return array de frequências em Hz
     */
    public static double[] midiToHz(double[] notes) {
        double[] freqs = new double[notes.length];
        for (int i = 0; i < notes.length; i++) {
            freqs[i] = midiToHz(notes[i]);
        }
        return freqs;
    }

    public static void main(String[] args) {
        // Escalar inteiro (igual ao Python)
        System.out.println(midiToHz(36));   // ~65.406 Hz

        // Escalar fracionário (igual ao Python)
        System.out.println(midiToHz(36.5)); // ~67.351 Hz

        // Vetor (igual ao numpy array do Python)
        double[] notes = {36, 36.5, 37, 47};
        double[] freqs = midiToHz(notes);

        for (double f : freqs) {
            System.out.printf("%.3f ", f);
        }
    }
}

