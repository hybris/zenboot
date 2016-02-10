import org.zenboot.portal.UserNotification

class ZenbootFilters {
   def filters = {
     notificationFilter(controller:'*', action:'*') {
       after = { Map model ->
         if (model) {
             def notifications = UserNotification.findAllByEnabledAndMessageIsNotNull(true)
             model["notifications"] = notifications.sort { it.type }
         }
        }
      }
    }
}
