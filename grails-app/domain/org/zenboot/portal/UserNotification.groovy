package org.zenboot.portal

class UserNotification {

  static auditable = true

  Date creationDate
  boolean enabled
  String message

  static constraints = {
  }
}
