package com.ait.lienzo.client.core.util;

import org.junit.Test;

import static com.ait.lienzo.client.core.util.StringFormatter.format;
import static com.ait.lienzo.client.core.util.StringFormatter.toFixed;
import static org.junit.Assert.assertEquals;

public class StringFormatterTest
{

    @Test
    public void testFormat()
    {
        assertEquals("", format(""));

        assertEquals("Sample String", format("Sample String", 1, 2));
        assertEquals("{Sample String}", format("{Sample String}", 3, 4));

        assertEquals("{0", format("{0", "test"));
        assertEquals("0}", format("0}", "test2"));

        assertEquals("test!", format("{0}", "test!"));
        assertEquals("simple sample", format("simple {0}", "sample", "redundant"));

        assertEquals("simple {0}", format("simple {0}"));
    }

    @Test(expected = NullPointerException.class)
    public void testFormatNull()
    {
        assertEquals(null, format(null));
    }

    @Test
    public void testToFixed()
    {
        assertEquals("0.0", toFixed(0, 10));
        assertEquals("1.23456789", toFixed(1.23456789, -1));

        assertEquals("1.0", toFixed(1.45, 0));
        assertEquals("2.0", toFixed(1.54665, 0));

        assertEquals("4.568", toFixed(4.5678, 3));
        assertEquals("4.5671", toFixed(4.56714, 4));


        assertEquals("-1.68", toFixed(-1.678245423, 2));
        assertEquals("-0.67", toFixed(-0.671347568, 2));

    }

}
