//**********************************************************************************************
//These are default values. The configuration defined in zenboot.properties will have precedence!
//**********************************************************************************************

dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""

    properties {
      //run the evictor every 30 minutes and evict any connections older than 30 minutes.
      minEvictableIdleTimeMillis=1800000
      timeBetweenEvictionRunsMillis=1800000
      numTestsPerEvictionRun=3
      //test the connection while its idle, before borrow and return it
      testOnBorrow=true
      testWhileIdle=true
      testOnReturn=true
      validationQuery="SELECT 1"
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
            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:devDb;MVCC=TRUE"
            logSql = false
            formatSql = false
        }
    }
    test {
        dataSource {
            dbCreate = "create"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE"
        }
    }
    production {
        dataSource {
            dbCreate = "create" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:devDb;MVCC=TRUE"
        }
    }
}
