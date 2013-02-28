package org.zenboot.portal.processing.meta


class MetadataParameterComparator implements Comparator<ParameterMetadata> {
    @Override
    public int compare(ParameterMetadata o1, ParameterMetadata o2) {
        return o1.getName().compareTo(o2.getName())
    }
}