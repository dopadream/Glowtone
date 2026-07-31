/*
 * Copyright 2025-2026 FrozenBlock
 * This file is part of Glowtone.
 *
 * This program is free software; you can modify it under
 * the terms of version 1 of the FrozenBlock Modding Oasis License
 * as published by FrozenBlock Modding Oasis.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * FrozenBlock Modding Oasis License for more details.
 *
 * You should have received a copy of the FrozenBlock Modding Oasis License
 * along with this program; if not, see <https://github.com/FrozenBlock/Licenses>.
 */

package net.frozenblock.glowtone;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Internal
public final class GlowtoneConstants {
	public static final String PROJECT_ID = "Glowtone";
	public static final String MOD_ID = "glowtone";

	public static final String EMISSIVE_SUFFIX = "_glowtone_emissive";
	public static final String EMISSIVE_SUFFIX_SHORT = "_ge";
	public static final String[] EMISSIVE_SUFFIXES = {EMISSIVE_SUFFIX, EMISSIVE_SUFFIX_SHORT};

	public static boolean GLOWTONE_REPLACEMENTS = false;
	public static boolean GLOWTONE_SHADING = false;

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(GlowtoneConstants.MOD_ID, path);
	}

	public static String string(String path) {
		return id(path).toString();
	}

	public static String safeString(String path) {
		return id(path).toString().replace(":", "_");
	}

	public static boolean isEmissivePath(String path) {
		for (String suffix : EMISSIVE_SUFFIXES) {
			if (path.endsWith(suffix)) return true;
		}
		return false;
	}

	@Nullable
	public static <T> T findEmissiveVariant(Identifier baseLocation, Function<Identifier, T> lookup, Predicate<T> isValid) {
		for (String suffix : EMISSIVE_SUFFIXES) {
			final T candidate = lookup.apply(withEmissiveSuffix(baseLocation, suffix));
			if (isValid.test(candidate)) return candidate;
		}
		return null;
	}

	private static Identifier withEmissiveSuffix(Identifier location, String suffix) {
		final String path = location.getPath();
		final int lastSlash = path.lastIndexOf('/');
		final int lastDot = path.lastIndexOf('.');
		if (lastDot > lastSlash) {
			return location.withPath(path.substring(0, lastDot) + suffix + path.substring(lastDot));
		}
		return location.withSuffix(suffix);
	}

	private GlowtoneConstants() {}
}
