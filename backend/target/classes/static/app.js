const signupForm = document.getElementById("signup-form");
const loginForm = document.getElementById("login-form");
const signupStatus = document.getElementById("signup-status");
const loginStatus = document.getElementById("login-status");

function setStatus(element, message, ok) {
  element.textContent = message;
  element.classList.remove("success", "error");
  element.classList.add(ok ? "success" : "error");
}

async function fetchJson(url, options) {
  const response = await fetch(url, options);
  const data = await response.json().catch(() => ({}));
  return { response, data };
}

signupForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  signupStatus.textContent = "Signing you up...";

  const payload = Object.fromEntries(new FormData(signupForm).entries());
  const response = await fetch("/api/auth/signup", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  const data = await response.json().catch(() => ({}));
  if (response.ok && data.success) {
    setStatus(signupStatus, "Signup successful. You can log in now.", true);
    signupForm.reset();
  } else {
    setStatus(signupStatus, data.message || "Signup failed.", false);
  }
});

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  loginStatus.textContent = "Logging you in...";

  const payload = Object.fromEntries(new FormData(loginForm).entries());
  const { response, data } = await fetchJson("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (response.ok && data.success) {
    setStatus(loginStatus, "Login successful.", true);
    const role = data.role;
    const username = payload.username;
    localStorage.setItem("pe_user", JSON.stringify({ role, username }));
    const target = role === "customer"
      ? "/customer.html"
      : role === "restaurant"
        ? "/restaurant.html"
        : "/rider.html";
    window.open(target, "_blank");
    loginForm.reset();
  } else {
    setStatus(loginStatus, data.message || "Login failed.", false);
  }
});
