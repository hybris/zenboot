<script>
    function resizeIframe(obj) {
        obj.style.height = obj.contentWindow.document.body.scrollHeight + 21 +  'px';
    }
</script>

<div>
	<iframe onload="resizeIframe(this)" width="100%" style="border: solid 1px #EEE" src="${url}">IFrames are not supported by your browser.</iframe>
</div>