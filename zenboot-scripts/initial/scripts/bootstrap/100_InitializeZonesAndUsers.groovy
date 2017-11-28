import org.zenboot.portal.HostState
import org.zenboot.portal.processing.meta.annotation.*

import org.apache.commons.logging.LogFactory
import org.zenboot.portal.processing.ScriptExecutionException
import org.zenboot.portal.processing.ProcessContext

import org.zenboot.portal.Host

@Scriptlet(author="Gordian Edenhofer", description="Placeholder for the actual initialization which in practise depends on the field of use")
public class InitializeZonesAndUsers {
  private static final log = LogFactory.getLog("org.zenboot.portal.InitializeZonesAndUsers")

  def execute(ProcessContext ctx) {
    log.info("This function runs when bootstrap is executed.")
  }
}
