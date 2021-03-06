class GCDLCM {
	static long gcd(long a, long b) {
		if(b > a) {
			long temp = b;
			b = a;
			a = temp;
		}
		while(b > 0) {
			long temp = a % b;
			a = b;
			b = temp;
		}
		return a;
	}
	static long lcm(long a, long b) {
		return a / gcd(a, b) * b;
	}
}
