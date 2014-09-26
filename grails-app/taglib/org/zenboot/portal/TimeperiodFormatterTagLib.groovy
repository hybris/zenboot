package org.zenboot.portal

import org.joda.time.format.PeriodFormatterBuilder

class TimeperiodFormatterTagLib {

    static namespace = "tpf"

    def formatPeriod = { attrs ->
      def periodFormatter = new PeriodFormatterBuilder()
       .appendHours()
       .appendSuffix(" hr")
       .appendSeparator(" ")
       .printZeroRarelyLast()
       .appendMinutes()
       .appendSuffix(" min")
       .appendSeparator(" ")
       .appendSeconds()
       .appendSuffix(" sec")
       .toFormatter();
      out << periodFormatter.print(attrs.value)
    }
}
