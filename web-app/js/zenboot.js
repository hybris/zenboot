zenboot = {}


zenboot.refreshInterval = null

zenboot.startProcessQueue = function(url, refreshRate) {
	zenboot.refreshInterval = setInterval(function() {
	    if (!zenboot.hasFocus) {
	        return;
	    }

		$.ajax({
			url : url
		}).done(function(data) {
			$("#processqueue").html(data);
		}).error(zenboot.stopProcessQueue);
	}, refreshRate);
}

zenboot.stopProcessQueue = function() {
	if (zenboot.refreshInterval != null) {
		clearInterval(zenboot.refreshInterval)
	}
}

zenboot.enableCollapsableList = function() {
	$('.collapsable-list > a').click(
		function() {
			$(this).find('ul').hide();
			$(this).toggleClass('expanded').toggleClass('collapsed').next('ul').toggle('normal');
			$(this).find('i').toggleClass('icon-resize-full').toggleClass('icon-resize-small')
		}
	);
}

zenboot.addParameter = function(key, value, description) {
	if (key === undefined) {
		key = ''
	}
	if (value === undefined) {
		value = ''
	}
	if (description === undefined) {
		description = ''
	}
	$('.exec-parameters-table tbody').append(
		'<tr>'
		+ '<td>'
		+ '<input type="text" name="parameters.key" value="'+key+'" />'
		+ '</td>'
		+ '<td>'
		+ '<input type="text" name="parameters.value" value="'+value+'" />'
		+ '</td>'
		+ '<td>'
		+ '<span title="Remove parameter" onclick="zenboot.removeParameter.call(this)" class="btn btn-mini"><i class="icon-minus-sign"></i></span>&nbsp;'
		+ '</td>'
		+ '</tr>'
	);
}

zenboot.addProcessingParameter = function(key, value, description) {
	if (key === undefined) {
		key = ''
	}
	if (value === undefined) {
		value = ''
	}
	if (description === undefined) {
		description = ''
	}
	$('.parameters-table tbody').append(
		'<tr>'
		+ '<td>'
		+ '<input type="text" name="parameters.key" value="'+key+'" />'
		+ '</td>'
		+ '<td>'
		+ '<input type="text" name="parameters.value" value="'+value+'" />'
		+ '</td>'
		+ '<td>'
		+ '<input type="text" name="parameters.description" value="'+description+'" />'
		+ '</td>'
		+ '<td>'
		+ '<input type="hidden" name="parameters.exposed" value="false" /><input type="checkbox" name="exported" onclick="zenboot.toggleParameterCheckbox.apply(this, [\'exposed\'])" />'
		+ '</td>'
		+ '<td>'
		+ '<input type="hidden" name="parameters.published" value="false" /><input type="checkbox" name="published" onclick="zenboot.toggleParameterCheckbox.apply(this, [\'published\'])" />'
		+ '</td>'
		+ '<td>'
		+ '<span title="Remove parameter" onclick="zenboot.removeParameter.call(this)" class="btn btn-mini"><i class="icon-minus-sign"></i></span>&nbsp;'
		+ '</td>'
		+ '</tr>'
	);
}

zenboot.removeParameter = function() {
	$(this).parents('tr').remove();
}

zenboot.resetParameter = function() {
	$('.parameters-table tbody tr').remove()
}

zenboot.enableParameterButtons = function (callback) {
	$('.add-parameter-button').click(function() {
		zenboot.addParameter.call(this)
		if (typeof(callback) == "function") {
			callback.call(this)
		}
	});
	$('.remove-parameter-button').click(function() {
		zenboot.removeParameter.call(this)
		if (typeof(callback) == "function") {
			callback.call(this)
		}
	});
}

zenboot.enableProcessingParameterButtons = function (callback) {
	$('.add-parameter-button').click(function() {
		zenboot.addProcessingParameter.call(this)
		if (typeof(callback) == "function") {
			callback.call(this)
		}
	});
	$('.remove-parameter-button').click(function() {
		zenboot.removeParameter.call(this)
		if (typeof(callback) == "function") {
			callback.call(this)
		}
	});
	$('input[name=exposed]').click(function() {
		zenboot.toggleParameterCheckbox.apply(this, ["exposed"]);
	});
	$('input[name=published]').click(function() {
		zenboot.toggleParameterCheckbox.apply(this, ["published"]);
	});
}

zenboot.toggleParameterCheckbox = function(type) {
	var node = $(this).prev('input[name=parameters.' + type + ']');
	(node.val() == "true") ? node.val("false") : node.val("true");
}

zenboot.enableTooltip = function() {
	$('.tooltip, .zb-tooltip').tooltip({
		delay : 800
	})
}

zenboot.disableCopyButton = function() {
	$(".copy-button").zclip('remove');
}

