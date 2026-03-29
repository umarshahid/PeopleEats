import React, { useEffect, useRef, useState } from "react";
import { apiFetch, getStoredUser } from "../api.js";

export default function RiderPage() {
  const user = getStoredUser();
  const [available, setAvailable] = useState([]);
  const [assigned, setAssigned] = useState([]);
  const [readyToPick, setReadyToPick] = useState([]);
  const [onTheWay, setOnTheWay] = useState([]);
  const [selectedAvailable, setSelectedAvailable] = useState(null);
  const [selectedAssigned, setSelectedAssigned] = useState(null);
  const [selectedReady, setSelectedReady] = useState(null);
  const [selectedOnTheWay, setSelectedOnTheWay] = useState(null);
  const [notice, setNotice] = useState(null);
  const [status, setStatus] = useState("");
  const [knownPrep, setKnownPrep] = useState(new Set());
  const [rating, setRating] = useState(null);
  const [history, setHistory] = useState([]);
  const lastNoticeKeyRef = useRef("");

  async function loadPreparing() {
    const { response, data } = await apiFetch("/api/orders?state=PREPARING");
    if (response.ok) {
      const list = Array.isArray(data) ? data : [];
      const unassigned = list.filter((o) => !o.riderName);
      const mine = list.filter((o) => o.riderName && o.riderName === user.username);
      setAvailable(unassigned);
      setAssigned(mine);

      const fresh = new Set(unassigned.map((o) => o.orderNo));
      const newOrders = [...fresh].filter((id) => !knownPrep.has(id));
      if (newOrders.length > 0 && !notice) {
        const key = `prep-${newOrders.join(",")}`;
        if (lastNoticeKeyRef.current !== key) {
          lastNoticeKeyRef.current = key;
          setNotice({ title: "Orders preparing", body: `New order(s): ${newOrders.join(", ")}` });
        }
      }
      setKnownPrep(fresh);
    }
  }

  async function loadReady() {
    const { response, data } = await apiFetch("/api/orders?state=READY");
    if (response.ok) {
      const list = Array.isArray(data) ? data : [];
      setReadyToPick(list);
    }
  }

  async function loadOnTheWay() {
    const { response, data } = await apiFetch("/api/orders?state=ON_THE_WAY");
    if (response.ok) {
      const list = Array.isArray(data) ? data : [];
      setOnTheWay(list.filter((o) => o.riderName === user.username));
    }
  }

  async function loadRating() {
    if (!user.username) {
      return;
    }
    const { response, data } = await apiFetch(`/api/feedback/rider?name=${encodeURIComponent(user.username)}`);
    if (response.ok) {
      setRating(data);
    }
  }

  async function loadHistory() {
    if (!user.username) {
      return;
    }
    const { response, data } = await apiFetch(`/api/feedback/rider/history?name=${encodeURIComponent(user.username)}&limit=10`);
    if (response.ok) {
      setHistory(Array.isArray(data) ? data : []);
    }
  }

  async function acceptDelivery() {
    if (!selectedAvailable) {
      setStatus("Select an order to accept.");
      return;
    }
    const { response } = await apiFetch(`/api/orders/${selectedAvailable}/state`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ state: "PREPARING", riderName: user.username || "Rider" })
    });
    if (response.ok) {
      setStatus(`Order ${selectedAvailable} accepted.`);
      setSelectedAvailable(null);
      loadPreparing();
      loadReady();
      loadOnTheWay();
    } else {
      setStatus("Failed to accept order.");
    }
  }

  async function markOnTheWay() {
    if (!selectedReady) {
      setStatus("Select a READY order first.");
      return;
    }
    const { response } = await apiFetch(`/api/orders/${selectedReady}/state`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ state: "ON_THE_WAY", riderName: user.username || "Rider" })
    });
    if (response.ok) {
      setStatus(`Order ${selectedReady} marked ON_THE_WAY.`);
      setSelectedReady(null);
      loadReady();
      loadOnTheWay();
    } else {
      setStatus("Failed to update order.");
    }
  }

  async function markDelivered() {
    if (!selectedOnTheWay) {
      setStatus("Select an ON_THE_WAY order first.");
      return;
    }
    const { response } = await apiFetch(`/api/orders/${selectedOnTheWay}/state`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ state: "DELIVERED" })
    });
    if (response.ok) {
      setStatus(`Order ${selectedOnTheWay} marked DELIVERED.`);
      setSelectedOnTheWay(null);
      loadOnTheWay();
      loadRating();
      loadHistory();
    } else {
      setStatus("Failed to update order.");
    }
  }

  useEffect(() => {
    loadPreparing();
    loadReady();
    loadOnTheWay();
    loadRating();
    loadHistory();
    const handle = setInterval(() => {
      loadPreparing();
      loadReady();
      loadOnTheWay();
      loadRating();
      loadHistory();
    }, 5000);
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
          <span className="badge">Rider</span>
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
          <h3 className="section-title">Your rating</h3>
          {rating ? (
            <p>
              {rating.averageRating.toFixed(1)} / 5 ? {rating.totalRatings} ratings
            </p>
          ) : (
            <p className="muted">No ratings yet.</p>
          )}
        </div>
        <div className="panel">
          <h3 className="section-title">Available orders</h3>
          <div className="row">
            <button onClick={loadPreparing}>Refresh</button>
            <button onClick={acceptDelivery}>Accept</button>
          </div>
          <div className="list">
            {available.length === 0 && <p className="muted">No new orders.</p>}
            {available.map((order) => (
              <button
                key={order.orderNo}
                className={`list-item ${selectedAvailable === order.orderNo ? "active" : ""}`}
                onClick={() => setSelectedAvailable(order.orderNo)}
              >
                <div className="item-title">Order {order.orderNo}</div>
                <div className="muted">{order.customer} ? {order.item}</div>
              </button>
            ))}
          </div>
        </div>
        <div className="panel">
          <h3 className="section-title">Assigned (preparing)</h3>
          <div className="row">
            <button onClick={loadPreparing}>Refresh</button>
          </div>
          <div className="list">
            {assigned.length === 0 && <p className="muted">No assigned orders.</p>}
            {assigned.map((order) => (
              <button
                key={order.orderNo}
                className={`list-item ${selectedAssigned === order.orderNo ? "active" : ""}`}
                onClick={() => setSelectedAssigned(order.orderNo)}
              >
                <div className="item-title">Order {order.orderNo}</div>
                <div className="muted">{order.customer} ? {order.item}</div>
              </button>
            ))}
          </div>
        </div>
      </section>

      <section className="grid-2">
        <div className="panel">
          <h3 className="section-title">Ready to pick up</h3>
          <div className="row">
            <button onClick={loadReady}>Refresh</button>
            <button onClick={markOnTheWay}>Picked up</button>
          </div>
          <div className="list">
            {readyToPick.length === 0 && <p className="muted">No ready orders.</p>}
            {readyToPick.map((order) => (
              <button
                key={order.orderNo}
                className={`list-item ${selectedReady === order.orderNo ? "active" : ""}`}
                onClick={() => setSelectedReady(order.orderNo)}
              >
                <div className="item-title">Order {order.orderNo}</div>
                <div className="muted">{order.customer} ? {order.item} ? Rider: {order.riderName || "Unassigned"}</div>
              </button>
            ))}
          </div>
        </div>
        <div className="panel">
          <h3 className="section-title">On the way</h3>
          <div className="row">
            <button onClick={loadOnTheWay}>Refresh</button>
            <button onClick={markDelivered}>Delivered</button>
          </div>
          <div className="list">
            {onTheWay.length === 0 && <p className="muted">No orders on the way.</p>}
            {onTheWay.map((order) => (
              <button
                key={order.orderNo}
                className={`list-item ${selectedOnTheWay === order.orderNo ? "active" : ""}`}
                onClick={() => setSelectedOnTheWay(order.orderNo)}
              >
                <div className="item-title">Order {order.orderNo}</div>
                <div className="muted">{order.customer} ? {order.item}</div>
              </button>
            ))}
          </div>
        </div>
      </section>

      <section className="panel">
        <h3 className="section-title">Recent feedback</h3>
        <div className="list">
          {history.length === 0 && <p className="muted">No feedback yet.</p>}
          {history.map((entry) => (
            <div key={`${entry.orderNo}-${entry.createdAt}`}>
              Order {entry.orderNo} ? {entry.rating}/5 ? {entry.comment || "No comment"}
            </div>
          ))}
        </div>
      </section>

      <p className="status">{status}</p>
    </main>
  );
}
