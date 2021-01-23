package com.syntaxphoenix.blockylog.data;

import net.sourcewriters.minecraft.versiontools.shaded.syntaxapi.utils.key.AbstractNamespaced;
import net.sourcewriters.minecraft.versiontools.shaded.syntaxapi.utils.key.IKey;
import net.sourcewriters.minecraft.versiontools.shaded.syntaxapi.utils.key.INamespace;

public class NumericSpace extends AbstractNamespaced {

    public static final String COORD_FORMAT = "%s-%s-%s";

    public NumericSpace(INamespace<?> namespace) {
        super(namespace);
    }

    public IKey create(int x, int y, int z) {
        return namespace.create(String.format(COORD_FORMAT, x, y, z));
    }

}
