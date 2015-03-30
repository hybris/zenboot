package org.zenboot.portal.processing.meta

import grails.test.mixin.*
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Test

import org.zenboot.portal.processing.meta.annotation.ParameterType

@TestMixin(GrailsUnitTestMixin)
class ParameterMetadataListTest {

    @Test
    void testNew() {
      ParameterMetadataList pml = new ParameterMetadataList()

      ParameterMetadata pmOne = new ParameterMetadata(name:"aName1", defaultValue:"aDefaultValue1", type:ParameterType.CONSUME)
      ParameterMetadata pmTwo = new ParameterMetadata(name:"aName2", defaultValue:"aDefaultValue2", type:ParameterType.CONSUME)

      def coll = []

      coll << pmOne
      coll << pmTwo

      pml.addParameters(coll)

      assertEquals("expect 2", 2, pml.parameters.size())


    }
}
