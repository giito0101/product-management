function togglePassword() {
	const field = document.getElementById("validation02");
	field.type = field.type === "password" ? "text" : "password";
}