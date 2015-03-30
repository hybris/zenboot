package org.zenboot.portal.processing.meta

import grails.test.mixin.*
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Test
import org.zenboot.portal.processing.meta.annotation.ParameterType

@TestMixin(GrailsUnitTestMixin)
class ParameterMetadataTest {

    @Test
    void testNew() {
      ParameterMetadata pm = new ParameterMetadata(name:"aName", defaultValue:"aDefaultValue", type:ParameterType.CONSUME)
    }

    @Test
    void testClone() {
      ParameterMetadata pm1 = new ParameterMetadata(name:"aName", defaultValue:"aDefaultValue", type:ParameterType.CONSUME)
      ParameterMetadata pm2 = pm1.clone()
      assertEquals("Should be equal if cloning", pm1.name, pm2.name)
      assertEquals("Should be equal if cloning", pm1.defaultValue, pm2.defaultValue)
      assertEquals("Should be equal if cloning", pm1.type, pm2.type)
      assertTrue("Should be true", pm1.equals(pm2))
    }

    @Test
    void testMetaClassStuff() {
      ParameterMetadata pm1 = new ParameterMetadata(name:"aName", defaultValue:"aDefaultValue", type:ParameterType.CONSUME)
      println "pm1.metaClass " + pm1.metaClass
      pm1.metaClass.value = "hi"
      assertEquals("hi",pm1.value)
      // Doesn't look that awfull? We're using metaClass features to create properties on the fly
      // this is used in ExecutionZoneService:235-240

      // ... not sure why this is implemented like that, though. 
    }


}
