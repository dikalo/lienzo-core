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

package com.ait.lienzo.client.core.shape;

import java.util.Collection;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.image.ImageClipBounds;
import com.ait.lienzo.client.core.image.ImageShapeFilteredHandler;
import com.ait.lienzo.client.core.image.ImageShapeLoadedHandler;
import com.ait.lienzo.client.core.image.PictureFilteredHandler;
import com.ait.lienzo.client.core.image.PictureLoadedHandler;
import com.ait.lienzo.client.core.image.filter.ImageDataFilter;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterChain;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterable;
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.client.core.shape.json.JSONDeserializer;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import com.ait.lienzo.shared.core.types.ImageSerializationMode;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.tooling.nativetools.client.collection.MetaData;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ImageResource;

/**
 * Image Support for Canvas
 * <ul>
 *  <li>Supports {@link ImageResource}</li>
 *  <li>Supports Image based on URL</li>
 * </ul>
 *
 * If the <code>listening</code> attribute is set to false, it will not be drawn in the Selection Layer,
 * which means it can not be dragged or picked. This also means, it will not respond
 * to events.
 * <p>
 * The upside is that it will not need to generate a separate Image for the Selection Layer,
 * which saves memory and time, both for generating the selection layer Image and when drawing the Picture.
 */
public class Picture extends AbstractImageShape<Picture> implements ImageDataFilterable<Picture>
{
    protected Picture(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.PICTURE, node, ctx);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     */
    public Picture(final String url)
    {
        super(ShapeType.PICTURE, url, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(createPictureLoader());

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler)
    {
        super(ShapeType.PICTURE, url, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, url, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final ImageSelectionMode mode)
    {
        super(ShapeType.PICTURE, url, true, mode);

        onLoaded(loadedHandler);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final ImageSelectionMode mode, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, url, true, mode);

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final boolean listening)
    {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final boolean listening, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final boolean listening, final ImageSelectionMode mode)
    {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final boolean listening, final ImageSelectionMode mode, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth
     * @param sh clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final boolean listening)
    {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth
     * @param sh clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final boolean listening, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth
     * @param sh clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final boolean listening, final ImageSelectionMode mode)
    {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param url
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth
     * @param sh clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final boolean listening, final ImageSelectionMode mode, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL using the default category.
     *
     * @param url
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth (0 means: use image width)
     * @param sh clippedImageHeight (0 means: use image height)
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final int dw, final int dh, final boolean listening)
    {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL using the default category.
     *
     * @param url
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth (0 means: use image width)
     * @param sh clippedImageHeight (0 means: use image height)
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final int dw, final int dh, final boolean listening, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL using the default category.
     *
     * @param url
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth (0 means: use image width)
     * @param sh clippedImageHeight (0 means: use image height)
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final int dw, final int dh, final boolean listening, final ImageSelectionMode mode)
    {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL using the default category.
     *
     * @param url
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth (0 means: use image width)
     * @param sh clippedImageHeight (0 means: use image height)
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final int dw, final int dh, final boolean listening, final ImageSelectionMode mode, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>category
     * </ul>
     *
     * @param url
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int dw, final int dh, final boolean listening)
    {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>category
     * </ul>
     *
     * @param url
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int dw, final int dh, final boolean listening, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, url, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>category
     * </ul>
     *
     * @param url
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int dw, final int dh, final boolean listening, final ImageSelectionMode mode)
    {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from a URL.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>category
     * </ul>
     *
     * @param url
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final String url, final PictureLoadedHandler loadedHandler, final int dw, final int dh, final boolean listening, final ImageSelectionMode mode, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, url, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(url);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     */
    public Picture(final ImageResource resource)
    {
        super(ShapeType.PICTURE, resource, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(createPictureLoader());

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler)
    {
        super(ShapeType.PICTURE, resource, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, resource, true, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final ImageSelectionMode mode)
    {
        super(ShapeType.PICTURE, resource, true, mode);

        onLoaded(loadedHandler);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final ImageSelectionMode mode, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, resource, true, mode);

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final boolean listening)
    {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final boolean listening, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final boolean listening, final ImageSelectionMode mode)
    {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * <li>category
     * </ul>
     *
     * @param resource ImageResource
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final boolean listening, final ImageSelectionMode mode, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * </ul>
     *
     * @param resource ImageResource
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth
     * @param sh clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final boolean listening)
    {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * </ul>
     *
     * @param resource ImageResource
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth
     * @param sh clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final boolean listening, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * </ul>
     *
     * @param resource ImageResource
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth
     * @param sh clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final boolean listening, final ImageSelectionMode mode)
    {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageDestinationWidth - 0 (means: use clippedImageWidth)
     * <li>clippedImageDestinationHeight - 0 (means: use clippedImageHeight)
     * </ul>
     *
     * @param resource ImageResource
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth
     * @param sh clippedImageHeight
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final boolean listening, final ImageSelectionMode mode, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     *
     * @param resource ImageResource
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth (0 means: use image width)
     * @param sh clippedImageHeight (0 means: use image height)
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final int dw, final int dh, final boolean listening)
    {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     *
     * @param resource ImageResource
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth (0 means: use image width)
     * @param sh clippedImageHeight (0 means: use image height)
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final int dw, final int dh, final boolean listening, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     *
     * @param resource ImageResource
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth (0 means: use image width)
     * @param sh clippedImageHeight (0 means: use image height)
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final int dw, final int dh, final boolean listening, final ImageSelectionMode mode)
    {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     *
     * @param resource ImageResource
     * @param sx clippedImageStartX
     * @param sy clippedImageStartY
     * @param sw clippedImageWidth (0 means: use image width)
     * @param sh clippedImageHeight (0 means: use image height)
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int sx, final int sy, final int sw, final int sh, final int dw, final int dh, final boolean listening, final ImageSelectionMode mode, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageStartX(sx);

        setClippedImageStartY(sy);

        setClippedImageWidth(sw);

        setClippedImageHeight(sh);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * </ul>
     *
     * @param resource ImageResource
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int dw, final int dh, final boolean listening)
    {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * </ul>
     *
     * @param resource ImageResource
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int dw, final int dh, final boolean listening, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, resource, listening, LienzoCore.get().getDefaultImageSelectionMode());

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * </ul>
     *
     * @param resource ImageResource
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int dw, final int dh, final boolean listening, final ImageSelectionMode mode)
    {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().load(resource);
    }

    /**
     * Creates a Picture from an ImageResource.
     * The following attributes are defaulted:
     * <ul>
     * <li>clippedImageStartX - 0
     * <li>clippedImageStartY - 0
     * <li>clippedImageWidth - 0 (means: use image width)
     * <li>clippedImageHeight - 0 (means: use image height)
     * </ul>
     *
     * @param resource ImageResource
     * @param dw clippedImageDestinationWidth (0 means: use clippedImageWidth)
     * @param dh clippedImageDestinationHeight (0 means: use clippedImageHeight)
     * @param listening When set to false, the Picture can't be dragged or picked,
     *      but it will be drawn faster and use less memory.
     */
    public Picture(final ImageResource resource, final PictureLoadedHandler loadedHandler, final int dw, final int dh, final boolean listening, final ImageSelectionMode mode, final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        super(ShapeType.PICTURE, resource, listening, mode);

        onLoaded(loadedHandler);

        setClippedImageDestinationWidth(dw);

        setClippedImageDestinationHeight(dh);

        getImageProxy().setFilters(filter, filters);

        getImageProxy().load(resource);
    }

    @Override
    public Picture setFilters(final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        getImageProxy().setFilters(filter, filters);

        return this;
    }

    @Override
    public Picture addFilters(final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        getImageProxy().addFilters(filter, filters);

        return this;
    }

    @Override
    public Picture removeFilters(final ImageDataFilter<?> filter, final ImageDataFilter<?>... filters)
    {
        getImageProxy().removeFilters(filter, filters);

        return this;
    }

    @Override
    public Picture clearFilters()
    {
        getImageProxy().clearFilters();

        return this;
    }

    @Override
    public Collection<ImageDataFilter<?>> getFilters()
    {
        return getImageProxy().getFilters();
    }

    @Override
    public Picture setFiltersActive(final boolean active)
    {
        getImageProxy().setFiltersActive(active);

        return this;
    }

    @Override
    public boolean areFiltersActive()
    {
        return getImageProxy().areFiltersActive();
    }

    @Override
    public Picture setFilters(final Iterable<ImageDataFilter<?>> filters)
    {
        getImageProxy().setFilters(filters);

        return this;
    }

    @Override
    public Picture addFilters(final Iterable<ImageDataFilter<?>> filters)
    {
        getImageProxy().addFilters(filters);

        return this;
    }

    @Override
    public Picture removeFilters(final Iterable<ImageDataFilter<?>> filters)
    {
        getImageProxy().removeFilters(filters);

        return this;
    }

    /**
     * Serializes this shape as a {@link JSONObject}
     *
     * @return JSONObject
     */
    @Override
    public JSONObject toJSONObject()
    {
        final JSONObject attr = new JSONObject(getAttributes().getJSO());

        if (getImageSerializationMode() == ImageSerializationMode.DATA_URL)
        {
            String url = getImageProxy().getImageElementURL();

            if (null == url)
            {
                url = getAttributes().getURL();
            }
            if (url.startsWith("data:"))
            {
                attr.put("url", new JSONString(url));
            }
            else
            {
                attr.put("url", new JSONString(toDataURL(false)));
            }
        }
        final JSONObject object = new JSONObject();

        object.put("type", new JSONString(getShapeType().getValue()));

        if (hasMetaData())
        {
            final MetaData meta = getMetaData();

            if (false == meta.isEmpty())
            {
                object.put("meta", new JSONObject(meta.getJSO()));
            }
        }
        object.put("attributes", attr);

        final ImageDataFilterChain chain = getImageProxy().getFilterChain();

        if ((null != chain) && (chain.size() > 0))
        {
            final JSONArray filters = new JSONArray();

            final JSONObject filter = new JSONObject();

            filter.put("active", JSONBoolean.getInstance(chain.isActive()));

            for (final ImageDataFilter<?> ifilter : chain.getFilters())
            {
                if (null != ifilter)
                {
                    final JSONObject make = ifilter.toJSONObject();

                    if (null != make)
                    {
                        filters.set(filters.size(), make);
                    }
                }
            }
            filter.put("filters", filters);

            object.put("filter", filter);
        }
        return object;
    }

    /**
     * Draws the image on the canvas.
     *
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        context.save();

        if (false == context.isSelection())
        {
            context.setGlobalAlpha(alpha);

            if (attr.hasShadow())
            {
                doApplyShadow(context, attr);
            }
        }
        getImageProxy().drawImage(context);

        context.restore();

        return false;
    }

    /**
     * Returns the x coordinate of the picture's clip region.
     * The default value is 0.
     *
     * @return int
     */
    public int getClippedImageStartX()
    {
        return getAttributes().getClippedImageStartX();
    }

    /**
     * Sets the x coordinate of the picture's clip region.
     * The default value is 0.
     *
     * @param sx
     * @return Picture this picture
     */
    public Picture setClippedImageStartX(final int sx)
    {
        getAttributes().setClippedImageStartX(sx);

        return this;
    }

    /**
     * Returns the y coordinate of the picture's clip region.
     * The default value is 0.
     *
     * @return int
     */
    public int getClippedImageStartY()
    {
        return getAttributes().getClippedImageStartY();
    }

    /**
     * Returns the y coordinate of the picture's clip region.
     * The default value is 0.
     *
     * @return Picture this picture
     */
    public Picture setClippedImageStartY(final int clippedImageStartY)
    {
        getAttributes().setClippedImageStartY(clippedImageStartY);

        return this;
    }

    /**
     * Returns the width of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the width of the loaded image.
     *
     * @return int
     */
    public int getClippedImageWidth()
    {
        return getAttributes().getClippedImageWidth();
    }

    /**
     * Sets the width of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the width of the loaded image.
     *
     * @param clippedImageWidth
     * @return Picture this picture
     */
    public Picture setClippedImageWidth(final int clippedImageWidth)
    {
        getAttributes().setClippedImageWidth(clippedImageWidth);

        return this;
    }

    /**
     * Returns the height of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the height of the loaded image.
     *
     * @return int
     */
    public int getClippedImageHeight()
    {
        return getAttributes().getClippedImageHeight();
    }

    /**
     * Sets the height of the picture's clip region.
     * If the value is not set, it defaults to 0, which means it will
     * use the height of the loaded image.
     *
     * @param clippedImageHeight
     * @return Picture this picture
     */
    public Picture setClippedImageHeight(final int clippedImageHeight)
    {
        getAttributes().setClippedImageHeight(clippedImageHeight);

        return this;
    }

    /**
     * Returns the width of the destination region.
     * The default value is 0, which means it will use the clippedImageWidth.
     *
     * @return int
     */
    public int getClippedImageDestinationWidth()
    {
        return getAttributes().getClippedImageDestinationWidth();
    }

    /**
     * Sets the width of the destination region.
     * The default value is 0, which means it will use the clippedImageWidth.
     *
     * @param clippedImageDestinationWidth
     * @return Picture
     */
    public Picture setClippedImageDestinationWidth(final int clippedImageDestinationWidth)
    {
        getAttributes().setClippedImageDestinationWidth(clippedImageDestinationWidth);

        return this;
    }

    /**
     * Returns the height of the destination region.
     * The default value is 0, which means it will use the clippedImageHeight.
     * <p>
     * Setting this value will cause the image to be scaled.
     * This can be used to reduce the memory footprint of the Image
     * used in the selection layer.
     * <p>
     * Note that further scaling can be achieved via the <code>scale</code>
     * or <code>transform</code> attributes, which apply to all Shapes.
     *
     * @return int
     */
    public int getClippedImageDestinationHeight()
    {
        return getAttributes().getClippedImageDestinationHeight();
    }

    /**
     * Sets the height of the destination region.
     * The default value is 0, which means it will use the clippedImageHeight.
     * <p>
     * Setting this value will cause the image to be scaled.
     * This can be used to reduce the memory footprint of the Image
     * used in the selection layer.
     * <p>
     * Note that further scaling can be achieved via the <code>scale</code>
     * or <code>transform</code> attributes, which apply to all Shapes.
     *
     * @param clippedImageDestinationHeight
     * @return Picture
     */
    public Picture setClippedImageDestinationHeight(final int clippedImageDestinationHeight)
    {
        getAttributes().setClippedImageDestinationHeight(clippedImageDestinationHeight);

        return this;
    }

    public Picture reFilter(final PictureFilteredHandler handler)
    {
        getImageProxy().reFilter(new ImageShapeFilteredHandler<Picture>()
        {
            @Override
            public void onImageShapeFiltered(final Picture picture)
            {
                handler.onPictureFiltered(picture);
            }
        });
        return this;
    }

    public Picture unFilter(final PictureFilteredHandler handler)
    {
        getImageProxy().unFilter(new ImageShapeFilteredHandler<Picture>()
        {
            @Override
            public void onImageShapeFiltered(final Picture picture)
            {
                handler.onPictureFiltered(picture);
            }
        });
        return this;
    }

    @Override
    public ImageClipBounds getImageClipBounds()
    {
        return new ImageClipBounds(getClippedImageStartX(), getClippedImageStartY(), getClippedImageWidth(), getClippedImageHeight(), getClippedImageDestinationWidth(), getClippedImageDestinationHeight());
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return asAttributes(Attribute.URL, Attribute.CLIPPED_IMAGE_START_X, Attribute.CLIPPED_IMAGE_START_Y, Attribute.CLIPPED_IMAGE_WIDTH, Attribute.CLIPPED_IMAGE_HEIGHT, Attribute.CLIPPED_IMAGE_DESTINATION_WIDTH, Attribute.CLIPPED_IMAGE_DESTINATION_HEIGHT);
    }

    public static class PictureFactory extends ShapeFactory<Picture>
    {
        public PictureFactory()
        {
            super(ShapeType.PICTURE);

            addAttribute(Attribute.URL, true);

            addAttribute(Attribute.CLIPPED_IMAGE_START_X);

            addAttribute(Attribute.CLIPPED_IMAGE_START_Y);

            addAttribute(Attribute.CLIPPED_IMAGE_WIDTH);

            addAttribute(Attribute.CLIPPED_IMAGE_HEIGHT);

            addAttribute(Attribute.CLIPPED_IMAGE_DESTINATION_WIDTH);

            addAttribute(Attribute.CLIPPED_IMAGE_DESTINATION_HEIGHT);

            addAttribute(Attribute.SERIALIZATION_MODE);

            addAttribute(Attribute.IMAGE_SELECTION_MODE);
        }

        @Override
        public Picture create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            final Picture picture = new Picture(node, ctx);

            JSONValue jval = node.get("filter");

            if (null != jval)
            {
                final JSONObject object = jval.isObject();

                if (null != object)
                {
                    JSONDeserializer.get().deserializeFilters(picture, object, ctx);

                    jval = object.get("active");

                    final JSONBoolean active = jval.isBoolean();

                    if (null != active)
                    {
                        picture.setFiltersActive(active.booleanValue());
                    }
                }
            }
            return picture;
        }

        @Override
        public boolean isPostProcessed()
        {
            return true;
        }

        @Override
        public void process(final IJSONSerializable<?> node, final ValidationContext ctx) throws ValidationException
        {
            if (false == (node instanceof Picture))
            {
                return;
            }
            final Picture self = (Picture) node;

            if (false == self.isLoaded())
            {
                self.getImageProxy().load(self.getURL());

                self.onLoaded(self.createPictureLoader());
            }
        }
    }

    private PictureLoadedHandler createPictureLoader()
    {
        return new PictureLoadedHandler()
        {
            @Override
            public void onPictureLoaded(final Picture picture)
            {
                if (picture.isLoaded() && picture.isVisible())
                {
                    final Layer layer = picture.getLayer();

                    if ((null != layer) && (null != layer.getViewport()))
                    {
                        layer.batch();
                    }
                }
            }
        };
    }

    private void onLoaded(final PictureLoadedHandler handler)
    {
        getImageProxy().setImageShapeLoadedHandler(new ImageShapeLoadedHandler<Picture>()
        {
            @Override
            public void onImageShapeLoaded(final Picture picture)
            {
                handler.onPictureLoaded(picture);
            }
        });
    }
}
