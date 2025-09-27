import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class Convert {

    // Mapeamento das notas
    private static final Map<String, Integer> pitchMap = Map.of(
        "C", 0,
        "D", 2,
        "E", 4,
        "F", 5,
        "G", 7,
        "A", 9,
        "B", 11
    );

    // Mapeamento dos acidentes
    private static final Map<String, Integer> accMap = Map.ofEntries(
        Map.entry("#", 1),
        Map.entry("", 0),
        Map.entry("b", -1),
        Map.entry("!", -1),
        Map.entry("♯", 1),
        Map.entry("𝄪", 2),
        Map.entry("♭", -1),
        Map.entry("𝄫", -2),
        Map.entry("♮", 0)
    );

    // Regex para capturar nota, acidentes, oitava e cents
    private static final Pattern NOTE_RE = Pattern.compile(
        "^(?<note>[A-Ga-g])" +
        "(?<accidental>[#b!♯♭𝄪𝄫♮]*)" +
        "(?<octave>-?\\d+)?" +
        "(?<cents>\\d{2})?$"
    );

    /**
     * Converte uma nota (ex: "C#4", "Bb3") para número MIDI.
     *
     * @param note Nome da nota
     * @param roundMidi Se true arredonda para inteiro, senão mantém double
     * @return Número MIDI
     */
    public static Number noteToMidi(String note, boolean roundMidi) {
        Matcher m = NOTE_RE.matcher(note);
        if (!m.matches()) {
            throw new IllegalArgumentException("Formato de nota inválido: " + note);
        }

        String pitch = m.group("note").toUpperCase();
        String accidentals = m.group("accidental") == null ? "" : m.group("accidental");
        String octaveStr = m.group("octave");
        String centsStr = m.group("cents");

        // Calcula o deslocamento dos acidentes usando codePoints para suportar Unicode
        int offset = accidentals.codePoints()
            .map(cp -> accMap.getOrDefault(new String(Character.toChars(cp)), 0))
            .sum();

        int octave = (octaveStr == null) ? 0 : Integer.parseInt(octaveStr);
        double cents = (centsStr == null) ? 0.0 : Integer.parseInt(centsStr) * 1e-2;

        double noteValue = 12 * (octave + 1) + pitchMap.get(pitch) + offset + cents;

        if (roundMidi) {
            return (int) Math.round(noteValue);
        } else {
            return noteValue;
        }
    }

    /**
     * Versão que aceita várias notas ao mesmo tempo.
     *
     * @param notes Array de notas
     * @param roundMidi Se true arredonda para inteiro
     * @return Array de valores MIDI
     */
    public static double[] noteToMidi(String[] notes, boolean roundMidi) {
        return Arrays.stream(notes)
            .mapToDouble(n -> noteToMidi(n, roundMidi).doubleValue())
            .toArray();
    }

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

    // ===============================
    // NOTE -> HZ
    // ===============================
    public static double noteToHz(String note, boolean roundMidi) {
        double midi = noteToMidi(note, roundMidi).doubleValue();
        return midiToHz(midi);
    }

    public static double[] noteToHz(String[] notes, boolean roundMidi) {
        double[] midiValues = noteToMidi(notes, roundMidi);
        return midiToHz(midiValues);
    }

        public static void main(String[] args) {
        // Testando noteToMidi
        System.out.println(noteToMidi("C", true));      // 12
        System.out.println(noteToMidi("C#3", true));    // 49
        System.out.println(noteToMidi("Bb-1", true));   // 10
        System.out.println(noteToMidi("A!8", true));    // 116
        System.out.println(noteToMidi("C♭𝄫5", true));  // 69
        String[] chord = {"C", "E", "G"};
        System.out.println(Arrays.toString(noteToMidi(chord, true))); // [12.0, 16.0, 19.0]

        // Testando midiToHz
        System.out.println(midiToHz(36));   // ~65.406
        System.out.println(midiToHz(36.5)); // ~67.351

        double[] notes = {36, 36.5, 37, 47};
        System.out.println(Arrays.toString(midiToHz(notes)));

        // Testando noteToHz
        System.out.println(noteToHz("A4", true)); // 440.0
        System.out.println(Arrays.toString(noteToHz(new String[]{"A3", "A4", "A5"}, true))); 
        // [220.0, 440.0, 880.0]
    }
}

