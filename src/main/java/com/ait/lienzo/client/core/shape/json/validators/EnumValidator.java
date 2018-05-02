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

package com.ait.lienzo.client.core.shape.json.validators;

import java.util.Arrays;
import java.util.List;

import com.ait.lienzo.shared.core.types.EnumWithValue;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class EnumValidator<T extends Enum<T> & EnumWithValue> extends AbstractAttributeTypeValidator
{
    private final List<T> m_values;

    public EnumValidator(final String typeName, final T[] values)
    {
        super(typeName);

        m_values = Arrays.asList(values);
    }

    @Override
    public void validate(final JSONValue jval, final ValidationContext ctx) throws ValidationException
    {
        if (null == jval)
        {
            ctx.addBadTypeError(getTypeName());

            return;
        }
        final JSONString sval = jval.isString();

        if (null == sval)
        {
            ctx.addBadTypeError(getTypeName());
        }
        else
        {
            final String string = sval.stringValue();

            if (null != string)
            {
                for (final T value : m_values)
                {
                    if (string.equals(value.getValue()))
                    {
                        return;
                    }
                }
            }
            ctx.addBadValueError(getTypeName(), jval);
        }
    }
}