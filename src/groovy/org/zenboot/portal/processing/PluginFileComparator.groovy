package org.zenboot.portal.processing


class PluginFileComparator implements Comparator {

    private List attributes = []

    PluginFileComparator(List attributes) {
        this.attributes = attributes
    }

    @Override
    int compare(def pluginFile1, def pluginFile2) {
        return pluginFile1.getRank(this.attributes).compareTo(pluginFile2.getRank(this.attributes)) 
    }

}