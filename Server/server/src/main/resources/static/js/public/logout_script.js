fetch("/api/auth/logout", {
    method: "GET",
    credentials: "include"
})
.then(() => {
    window.location.href = "/";
})
.catch(error => {
    console.error("Error during logout:", error);
});