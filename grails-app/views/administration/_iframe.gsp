<script>
    function resizeIframe(obj) {
        var isChromium = window.chrome;
        if(isChromium) {
            obj.style.height = obj.contentDocument.documentElement.scrollHeight + 21 + 'px';
        }
        else {
            obj.style.height = obj.contentWindow.document.body.scrollHeight + 21 + 'px';
        }
    }
</script>

<div>
	<iframe onload="resizeIframe(this)" width="100%" style="border: solid 1px #EEE" src="${url}">IFrames are not supported by your browser.</iframe>
</div>