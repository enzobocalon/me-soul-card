package com.mesoulcard.helper;

import net.neoforged.fml.ModList;

import java.util.List;

public class ModCompatHelper {
    private static final List<String> COMPAT_LIST = List.of("appflux", "expandedae");

    public static boolean hasCompatModsLoaded() {
        for (String item : COMPAT_LIST ) {
            if (ModList.get().isLoaded(item)) {
                return true;
            }
        }
        return false;
    }
}