zenboot.enableParameterList = function() {
    zenboot.enableTooltip()

    $('.details-parameter-button').click(function() {
        $(this).parents('tr').next().find('.scriptlet-metadata').fadeToggle('fast')
    });

    $('.add-exec-parameter-button').click(function() {
        zenboot.addParameter()
    });

    $('.remove-parameter-button').click(function() {
        $(this).parents('tr').next().remove();
        $(this).parents('tr').remove();
    });

    $('.accept-parameter-button').click(function() {
        var input = $(this).parents('tr').prev().find("input[name=parameters\\.value]");
        input.val($(this).attr('rel'));
        if ($(this).parents('span').hasClass('scriptlet')) {
            input.parent().removeClass('info').addClass('success')
        } else {
            input.parent().removeClass('success').addClass('info')
        }
    });

    //remove all marker classes after a input field value has changed (no overlay, no defaultValue)
    $("input[name=parameters\\.value]").change(function() {
        $(this).parent().removeClass('info').removeClass('success')
    })
}

zenboot.prepareAjaxLoading = function(targetNodeId, spinnerNodeId) {
	if ($('#' + targetNodeId).is(':visible')) {
		$('#' + targetNodeId).fadeOut(function() {
			$(this).children().remove();
		});
		return false;
	}
	$('#' + spinnerNodeId).show();
	$('#' + targetNodeId).fadeIn('slow');
	return true;
}

zenboot.finalizeAjaxLoading = function(targetNodeId, spinnerNodeId) {
	$('#' + spinnerNodeId).hide();
}

zenboot.loadTemplateFrom = function(url) {
    $.ajax({
        url : url,
        dataType: "json",
        beforeSend : function() {
        	$('#templateParametersSpinner').fadeIn('fast');
        	$("#templateForm :input").attr("disabled", "disabled");
        },
        success: function(data) {
        	$('#templateParametersSpinner').hide();
        	$('input#name').val(data.template.name);
        	$('#templateForm').attr("action", data.template.updateUrl);
        	$("#templateForm :submit").attr("name", "_action_update")
        	$("#template_versions").html("");
      		$("#templateForm a#showFileButton").removeAttr('disabled');
     			$("#templateForm a#showFileButton").off('click');
      		$("#templateForm a#showFileButton").click( function(e) {
        		zenboot.loadTextAreaContent(data.template.showFileUrl, $('#show-file-field'));
        		$("#show-file-name").html(data.template.name);
      		  $('#show-file').modal("show");
      		});
        	
        	$.each(data.template.versions.reverse(), function(index, version){
        		$("#template_versions").append($("<option>").val(version.url).html(version.create + " (" + version.user + ")"));
        	});
        	
        	$('.delete_template').removeAttr('disabled');
        	$('#template-remove form').attr("action", data.template.deleteTemplateUrl);
        	
        	zenboot.loadTemplate(data.template.url);
        	$('#templateForm :input').removeAttr('disabled');
        	$("#templateForm a#cancelbtn").removeAttr('disabled');
        },
        error: function(jqHXR, status, error) {
	        $('#templateParametersSpinner').hide();
	        $('#templateForm :input').removeAttr('disabled');
        }
    });	
}

zenboot.loadTemplate = function(url) {
    $.ajax({
        url : url,
        dataType: "json",
        beforeSend : function() {
        	$('#templateParametersSpinner').fadeIn('fast');
        	$("#templateForm :input").attr("disabled", "disabled");
        },
        success: function(data) {
        	$('#templateParametersSpinner').hide();
        	zenboot.loadTextAreaContent(data.version.url, $('textarea#template'));
        	zenboot.loadPlaceholderContent(data.version.commentUrl, $('textarea#message'));
        	$('#templateForm :input').removeAttr('disabled');
        },
        error: function(jqHXR, status, error) {
	        $('#templateParametersSpinner').hide();
	        $('#templateForm :input').removeAttr('disabled');
        }
    });
}

zenboot.loadTextAreaContent = function(url, textArea) {
    $.ajax({
        url : url,
        beforeSend : function() {
        	$('#templateParametersSpinner').fadeIn('fast');
        	$("#templateForm :input").attr("disabled", "disabled");
        },
        success: function(data) {
        	$('#templateParametersSpinner').hide();
        	textArea.val(data);
        	$('#templateForm :input').removeAttr('disabled');
        },
        error: function(jqHXR, status, error) {
	        $('#templateParametersSpinner').hide();
	        $('#templateForm :input').removeAttr('disabled');
        }
    });
}

zenboot.loadPlaceholderContent = function(url, textArea) {
    $.ajax({
        url : url,
        beforeSend : function() {
        	$('#templateParametersSpinner').fadeIn('fast');
        	$("#templateForm :input").attr("disabled", "disabled");
        },
        success: function(data) {
        	$('#templateParametersSpinner').hide();
        	textArea.val("");
        	textArea.attr('placeholder',data);
        	$('#templateForm :input').removeAttr('disabled');
        },
        error: function(jqHXR, status, error) {
	        $('#templateParametersSpinner').hide();
	        $('#templateForm :input').removeAttr('disabled');
        }
    });
}

