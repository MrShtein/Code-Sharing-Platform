<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create</title>
</head>
<body>
<textarea id="code_snippet" cols="30" rows="10"></textarea>
<br>
<p>Time restriction</p>
<input id="time_restriction" type="text"/>
<br>
<p>Views restriction</p>
<input id="views_restriction" type="text"/>
<button id="send_snippet" type="submit" onclick="send()">Submit</button>
<script>
    let send = function () {
        let object = {
            "code": document.getElementById("code_snippet").value,
            "views": Number.parseInt(document.getElementById("views_restriction").value),
            "time": Number.parseInt(document.getElementById("time_restriction").value)

        };

        let json = JSON.stringify(object);

        let xhr = new XMLHttpRequest();
        xhr.open("POST", '/api/code/new', false)
        xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
        xhr.send(json);

        if (xhr.status == 200) {
            alert("Success!");
        }
    }
</script>
</body>
</html>