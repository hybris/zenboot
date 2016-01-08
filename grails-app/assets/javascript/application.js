//= require jquery/jquery-1.7.2.js
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