zenboot.templateCancel = function(link) {
  $('#templateParametersSpinner').hide();      	
  $('#templateForm').attr("action", link);
	$("#templateForm input#name").val("");
	$('.delete_template').attr("disabled", "disabled");
	$("#template_versions").attr("disabled", "disabled");
	$("#templateForm textarea#template").val("");
	$("#templateForm textarea#message").val("");
  placeholder = $("#templateForm textarea#message").attr("data-placeholder");	
  $("#templateForm select#template_versions").html("");
  $("#templateForm textarea#message").attr("placeholder", placeholder);	
	$("#templateForm :submit").attr("name", "save");
	$("#templateForm a#cancelbtn").attr("disabled", "disabled");
	$("#templateForm a#showFileButton").attr("disabled", "disabled");
	$("#templateForm a#showFileButton").off('click');
	$("#executionZone_templates option:selected").removeAttr("selected");
}

zenboot.templateSave = function(url){
    $.ajax({
        url : $('#templateForm').attr("action"),
        data : $('#templateForm').serialize(),
        dataType: "json",
        type: "POST",
        beforeSend : function() {
        	$('#templateParametersSpinner').fadeIn('fast');
        	$("#templateForm :input").attr("disabled", "disabled");
        },
        success: function(data) {
        	$('#templateParametersSpinner').hide();
        	$('input#name').val(data.template.name);
        	$('#templateForm').attr("action", data.template.updateUrl);
        	$("#templateForm :submit").attr("name", "_action_update")
        	$("#template_versions").html("");
        	
        	$.each(data.template.versions.reverse(), function(index, version){
        		$("#template_versions").append($("<option>").val(version.url).html(version.create + " (" + version.user + ")"));
        	});
        	
        	$('.delete_template').removeAttr('disabled');
        	$('#template-remove form').attr("action", data.template.deleteTemplateUrl);
        	
        	zenboot.loadTemplate(data.template.url);
        	
        	$('#templateForm :input').removeAttr('disabled');
        	$("#templateForm a#cancelbtn").removeAttr('disabled');

            $("#template_messages").html("<div class='alert alert-info'>" + data.template.message + "</div>")
            if ( "warning" in data.template  && data.template.warning != null && data.template.warning.length > 0){
                $("#template_messages").append("<div class='alert alert-error'>" + data.template.warning + "</div>")
            }

        	zenboot.loadTemplateList(url);
        },
        error: function(jqHXR, status, error) {
	        $('#templateParametersSpinner').hide();
	        $('#templateForm :input').removeAttr('disabled');
        	$("#template_messages").html("<div class='alert alert-error'>" + jqHXR.responseText + "</div>")
        }
    });	
}

zenboot.templateRemove = function(url){
    $.ajax({
        url : $('#templateRemoveForm').attr("action"),
        data : $('#templateRemoveForm').serialize(),
        type: "POST",
        beforeSend : function() {
        	$('#templateParametersSpinner').fadeIn('fast');
        	$("#templateForm :input").attr("disabled", "disabled");
        },
        success: function(data) {
        	$('#templateParametersSpinner').hide();

        	$('#templateForm :input').removeAttr('disabled');
        	$("#template_messages").html("<div class='alert alert-info'>" + data + "</div>")
        	zenboot.loadTemplateList(url);
        },
        error: function(jqHXR, status, error) {

	        $('#templateParametersSpinner').hide();
	        $('#templateForm :input').removeAttr('disabled');
        	$("#template_messages").html("<div class='alert alert-error'>" + jqHXR.responseText + "</div>")
        }
    });	
}

zenboot.loadTemplateList = function(url){
    $.ajax({
      url : url,
      dataType: "json",
      beforeSend : function() {
      	$('#templateParametersSpinner').fadeIn('fast');
      	$("#executionZone_templates").attr("disabled", "disabled");
      },
      success: function(data) {
      	$('#templateParametersSpinner').hide();
      	$("#executionZone_templates").html("");
      	$.each(data, function(index, template){
      		$("#executionZone_templates").append($("<option>").val(template.id).html(template.name));
      	});
      	
      	$("#executionZone_templates").removeAttr('disabled');

      },
      error: function(jqHXR, status, error) {
        alert("ERROR");
        $('#templateParametersSpinner').hide();
        $('#templateForm :input').removeAttr('disabled');
      }
    });	
}

zenboot.templateCheck = function(url){
    $.ajax({
      url : url,
      dataType: "json",
      beforeSend : function() {
      	$('#templateParametersSpinner').fadeIn('fast');
      },
      success: function(data) {
      	$('#templateParametersSpinner').hide();
      	$("#check-templates-field").html("");
      	$.each(data, function(index, param){
      		$("#check-templates-field").append(param.name + "\n");
      	});
      	$('#check-templates').modal('show');
      	

      },
      error: function(jqHXR, status, error) {
        alert("ERROR");
        $('#templateParametersSpinner').hide();
      }
    });	
}

$(document).ready(function() {
	zenboot.enableCollapsableList()
    zenboot.hasFocus = true;

	$.winFocus({
        blur: function(event) {
            zenboot.hasFocus = false;
        },
        focus: function(event) {
            zenboot.hasFocus = true;
        }
    });
});

$(document).unload(function() {
	zenboot.stopProcessQueue()
});
