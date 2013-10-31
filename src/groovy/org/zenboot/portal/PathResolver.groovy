package org.zenboot.portal

import grails.util.Environment

import org.codehaus.groovy.grails.web.context.ServletContextHolder

class PathResolver {

    static String getAbosolutePath(String path) {
        if (path.startsWith('/')) {
            return path
        }
        if (Environment.current == Environment.PRODUCTION) {
            return "${new File(ServletContextHolder.servletContext.getRealPath('/'))}${System.getProperty('file.separator')}${path}"
        } else {
            return "${new File(ServletContextHolder.servletContext.getRealPath('/')).getParent()}${System.getProperty('file.separator')}${path}"
        }
    }
}
