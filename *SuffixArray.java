class SuffixArray {
	final int max = Integer.MAX_VALUE / 2;
	// change to -max, -max + 1 for integers
	final char end = (char)0, delim = (char)1;

	int n;
	char[] arr;

	int[] firstIdx, map;

	int[] suff, equiv, newVals, idxOf;
	int k;
	boolean sortChar;

	int[] lcp;
	
	RMQ rmq;
	SuffixArray(String ... in){
		char[][] vals = new char[in.length][];
		firstIdx = new int[vals.length];
		for(int i = 0; i < vals.length; ++i) {
			vals[i] = in[i].toCharArray();
			firstIdx[i] = n;
			n += vals[i].length + 1;
		}

		arr = new char[n];
		map = new int[n];
		Arrays.fill(map, -1);
		for(int i = 0; i < vals.length; ++i) {
			for(int j = 0; j < vals[i].length; ++j) {
				arr[firstIdx[i] + j] = vals[i][j];
				map[firstIdx[i] + j] = i;
			}
			arr[firstIdx[i] + vals[i].length] = delim;
		}
		arr[n - 1] = end;

		getSuff();
		getLCP();
		getRMQ();
	}
	void getSuff() {
		Integer[] temp = new Integer[n];
		for(int i = 0; i < n; ++i) temp[i] = i;
		Arrays.sort(temp, (a, b) -> arr[a] - arr[b]);

		suff = new int[n];
		equiv = new int[n];
		newVals = new int[n];
		
		for(int i = 0; i < n; ++i) suff[i] = temp[i];

		sortChar = true;
		setEquiv();
		sortChar = false;

		int[] buckIdx = new int[n];
		for(k = 0; (1 << k) < n; ++k) {
			for(int i = 0; i < n; ++i) suff[i] = (suff[i] - (1 << k) + n) % n;

			// radix sort
			int[] num = new int[n];
			for(int a : suff) ++num[equiv[a]];

			buckIdx[0] = 0;
			for(int i = 1; i < n; ++i) buckIdx[i] = buckIdx[i - 1] + num[i - 1];

			// place em in
			for(int a : suff) newVals[buckIdx[equiv[a]]++] = a;

			for(int i = 0; i < n; ++i) suff[i] = newVals[i];

			setEquiv();
		}

		idxOf = new int[n];
		for(int i = 0; i < n; ++i) idxOf[suff[i]] = i;
	}
	void setEquiv() {
		int eqIdx = 0;
		newVals[suff[0]] = 0;
		for(int i = 1; i < n; ++i) {
			if(!equal(suff[i], suff[i - 1])) ++eqIdx;
			newVals[suff[i]] = eqIdx;
		}
		for(int i = 0; i < n; ++i) equiv[i] = newVals[i];
	}
	boolean equal(int a, int b) {
		if(sortChar) return arr[a] == arr[b];
		return equiv[a] == equiv[b] && equiv[(a + (1 << k)) % n] == equiv[(b + (1 << k)) % n];
	}
	void getLCP() {
		int curr = 0;
		lcp = new int[n - 1];
		for(int a = 0; a < n - 1; ++a) {
			while(arr[a + curr] == arr[suff[idxOf[a] - 1] + curr]) ++curr;

			lcp[idxOf[a] - 1] = curr;

			if(curr > 0) --curr;
		}
	}
	void getRMQ() {
		rmq = new RMQ(lcp);
	}
	// returns the LCP of suffix with index i and suffix with index j
	int lcp(int i, int j) {
		i = idxOf[i];
		j = idxOf[j];

		if(i == j) return max;
		if(i > j) i = j ^ i ^ (j = i);
		return rmq.getMin(i, j - 1);
	}
	int min(int a, int b) {
		return a < b ? a : b;
	}
	// returns the compareTo for two substrings
	int compare(int l1, int r1, int l2, int r2) {
		int len1 = r1 - l1 + 1, len2 = r2 - l2 + 1;
		int smallLen = min(len1, len2);

		if(lcp(l1, l2) < smallLen) return idxOf[l1] - idxOf[l2];
		if(len1 != len2) return len1 - len2;
		// if they are the same substring, break ties on starting index:
		return l1 - l2;
	}
	class RMQ {
		int n, m;
		int[] bitIdx;
		int[][] rmq;
		RMQ(int[] arr){
			n = arr.length;
			bitIdx = new int[n + 1];
			
			m = 0;
			for(int i = 1; i <= n; ++i) {
				if(i == 1 << (m + 1)) ++m;
				bitIdx[i] = m;
			}
			
			rmq = new int[m + 1][n];
			for(int i = 0; i < n; ++i) rmq[0][i] = arr[i];
			
			for(int k = 1; k <= m; ++k) {
				for(int i = 0; i < n; ++i) {
					if(i + (1 << k) - 1 < n) rmq[k][i] = min(rmq[k - 1][i], rmq[k - 1][i + (1 << (k - 1))]);
				}
			}
		}
		int getMin(int l, int r) {
			int k = bitIdx[r - l + 1];
			return min(rmq[k][l], rmq[k][r - (1 << k) + 1]);
		}
	}
}
