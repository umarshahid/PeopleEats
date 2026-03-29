import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage.jsx";
import CustomerPage from "./pages/CustomerPage.jsx";
import RestaurantPage from "./pages/RestaurantPage.jsx";
import RiderPage from "./pages/RiderPage.jsx";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/customer" element={<CustomerPage />} />
      <Route path="/restaurant" element={<RestaurantPage />} />
      <Route path="/rider" element={<RiderPage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
