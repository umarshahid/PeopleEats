import React, { useState } from "react";
import { apiFetch, setStoredUser } from "../api.js";

export default function LoginPage() {
  const [signupStatus, setSignupStatus] = useState("");
  const [loginStatus, setLoginStatus] = useState("");

  async function handleSignup(event) {
    event.preventDefault();
    setSignupStatus("Signing you up...");
    const formData = new FormData(event.target);
    const payload = Object.fromEntries(formData.entries());
    const { response, data } = await apiFetch("/api/auth/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    if (response.ok && data.success) {
      setSignupStatus("Signup successful. You can log in now.");
      event.target.reset();
    } else {
      setSignupStatus(data.message || "Signup failed.");
    }
  }

  async function handleLogin(event) {
    event.preventDefault();
    setLoginStatus("Logging you in...");
    const formData = new FormData(event.target);
    const payload = Object.fromEntries(formData.entries());
    const { response, data } = await apiFetch("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    if (response.ok && data.success) {
      const role = data.role;
      setStoredUser({ role, username: payload.username });
      setLoginStatus("Login successful. Opening dashboard...");
      const target = role === "customer" ? "/customer" : role === "restaurant" ? "/restaurant" : "/rider";
      window.open(target, "_blank");
      event.target.reset();
    } else {
      setLoginStatus(data.message || "Login failed.");
    }
  }

  return (
    <main className="page">
      <header className="topbar">
        <div className="brand">
          <span className="brand-mark" />
          <span>PeopleEats</span>
        </div>
        <span className="badge">Fast delivery</span>
      </header>

      <section className="hero">
        <div className="hero-text">
          <p className="eyebrow">Order smarter</p>
          <h1>Delivery that keeps pace with your day.</h1>
          <p className="subtitle">Sign up or log in to manage orders, kitchens, and rider flow.</p>
        </div>
        <div className="hero-panel">
          <div className="panel">
            <h2>Create account</h2>
            <form onSubmit={handleSignup}>
              <label>
                Username
                <input name="username" type="text" placeholder="e.g. sana" required />
              </label>
              <label>
                Password
                <input name="password" type="password" placeholder="at least 6 chars" required />
              </label>
              <label>
                Role
                <select name="role">
                  <option value="customer">Customer</option>
                  <option value="restaurant">Restaurant</option>
                  <option value="rider">Rider</option>
                </select>
              </label>
              <button type="submit">Sign up</button>
            </form>
            <p className="status">{signupStatus}</p>
          </div>
          <div className="panel">
            <h2>Log in</h2>
            <form onSubmit={handleLogin}>
              <label>
                Username
                <input name="username" type="text" placeholder="e.g. sana" required />
              </label>
              <label>
                Password
                <input name="password" type="password" placeholder="your password" required />
              </label>
              <button type="submit">Log in</button>
            </form>
            <p className="status">{loginStatus}</p>
          </div>
        </div>
      </section>
    </main>
  );
}
