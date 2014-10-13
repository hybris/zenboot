#!/usr/bin/perl

#@Scriptlet(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Generate a DNS entry (FAKE)")
#@Parameters([
#  @Parameter(name="FQDN",     type=ParameterType.EMIT,    description="Creates a full qualified domain name"),
#  @Parameter(name="HOSTNAME", type=ParameterType.CONSUME, description="Name of the host"),
#  @Parameter(name="DOMAIN",   type=ParameterType.CONSUME, description="Domain name to be used", defaultValue="my-domain.org")
#])



print "# I'm creating a fake DNS entry for IP address $ENV{'IP'}\n";

print "FQDN=$ENV{'HOSTNAME'}.$ENV{'DOMAIN'}\n";

sleep(1);
