package org.zenboot.portal.processing

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware


class CronjobService implements ApplicationEventPublisherAware {

    static transactional = false

    def grailsApplication
    def applicationEventPublisher
    def springSecurityService
    def executionZoneService

    void executeJobs(CronjobExpression cronExpression) {
        List jobs = this.getJobList(cronExpression)
        jobs.each { job ->
            def properties = job.metaClass.properties*.name

            JobContext jobContext = new JobContext()

            if (properties.contains('before')) {
                job.before(jobContext)
            }
            jobContext.actions.each { ExecutionZoneAction action ->
                this.applicationEventPublisher.publishEvent(new ProcessingEvent(action, springSecurityService.currentUser))
                Thread.sleep(jobContext.jobExecutionDelay)
            }
            if (properties.contains('after')) {
                job.after(jobContext)
            }
        }
    }


	private List getJobList(CronjobExpression cronExpression) {
        List jobs = []

		def exposedActions = ExposedExecutionZoneAction.findAllByCronExpression(cronExpression.toString())

		exposedActions.each { ExposedExecutionZoneAction exposedAction ->
            PluginResolver jobResolver = new PluginResolver(executionZoneService.getJobDir(exposedAction.executionZone.type))
			File cronJob = jobResolver.resolveScriptletBatchPlugin(exposedAction.scriptDir)
			if (!cronJob) {
				log.warn("No cronjob found for exposed action ${exposedAction.scriptDir}")
                log.info("Let's do a nasty shortcut and execute immediately")
                ExecutionZoneAction action = executionZoneService.createExecutionZoneAction(exposedAction)
                this.applicationEventPublisher.publishEvent(new ProcessingEvent(action, springSecurityService.currentUser))
			} else {
			    jobs << this.getJob(cronJob, exposedAction)
			}
		}

		return jobs
	}

    private def getJob(File jobClass, def exposedAction) {
        if (jobClass == null) {
            return null;
        }
        GroovyClassLoader gcl = new GroovyClassLoader(this.class.classLoader)
        Class clazz = gcl.parseClass(jobClass)

        def job = clazz.newInstance()
        def properties = job.metaClass.properties*.name
        job.metaClass.log = this.log
        if (properties.contains('grailsApplication')) {
            job.grailsApplication = grailsApplication
        }
        if (properties.contains('exposedAction')) {
            job.exposedAction = exposedAction
        }
        return job
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.applicationEventPublisher = eventPublisher
    }

}
