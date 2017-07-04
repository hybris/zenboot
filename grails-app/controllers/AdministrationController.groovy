
class AdministrationController {

    def accessService

    def index = {
        redirect(action:"user")
    }

    def user = {}

    def dbconsole = {}

    def accessCache = {}

    def clear = {
      accessService.clearAccessCache()
      redirect(action:"accessCacheCleared")
    }

    def accessCacheCleared = {}
}
