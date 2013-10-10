zenboot = {}

zenboot.refreshInterval = null

zenboot.startProcessQueue = function(url, refreshRate) {
	zenboot.refreshInterval = setInterval(function() {
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
		+ '<span title="Add parameter" onclick="zenboot.addParameter.call(this)" class="btn btn-mini"><i class="icon-plus-sign"></i></span>'
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
		+ '<span title="Add parameter" onclick="zenboot.addProcessingParameter.call(this)" class="btn btn-mini"><i class="icon-plus-sign"></i></span>'
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

$(document).ready(function() {
	zenboot.enableCollapsableList()
});

$(document).unload(function() {
	zenboot.stopProcessQueue()
});