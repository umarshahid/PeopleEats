import React, { useEffect, useMemo, useRef, useState } from "react";
import { apiFetch, getStoredUser } from "../api.js";

export default function CustomerPage() {
  const user = getStoredUser();
  const [restaurants, setRestaurants] = useState([]);
  const [menu, setMenu] = useState([]);
  const [orders, setOrders] = useState([]);
  const [notice, setNotice] = useState(null);
  const [feedbackVisible, setFeedbackVisible] = useState(false);
  const [feedbackStatus, setFeedbackStatus] = useState("");
  const [orderStatus, setOrderStatus] = useState("");
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  const [knownStates, setKnownStates] = useState({});
  const [feedbackOrderNo, setFeedbackOrderNo] = useState(null);
  const [feedbackRider, setFeedbackRider] = useState("");
  const [rating, setRating] = useState(5);
  const [orderDialogOpen, setOrderDialogOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const lastNoticeKeyRef = useRef("");

  const myOrders = useMemo(() => {
    return orders.filter((order) => order.customer === user.username);
  }, [orders, user.username]);

  async function loadRestaurants() {
    const { response, data } = await apiFetch("/api/restaurants");
    if (response.ok) {
      setRestaurants(data);
    }
  }

  async function loadMenu(restaurantId) {
    const { response, data } = await apiFetch(`/api/restaurants/${restaurantId}/menu`);
    if (response.ok) {
      setMenu(data);
    }
  }

  async function loadOrders() {
    const { response, data } = await apiFetch("/api/orders");
    if (response.ok) {
      setOrders(Array.isArray(data) ? data : []);
    }
  }

  function handleSelectRestaurant(restaurant) {
    setSelectedRestaurant(restaurant);
    loadMenu(restaurant.id);
  }

  async function handlePlaceOrder(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const payload = Object.fromEntries(formData.entries());
    payload.customer = user.username;
    payload.price = Number(payload.price);
    const { response, data } = await apiFetch("/api/orders", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    if (response.ok) {
      setOrderStatus(`Order placed. Order ${data.orderNo}`);
      event.target.reset();
      setOrderDialogOpen(false);
      setSelectedItem(null);
      loadOrders();
    } else {
      setOrderStatus("Failed to place order.");
    }
  }

  async function handleFeedback(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const payload = Object.fromEntries(formData.entries());
    payload.orderNo = Number(payload.orderNo);
    payload.rating = rating;
    const { response, data } = await apiFetch("/api/feedback", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    if (response.ok && data.success) {
      setFeedbackStatus("Feedback submitted. Thank you!");
      event.target.reset();
      setFeedbackVisible(false);
      setRating(5);
    } else {
      setFeedbackStatus(data.message || "Failed to submit feedback.");
    }
  }

  useEffect(() => {
    loadRestaurants();
    loadOrders();
    const handle = setInterval(loadOrders, 5000);
    return () => clearInterval(handle);
  }, []);

  useEffect(() => {
    const updates = {};
    myOrders.forEach((order) => {
      const prev = knownStates[order.orderNo];
      if (prev && prev !== order.state) {
        if (order.state === "PREPARING") {
          const key = `${order.orderNo}-PREPARING`;
          if (lastNoticeKeyRef.current !== key && !notice) {
            lastNoticeKeyRef.current = key;
            setNotice({ title: "Order accepted", body: `Order ${order.orderNo} is now preparing.` });
          }
        }
        if (order.state === "ON_THE_WAY") {
          const key = `${order.orderNo}-ON_THE_WAY`;
          if (lastNoticeKeyRef.current !== key && !notice) {
            lastNoticeKeyRef.current = key;
            setNotice({ title: "Order picked up", body: `Order ${order.orderNo} is on the way.` });
          }
        }
        if (order.state === "DELIVERED") {
          const key = `${order.orderNo}-DELIVERED`;
          if (lastNoticeKeyRef.current !== key && !notice) {
            lastNoticeKeyRef.current = key;
            setNotice({ title: "Order delivered", body: `Order ${order.orderNo} was delivered.` });
          }
          setFeedbackVisible(true);
          setFeedbackOrderNo(order.orderNo);
          setFeedbackRider(order.riderName || "Rider");
        }
      }
      updates[order.orderNo] = order.state;
    });
    if (Object.keys(updates).length) {
      setKnownStates((prev) => ({ ...prev, ...updates }));
    }
  }, [myOrders, knownStates, notice]);

  return (
    <main className="page">
      <header className="topbar">
        <div className="brand">
          <span className="brand-mark" />
          <span>PeopleEats</span>
        </div>
        <div className="row">
          <span className="badge">Customer</span>
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

      <section className="layout">
        <div className="main">
          <div className="panel">
            <h3 className="section-title">Order updates</h3>
            <div className="list muted">
              {myOrders.length === 0 && <p>No orders yet.</p>}
              {myOrders.map((order) => (
                <div key={`${order.orderNo}-updates`} className="order-card">
                  <div>
                    <strong>Order {order.orderNo}</strong>
                    <span className="muted"> ? {order.item}</span>
                  </div>
                  <span className={`status-pill status-${order.state.toLowerCase()}`}>{order.state}</span>
                </div>
              ))}
            </div>
          </div>
          <div className="panel">
            <h3 className="section-title">Order timeline</h3>
            <div className="list muted">
              {myOrders.length === 0 && <p>No timeline yet.</p>}
              {myOrders.map((order) => (
                <div key={`${order.orderNo}-timeline`} className="timeline-row">
                  <span className="dot" />
                  <div>
                    <div>Order {order.orderNo}</div>
                    <div className="muted">Status: {order.state}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
        <aside className="sidebar">
          <div className="panel">
            <h3 className="section-title">Restaurants</h3>
            <div className="list">
              {restaurants.map((restaurant) => (
                <button
                  key={restaurant.id}
                  className={`list-item ${selectedRestaurant?.id === restaurant.id ? "active" : ""}`}
                  onClick={() => handleSelectRestaurant(restaurant)}
                >
                  <div className="item-title">{restaurant.name}</div>
                  <div className="muted">{restaurant.location}</div>
                </button>
              ))}
            </div>
          </div>
          <div className="panel">
            <h3 className="section-title">Menu</h3>
            <div className="list">
              {menu.length === 0 && <p className="muted">Select a restaurant to see its menu.</p>}
              {menu.map((item) => (
                <button
                  key={item.name}
                  className="list-item"
                  onClick={() => {
                    setSelectedItem(item);
                    setOrderDialogOpen(true);
                  }}
                >
                  <div className="item-title">{item.name}</div>
                  <div className="muted">${item.price.toFixed(2)}</div>
                </button>
              ))}
            </div>
          </div>
        </aside>
      </section>

      {orderDialogOpen && (
        <div className="modal-backdrop">
          <section className="modal">
            <div className="modal-header">
              <div>
                <p className="eyebrow">Order</p>
                <h3>Place order</h3>
              </div>
              <button className="modal-close" onClick={() => setOrderDialogOpen(false)}>Close</button>
            </div>
            <form id="order-form" className="form" onSubmit={handlePlaceOrder}>
              <label>
                Item
                <input name="item" type="text" required defaultValue={selectedItem?.name || ""} />
              </label>
              <label>
                Price
                <input
                  name="price"
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  defaultValue={selectedItem ? selectedItem.price.toFixed(2) : ""}
                />
              </label>
              <label>
                Order type
                <select name="orderType">
                  <option value="DELIVERY">Delivery</option>
                  <option value="NOT DELIVERY">Pickup</option>
                </select>
              </label>
              <button type="submit">Place order</button>
            </form>
            <p className="status">{orderStatus}</p>
          </section>
        </div>
      )}

      {feedbackVisible && (
        <div className="modal-backdrop">
          <section className="modal">
            <div className="modal-header">
              <div>
                <p className="eyebrow">Feedback</p>
                <h3>Rate your rider</h3>
              </div>
              <button className="modal-close" onClick={() => setFeedbackVisible(false)}>Close</button>
            </div>
            <form className="form" onSubmit={handleFeedback}>
              <label>
                Order number
                <input name="orderNo" type="number" min="1" required defaultValue={feedbackOrderNo || ""} />
              </label>
              <label>
                Rider name
                <input name="riderName" type="text" placeholder="Rider" required defaultValue={feedbackRider} />
              </label>
              <label>
                Rating
                <div className="stars">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      type="button"
                      key={star}
                      className={`star ${rating >= star ? "active" : ""}`}
                      onClick={() => setRating(star)}
                    >
                      *
                    </button>
                  ))}
                </div>
              </label>
              <label>
                Comment
                <input name="comment" type="text" placeholder="Optional feedback" />
              </label>
              <button type="submit">Submit feedback</button>
            </form>
            <p className="status">{feedbackStatus}</p>
          </section>
        </div>
      )}
    </main>
  );
}
