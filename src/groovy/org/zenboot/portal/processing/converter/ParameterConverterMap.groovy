package org.zenboot.portal.processing.converter

class ParameterConverterMap implements Map {

    def parameterConverters

    private Map parameters = [:]
    private Map objects = [:]

    @Override
    public int size() {
        return this.parameters.size()
    }

    @Override
    public boolean isEmpty() {
        return this.parameters.isEmpty()
    }

    @Override
    public boolean containsKey(Object key) {
        return this.parameters.containsKey(key)
    }

    @Override
    public boolean containsValue(Object value) {
        return this.parameters.containsValue(value)
    }

    @Override
    public Object get(Object key) {
        return this.parameters.get(key)
    }

    public Object getObject(Object key) {
        return objects.get(key)
    }

    @Override
    public Object put(Object key, Object value) {
        def result = this.parameters.put(key, value)
        this.objects.put(key, value)

        this.addConvertedParameters(key, value)

        return result
    }

    @Override
    public Object remove(Object key) {
        def result = this.parameters.remove(key)
        this.objects.remove(key)

        this.removeConvertedParameters(key)

        return result
    }

    @Override
    public void putAll(Map map) {
        map.each { key, value ->
            this.addConvertedParameters(key, value)
        }
        this.objects.putAll(map)
    }

    @Override
    public void clear() {
        this.parameters.clear()
        this.objects.clear()
    }

    @Override
    public Set keySet() {
        return this.parameters.keySet()
    }

    @Override
    public Collection values() {
        return this.parameters.values()
    }

    @Override
    public Set entrySet() {
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