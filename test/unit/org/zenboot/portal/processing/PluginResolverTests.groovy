package org.zenboot.portal.processing

import java.io.File;

import grails.test.mixin.*
import grails.test.mixin.support.GrailsUnitTestMixin

import org.junit.Test

@TestMixin(GrailsUnitTestMixin)
class PluginResolverTests {

    @Test
    void testResolveScriptletPlugin() {     
        PluginResolver pluginResolver = new PluginResolver(getTestScriptDir("testPluginResolver"))

        //no attributes - get default plugin
        File plugin1 = pluginResolver.resolveScriptletPlugin(new Scriptlet(file:new File('/bla/blub/100_attr1_script1.muh')))
        assertEquals("Resovled plugin was wrong", 'Script1.groovy', plugin1.name)

        //use plugin with best rank for attr1
        File plugin1WithAttr = pluginResolver.resolveScriptletPlugin(new Scriptlet(file:new File('/bla/blub/100_attrA_script1.muh')), ['attr1'])
        assertEquals("Resovled plugin was wrong", 'Script1.groovy', plugin1WithAttr.name)

        //use plugin with best rank for attr1
        File pluginNotFound = pluginResolver.resolveScriptletPlugin(new Scriptlet(file:new File('/bla/blub/200_attrB_xyz.muh')), ['attr1'])
        assertNull("Resovled plugin was wrong", pluginNotFound)

        //use plugin with best rank for attrXYZ
        File plugin2WithAttrNotFound = pluginResolver.resolveScriptletPlugin(new Scriptlet(file:new File('/bla/blub/100_script2.muh')), ['attrXYZ'])
        assertEquals("Resovled plugin was wrong", 'Script2.groovy', plugin2WithAttrNotFound.name)

        //use plugin with best rank for attr1
        File plugin2WithAttr = pluginResolver.resolveScriptletPlugin(new Scriptlet(file:new File('/bla/blub/100_script2.muh')), ['attr1'])
        assertEquals("Resovled plugin was wrong", 'Attr1Script2.groovy', plugin2WithAttr.name)

        //use plugin with best rank for attr1+attr2
        File plugin2With2Attr = pluginResolver.resolveScriptletPlugin(new Scriptlet(file:new File('/bla/blub/100_script2.muh')), ['attr1', 'attr2'])
        assertEquals("Resovled plugin was wrong", 'Attr1Attr2Script2.groovy', plugin2With2Attr.name)
    }

    @Test
    void testResolveScriptletBatchPlugin() {
        PluginResolver pluginResolver = new PluginResolver(getTestScriptDir("testPluginResolver"))

        //not hits - use default plugin for ExecutionZoneAction
        File pluginFolder1 = pluginResolver.resolveScriptletBatchPlugin(new ScriptletBatch(
            executionZoneAction:createExecutionZoneMock("exec_folder_1", "zone_type_1")
        ))
        assertEquals("Resolved plugin was wrong", 'ExecFolder1.groovy', pluginFolder1.name)

        //no default plugin (use ExecutionZoneAction)
        File pluginFolder2 = pluginResolver.resolveScriptletBatchPlugin(new ScriptletBatch(
            executionZoneAction:createExecutionZoneMock("exec_folder_2", "zone_type_1")
        ))
        assertNull("Resolved plugin was wrong", pluginFolder2)

        //use plugin with best rank for attr2 (use ExecutionZoneAction)
        File pluginFolder2Attr = pluginResolver.resolveScriptletBatchPlugin(new ScriptletBatch(
            executionZoneAction:createExecutionZoneMock("exec_folder_2", "zone_type_1")),
            ['attr2']
        )
        assertNull("Resolved plugin was wrong", pluginFolder2Attr)

        //use plugin with best rank for attr1 (use ExecutionZoneAction) 
        File pluginFolder2Attr2 = pluginResolver.resolveScriptletBatchPlugin(new ScriptletBatch(
            executionZoneAction:createExecutionZoneMock("exec_folder_2", "zone_type_1")),
            ['attr1']
        )
        assertEquals("Resolved plugin was wrong", 'Attr1ExecFolder2.groovy', pluginFolder2Attr2.name)

        //use plugin with best rank for attr1+attr2 (use ExecutionZoneAction)
        File pluginFolder2Attr3 = pluginResolver.resolveScriptletBatchPlugin(new ScriptletBatch(
            executionZoneAction:createExecutionZoneMock("exec_folder_2", "zone_type_1")),
            ['attr1', 'attr2']
        )
        assertEquals("Resolved plugin was wrong", 'Attr1Attr2ExecFolder2.groovy', pluginFolder2Attr3.name)
    }

	private ExecutionZoneAction createExecutionZoneMock(String scriptDir, String typeName) {
		ExecutionZoneAction execZoneAction = new ExecutionZoneAction(scriptDir:new File("/bla/blub/${scriptDir}"))
		ExecutionZone execZone = new ExecutionZone()
		execZoneAction.executionZone = execZone
		ExecutionZoneType execZoneType = new ExecutionZoneType(name:typeName)
		execZone.type = execZoneType
		return execZoneAction
	}

    private File getTestScriptDir(String scriptDir) {
        return new File("test/resources/${this.class.getPackage().getName()}/${scriptDir}")
    }

}
