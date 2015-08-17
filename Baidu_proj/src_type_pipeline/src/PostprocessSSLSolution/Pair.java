package PostprocessSSLSolution;

class Pair {
	String key;
	double value;

	public Pair(String k, double v) {
		this.key = k;
		this.value = v;
	}

	public String toString() {
		return key + " " + value;
	}
}