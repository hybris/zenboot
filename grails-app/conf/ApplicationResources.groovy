modules = {
    application {
        dependsOn 'jquery'
        dependsOn 'bootstrap'
        resource url:'css/zenboot.css'
        resource url:'js/jquery.winFocus.js'
        resource url:'js/zenboot.js'
    }
    epiceditor {
        resource url:'js/epiceditor/js/epiceditor.min.js'
    }
}