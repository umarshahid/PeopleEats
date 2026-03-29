const readyEl = document.getElementById("ready-orders");
const onTheWayEl = document.getElementById("ontheway-orders");
const refreshReadyBtn = document.getElementById("refresh-ready");
const refreshOnTheWayBtn = document.getElementById("refresh-ontheway");
const takeOrderBtn = document.getElementById("take-order");
const deliverOrderBtn = document.getElementById("deliver-order");
const orderStatus = document.getElementById("order-status");
const welcomeTitle = document.getElementById("welcome-title");
const notice = document.getElementById("rider-notice");
const noticeTitle = document.getElementById("notice-title");
const noticeBody = document.getElementById("notice-body");
const noticeDismiss = document.getElementById("notice-dismiss");

let selectedReady = null;
let selectedOnTheWay = null;
let knownReady = new Set();
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

function renderOrders(container, orders, selectHandler) {
  container.innerHTML = "";
  if (!orders.length) {
    container.textContent = "No orders found.";
    return;
  }
  orders.forEach((order) => {
    const row = document.createElement("button");
    row.className = "list-item";
    row.textContent = `#${order.orderNo} · ${order.customer} · ${order.item}`;
    row.addEventListener("click", () => selectHandler(order.orderNo, row, container));
    container.appendChild(row);
  });
}

function selectOrder(orderNo, row, container) {
  [...container.children].forEach((child) => child.classList.remove("active"));
  row.classList.add("active");
  return orderNo;
}

async function loadReady() {
  readyEl.textContent = "Loading READY orders...";
  const { response, data } = await fetchJson("/api/orders?state=READY");
  if (response.ok) {
    const list = Array.isArray(data) ? data : [];
    renderOrders(readyEl, list, (orderNo, row, container) => {
      selectedReady = selectOrder(orderNo, row, container);
    });
    const fresh = new Set(list.map((order) => order.orderNo));
    const newOrders = [...fresh].filter((id) => !knownReady.has(id));
    if (newOrders.length > 0) {
      showNotice("New READY order", `New order(s): ${newOrders.join(", ")}`);
    } else if (list.length > 0 && notice.classList.contains("hidden")) {
      showNotice("READY orders waiting", `${list.length} order(s) are ready to pick up.`);
    }
    knownReady = fresh;
  } else {
    readyEl.textContent = "Failed to load READY orders.";
  }
}

async function loadOnTheWay() {
  onTheWayEl.textContent = "Loading ON_THE_WAY orders...";
  const { response, data } = await fetchJson("/api/orders?state=ON_THE_WAY");
  if (response.ok) {
    renderOrders(onTheWayEl, Array.isArray(data) ? data : [], (orderNo, row, container) => {
      selectedOnTheWay = selectOrder(orderNo, row, container);
    });
  } else {
    onTheWayEl.textContent = "Failed to load ON_THE_WAY orders.";
  }
}

takeOrderBtn.addEventListener("click", async () => {
  if (!selectedReady) {
    setStatus("Select a READY order first.", false);
    return;
  }
  const { response } = await fetchJson(`/api/orders/${selectedReady}/state`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ state: "ON_THE_WAY" }),
  });
  if (response.ok) {
    setStatus(`Order #${selectedReady} marked ON_THE_WAY.`, true);
    knownReady.delete(selectedReady);
    selectedReady = null;
    loadReady();
    loadOnTheWay();
  } else {
    setStatus("Failed to update order.", false);
  }
});

deliverOrderBtn.addEventListener("click", async () => {
  if (!selectedOnTheWay) {
    setStatus("Select an ON_THE_WAY order first.", false);
    return;
  }
  const { response } = await fetchJson(`/api/orders/${selectedOnTheWay}/state`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ state: "DELIVERED" }),
  });
  if (response.ok) {
    setStatus(`Order #${selectedOnTheWay} marked DELIVERED.`, true);
    selectedOnTheWay = null;
    loadOnTheWay();
  } else {
    setStatus("Failed to update order.", false);
  }
});

refreshReadyBtn.addEventListener("click", loadReady);
refreshOnTheWayBtn.addEventListener("click", loadOnTheWay);
noticeDismiss.addEventListener("click", hideNotice);

const user = getUser();
if (user.username) {
  welcomeTitle.textContent = `Rider dashboard · ${user.username}`;
}

loadReady();
loadOnTheWay();

pollHandle = setInterval(() => {
  loadReady();
  loadOnTheWay();
}, 5000);
