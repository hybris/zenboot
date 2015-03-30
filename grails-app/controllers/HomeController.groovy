import org.zenboot.portal.Host
import org.zenboot.portal.HostState

import org.zenboot.portal.processing.ExecutionZone
import org.zenboot.portal.processing.ScriptletBatch
import org.zenboot.portal.processing.Processable.ProcessState


class HomeController {

    def index() {

      int allHostsCount = Host.count()
      int completedHostsCount = Host.countByState(HostState.COMPLETED)
      int stillRunningRate = completedHostsCount / allHostsCount * 100

      def allActiveExecutionZone = ExecutionZone.findAllByEnabled(true)
      int allActiveExecutionZoneCount = allActiveExecutionZone.size()
      int maxHostsExecutionZoneHostCount = allActiveExecutionZone.collect {it.getNonDeletedHosts().size()}.max()

      int allExecZoneActionCount = ScriptletBatch.count()
      int successfulExecZoneActionCount = ScriptletBatch.countByState(ProcessState.SUCCESS)
      int successRate = successfulExecZoneActionCount / allExecZoneActionCount * 100

      [allHostsCount: allHostsCount,
       completedHostsCount: completedHostsCount,
       stillRunningRate: stillRunningRate,

       allActiveExecutionZoneCount: allActiveExecutionZoneCount,
       maxHostsExecutionZoneHostCount: maxHostsExecutionZoneHostCount,

       allExecZoneActionCount: allExecZoneActionCount,
       successfulExecZoneActionCount: successfulExecZoneActionCount,
       successRate: successRate
       ]
    }
}
