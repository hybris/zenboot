//**********************************************************************************************
//These are default values. The configuration defined in zenboot.properties will have precedence!
//**********************************************************************************************

dataSource {
    pooled: true
    jmxExport: true
    driverClassName: "com.mysql.jdbc.Driver"
    dialect: "org.hibernate.dialect.MySQL5InnoDBDialect"
    username: root
    password: root

    properties {
      //run the evictor every 30 minutes and evict any connections older than 30 minutes.
      minEvictableIdleTimeMillis=1800000
      timeBetweenEvictionRunsMillis=1800000
      numTestsPerEvictionRun=3
      //test the connection while its idle, before borrow and return it
      testOnBorrow=true
      testWhileIdle=true
      testOnReturn=true
      validationQuery="SELECT username FROM person WHERE id=1" // probably need a real query here should be "admin"
    }
}

hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}

environments {
    development {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://localhost:3306/zenboot"
            username: root
            password: root
        }
    }
    test {
        dataSource {
            dbCreate = "create"
            url = "jdbc:mysql://localhost:3306/zenboot"
            username: root
            password: root
        }
    }
    production {
        dataSource {
            dbCreate = "create" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://localhost:3306/zenboot"
            username: root
            password: root
        }
    }
}
