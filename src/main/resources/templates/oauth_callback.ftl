<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Redirect Page</title>
 	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" integrity="sha384-9aIt2nRpC12Uk9gS9baDl411NQApFmC26EwAOH8WgZl5MYYxFfc+NcPb1dKGj7Sk" crossorigin="anonymous">
	<style>
		.container {
			margin-top: 80px;
		}
	</style>
</head>
<body>
	<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
			<a class="navbar-brand">Rubric Service</a>
			<button class="navbar-toggler" type="button" data-toggle="collapse"
				data-target="#navbarsExampleDefault"s
				aria-controls="navbarsExampleDefault" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
	</nav>
	<div class="container">
	<form action="http://localhost:3000">
    	<button type"submit" class="btn btn-primary">Back to Rubric Service</button>
    	<script type="text/javascript">
    		sessionStorage.setItem('canvasToken', `${token}`);
		</script> 
    </form>
    </div>
</body>
</html>