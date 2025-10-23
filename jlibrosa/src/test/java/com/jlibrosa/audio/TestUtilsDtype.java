package com.jlibrosa.audio;
import com.jlibrosa.audio.util.Utils;

public class TestUtilsDtype {
    public static void main(String[] args) {
        System.out.println(Utils.dtypeR2C(Float.TYPE));     // complex64
        System.out.println(Utils.dtypeR2C(Double.TYPE));    // complex128
        System.out.println(Utils.dtypeR2C(Integer.TYPE));   // complex64
        System.out.println(Utils.dtypeR2C(String.class));   // complex64 (default)
        System.out.println(Utils.dtypeR2C(Complex.class));  // Complex (já é complexo)
    }

    // Exemplo simbólico de tipo complexo
    static class Complex {
        float real;
        float imag;
    }
}
