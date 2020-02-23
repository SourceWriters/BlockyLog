package com.syntaxphoenix.syntaxapi.utils.java.lang;

public class LongTools {
	
	public static int stringSize(long var0) {
		long var2 = 10L;

		for (int var4 = 1; var4 < 19; ++var4) {
			if (var0 < var2) {
				return var4;
			}

			var2 = 10L * var2;
		}

		return 19;
	}
	
	public static void getChars(Long var0, int var1, char[] var2) {
		StringTools.getChars(var0.toString(), var2, var1);
	}
	
}
