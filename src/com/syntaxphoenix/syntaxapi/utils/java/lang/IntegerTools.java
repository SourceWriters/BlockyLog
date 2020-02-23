package com.syntaxphoenix.syntaxapi.utils.java.lang;

public class IntegerTools {
	
	static final int[] sizeTable = new int[]{9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};

	public static int stringSize(int var0) {
		int var1;
		for (var1 = 0; var0 > sizeTable[var1]; ++var1) {
			;
		}

		return var1 + 1;
	}
	
	public static void getChars(Integer var0, int var1, char[] var2) {
		StringTools.getChars(var0.toString(), var2, var1);
	}

}
