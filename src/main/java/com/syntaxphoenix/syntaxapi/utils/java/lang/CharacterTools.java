package com.syntaxphoenix.syntaxapi.utils.java.lang;

public class CharacterTools {

	public static void toSurrogates(int var0, char[] var1, int var2) {
		var1[var2 + 1] = Character.lowSurrogate(var0);
		var1[var2] = Character.highSurrogate(var0);
	}
	
	public static int codePointBeforeImpl(char[] var0, int var1, int var2) {
		--var1;
		char var3 = var0[var1];
		if (Character.isLowSurrogate(var3) && var1 > var2) {
			--var1;
			char var4 = var0[var1];
			if (Character.isHighSurrogate(var4)) {
				return Character.toCodePoint(var4, var3);
			}
		}

		return var3;
	}

	static int codePointAtImpl(char[] var0, int var1, int var2) {
		char var3 = var0[var1];
		if (Character.isHighSurrogate(var3)) {
			++var1;
			if (var1 < var2) {
				char var4 = var0[var1];
				if (Character.isLowSurrogate(var4)) {
					return Character.toCodePoint(var3, var4);
				}
			}
		}

		return var3;
	}

	public static int codePointCountImpl(char[] var0, int var1, int var2) {
		int var3 = var1 + var2;
		int var4 = var2;
		int var5 = var1;

		while (var5 < var3) {
			if (Character.isHighSurrogate(var0[var5++]) && var5 < var3 && Character.isLowSurrogate(var0[var5])) {
				--var4;
				++var5;
			}
		}

		return var4;
	}

	public static int offsetByCodePointsImpl(char[] var0, int var1, int var2, int var3, int var4) {
		int var5 = var3;
		int var6;
		if (var4 >= 0) {
			var6 = var1 + var2;

			int var7;
			for (var7 = 0; var5 < var6 && var7 < var4; ++var7) {
				if (Character.isHighSurrogate(var0[var5++]) && var5 < var6 && Character.isLowSurrogate(var0[var5])) {
					++var5;
				}
			}

			if (var7 < var4) {
				throw new IndexOutOfBoundsException();
			}
		} else {
			for (var6 = var4; var5 > var1 && var6 < 0; ++var6) {
				--var5;
				if (Character.isLowSurrogate(var0[var5]) && var5 > var1 && Character.isHighSurrogate(var0[var5 - 1])) {
					--var5;
				}
			}

			if (var6 < 0) {
				throw new IndexOutOfBoundsException();
			}
		}

		return var5;
	}

}
