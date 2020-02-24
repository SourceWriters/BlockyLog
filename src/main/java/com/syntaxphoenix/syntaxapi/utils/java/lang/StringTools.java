package com.syntaxphoenix.syntaxapi.utils.java.lang;

public class StringTools {
	
	public static void getChars(String var0, char[] var1, int var2) {
		char[] var4 = var0.toCharArray();
		System.arraycopy(var4, 0, var1, var2, var4.length);
	}
	
	public static int lastIndexOf(char[] var0, int var1, int var2, String var3, int var4) {
		char[] var5 = var3.toCharArray();
		return lastIndexOf(var0, var1, var2, var5, 0, var5.length, var4);
	}

	public static int lastIndexOf(char[] var0, int var1, int var2, char[] var3, int var4, int var5, int var6) {
		int var7 = var2 - var5;
		if (var6 < 0) {
			return -1;
		} else {
			if (var6 > var7) {
				var6 = var7;
			}

			if (var5 == 0) {
				return var6;
			} else {
				int var8 = var4 + var5 - 1;
				char var9 = var3[var8];
				int var10 = var1 + var5 - 1;
				int var11 = var10 + var6;

				while (true) {
					while (var11 < var10 || var0[var11] == var9) {
						if (var11 < var10) {
							return -1;
						}

						int var12 = var11 - 1;
						int var13 = var12 - (var5 - 1);
						int var14 = var8 - 1;

						do {
							if (var12 <= var13) {
								return var13 - var1 + 1;
							}
						} while (var0[var12--] == var3[var14--]);

						--var11;
					}

					--var11;
				}
			}
		}
	}
	
	static int indexOf(char[] var0, int var1, int var2, String var3, int var4) {
		char[] var5 = var3.toCharArray();
		return indexOf(var0, var1, var2, var5, 0, var5.length, var4);
	}

	static int indexOf(char[] var0, int var1, int var2, char[] var3, int var4, int var5, int var6) {
		if (var6 >= var2) {
			return var5 == 0 ? var2 : -1;
		} else {
			if (var6 < 0) {
				var6 = 0;
			}

			if (var5 == 0) {
				return var6;
			} else {
				char var7 = var3[var4];
				int var8 = var1 + (var2 - var5);

				for (int var9 = var1 + var6; var9 <= var8; ++var9) {
					if (var0[var9] != var7) {
						do {
							++var9;
						} while (var9 <= var8 && var0[var9] != var7);
					}

					if (var9 <= var8) {
						int var10 = var9 + 1;
						int var11 = var10 + var5 - 1;

						for (int var12 = var4 + 1; var10 < var11 && var0[var10] == var3[var12]; ++var12) {
							++var10;
						}

						if (var10 == var11) {
							return var9 - var1;
						}
					}
				}

				return -1;
			}
		}
	}

}
