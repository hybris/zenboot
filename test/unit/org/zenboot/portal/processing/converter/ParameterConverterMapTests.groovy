package org.zenboot.portal.processing.converter

import grails.test.mixin.*
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Test

import javax.mail.internet.InternetAddress

@TestMixin(GrailsUnitTestMixin)
class ParameterConverterMapTests {

    @Test
    void testMap() {
        ParameterConverterMap convMap = new ParameterConverterMap(parameterConverters:[new DefaultParameterConverter()])

        def key1 = "stringKey"
        def entry1 = "stringValue"
        def key2 = new Integer(1)
        def entry2 = new InternetAddress("test@blub.com")

        assertTrue("Map should be empty", convMap.isEmpty())

        convMap.put(key1, entry1)
        convMap[key2] =  entry2

        assertFalse("Map should be empty", convMap.isEmpty())
        assertEquals("Size is wrong", 2, convMap.size())

        assertEquals("Map entry is missing", entry1, convMap.get(key1))
        assertEquals("Map entry is missing", entry1, convMap.getObject(key1))
        assertEquals("Map entry is missing", entry2.toString(), convMap[key2])
        assertEquals("Map entry is missing", entry2, convMap.getObject(key2))

        assertTrue("ContainsKey is failing", convMap.containsKey(key1))
        assertTrue("ContainsKey is failing", convMap.containsKey(key2))
        assertTrue("ContainsValue is failing", convMap.containsValue(entry1))
        assertTrue("ContainsValue is failing", convMap.containsValue(entry2.toString()))

        assertEquals("Remove failed", entry1, convMap.remove(key1))
        assertEquals("Remove failed", entry2.toString(), convMap.remove(key2))
        assertTrue("Map has to be empty", convMap.isEmpty())

        convMap.putAll([(key1):entry1, (key2):entry2])
        assertFalse("Map should be empty", convMap.isEmpty())
        assertEquals("Size is wrong", 2, convMap.size())

        assertEquals("Get key set failed", [key1, key2].asType(String[]), convMap.keySet().asType(String[]))
        assertEquals("Get values failed", [entry1, entry2.toString()].asType(String[]), convMap.values().asType(String[]))
        assertEquals("Entry set failed", 2, convMap.entrySet().size())

        convMap.clear()
        assertTrue("Map should be empty", convMap.isEmpty())
        assertEquals("Size is wrong", 0, convMap.size())
    }
}