public class Main {
    public static void main(String[] args) {
	 double[] freqs = {32.70, 65.41, 130.81};
	 int sr = 22050;
	 double filterScale = 1.0;
	 double norm = 1.0;
	 double sparsity = 0.0;
	
	 VQTFilters.FilterResult filterRes = VQTFilters.vqtFilterFFT(sr, freqs, filterScale, norm, sparsity, 512, "hann", 0.0, null);
	
	 System.out.println("FFT basis shape: " + filterRes.fftBasis.length + " x " + filterRes.fftBasis[0].length);
     }

//    public static void main(String[] args) {
//        double[] freqs = {32.70, 65.41, 130.81}; // Ex.: C1, C2, C3
//        int sr = 22050;
//        double filterScale = 1.0;
//        String window = "hann";
//
//        WaveletLengths.Result res = WaveletLengths.compute(freqs, sr, window, filterScale, 0.0, null);
//
//        System.out.println("Lengths:");
//        for (double len : res.lengths) {
//            System.out.println(len);
//        }
//        System.out.println("F_cutoff: " + res.fCutoff);
//    }
}
