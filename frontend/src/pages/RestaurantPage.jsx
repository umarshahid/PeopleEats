import React, { useEffect, useRef, useState } from "react";
import { apiFetch, getStoredUser } from "../api.js";

export default function RestaurantPage() {
  const user = getStoredUser();
  const [orders, setOrders] = useState([]);
  const [requested, setRequested] = useState([]);
  const [preparing, setPreparing] = useState([]);
  const [selectedRequested, setSelectedRequested] = useState(null);
  const [selectedPreparing, setSelectedPreparing] = useState(null);
  const [status, setStatus] = useState("");
  const [notice, setNotice] = useState(null);
  const knownDeliveredRef = useRef(new Set());
  const riderMapRef = useRef({});
  const lastNoticeKeyRef = useRef("");

  function splitOrders(list) {
    setRequested(list.filter((o) => o.state === "REQUESTED"));
    setPreparing(list.filter((o) => o.state === "PREPARING"));
  }

  async function loadOrders() {
    const { response, data } = await apiFetch("/api/orders");
    if (response.ok) {
      const list = Array.isArray(data) ? data : [];
      setOrders(list);
      splitOrders(list);

      const delivered = list.filter((o) => o.state === "DELIVERED");
      const fresh = new Set(delivered.map((o) => o.orderNo));
      const newDelivered = [...fresh].filter((id) => !knownDeliveredRef.current.has(id));
      if (newDelivered.length > 0) {
        const key = `delivered-${newDelivered.join(",")}`;
        if (lastNoticeKeyRef.current !== key && !notice) {
          lastNoticeKeyRef.current = key;
          setNotice({ title: "Order delivered", body: `Delivered order(s): ${newDelivered.join(", ")}` });
        }
      }
      knownDeliveredRef.current = fresh;

      const riderChanges = [];
      list.filter((o) => o.state === "PREPARING").forEach((order) => {
        const prev = riderMapRef.current[order.orderNo] || "";
        if (!prev && order.riderName) {
          riderChanges.push(`Order ${order.orderNo} accepted by ${order.riderName}`);
        }
        riderMapRef.current[order.orderNo] = order.riderName || "";
      });
      if (riderChanges.length > 0 && !notice) {
        const key = `rider-${riderChanges.join("|")}`;
        if (lastNoticeKeyRef.current !== key) {
          lastNoticeKeyRef.current = key;
          setNotice({ title: "Rider accepted", body: riderChanges[0] });
        }
      }

      const newRequests = list.filter((o) => o.state === "REQUESTED").map((o) => o.orderNo);
      if (newRequests.length > 0) {
        const key = `request-${newRequests.join(",")}`;
        if (lastNoticeKeyRef.current !== key && !notice) {
          lastNoticeKeyRef.current = key;
          setNotice({ title: "New order request", body: `New order(s): ${newRequests.join(", ")}` });
        }
      }
    }
  }

  async function acceptRequest() {
    if (!selectedRequested) {
      setStatus("Select a request first.");
      return;
    }
    const { response } = await apiFetch(`/api/orders/${selectedRequested}/state`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ state: "PREPARING" })
    });
    if (response.ok) {
      setStatus(`Order ${selectedRequested} accepted and preparing.`);
      setSelectedRequested(null);
      loadOrders();
    } else {
      setStatus("Failed to update order.");
    }
  }

  async function markReady() {
    if (!selectedPreparing) {
      setStatus("Select a preparing order first.");
      return;
    }
    const { response } = await apiFetch(`/api/orders/${selectedPreparing}/state`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ state: "READY" })
    });
    if (response.ok) {
      setStatus(`Order ${selectedPreparing} marked READY.`);
      setSelectedPreparing(null);
      loadOrders();
    } else {
      setStatus("Failed to update order.");
    }
  }

  useEffect(() => {
    loadOrders();
    const handle = setInterval(loadOrders, 5000);
    return () => clearInterval(handle);
  }, []);

  return (
    <main className="page">
      <header className="topbar">
        <div className="brand">
          <span className="brand-mark" />
          <span>PeopleEats</span>
        </div>
        <div className="row">
          <span className="badge">Restaurant</span>
          <a className="ghost-link" href="/">Log out</a>
        </div>
      </header>

      {notice && (
        <div className="modal-backdrop">
          <section className="modal">
            <div className="modal-header">
              <div>
                <p className="eyebrow">Update</p>
                <h3>{notice.title}</h3>
              </div>
            </div>
            <p className="subtitle">{notice.body}</p>
            <div className="form-actions">
              <button type="button" onClick={() => setNotice(null)}>OK</button>
            </div>
          </section>
        </div>
      )}

      <section className="grid-3">
        <div className="panel">
          <h3 className="section-title">New requests</h3>
          <div className="row">
            <button onClick={loadOrders}>Refresh</button>
            <button onClick={acceptRequest}>Accept request</button>
          </div>
          <div className="list">
            {requested.length === 0 && <p className="muted">No requests.</p>}
            {requested.map((order) => (
              <button
                key={order.orderNo}
                className={`list-item ${selectedRequested === order.orderNo ? "active" : ""}`}
                onClick={() => setSelectedRequested(order.orderNo)}
              >
                <div className="item-title">Order {order.orderNo}</div>
                <div className="muted">{order.customer} ? {order.item}</div>
              </button>
            ))}
          </div>
        </div>
        <div className="panel">
          <h3 className="section-title">Preparing</h3>
          <div className="row">
            <button onClick={loadOrders}>Refresh</button>
            <button onClick={markReady}>Mark READY</button>
          </div>
          <div className="list">
            {preparing.length === 0 && <p className="muted">No preparing orders.</p>}
            {preparing.map((order) => (
              <button
                key={order.orderNo}
                className={`list-item ${selectedPreparing === order.orderNo ? "active" : ""}`}
                onClick={() => setSelectedPreparing(order.orderNo)}
              >
                <div className="item-title">Order {order.orderNo}</div>
                <div className="muted">{order.item} ? Rider: {order.riderName || "Unassigned"}</div>
              </button>
            ))}
          </div>
        </div>
        <div className="panel">
          <h3 className="section-title">All orders</h3>
          <div className="list">
            {orders.length === 0 && <p className="muted">No orders yet.</p>}
            {orders.map((order) => (
              <div key={order.orderNo} className="order-card">
                <strong>Order {order.orderNo}</strong>
                <span className={`status-pill status-${order.state.toLowerCase()}`}>{order.state}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      <p className="status">{status}</p>
    </main>
  );
}
