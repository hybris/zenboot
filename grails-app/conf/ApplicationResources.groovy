modules = {
    application {
        dependsOn 'jquery'
        dependsOn 'bootstrap'
        resource url:'css/zenboot.css'
        resource url:'js/zenboot.js'
    }
    zclip {
        resource url:'js/zclip/jquery.zclip.min.js'
    }
    epiceditor {
        resource url:'js/epiceditor/js/epiceditor.min.js'
    }
}