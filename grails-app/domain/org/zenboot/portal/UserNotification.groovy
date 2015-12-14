package org.zenboot.portal

class UserNotification {

  static auditable = true

  Date creationDate
  boolean enabled
  String message

  def beforeInsert = {
      this.creationDate = new Date()
  }

  static constraints = {
  }
}
