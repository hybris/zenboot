Hi admin of duty,

following virtual machines reached their time-to-life and should be destroyed:

<g:each in="${hosts}" status="i" var="host">
${i+1}) ${host.instanceId} [${host}] (click to delete in Zenboot: ${grailsApplication.config.zenboot.serverUrl}<g:createLink controller="host" action="show" params="[id:host.id, delete:true]" />)
</g:each>

Thanks for your help!

Your inferior,
Zenboot