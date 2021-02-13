/*
 * A Gradle plugin for the creation of Minecraft mods and MinecraftForge plugins.
 * Copyright (C) 2021-2021 anatawa12 and other contributors
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package net.minecraftforge.gradle;

import org.gradle.util.GradleVersion;

public class GradleVersionUtils {
    /**
     * @param versionName includes this version
     * @param action the action runs if gradle version is equals to or after {@code versionName}.
     */
    public static void ifAfter(String versionName, Runnable action) {
        GradleVersion gradleVersion = GradleVersion.current();
        GradleVersion version = GradleVersion.version(versionName);

        if (gradleVersion.compareTo(version) > 0) {
            action.run();
        }
    }
    /**
     * same version includes after
     * @param versionName includes this version
     */
    public static <T> T choose(String versionName, Callable<? extends T> before, Callable<? extends T> after) {
        if (isBefore(versionName)) {
            return before.call();
        } else {
            return after.call();
        }
    }

    /**
     * same version includes after
     * @param versionName includes this version
     */
    public static boolean isBefore(String versionName) {
        GradleVersion gradleVersion = GradleVersion.current();
        GradleVersion version = GradleVersion.version(versionName);

        if (gradleVersion.compareTo(version) < 0) {
            return true;
        } else {
            return false;
        }
    }

    public interface Callable<T> {
        T call();
    }
}
