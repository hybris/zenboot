package org.zenboot.portal.processing.converter

class ParameterConverterMap implements Map {

    def parameterConverters

    private Map parameters = [:]
    private Map objects = [:]

    @Override
    int size() {
        return this.parameters.size()
    }

    @Override
    boolean isEmpty() {
        return this.parameters.isEmpty()
    }

    @Override
    boolean containsKey(Object key) {
        return this.parameters.containsKey(key)
    }

    @Override
    boolean containsValue(Object value) {
        return this.parameters.containsValue(value)
    }

    @Override
    Object get(Object key) {
        return this.parameters.get(key)
    }

    Object getObject(Object key) {
        return objects.get(key)
    }

    @Override
    Object put(Object key, Object value) {
        def result = this.parameters.put(key, value)
        this.objects.put(key, value)

        this.addConvertedParameters(key, value)

        return result
    }

    @Override
    Object remove(Object key) {
        def result = this.parameters.remove(key)
        this.objects.remove(key)

        this.removeConvertedParameters(key)

        return result
    }

    @Override
    void putAll(Map map) {
        map.each { key, value ->
            this.addConvertedParameters(key, value)
        }
        this.objects.putAll(map)
    }

    @Override
    void clear() {
        this.parameters.clear()
        this.objects.clear()
    }

    @Override
    Set keySet() {
        return this.parameters.keySet()
    }

    @Override
    Collection values() {
        return this.parameters.values()
    }

    @Override
    Set entrySet() {
        this.parameters.entrySet()
    }

    private void addConvertedParameters(def key, def value) {
        ParameterConverter converter = this.parameterConverters.find { ParameterConverter converter ->
            converter.supports(value.class)
        }
        if (!converter) {
            converter = new DefaultParameterConverter()
        }
        converter.addParameters(this.parameters, key, value)
    }

    private void removeConvertedParameters(def key) {
        ParameterConverter converter = this.parameterConverters.find { ParameterConverter converter ->
            converter.supports(key)
        }
        if (!converter) {
            converter = new DefaultParameterConverter()
        }
        converter.removeParameters(this.parameters, key)
    }

}