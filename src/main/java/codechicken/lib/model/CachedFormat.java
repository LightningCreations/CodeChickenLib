/*
 * This file is part of CodeChickenLib.
 * Copyright (c) 2018, covers1624, All rights reserved.
 *
 * CodeChickenLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * CodeChickenLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CodeChickenLib. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package codechicken.lib.model;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple VertexFormat cache.
 * This caches the existence of attributes and their indexes.
 *
 * @author covers1624
 */
public class CachedFormat {

    public static final Map<VertexFormat, CachedFormat> formatCache = new ConcurrentHashMap<>();
    private static final CachedFormat BLOCK = new CachedFormat(DefaultVertexFormats.BLOCK);

    /**
     * Lookup or create the CachedFormat for a given VertexFormat.
     *
     * @param format The format to lookup.
     * @return The CachedFormat.
     */
    public static CachedFormat lookup(VertexFormat format) {
        //Hotwire the common one.
        if (format == DefaultVertexFormats.BLOCK) {
            return BLOCK;
        }
        return formatCache.computeIfAbsent(format, CachedFormat::new);
    }

    public VertexFormat format;

    public boolean hasPosition;
    public boolean hasNormal;
    public boolean hasColor;
    public boolean hasUV;
    public boolean hasOverlay;
    public boolean hasLightMap;

    public int positionIndex = -1;
    public int normalIndex = -1;
    public int colorIndex = -1;
    public int uvIndex = -1;
    public int overlayIndex = -1;
    public int lightMapIndex = -1;

    public int elementCount;

    /**
     * Caches the vertex format element indexes for efficiency.
     *
     * @param format The format.
     */
    public CachedFormat(VertexFormat format) {
        this.format = format;
        List<VertexFormatElement> elements = format.getElements();
        elementCount = elements.size();
        for (int i = 0; i < elementCount; i++) {
            VertexFormatElement element = elements.get(i);
            label:
            switch (element.getUsage()) {
                case POSITION:
                    if (hasPosition) {
                        throw new IllegalStateException("Found 2 position elements..");
                    }
                    hasPosition = true;
                    positionIndex = i;
                    break;
                case NORMAL:
                    if (hasNormal) {
                        throw new IllegalStateException("Found 2 normal elements..");
                    }
                    hasNormal = true;
                    normalIndex = i;
                    break;
                case COLOR:
                    if (hasColor) {
                        throw new IllegalStateException("Found 2 color elements..");
                    }
                    hasColor = true;
                    colorIndex = i;
                    break;
                case UV:
                    switch (element.getIndex()) {
                        case 0:
                            if (hasUV) {
                                throw new IllegalStateException("Found 2 UV elements..");
                            }
                            hasUV = true;
                            uvIndex = i;
                            break label;
                        case 1:
                            if (hasOverlay) {
                                throw new IllegalStateException("Found 2 Overlay elements..");
                            }
                            hasOverlay = true;
                            overlayIndex = i;
                            break label;
                        case 2:
                            if (hasLightMap) {
                                throw new IllegalStateException("Found 2 LightMap elements..");
                            }
                            hasLightMap = true;
                            lightMapIndex = i;
                            break label;
                    }
                    break;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CachedFormat)) {
            return false;
        }
        CachedFormat other = (CachedFormat) obj;
        return other.elementCount == elementCount && //
                other.positionIndex == positionIndex && //
                other.normalIndex == normalIndex && //
                other.colorIndex == colorIndex && //
                other.uvIndex == uvIndex && //
                other.lightMapIndex == lightMapIndex;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + elementCount;
        result = 31 * result + positionIndex;
        result = 31 * result + normalIndex;
        result = 31 * result + colorIndex;
        result = 31 * result + uvIndex;
        result = 31 * result + lightMapIndex;
        return result;
    }
}
