<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Code</title>
    <link rel="stylesheet"
          target="_blank" href="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css">
    <script src="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
    <script>hljs.initHighlightingOnLoad();</script>
</head>
<body>
<p>
    <span id="load_date">${root.date}</span>
</p>

<#if  (root.viewRest == 1)>
    <p><span id="views_restriction">${root.views} more views allowed</span></p>
</#if>
<#if (root.timeRest == 1)>
    <p><span id="time_restriction">The code will be available for ${root.time} seconds</span></p>
</#if>
<pre id="code_snippet"><code class="language-java">${root.code}</code></pre>
</body>
</html>