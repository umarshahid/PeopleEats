const restaurantsEl = document.getElementById("restaurants");
const menuEl = document.getElementById("menu");
const orderForm = document.getElementById("order-form");
const orderStatus = document.getElementById("order-status");
const ordersEl = document.getElementById("orders");
const refreshOrdersBtn = document.getElementById("refresh-orders");
const welcomeTitle = document.getElementById("welcome-title");
const notice = document.getElementById("customer-notice");
const noticeTitle = document.getElementById("customer-notice-title");
const noticeBody = document.getElementById("customer-notice-body");
const noticeDismiss = document.getElementById("customer-notice-dismiss");
const feedbackPanel = document.getElementById("feedback-panel");
const feedbackForm = document.getElementById("feedback-form");
const feedbackStatus = document.getElementById("feedback-status");

let selectedRestaurant = null;
let selectedMenu = [];
let knownStates = new Map();
let pollHandle = null;

function getUser() {
  try {
    return JSON.parse(localStorage.getItem("pe_user") || "{}");
  } catch {
    return {};
  }
}

function setStatus(message, ok) {
  orderStatus.textContent = message;
  orderStatus.classList.remove("success", "error");
  orderStatus.classList.add(ok ? "success" : "error");
}

function setFeedbackStatus(message, ok) {
  feedbackStatus.textContent = message;
  feedbackStatus.classList.remove("success", "error");
  feedbackStatus.classList.add(ok ? "success" : "error");
}

function showNotice(title, message) {
  noticeTitle.textContent = title;
  noticeBody.textContent = message;
  notice.classList.remove("hidden");
}

function hideNotice() {
  notice.classList.add("hidden");
}

async function fetchJson(url, options) {
  const response = await fetch(url, options);
  const data = await response.json().catch(() => ({}));
  return { response, data };
}

function renderRestaurants(restaurants) {
  restaurantsEl.innerHTML = "";
  restaurants.forEach((restaurant) => {
    const btn = document.createElement("button");
    btn.className = "list-item";
    btn.textContent = `${restaurant.name} · ${restaurant.location}`;
    btn.addEventListener("click", () => loadMenu(restaurant));
    restaurantsEl.appendChild(btn);
  });
}

function renderMenu(menu) {
  menuEl.innerHTML = "";
  if (!menu.length) {
    menuEl.textContent = "No menu items yet.";
    return;
  }
  menu.forEach((item) => {
    const btn = document.createElement("button");
    btn.className = "list-item";
    btn.textContent = `${item.name} — $${item.price.toFixed(2)}`;
    btn.addEventListener("click", () => {
      orderForm.item.value = item.name;
      orderForm.price.value = item.price.toFixed(2);
    });
    menuEl.appendChild(btn);
  });
}

async function loadRestaurants() {
  restaurantsEl.textContent = "Loading restaurants...";
  const { response, data } = await fetchJson("/api/restaurants");
  if (response.ok) {
    renderRestaurants(data);
  } else {
    restaurantsEl.textContent = "Failed to load restaurants.";
  }
}

async function loadMenu(restaurant) {
  selectedRestaurant = restaurant;
  menuEl.textContent = `Loading menu for ${restaurant.name}...`;
  const { response, data } = await fetchJson(`/api/restaurants/${restaurant.id}/menu`);
  if (response.ok) {
    selectedMenu = data;
    renderMenu(data);
  } else {
    menuEl.textContent = "Failed to load menu.";
  }
}

async function loadOrders() {
  ordersEl.textContent = "Loading orders...";
  const { response, data } = await fetchJson("/api/orders");
  if (!response.ok) {
    ordersEl.textContent = "Failed to load orders.";
    return;
  }
  const { username } = getUser();
  const mine = Array.isArray(data)
    ? data.filter((order) => order.customer === username)
    : [];
  if (!mine.length) {
    ordersEl.textContent = "No orders yet.";
    return;
  }
  ordersEl.textContent = mine.map((order) => {
    return `#${order.orderNo} · ${order.item} · ${order.state}`;
  }).join("\n");

  mine.forEach((order) => {
    const prev = knownStates.get(order.orderNo);
    if (prev && prev !== order.state) {
      if (order.state === "ON_THE_WAY") {
        showNotice("Order picked up", `Order #${order.orderNo} is on the way.`);
      }
      if (order.state === "DELIVERED") {
        showNotice("Order delivered", `Order #${order.orderNo} was delivered.`);
        feedbackPanel.classList.remove("hidden");
        feedbackForm.orderNo.value = order.orderNo;
      }
    }
    knownStates.set(order.orderNo, order.state);
  });
}

orderForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const { username } = getUser();
  if (!username) {
    setStatus("Please log in again.", false);
    return;
  }
  const payload = {
    customer: username,
    item: orderForm.item.value.trim(),
    price: Number(orderForm.price.value),
    orderType: orderForm.orderType.value,
  };
  if (!payload.item || payload.price <= 0) {
    setStatus("Choose an item and price.", false);
    return;
  }
  const { response, data } = await fetchJson("/api/orders", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
  if (response.ok) {
    setStatus(`Order placed. Order #${data.orderNo}`, true);
    loadOrders();
  } else {
    setStatus("Failed to place order.", false);
  }
});

refreshOrdersBtn.addEventListener("click", loadOrders);
noticeDismiss.addEventListener("click", hideNotice);

feedbackForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(feedbackForm).entries());
  payload.orderNo = Number(payload.orderNo);
  payload.rating = Number(payload.rating);
  const { response, data } = await fetchJson("/api/feedback", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
  if (response.ok && data.success) {
    setFeedbackStatus("Feedback submitted. Thank you!", true);
    feedbackForm.reset();
  } else {
    setFeedbackStatus(data.message || "Failed to submit feedback.", false);
  }
});

const user = getUser();
if (user.username) {
  welcomeTitle.textContent = `Welcome, ${user.username}`;
}

loadRestaurants();
loadOrders();

pollHandle = setInterval(() => {
  loadOrders();
}, 5000);
