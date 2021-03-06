package fr.neatmonster.nocheatplus.compat.versions;

/**
 * Static utility that stores the Minecraft version, providing some parsing
 * methods. This has to be initialized from an external source (e.g. a plugin
 * calling BukkitVersion.init).
 * <hr/>
 * Taken from the TrustCore plugin.
 * 
 * @author mc_dev
 *
 */
public class ServerVersion {

    private static String minecraftVersion = GenericVersion.UNKNOWN_VERSION; 

    private static final String[][] versionTagsMatch = {
        {"1.7.2-r", "1.7.2"}, // Example. Probably this method will just be removed.
    };

    /**
     * Test if the set Minecraft version is unknown.
     */
    public static boolean isMinecraftVersionUnknown() {
        return GenericVersion.isVersionUnknown(minecraftVersion);
    }

    /**
     * Attempt to return the Minecraft version for a given server version
     * string.
     * 
     * @param serverVersion
     *            As returned by Bukkit.getServer().getVersion();
     * @return null if not known/parsable.
     */
    public static String parseMinecraftVersion(String... versionCandidates) {
        for (String serverVersion : versionCandidates) {
            serverVersion = serverVersion.trim();
            for (String minecraftVersion : new String[]{
                    GenericVersion.collectVersion(serverVersion, 0),
                    parseMinecraftVersionGeneric(serverVersion),
                    parseMinecraftVersionTokens(serverVersion)
            }) {
                // Validate if not null, might mean double validation.
                if (minecraftVersion != null && validateMinecraftVersion(minecraftVersion)) {
                    return minecraftVersion;
                } 
            }
        }
        return null;
    }

    /**
     * Simple consistency check.
     * 
     * @param minecraftVersion
     * @return
     */
    private static boolean validateMinecraftVersion(String minecraftVersion) {
        return GenericVersion.collectVersion(minecraftVersion, 0) != null;
    }

    /**
     * Match directly versus hard coded examples. Not for direct use.
     * 
     * @param serverVersion
     * @return
     */
    private static String parseMinecraftVersionTokens(String serverVersion) {
        serverVersion = serverVersion.trim().toLowerCase();
        for (String[] entry : versionTagsMatch) {
            if (serverVersion.contains(entry[0])) {
                return entry[1];
            }
        }
        return null;
    }

    /**
     * 
     * @param serverVersion
     * @return
     */
    private static String parseMinecraftVersionGeneric(String serverVersion) {
        String lcServerVersion = serverVersion.trim().toLowerCase();
        for (String candidate : new String[] {
                GenericVersion.parseVersionDelimiters(lcServerVersion, "(mc:", ")"),
                GenericVersion.parseVersionDelimiters(lcServerVersion, "mcpc-plus-", "-"),
                GenericVersion.parseVersionDelimiters(lcServerVersion, "git-bukkit-", "-r"),
                GenericVersion.parseVersionDelimiters(lcServerVersion, "", "-r"),
                // TODO: Other server mods + custom builds !?.
        }) {
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Sets lower-case Minecraft version - or GenericVersion.UNKNOWN_VERSION, if
     * null or empty.
     * 
     * @param version
     *            Passing null will yield GenericVersion.UNKNOWN_VERSION. If the
     *            input equals GenericVersion.UNKNOWN_VERSION,
     *            GenericVersion.UNKNOWN_VERSION is set.
     * @return The String that minecraftVersion has been set to.
     */
    public static String setMinecraftVersion(String version) {
        if (version == null) {
            minecraftVersion = GenericVersion.UNKNOWN_VERSION;
        } else {
            version = version.trim().toLowerCase();
            if (version.isEmpty() || version.equals(GenericVersion.UNKNOWN_VERSION)) {
                minecraftVersion = GenericVersion.UNKNOWN_VERSION;
            } else {
                minecraftVersion = version;
            }
        }
        return minecraftVersion;
    }

    /**
     * 
     * @return Some version string, or UNKNOWN_VERSION.
     */
    public static String getMinecraftVersion() {
        return minecraftVersion;
    }

    /**
     * Convenience for compareVersions(getMinecraftVersion(), version).
     * 
     * @param version
     *            Can not be GenericVersion.UNKNOWN_VERSION.
     * @return 0 if equal, -1 if the Minecraft version is lower, 1 if the
     *         Minecraft version is higher.
     */
    public static int compareMinecraftVersion(String toVersion) {
        return GenericVersion.compareVersions(getMinecraftVersion(), toVersion);
    }

    /**
     * Test if the Minecraft version is between the two given ones.
     * 
     * @param versionLow
     *            Can not be GenericVersion.UNKNOWN_VERSION.
     * @param includeLow
     *            If to allow equality for the low edge.
     * @param versionHigh
     *            Can not be GenericVersion.UNKNOWN_VERSION.
     * @param includeHigh
     *            If to allow equality for the high edge.
     * @return
     */
    public static boolean isMinecraftVersionBetween(String versionLow, boolean includeLow, String versionHigh, boolean includeHigh) {
        final String minecraftVersion = getMinecraftVersion();
        if (includeLow) {
            if (GenericVersion.compareVersions(minecraftVersion, versionLow) == -1) {
                return false;
            }
        } else {
            if (GenericVersion.compareVersions(minecraftVersion, versionLow) <= 0) {
                return false;
            }
        }
        if (includeHigh) {
            if (GenericVersion.compareVersions(minecraftVersion, versionHigh) == 1) {
                return false;
            }
        } else {
            if (GenericVersion.compareVersions(minecraftVersion, versionHigh) >= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Select a value based on the Minecraft version.
     * 
     * @param cmpVersion
     *            Version to compare to, comparison of server version vs. given
     *            version.
     * @param valueLT
     *            Server has an earlier version.
     * @param valueEQ
     *            Same versions.
     * @param valueGT
     *            The server version is later.
     * @param valueUnknown
     *            Value to return, if the server version could not be
     *            determined.
     * @return
     */
    public static <V> V select(final String cmpVersion, final V valueLT, final V valueEQ, final V valueGT, final V valueUnknown) {
        final String mcVersion = ServerVersion.getMinecraftVersion();
        if (mcVersion == GenericVersion.UNKNOWN_VERSION) {
            return valueUnknown;
        } else {
            final int cmp = GenericVersion.compareVersions(mcVersion, cmpVersion);
            if (cmp == 0) {
                return valueEQ;
            } else if (cmp < 0) {
                return valueLT;
            } else {
                return valueGT;
            }
        }
    }

}
