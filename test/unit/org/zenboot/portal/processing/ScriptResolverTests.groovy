package org.zenboot.portal.processing

import grails.test.mixin.*
import grails.test.mixin.support.GrailsUnitTestMixin

import org.junit.Test

@TestMixin(GrailsUnitTestMixin)
class ScriptResolverTests {

    @Test
    void testScriptFile() {
        File scriptFile = new File("/blub/100_attr1_attr2_scriptname.sh")
        ScriptFile scriptlet = new ScriptFile(scriptFile)
        assertEquals("Qualifiers are wrong", ['attr1', 'attr2'], scriptlet.qualifier.sort())
        assertEquals("Order is wrong", 100, scriptlet.order)
        assertEquals("File is wrong", scriptFile, scriptlet.file)
        assertEquals("Rank is wrong", 999, scriptlet.getRank(['attr2'])) //one fitting qualifier (1000) -  one not fitting qualifier (-1) | (=1000-1)
        assertEquals("Rank is wrong", 2000, scriptlet.getRank(['attr1', 'attr2'])) //two fitting qualifiers a´ 1000 | (=1000+1000)
        assertEquals("Rank is wrong", -2, scriptlet.getRank(['attr3'])) //two not fitting qualifiers a´-1 | (=0-2)
    }

    @Test
    void testScriptResolver() {     
        ScriptResolver scriptletList = new ScriptResolver(getTestScriptDir("testScriptResolver"))

        //no hits - last alphabetically script wins (just relevant for script4-x)
        List scriptletsNoAttr = scriptletList.resolve([])
        assertEquals(
            "Filtered scriptlet list was wrong",
            ['100_attr1_attr2_script1.sh', '101_attr3_script2.sh', '300_script3.sh', '1000_attr1_attr2_attr4_script4-2.groovy', '1001_script5.php'],
            scriptletsNoAttr*.name
        )

        //1hit in script4-1 and script4-2 >> script4-2 wins because its the alphabetically last script
        List scriptletsWithAttr = scriptletList.resolve(['attr1'])
        assertEquals(
            "Filtered scriptlet list was wrong",
            ['100_attr1_attr2_script1.sh', '101_attr3_script2.sh', '300_script3.sh', '1000_attr1_attr2_attr4_script4-2.groovy', '1001_script5.php'],
            scriptletsWithAttr*.name
        )

        //1 hits for script4-1 - script4-1 wins
        List scriptletsWithAtt1 = scriptletList.resolve(['attr3'])
        assertEquals(
            "Filtered scriptlet list was wrong",
            ['100_attr1_attr2_script1.sh', '101_attr3_script2.sh', '300_script3.sh', '1000_attr1_attr2_attr3_script4-1.groovy', '1001_script5.php'],
            scriptletsWithAtt1*.name
        )

        //2 hits for script4-2 - script4-1 wins
        List scriptletsWithAttr2 = scriptletList.resolve(['attr1', 'attr3'])
        assertEquals(
            "Filtered scriptlet list was wrong",
            ['100_attr1_attr2_script1.sh', '101_attr3_script2.sh', '300_script3.sh', '1000_attr1_attr2_attr3_script4-1.groovy', '1001_script5.php'],
            scriptletsWithAttr2*.name
        )
    }

    private File getTestScriptDir(String scriptDir) {
        URL scriptDirUrl = this.class.getClassLoader().getResource("${this.class.getPackage().getName()}/${scriptDir}")
        return new File(scriptDirUrl.getFile())
    }
}
