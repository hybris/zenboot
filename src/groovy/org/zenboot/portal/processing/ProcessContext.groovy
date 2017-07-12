package org.zenboot.portal.processing

import org.zenboot.portal.Host
import org.zenboot.portal.processing.converter.ParameterConverterMap
import org.zenboot.portal.security.Person

class ProcessContext {

    ParameterConverterMap parameters
    Host host
    Person user
    ExecutionZone execZone
    ScriptletBatch scriptletBatch

    @Override
    String toString() {
      return "${this.class.getSimpleName()} (host=${this.host}/params=\n${this.getParamsAsString()}/users=${this.user==null ? "null" : this.user.username}/ExecZone=${this.execZone})"
    }

    String getParamsAsString() {
      String paramsAsString=""
      for ( key in parameters?.keySet() ) {
           paramsAsString += key +"="+parameters.get(key) +"\n"
      }
      return paramsAsString
    }

}
