package Models;

public class DataLock {
	static char alpha[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z'};
	static char reverseAlpha[] = {'z', 'y', 'x', 'w', 'v', 'u', 't', 's',
			'r', 'q', 'p', 'o', 'n', 'm', 'l', 'k', 'j', 'i', 'h',
			'g', 'f', 'e', 'd', 'c', 'b', 'a'};
	public static String encrypt(String val) {
		char in[] = new char[val.length()];
		char out[] = new char[val.length()];
		for(int count = 0;count<val.length();count++) {
			in[count] = val.charAt(count);
		}
		for(int count = 0;count<in.length;count++) {
			int index = getForwardIndex(in[count]);
			if(index == -1) {
				int num = Character.getNumericValue(in[count]);
				num += 3;
				num %= 10;
				out[count] = (char) (num + 48);
			}
			else {
				out[count] = reverseAlpha[index];
			}
		}
		val = "";
		for(int count=0;count<out.length;count++) {
			val += out[count];
		}
		return val;
	}
	public static String decrypt(String val) {
		char in[] = new char[val.length()];
		char out[] = new char[val.length()];
		for(int count = 0;count<val.length();count++) {
			in[count] = val.charAt(count);
		}
		for(int count = 0;count<in.length;count++) {
			int index = getReverseIndex(in[count]);
			if(index == -1) {
				int num = Character.getNumericValue(in[count]);
				num -= 3;				
				num %= 10;
				out[count] = (char) (num + 48);
			}
			else {
				out[count] = alpha[index];
			}
		}
		val = "";
		for(int count=0;count<out.length;count++) {
			val += out[count];
		}
		return val;
	}
	private static int getForwardIndex(char val) {
		for(int count = 0;count<alpha.length;count++) {
			if(val == alpha[count]) {
				return count;
			}
		}
		return -1;
	}
	private static int getReverseIndex(char val) {
		for(int count = 0;count<reverseAlpha.length;count++) {
			if(val == reverseAlpha[count]) {
				return count;
			}
		}
		return -1;
	}
}
