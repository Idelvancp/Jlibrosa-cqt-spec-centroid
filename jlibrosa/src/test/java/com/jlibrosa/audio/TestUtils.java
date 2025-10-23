package com.jlibrosa.audio;
import com.jlibrosa.audio.util.Utils;
public class TestUtils {
    public static void main(String[] args) {
        System.out.println(Utils.isPositiveInt(5));       // true
        System.out.println(Utils.isPositiveInt(0));       // false
        System.out.println(Utils.isPositiveInt(-3));      // false
        System.out.println(Utils.isPositiveInt(3.0));     // false
        System.out.println(Utils.isPositiveInt(null));    // false
        System.out.println(Utils.isPositiveInt(100L));    // true
    }
}

