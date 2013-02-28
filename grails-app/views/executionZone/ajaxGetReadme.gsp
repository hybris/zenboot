<div id="container_${editorId}" class="readme"></div>

<a class="btn btn-mini" style="margin-top:5px;" id="button-update-${editorId}">Update Readme</a>
<span class="label label-important hide" id="label-failure-${editorId}"><i class="icon-thumbs-down icon-white"></i></span>
<span class="label label-success hide" id="label-success-${editorId}"><i class="icon-thumbs-up icon-white"></i></span>

<g:hiddenField name="checksum-${editorId}" id="checksum-${editorId}" value="${checksum}"/>

<script type="text/javascript">
var opts = {
  container: 'container_${editorId}',
  basePath: '<g:resource dir="/js/epiceditor" />',
  clientSideStorage: false,
  parser: marked,
  file: {
    autoSave: false
  },
  theme: {
    base:'/themes/base/epiceditor.css',
    preview:'/themes/preview/github.css',
    editor:'/themes/editor/epic-light.css'
  },
  focusOnLoad: false,
  shortcut: {
    modifier: 18,
    fullscreen: 70,
    preview: 80,
    edit: 79
  }
}

${editorId} = new EpicEditor(opts);
${editorId}.load();
${editorId}.importFile('${scriptDir}', '${markdown.replace("\n", "\\n")}');
${editorId}.preview();
${editorId}.on('save', function() {
    $.ajax({
        url : '<g:createLink action="ajaxUpdateReadme" controller="executionZone" />?checksum=' + $('#checksum-${editorId}').val() + '&scriptDir=${scriptDir}',
        data : 'markdown='+encodeURIComponent(${editorId}.exportFile()),
        contentType: 'application/json',
        dataType: 'json',
        success: function(data, text) {
        	$('#checksum-${editorId}').val(data.value)
            $('#label-success-${editorId}').fadeIn();
            setTimeout(function() {$('#label-success-${editorId}').fadeOut()}, 1500);
        },
        error: function(xhr, textStatus, error) {
            $('#label-failure-${editorId}').fadeIn(function() {
                var body = eval("("+xhr.responseText+")");
                alert(body.message);
            });
            setTimeout(function() {$('#label-failure-${editorId}').fadeOut()}, 1500);
        }
    });
});

$('#button-update-${editorId}').click(function() {
	${editorId}.save()
});
</script>