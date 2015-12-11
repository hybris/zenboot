import org.zenboot.portal.UserNotification

class ZenbootFilters {
   def filters = {
     notificationFilter(controller:'*', action:'*') {
       after = { Map model ->
         if (model) {
           String message = UserNotification.findByEnabled(true)?.message
           if (message) {
             model["notification"]=message
           }
         }
        }
      }
    }
}
