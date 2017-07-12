package org.zenboot.portal.processing


class ScriptFileComparator implements Comparator {

    private List attributes = []

    ScriptFileComparator(List attributes) {
        this.attributes = attributes
    }

    @Override
    int compare(def scriptFile1, def scriptFile2) {
        int cmpOrder = scriptFile1.order.compareTo(scriptFile2.order)
        if (cmpOrder == 0) {
            return scriptFile1.getRank(this.attributes).compareTo(scriptFile2.getRank(this.attributes))
        } else {
            return cmpOrder
        }   
    }

}