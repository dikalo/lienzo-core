/*
 * Copyright (c) 2018 Ahome' Innovation Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;

public class BackingColorMapUtils
{
    public static ImageData drawShapesToBacking(final NFastArrayList<WiresShape> prims, final ScratchPad scratch, final WiresContainer skip, final NFastStringMap<WiresShape> shape_color_map)
    {
        scratch.clear();
        final Context2D ctx = scratch.getContext();

        shape_color_map.clear();
        drawShapesToBacking(prims, ctx, skip, shape_color_map);

        return ctx.getImageData(0, 0, scratch.getWidth(), scratch.getHeight());
    }

    public static void drawShapesToBacking(final NFastArrayList<WiresShape> prims, final Context2D ctx, final WiresContainer skip, final NFastStringMap<WiresShape> shape_color_map)
    {
        for (int j = 0; j < prims.size(); j++)
        {
            final WiresShape prim = prims.get(j);
            if (prim == skip)
            {
                continue;
            }
            drawShapeToBacking(ctx, prim, MagnetManager.m_c_rotor.next(), shape_color_map);

            if ((prim.getChildShapes() != null) && !prim.getChildShapes().isEmpty())
            {
                drawShapesToBacking(prim.getChildShapes(), ctx, skip, shape_color_map);
            }
        }
    }

    public static void drawShapeToBacking(final Context2D ctx, final WiresShape shape, final String color, final NFastStringMap<WiresShape> m_shape_color_map)
    {
        m_shape_color_map.put(color, shape);
        drawShapeToBacking(ctx, shape, color);
    }

    public static void drawShapeToBacking(final Context2D ctx, final WiresShape shape, final String color)
    {
        final MultiPath multiPath = shape.getPath();
        drawShapeToBacking(ctx, shape, color, multiPath.getStrokeWidth(), true);
    }

    public static void drawShapeToBacking(final Context2D ctx, final WiresShape shape, final String color, final double strokeWidth, final boolean fill)
    {
        drawShapeToBacking(ctx, shape.getPath(), color, strokeWidth, fill);
    }

    public static void drawShapeToBacking(final Context2D ctx, final MultiPath multiPath, final String color, final double strokeWidth, final boolean fill)
    {
        final NFastArrayList<PathPartList> listOfPaths = multiPath.getActualPathPartListArray();

        for (int k = 0; k < listOfPaths.size(); k++)
        {
            final PathPartList path = listOfPaths.get(k);

            ctx.setStrokeWidth(strokeWidth);
            ctx.setStrokeColor(color);
            ctx.setFillColor(color);
            ctx.beginPath();

            final Point2D absLoc = multiPath.getComputedLocation();
            final double offsetX = absLoc.getX();
            final double offsetY = absLoc.getY();

            ctx.moveTo(offsetX, offsetY);

            boolean closed = false;
            for (int i = 0; i < path.size(); i++)
            {
                final PathPartEntryJSO entry = path.get(i);
                NFastDoubleArrayJSO points = entry.getPoints();

                switch (entry.getCommand())
                {
                    case PathPartEntryJSO.MOVETO_ABSOLUTE:
                    {
                        ctx.moveTo(points.get(0) + offsetX, points.get(1) + offsetY);
                        break;
                    }
                    case PathPartEntryJSO.LINETO_ABSOLUTE:
                    {
                        points = entry.getPoints();
                        final double x0 = points.get(0) + offsetX;
                        final double y0 = points.get(1) + offsetY;
                        ctx.lineTo(x0, y0);
                        break;
                    }
                    case PathPartEntryJSO.CLOSE_PATH_PART:
                    {
                        ctx.closePath();
                        closed = true;
                        break;
                    }
                    case PathPartEntryJSO.CANVAS_ARCTO_ABSOLUTE:
                    {
                        points = entry.getPoints();

                        final double x0 = points.get(0) + offsetX;
                        final double y0 = points.get(1) + offsetY;

                        final double x1 = points.get(2) + offsetX;
                        final double y1 = points.get(3) + offsetY;
                        final double r = points.get(4);
                        ctx.arcTo(x0, y0, x1, y1, r);

                    }
                    break;
                }
            }

            if (!closed)
            {
                ctx.closePath();
            }
            if (fill)
            {
                ctx.fill();
            }
            ctx.stroke();
        }
    }

    public static String findColorAtPoint(final ImageData imageData, final int x, final int y)
    {
        final int red = imageData.getRedAt(x, y);
        final int green = imageData.getGreenAt(x, y);
        final int blue = imageData.getBlueAt(x, y);
        final int alpha = imageData.getAlphaAt(x, y);

        if (alpha != 255)
        {
            return null;
        }
        final String color = Color.rgbToBrowserHexColor(red, green, blue);

        return color;
    }
}