grails {
   plugin {
       springsecurity {
           ldap {
               active = true
               context.managerDn = 'cn=admin,dc=example,dc=com'
               context.managerPassword = 'V3rys3cr37'
               context.server = 'ldap://localhost:389'
               search.base = 'ou=users,dc=example,dc=com'
               authorities.groupSearchBase = 'ou=group,dc=example,dc=com'

               authorities.groupSearchFilter = '(member={0})'
               authenticator.useBind = true
               auth.hideUserNotFoundExceptions = false

               search.filter = '(&(objectCategory=cn=person,dc=example,dc=com)(cn={0}))'

               search.searchSubtree = true
               authorities.retrieveDatabaseRoles = true

               useRememberMe = false
               rememberMe.usernameMapper.userDnBase = 'cn=person,dc=example,dc=com'

               rememberMe.usernameMapper.usernameAttribute = 'cn'
               rememberMe.detailsManager.groupMemberAttributeName = 'member'
               rememberMe.detailsManager.groupSearchBase = 'ou=group,dc=example,dc=com'

               authorities.retrieveGroupRoles = false
           }
       }
   }
}

