//= require jquery
//= require bootstrap
//= require epiceditor/js/epiceditor.min.js
//= require jquery.winFocus.js
//= require zenboot.js
//= require_self

if (typeof jQuery !== 'undefined') {
	(function($) {
		$('#spinner').ajaxStart(function() {
			$(this).fadeIn();
		}).ajaxStop(function() {
			$(this).fadeOut();
		});
	})(jQuery);
}
