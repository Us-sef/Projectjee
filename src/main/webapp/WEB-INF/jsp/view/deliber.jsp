<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset='utf-8'>
    <meta http-equiv='X-UA-Compatible' content='IE=edge'>
    <title>Genrate Deliberations File</title>
    <meta name='viewport' content='width=device-width, initial-scale=1'>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-wEmeIV1mKuiNpC+IOBjI7aAzPcEZeedi5yW5f2yOq55WWLwNGmvvx4Um1vskeMj0" crossorigin="anonymous">
</head>
<body>

<div class="container border border-1 border-secondary p-5 rounded-3 bg-light shadow p-2 mb-2" style="margin-top :110px; width: 32%;">
    <form class="row g-2" action="${pageContext.request.contextPath}/deliberations">
        <h2 style="margin-left: 23%;" class="text-secondary">Deliberations</h2>
        <select class="form-select mb-2 " aria-label="Default select example" name="niveau">
            <option selected>Niveau</option>
            <option value="1">Gi1</option>
            <option value="2">Gi2</option>
            <option value="3">Ap1</option>
            <option value="4">Ap2</option>
            <option value="5">Gi3</option>
            <option value="6">Gc1</option>
            <option value="7">Gc2</option>
        </select>
        <select class="form-select mb-2" aria-label="Default select example" name="anne">
            <option selected>Annnee</option>
            <option value="2020">2020</option>
            <option value="2019">2019</option>
            <option value="2018">2018</option>
            <option value="2017">2017</option>
            <option value="2016">2016</option>
        </select>
        <button type="submit" class="btn btn-outline-secondary">Export File</button>
    </form>
</div>

</body>
</html>