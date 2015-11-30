;(function($) {
	if (!$.winFocus) {
		$.extend({
			winFocus: function() {
				var init = true, methods = [];
				if (!$(document).data('winFocus')) $(document).data('winFocus', $.winFocus.init());
				for (x in arguments) {
					if (typeof arguments[x] == "object") {
						if (arguments[x]["blur"]) $.winFocus.methods.blur.push(arguments[x].blur);
						if (arguments[x]["focus"]) $.winFocus.methods.focus.push(arguments[x].focus);
						if (arguments[x]["blurFocus"]) $.winFocus.methods.blurFocus.push(arguments[x].blurFocus);
						if (arguments[x]["initRun"]) init = arguments[x].initRun;
					}
					else if (typeof arguments[x] == "function") methods.push(arguments[x]);
					else if (typeof arguments[x] == "boolean") {
						init = arguments[x];
					}
				}
				if (methods) {
					if (methods.length == 1) $.winFocus.methods.blurFocus.push(methods[0]);
					else {
						$.winFocus.methods.blur.push(methods[0]);
						$.winFocus.methods.focus.push(methods[1]);
					}
				}
				
				if (init) $.winFocus.methods.onChange();
			}
		});
		$.winFocus.init = function() {
			//	var document.hidden || document.msHidden || document.webkitHidden || document.mozHidden;
			
			// Standards:
			if ($.winFocus.props.hidden in document)	//	IE10 | FF20+
				document.addEventListener("visibilitychange", $.winFocus.methods.onChange);
			else if (($.winFocus.props.hidden = "mozHidden") in document)	//	Older FF Versions (?)
				document.addEventListener("mozvisibilitychange", $.winFocus.methods.onChange);
			else if (($.winFocus.props.hidden = "webkitHidden") in document)	//	Chrome
				document.addEventListener("webkitvisibilitychange", $.winFocus.methods.onChange);
			else if (($.winFocus.props.hidden = "msHidden") in document)	//	IE 4-6
				document.addEventListener("msvisibilitychange", $.winFocus.methods.onChange);
			else if (($.winFocus.props.hidden = "onfocusin") in document)	//	IE7-9
				document.onfocusin = document.onfocusout = $.winFocus.methods.onChange;
			else	//	All others:
				window.onpageshow = window.onpagehide = window.onfocus = window.onblur = $.winFocus.methods.onChange;
			
			return $.winFocus;
		}
		$.winFocus.methods = {
			blurFocus: [], blur: [], focus: [],
			exeCB: function(e) {
				if ($.winFocus.methods.blurFocus) $.each($.winFocus.methods.blurFocus, function(k, v) { if (typeof this == 'function') this.apply($.winFocus, [e, !e.hidden]) });
				if (e.hidden && $.winFocus.methods.blur) $.each($.winFocus.methods.blur, function(k, v) { if (typeof this == 'function') this.apply($.winFocus, [e]) });
				if (!e.hidden && $.winFocus.methods.focus) $.each($.winFocus.methods.focus, function(k, v) { if (typeof this == 'function') this.apply($.winFocus, [e]) });
			},
			onChange: function(e) {
				var eMap = { focus: false, focusin: false, pageshow: false, blur: true, focusout: true, pagehide: true };
				e = e || window.event;
				
				if (e) {
					e.hidden = e.type in eMap ? eMap[e.type] : document[$.winFocus.props.hidden];
					$(window).data("visible", !e.hidden);
					$.winFocus.methods.exeCB(e);
				}
				else {
					try { $.winFocus.methods.onChange.call(document, new Event('visibilitychange')); }
					catch(err) {  }
				}
			}
		}
		$.winFocus.props = { hidden: "hidden" }
	}
})(jQuery);
