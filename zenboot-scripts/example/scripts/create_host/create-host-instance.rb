#!/usr/bin/ruby

#@Scriptlet(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Create a host instance (FAKE!)")
#@Parameters([
#  @Parameter(name="IP",       type=ParameterType.EMIT,    description="A random IP address"),
#  @Parameter(name="MAC",      type=ParameterType.EMIT,    description="A random MAC address"),
#  @Parameter(name="HOSTNAME", type=ParameterType.CONSUME, description="The name of the host which will be set"),
#])

sleep(1)

puts "#These random values will be available as env-parameters in all following scripts"
puts "IP=#{Array.new(4){rand(256)}.join('.')}"
puts "MAC=#{(1..6).map{"%0.2X"%rand(256)}.join(":")}"
