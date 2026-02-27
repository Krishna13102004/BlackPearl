/* ============================================================
   BLACK PEARL SHIPYARD – API Service Layer (api.js)
   Handles all HTTP communication with the Spring Boot backend
   ============================================================ */

const API = (() => {
  const BASE_URL = 'http://localhost:8080/api';

  // ── HTTP helpers ──────────────────────────────────────────
  function getHeaders() {
    const token = localStorage.getItem('token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = `Bearer ${token}`;
    return headers;
  }

  async function request(method, endpoint, body = null) {
    const options = { method, headers: getHeaders() };
    if (body) options.body = JSON.stringify(body);
    try {
      const res = await fetch(`${BASE_URL}${endpoint}`, options);
      if (res.status === 401) {
        Auth.logout();
        return null;
      }
      if (!res.ok) {
        const err = await res.json().catch(() => ({ message: 'Request failed' }));
        throw new Error(err.message || `HTTP ${res.status}`);
      }
      return res.status === 204 ? null : await res.json();
    } catch (e) {
      console.error(`[API] ${method} ${endpoint}:`, e.message);
      throw e;
    }
  }

  const get    = (ep)       => request('GET',    ep);
  const post   = (ep, body) => request('POST',   ep, body);
  const put    = (ep, body) => request('PUT',    ep, body);
  const del    = (ep)       => request('DELETE', ep);
  const patch  = (ep, body) => request('PATCH',  ep, body);

  // ── Auth ──────────────────────────────────────────────────
  const auth = {
    login:    (data) => post('/auth/login', data),
    register: (data) => post('/auth/register', data),
    logout:   ()     => post('/auth/logout'),
    me:       ()     => get('/auth/me'),
  };

  // ── Users ─────────────────────────────────────────────────
  const users = {
    getAll:    ()       => get('/users'),
    getById:   (id)     => get(`/users/${id}`),
    update:    (id, d)  => put(`/users/${id}`, d),
    delete:    (id)     => del(`/users/${id}`),
    activate:  (id)     => patch(`/users/${id}/activate`),
    deactivate:(id)     => patch(`/users/${id}/deactivate`),
  };

  // ── Ship Orders ───────────────────────────────────────────
  const shipOrders = {
    getAll:    ()       => get('/ship-orders'),
    getMine:   ()       => get('/ship-orders/my'),
    getById:   (id)     => get(`/ship-orders/${id}`),
    create:    (d)      => post('/ship-orders', d),
    update:    (id, d)  => put(`/ship-orders/${id}`, d),
    approve:   (id)     => patch(`/ship-orders/${id}/approve`),
    reject:    (id)     => patch(`/ship-orders/${id}/reject`),
    delete:    (id)     => del(`/ship-orders/${id}`),
  };

  // ── Ship Repairs ──────────────────────────────────────────
  const shipRepairs = {
    getAll:    ()       => get('/ship-repairs'),
    getMine:   ()       => get('/ship-repairs/my'),
    getById:   (id)     => get(`/ship-repairs/${id}`),
    create:    (d)      => post('/ship-repairs', d),
    update:    (id, d)  => put(`/ship-repairs/${id}`, d),
    updateStatus:(id,s) => patch(`/ship-repairs/${id}/status`, { status: s }),
    delete:    (id)     => del(`/ship-repairs/${id}`),
  };

  // ── Tenders ───────────────────────────────────────────────
  const tenders = {
    getAll:    ()       => get('/tenders'),
    getOpen:   ()       => get('/tenders/open'),
    getById:   (id)     => get(`/tenders/${id}`),
    create:    (d)      => post('/tenders', d),
    update:    (id, d)  => put(`/tenders/${id}`, d),
    close:     (id)     => patch(`/tenders/${id}/close`),
    delete:    (id)     => del(`/tenders/${id}`),
    apply:     (id, d)  => post(`/tenders/${id}/apply`, d),
    getApplications: (id) => get(`/tenders/${id}/applications`),
    myApplications:  ()   => get('/tenders/my-applications'),
    approveApp:(tid, aid) => patch(`/tenders/${tid}/applications/${aid}/approve`),
    rejectApp: (tid, aid) => patch(`/tenders/${tid}/applications/${aid}/reject`),
  };

  // ── Inventory ─────────────────────────────────────────────
  const inventory = {
    getAll:    ()       => get('/inventory'),
    getById:   (id)     => get(`/inventory/${id}`),
    create:    (d)      => post('/inventory', d),
    update:    (id, d)  => put(`/inventory/${id}`, d),
    restock:   (id, qty)=> patch(`/inventory/${id}/restock`, { quantity: qty }),
    delete:    (id)     => del(`/inventory/${id}`),
  };

  // ── Stock Exports ─────────────────────────────────────────
  const stockExports = {
    getAll:    ()       => get('/stock-exports'),
    getMine:   ()       => get('/stock-exports/my'),
    getById:   (id)     => get(`/stock-exports/${id}`),
    create:    (d)      => post('/stock-exports', d),
    approve:   (id)     => patch(`/stock-exports/${id}/approve`),
    dispatch:  (id)     => patch(`/stock-exports/${id}/dispatch`),
    reject:    (id)     => patch(`/stock-exports/${id}/reject`),
  };

  // ── Payments ──────────────────────────────────────────────
  const payments = {
    getAll:    ()       => get('/payments'),
    getMine:   ()       => get('/payments/my'),
    getById:   (id)     => get(`/payments/${id}`),
    create:    (d)      => post('/payments', d),
    updateStatus:(id,s) => patch(`/payments/${id}/status`, { status: s }),
  };

  // ── Notifications ─────────────────────────────────────────
  const notifications = {
    getAll:    ()       => get('/notifications'),
    getMine:   ()       => get('/notifications/my'),
    markRead:  (id)     => patch(`/notifications/${id}/read`),
    markAllRead: ()     => patch('/notifications/read-all'),
    send:      (d)      => post('/notifications/send', d),
    delete:    (id)     => del(`/notifications/${id}`),
  };

  // ── Dashboard Stats ───────────────────────────────────────
  const dashboard = {
    userStats:   () => get('/dashboard/user'),
    adminStats:  () => get('/dashboard/admin'),
    summary:     () => get('/dashboard/summary'),  // Full summary: totalUsers, totalOrders, activeTenders, totalRevenue, revenueByMonth, ordersByStatus
  };

  // ── Public (no auth required) ────────────────────────────
  const publicApi = {
    getOpenTenders:    () => get('/public/tenders'),
    getAvailableInventory: () => get('/public/inventory'),
  };

  return { auth, users, shipOrders, shipRepairs, tenders, inventory, stockExports, payments, notifications, dashboard, public: publicApi };
})();
