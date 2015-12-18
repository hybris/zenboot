import org.zenboot.portal.UserNotification

class ZenbootFilters {
   def filters = {
     notificationFilter(controller:'*', action:'*') {
       after = { Map model ->
         if (model) {
             def query = UserNotification.where {
                 enabled == true
                 message != null
             }
             model["notifications"] = query.list().sort { it.type }
         }
        }
      }
    }
}
