// navigation configuration
navigation.tabs = [
    [controller:'home', title:'Home', action:'index', path:['home', 'index']],
    [controller:'executionZone', title:'Processing', action:'list', path:['processing', 'index']],
    [controller:'host', title:'Data Management', action:'list', path:['data', 'index']],
    [controller:'administration', title:'Administration', action:'index', path:['administration', 'index']],
]
navigation.menu = [
    [controller:'host', title:'Hosts', action:'list', path:['data', 'hosts']],
    [controller:'host', title:'Hosts', action:'edit', path:['data', 'hosts'], isVisible:false],
    [controller:'host', title:'Hosts', action:'show', path:['data', 'hosts'], isVisible:false],

    [controller:'dnsEntry', title:'DNS', action:'list', path:['data', 'dns']],
    [controller:'dnsEntry', title:'DNS', action:'edit', path:['data', 'dns'], isVisible:false],
    [controller:'dnsEntry', title:'DNS', action:'show', path:['data', 'dns'], isVisible:false],

    [controller:'customer', title:'Customer', action:'list', path:['data', 'customer']],
    [controller:'customer', title:'Customer', action:'edit', path:['data', 'customer'], isVisible:false],
    [controller:'customer', title:'Customer', action:'show', path:['data', 'customer'], isVisible:false],

    [controller:'executionZone', title:'Execution Zone', action:'list', path:['processing', 'executionzones']],
    [controller:'executionZone', title:'Execution Zone', action:'show', path:['processing', 'executionzones'], isVisible:false],
    [controller:'executionZone', title:'Execution Zone', action:'create', path:['processing', 'executionzones'], isVisible:false],
    [controller:'executionZone', title:'Execution Zone', action:'edit', path:['processing', 'executionzones'], isVisible:false],
    [controller:'executionZone', title:'Execution Zone', action:'delete', path:['processing', 'executionzones'], isVisible:false],

    [controller:'executionZoneAction', title:'Execution Zone', action:'list', path:['processing', 'executionzones'], isVisible:false],
    [controller:'executionZoneAction', title:'Execution Zone', action:'show', path:['processing', 'executionzones'], isVisible:false],
    [controller:'executionZoneAction', title:'Execution Zone', action:'delete', path:['processing', 'executionzones'], isVisible:false],

    [controller:'executionZoneType', title:'Execution Zone Types', action:'list', path:['processing', 'executionzonetypes']],
    [controller:'executionZoneType', title:'Execution Zone Types', action:'show', path:['processing', 'executionzonetypes'], isVisible:false],
    [controller:'executionZoneType', title:'Execution Zone Types', action:'create', path:['processing', 'executionzonetypes'], isVisible:false],
    [controller:'executionZoneType', title:'Execution Zone Types', action:'edit', path:['processing', 'executionzonetypes'], isVisible:false],
    [controller:'executionZoneType', title:'Execution Zone Types', action:'delete', path:['processing', 'executionzonetypes'], isVisible:false],

    [controller:'exposedExecutionZoneAction', title:'Exposed Actions', action:'list', path:['processing', 'executionzoneactions']],
    [controller:'exposedExecutionZoneAction', title:'Exposed Actions', action:'create', path:['processing', 'executionzoneactions'], isVisible:false],
    [controller:'exposedExecutionZoneAction', title:'Exposed Actions', action:'show', path:['processing', 'executionzoneactions'], isVisible:false],
    [controller:'exposedExecutionZoneAction', title:'Exposed Actions', action:'edit', path:['processing', 'executionzoneactions'], isVisible:false],
    [controller:'exposedExecutionZoneAction', title:'Exposed Actions', action:'delete', path:['processing', 'executionzoneactions'], isVisible:false],
    [controller:'exposedExecutionZoneAction', title:'Exposed Actions', action:'save', path:['processing', 'executionzoneactions'], isVisible:false],
    
    [controller:'scriptletBatch', title:'Executed Actions', action:'list', path:['processing', 'scriptletbatches']],
    [controller:'scriptletBatch', title:'Executed Actions', action:'show', path:['processing', 'scriptletbatches'], isVisible:false],

    [controller:'administration', title:'User Management', action:'user', path:['administration', 'user']],
    [controller:'administration', title:'DB Console', action:'dbconsole', path:['administration', 'dbconsole']],
]