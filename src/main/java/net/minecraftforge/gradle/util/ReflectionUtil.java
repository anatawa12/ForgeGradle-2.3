/*
 * A Gradle plugin for the creation of Minecraft mods and MinecraftForge plugins.
 * Copyright (C) 2013-2019 Minecraft Forge
 * Copyright (C) 2020-2023 anatawa12 and other contributors
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
package net.minecraftforge.gradle.util;

import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;

import java.lang.reflect.InvocationTargetException;

public class ReflectionUtil
{
    public static Object callMethod(Object self, String method) {
        try {
            return self.getClass().getMethod(method).invoke(self);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T callMethodOrNullWithReturnType(Object self, String method, Class<T> returns) {
        try {
            return returns.cast(self.getClass().getMethod(method).invoke(self));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassCastException e) {
            return null;
        }
    }

    public static Settings getSettingsOrNull(Gradle gradle) {
        return ReflectionUtil.callMethodOrNullWithReturnType(gradle,
                        "getSettings", Settings.class);
    }
}
