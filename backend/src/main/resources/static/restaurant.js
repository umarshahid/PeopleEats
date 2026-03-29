const ordersEl = document.getElementById("orders");
const preparingEl = document.getElementById("preparing-orders");
const orderStatus = document.getElementById("order-status");
const refreshBtn = document.getElementById("refresh-orders");
const refreshPreparingBtn = document.getElementById("refresh-preparing");
const markReadyBtn = document.getElementById("mark-ready");
const welcomeTitle = document.getElementById("welcome-title");
const notice = document.getElementById("restaurant-notice");
const noticeTitle = document.getElementById("restaurant-notice-title");
const noticeBody = document.getElementById("restaurant-notice-body");
const noticeDismiss = document.getElementById("restaurant-notice-dismiss");

let selectedOrderNo = null;
let pollHandle = null;
let knownDelivered = new Set();

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

function renderOrders(container, orders, selectable) {
  container.innerHTML = "";
  if (!orders.length) {
    container.textContent = "No orders found.";
    return;
  }
  orders.forEach((order) => {
    const row = document.createElement("button");
    row.className = "list-item";
    row.textContent = `#${order.orderNo} · ${order.customer} · ${order.item} · ${order.state}`;
    if (selectable) {
      row.addEventListener("click", () => {
        selectedOrderNo = order.orderNo;
        [...container.children].forEach((child) => child.classList.remove("active"));
        row.classList.add("active");
      });
    }
    container.appendChild(row);
  });
}

async function loadOrders() {
  ordersEl.textContent = "Loading orders...";
  const { response, data } = await fetchJson("/api/orders");
  if (response.ok) {
    renderOrders(ordersEl, Array.isArray(data) ? data : [], false);
    const delivered = (Array.isArray(data) ? data : []).filter((o) => o.state === "DELIVERED");
    const fresh = new Set(delivered.map((o) => o.orderNo));
    const newDelivered = [...fresh].filter((id) => !knownDelivered.has(id));
    if (newDelivered.length > 0) {
      showNotice("Order delivered", `Delivered order(s): ${newDelivered.join(", ")}`);
    }
    knownDelivered = fresh;
  } else {
    ordersEl.textContent = "Failed to load orders.";
  }
}

async function loadPreparingOrders() {
  preparingEl.textContent = "Loading PREPARING orders...";
  const { response, data } = await fetchJson("/api/orders?state=PREPARING");
  if (response.ok) {
    renderOrders(preparingEl, Array.isArray(data) ? data : [], true);
  } else {
    preparingEl.textContent = "Failed to load PREPARING orders.";
  }
}

markReadyBtn.addEventListener("click", async () => {
  if (!selectedOrderNo) {
    setStatus("Select an order first.", false);
    return;
  }
  const { response } = await fetchJson(`/api/orders/${selectedOrderNo}/state`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ state: "READY" }),
  });
  if (response.ok) {
    setStatus(`Order #${selectedOrderNo} marked READY.`, true);
    selectedOrderNo = null;
    [...preparingEl.children].forEach((child) => child.classList.remove("active"));
    await loadPreparingOrders();
    await loadOrders();
  } else {
    setStatus("Failed to update order.", false);
  }
});

refreshBtn.addEventListener("click", loadOrders);
refreshPreparingBtn.addEventListener("click", loadPreparingOrders);
noticeDismiss.addEventListener("click", hideNotice);

const user = getUser();
if (user.username) {
  welcomeTitle.textContent = `Restaurant dashboard · ${user.username}`;
}

loadPreparingOrders();
loadOrders();

pollHandle = setInterval(() => {
  loadPreparingOrders();
  loadOrders();
}, 5000);
