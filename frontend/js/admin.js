/* ============================================================
   BLACK PEARL SHIPYARD – Admin Module (admin.js)
   Full API-wired version — replaces all hardcoded demo arrays
   ============================================================ */

const AdminModule = (() => {

    // ── Init ──────────────────────────────────────────────────
    async function init() {
        if (typeof Auth !== 'undefined') {
            if (!Auth.isLoggedIn()) { window.location.href = 'login.html'; return; }
            if (!Auth.isAdmin()) { window.location.href = 'user-dashboard.html'; return; }
        }

        // Show admin name in topbar / sidebar
        const user = Auth.getUser();
        if (user) {
            const name = user.email ? user.email.split('@')[0] : 'Admin';
            const initial = name.charAt(0).toUpperCase();
            document.querySelectorAll('.user-name').forEach(el => el.textContent = name);
            document.querySelectorAll('.user-avatar').forEach(el => el.textContent = initial);
        }

        await loadDashboardSummary();

        // Start 30s polling
        setInterval(async () => {
            await loadDashboardSummary();
            console.log('[Admin Poll] Dashboard data refreshed');
        }, 30000);
    }

    // ── Dashboard Stats & Charts (API-driven) ─────────────────
    async function loadDashboardStats() {
        try {
            if (!window.API) return;
            const stats = await API.dashboard.adminStats();
            if (!stats) return;
            updateEl('statTotalUsers', stats.totalUsers || 0);
            updateEl('statTotalOrders', stats.totalOrders || 0);
            updateEl('statActiveTenders', stats.activeTenders || 0);
            const rev = stats.monthRevenue ? `₹${(Number(stats.monthRevenue) / 10000000).toFixed(1)}Cr` : '₹0';
            updateEl('statMonthRevenue', rev);
        } catch (e) {
            console.warn('[AdminModule] Could not load stats:', e.message);
        }
    }

    async function loadDashboardSummary() {
        try {
            if (!window.API) return;
            const summary = await API.dashboard.summary();
            if (!summary) return;
            updateEl('statTotalUsers', summary.totalUsers ?? 0);
            updateEl('statTotalOrders', summary.totalOrders ?? 0);
            updateEl('statActiveTenders', summary.activeTenders ?? 0);
            const rev = summary.totalRevenue ? `₹${(Number(summary.totalRevenue) / 100000).toFixed(1)}L` : '₹0';
            updateEl('statMonthRevenue', rev);
            updateChartsWithSummary(summary);
        } catch (e) {
            console.warn('[AdminModule] Could not load summary:', e.message);
        }
    }

    function updateChartsWithSummary(summary) {
        const rCtx = document.getElementById('revenueChart');
        const oCtx = document.getElementById('orderStatusChart');
        if (summary.revenueByMonth && rCtx) {
            const labels = summary.revenueByMonth.map(m => m.month);
            const data = summary.revenueByMonth.map(m => Number(m.revenue) / 100000);
            if (rCtx._chartInstance) {
                rCtx._chartInstance.data.labels = labels;
                rCtx._chartInstance.data.datasets[0].data = data;
                rCtx._chartInstance.update();
            } else {
                rCtx._chartInstance = new Chart(rCtx, {
                    type: 'line',
                    data: {
                        labels,
                        datasets: [{
                            label: 'Revenue (₹ Lakh)',
                            data,
                            borderColor: '#FFD700',
                            backgroundColor: 'rgba(255,215,0,0.1)',
                            tension: 0.4, fill: true, pointRadius: 5,
                            pointBackgroundColor: '#FFD700'
                        }]
                    },
                    options: {
                        responsive: true, maintainAspectRatio: false,
                        plugins: { legend: { labels: { color: '#8fa8c0' } } },
                        scales: {
                            x: { ticks: { color: '#8fa8c0' }, grid: { color: 'rgba(255,255,255,0.05)' } },
                            y: { ticks: { color: '#8fa8c0' }, grid: { color: 'rgba(255,255,255,0.05)' } }
                        }
                    }
                });
            }
        }
        if (summary.ordersByStatus && oCtx) {
            const ob = summary.ordersByStatus;
            const labels = ['Pending', 'Approved', 'In Progress', 'Completed', 'Rejected'];
            const data = [ob.PENDING || 0, ob.APPROVED || 0, ob.IN_PROGRESS || 0, ob.COMPLETED || 0, ob.REJECTED || 0];
            if (oCtx._chartInstance) {
                oCtx._chartInstance.data.labels = labels;
                oCtx._chartInstance.data.datasets[0].data = data;
                oCtx._chartInstance.update();
            } else {
                oCtx._chartInstance = new Chart(oCtx, {
                    type: 'doughnut',
                    data: {
                        labels,
                        datasets: [{
                            data,
                            backgroundColor: ['#ffa500', '#00c896', '#00aced', '#FFD700', '#ff4d6d'],
                            borderWidth: 0
                        }]
                    },
                    options: {
                        responsive: true, maintainAspectRatio: false,
                        plugins: { legend: { labels: { color: '#8fa8c0' } } }
                    }
                });
            }
        }
    }

    function updateEl(id, val) {
        const el = document.getElementById(id);
        if (el) el.textContent = val;
    }

    /** Call after any CRUD to refresh dashboard + current section */
    async function globalSyncAfterCRUD() {
        await loadDashboardSummary();
        const activeSection = document.querySelector('.dash-section.active');
        if (activeSection) {
            const id = activeSection.id.replace('sec-', '');
            onSectionShown(id);
        }
    }

    // ── User Management ───────────────────────────────────────
    let _cachedUsers = [];

    async function loadUsers() {
        try {
            if (!window.API) return;
            const users = await API.users.getAll();
            if (!users) return;
            _cachedUsers = users;
            renderUsersTable(users);
        } catch (e) {
            console.warn('[AdminModule] Could not load users:', e.message);
        }
    }

    function renderUsersTable(users) {
        const tbody = document.getElementById('usersBody');
        if (!tbody) return;
        if (users.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;color:#8fa8c0;padding:24px;">No users found.</td></tr>';
            return;
        }
        tbody.innerHTML = users.map(u => `
      <tr>
        <td>${u.id}</td>
        <td style="font-weight:600;color:#e0e8f0;">${u.firstName} ${u.lastName}</td>
        <td style="color:#8fa8c0;">${u.email}</td>
        <td>${u.department || '-'}</td>
        <td><span style="background:${u.role === 'ADMIN' ? 'rgba(255,77,109,0.15)' : 'rgba(0,172,237,0.1)'};color:${u.role === 'ADMIN' ? '#ff4d6d' : '#00aced'};padding:3px 10px;border-radius:50px;font-size:0.75rem;">${u.role}</span></td>
        <td style="color:#8fa8c0;">${formatDate(u.createdAt)}</td>
        <td><span class="status-badge ${u.active ? 'status-approved' : 'status-rejected'}">${u.active ? 'Active' : 'Inactive'}</span></td>
        <td style="display:flex;gap:6px;">
          <button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;" onclick="AdminModule.openEditUserModal(${u.id})">Edit</button>
          <button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;color:${u.active ? '#ff4d6d' : '#00c896'};" onclick="AdminModule.toggleUser(${u.id}, ${u.active})">${u.active ? 'Deactivate' : 'Activate'}</button>
        </td>
      </tr>`).join('');
    }

    function filterUsers(q) {
        if (!q) { renderUsersTable(_cachedUsers); return; }
        const filtered = _cachedUsers.filter(u =>
            (u.firstName + ' ' + u.lastName).toLowerCase().includes(q.toLowerCase()) ||
            u.email.toLowerCase().includes(q.toLowerCase())
        );
        renderUsersTable(filtered);
    }

    function openEditUserModal(id) {
        const user = _cachedUsers.find(u => u.id === id);
        if (!user) return;
        const modal = document.getElementById('editUserModal');
        if (!modal) return;
        modal.querySelector('#editUserId').value = user.id;
        modal.querySelector('#editUserFirstName').value = user.firstName || '';
        modal.querySelector('#editUserLastName').value = user.lastName || '';
        modal.querySelector('#editUserDept').value = user.department || '';
        modal.querySelector('#editUserRole').value = user.role || 'USER';
        document.getElementById('editUserModal').classList.add('open');
    }

    async function saveEditUser(e) {
        e.preventDefault();
        const modal = document.getElementById('editUserModal');
        const id = parseInt(modal.querySelector('#editUserId').value);
        const payload = {
            firstName: modal.querySelector('#editUserFirstName').value,
            lastName: modal.querySelector('#editUserLastName').value,
            department: modal.querySelector('#editUserDept').value,
            role: modal.querySelector('#editUserRole').value
        };
        try {
            await API.users.update(id, payload);
            showToast('User updated successfully!', 'success');
            modal.classList.remove('open');
            globalSyncAfterCRUD();
        } catch (e) { showToast('Error updating user', 'error'); }
    }

    async function toggleUser(id, currentlyActive) {
        try {
            if (!window.API) { showToast('User status updated! (Demo)', 'success'); return; }
            if (currentlyActive) {
                await API.users.deactivate(id);
                showToast('User deactivated.', 'warning');
            } else {
                await API.users.activate(id);
                showToast('User activated.', 'success');
            }
            globalSyncAfterCRUD();
        } catch (e) { showToast('Error updating user status', 'error'); }
    }

    async function addUser(e) {
        e.preventDefault();
        const f = e.target;
        const payload = {
            firstName: f.querySelector('[name="firstName"]').value,
            lastName: f.querySelector('[name="lastName"]').value,
            email: f.querySelector('[name="email"]').value,
            phone: f.querySelector('[name="phone"]')?.value || '',
            department: f.querySelector('[name="department"]').value,
            role: f.querySelector('[name="role"]').value,
            password: f.querySelector('[name="password"]').value,
        };
        try {
            await API.auth.register(payload);
            showToast('User added successfully!', 'success');
            document.getElementById('addUserModal').classList.remove('open');
            f.reset();
            globalSyncAfterCRUD();
        } catch (e) { showToast(e.message || 'Error adding user', 'error'); }
    }

    // ── Ship Orders ───────────────────────────────────────────
    async function loadAllOrders() {
        try {
            if (!window.API) return;
            const orders = await API.shipOrders.getAll();
            if (!orders) return;
            window.__adminOrders = orders;
            const tbody = document.getElementById('ordersBody');
            if (!tbody) return;
            if (orders.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;color:#8fa8c0;padding:24px;">No ship orders found.</td></tr>';
                return;
            }
            tbody.innerHTML = orders.map(o => `
        <tr>
          <td style="color:#FFD700;font-weight:600;">#SO-${String(o.id).padStart(3, '0')}</td>
          <td>${o.userName || o.userEmail || '-'}</td>
          <td>${o.shipType}</td>
          <td>${o.tonnage ? o.tonnage + ' DWT' : '-'}</td>
          <td>${o.material || '-'}</td>
          <td style="color:#8fa8c0;">${formatDate(o.createdAt)}</td>
          <td>${statusBadge(o.status)}</td>
          <td style="display:flex;gap:6px;">
            ${o.status === 'PENDING'
                    ? `<button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;color:#00c896;" onclick="AdminModule.approveOrder(${o.id})">Approve</button>
                   <button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;color:#ff4d6d;" onclick="AdminModule.rejectOrder(${o.id})">Reject</button>`
                    : `<button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;">View</button>`}
          </td>
        </tr>`).join('');
        } catch (e) { console.warn('[AdminModule] Could not load orders:', e.message); }
    }

    async function approveOrder(id) {
        try {
            if (!window.API) { showToast('Order approved! (Demo)', 'success'); return; }
            await API.shipOrders.approve(id);
            showToast('Ship order approved!', 'success');
            globalSyncAfterCRUD();
        } catch (e) { showToast('Error approving order', 'error'); }
    }

    async function rejectOrder(id) {
        try {
            if (!window.API) { showToast('Order rejected! (Demo)', 'warning'); return; }
            await API.shipOrders.reject(id);
            showToast('Ship order rejected.', 'warning');
            globalSyncAfterCRUD();
        } catch (e) { showToast('Error rejecting order', 'error'); }
    }

    // ── Ship Repairs ──────────────────────────────────────────
    async function loadRepairs() {
        try {
            if (!window.API) return;
            const repairs = await API.shipRepairs.getAll();
            if (!repairs) return;
            window.__adminRepairs = repairs;
            const tbody = document.getElementById('repairsBody');
            if (!tbody) return;
            if (repairs.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;color:#8fa8c0;padding:24px;">No repair requests found.</td></tr>';
                return;
            }
            tbody.innerHTML = repairs.map(r => `
        <tr>
          <td style="color:#FFD700;font-weight:600;">#REP-${String(r.id).padStart(3, '0')}</td>
          <td>${r.userEmail || '-'}</td>
          <td>${r.vesselName || '-'}</td>
          <td>${r.issueType || '-'}</td>
          <td style="color:${r.priority === 'HIGH' || r.priority === 'EMERGENCY' ? '#ff4d6d' : r.priority === 'MEDIUM' ? '#ffa500' : '#00c896'};">${r.priority || '-'}</td>
          <td style="color:#8fa8c0;">${formatDate(r.createdAt)}</td>
          <td>${statusBadge(r.status)}</td>
          <td>
            <select class="form-control" style="font-size:0.75rem;padding:4px 8px;width:auto;" onchange="AdminModule.updateRepairStatus(${r.id}, this.value)">
              ${['PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'].map(s => `<option value="${s}" ${r.status === s ? 'selected' : ''}>${s.replace('_', ' ')}</option>`).join('')}
            </select>
          </td>
        </tr>`).join('');
        } catch (e) { console.warn('[AdminModule] Could not load repairs:', e.message); }
    }

    async function updateRepairStatus(id, status) {
        try {
            await API.shipRepairs.updateStatus(id, status);
            showToast('Repair status updated!', 'success');
            globalSyncAfterCRUD();
        } catch (e) { showToast('Error updating repair status', 'error'); }
    }

    // ── Tenders ───────────────────────────────────────────────
    async function loadTenders() {
        try {
            if (!window.API) return;
            const tenders = await API.tenders.getAll();
            if (!tenders) return;
            window.__adminTenders = tenders;
            const tbody = document.getElementById('tendersBody');
            if (!tbody) return;
            if (tenders.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;color:#8fa8c0;padding:24px;">No tenders found.</td></tr>';
                return;
            }
            tbody.innerHTML = tenders.map(t => `
        <tr>
          <td style="color:#FFD700;font-weight:600;">${t.tenderNo}</td>
          <td>${t.title}</td>
          <td>${t.category}</td>
          <td>₹${t.value} Cr</td>
          <td style="text-align:center;">${t.applicationCount || 0}</td>
          <td style="color:#8fa8c0;">${formatDate(t.closingDate)}</td>
          <td>${statusBadge(t.status)}</td>
          <td style="display:flex;gap:6px;">
            <button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;" onclick="AdminModule.editTender(${t.id})">Edit</button>
            ${t.status !== 'CLOSED' && t.status !== 'AWARDED'
                    ? `<button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;color:#ff4d6d;" onclick="AdminModule.closeTender(${t.id})">Close</button>`
                    : ''}
          </td>
        </tr>`).join('');
        } catch (e) { console.warn('[AdminModule] Could not load tenders:', e.message); }
    }

    async function createTender(e) {
        e.preventDefault();
        const f = e.target;
        const payload = {
            tenderNo: 'BPS-' + Date.now(),
            title: f.querySelector('[name="title"]').value,
            category: f.querySelector('[name="category"]').value,
            value: parseFloat(f.querySelector('[name="value"]').value),
            closingDate: f.querySelector('[name="closingDate"]').value,
            description: f.querySelector('[name="description"]')?.value || ''
        };
        try {
            await API.tenders.create(payload);
            showToast('Tender published successfully!', 'success');
            document.getElementById('addTenderModal').classList.remove('open');
            f.reset();
            globalSyncAfterCRUD();
        } catch (e) { showToast(e.message || 'Error creating tender', 'error'); }
    }

    function editTender(id) {
        showToast('Tender edit coming in next version.', 'info');
    }

    async function closeTender(id) {
        try {
            if (!window.API) { showToast('Tender closed! (Demo)', 'warning'); return; }
            await API.tenders.close(id);
            showToast('Tender closed.', 'warning');
            globalSyncAfterCRUD();
        } catch (e) { showToast('Error closing tender', 'error'); }
    }

    // ── Inventory ─────────────────────────────────────────────
    async function loadInventory() {
        try {
            if (!window.API) return;
            const items = await API.inventory.getAll();
            if (!items) return;
            window.__adminInventory = items;
            const tbody = document.getElementById('inventoryBody');
            if (!tbody) return;
            if (items.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;color:#8fa8c0;padding:24px;">No inventory items found.</td></tr>';
                return;
            }
            tbody.innerHTML = items.map(i => `
        <tr>
          <td style="color:#FFD700;font-weight:600;">${i.itemCode}</td>
          <td>${i.name}</td>
          <td>${i.category}</td>
          <td style="font-weight:600;color:${i.quantity < 5 ? '#ffa500' : '#e0e8f0'};">${i.quantity}</td>
          <td>${i.unit}</td>
          <td style="color:#FFD700;">₹${i.unitPrice ? Number(i.unitPrice).toLocaleString('en-IN') : '-'}</td>
          <td>${statusBadge(i.status)}</td>
          <td style="display:flex;gap:6px;">
            <button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;" onclick="AdminModule.restockItem(${i.id})">Restock</button>
            <button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;color:#ff4d6d;" onclick="AdminModule.deleteInventoryItem(${i.id})">Delete</button>
          </td>
        </tr>`).join('');
        } catch (e) { console.warn('[AdminModule] Could not load inventory:', e.message); }
    }

    async function addInventoryItem(e) {
        e.preventDefault();
        const f = e.target;
        const payload = {
            itemCode: 'INV-' + Date.now(),
            name: f.querySelector('[name="name"]').value,
            category: f.querySelector('[name="category"]').value,
            quantity: parseInt(f.querySelector('[name="quantity"]').value),
            unit: f.querySelector('[name="unit"]').value,
            unitPrice: parseFloat(f.querySelector('[name="unitPrice"]').value) || null
        };
        try {
            await API.inventory.create(payload);
            showToast('Inventory item added!', 'success');
            document.getElementById('addItemModal').classList.remove('open');
            f.reset();
            globalSyncAfterCRUD();
        } catch (e) { showToast(e.message || 'Error adding item', 'error'); }
    }

    async function restockItem(id) {
        const qty = prompt('Enter quantity to add:');
        if (!qty || isNaN(qty) || parseInt(qty) <= 0) return;
        try {
            if (!window.API) { showToast(`Restocked +${qty} units! (Demo)`, 'success'); return; }
            await API.inventory.restock(id, parseInt(qty));
            showToast(`Restocked +${qty} units!`, 'success');
            globalSyncAfterCRUD();
        } catch (e) { showToast('Error restocking item', 'error'); }
    }

    async function deleteInventoryItem(id) {
        if (!confirm('Delete this inventory item?')) return;
        try {
            await API.inventory.delete(id);
            showToast('Item deleted.', 'warning');
            globalSyncAfterCRUD();
        } catch (e) { showToast('Error deleting item', 'error'); }
    }

    // ── Stock Exports ─────────────────────────────────────────
    async function loadStockExports() {
        try {
            if (!window.API) return;
            const exports = await API.stockExports.getAll();
            if (!exports) return;
            window.__adminExports = exports;
            const tbody = document.getElementById('exportsBody');
            if (!tbody) return;
            if (exports.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;color:#8fa8c0;padding:24px;">No stock export requests found.</td></tr>';
                return;
            }
            tbody.innerHTML = exports.map(se => `
        <tr>
          <td style="color:#FFD700;font-weight:600;">#SE-${String(se.id).padStart(3, '0')}</td>
          <td>${se.userEmail || '-'}</td>
          <td>${se.itemName || '-'}</td>
          <td>${se.quantity || '-'}</td>
          <td>${se.purpose || '-'}</td>
          <td style="color:#8fa8c0;">${formatDate(se.createdAt)}</td>
          <td>${statusBadge(se.status)}</td>
          <td style="display:flex;gap:6px;">
            ${se.status === 'PENDING'
                    ? `<button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;color:#00c896;" onclick="AdminModule.approveExport(${se.id})">Approve</button>
                   <button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;color:#ff4d6d;" onclick="AdminModule.rejectExport(${se.id})">Reject</button>`
                    : se.status === 'APPROVED'
                        ? `<button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;color:#FFD700;" onclick="AdminModule.dispatchExport(${se.id})">Dispatch</button>`
                        : `<button class="btn-glass" style="font-size:0.75rem;padding:5px 10px;">View</button>`}
          </td>
        </tr>`).join('');
        } catch (e) { console.warn('[AdminModule] Could not load exports:', e.message); }
    }

    async function approveExport(id) { await _exportAction(id, API.stockExports.approve, 'Export approved!', 'success'); }
    async function rejectExport(id) { await _exportAction(id, API.stockExports.reject, 'Export rejected.', 'warning'); }
    async function dispatchExport(id) { await _exportAction(id, API.stockExports.dispatch, 'Export dispatched!', 'success'); }

    async function _exportAction(id, fn, msg, type) {
        try { await fn(id); showToast(msg, type); globalSyncAfterCRUD(); }
        catch (e) { showToast('Action failed', 'error'); }
    }

    // ── Payments ──────────────────────────────────────────────
    async function loadPayments() {
        try {
            if (!window.API) return;
            const payments = await API.payments.getAll();
            if (!payments) return;
            window.__adminPayments = payments;
            const tbody = document.getElementById('paymentsBody');
            if (!tbody) return;
            if (payments.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#8fa8c0;padding:24px;">No payment records found.</td></tr>';
                return;
            }
            tbody.innerHTML = payments.map(p => `
        <tr>
          <td style="color:#FFD700;font-weight:600;">#PAY-${String(p.id).padStart(3, '0')}</td>
          <td>${p.userName || p.userEmail || '-'}</td>
          <td style="color:#8fa8c0;">${p.paymentRef || '-'}</td>
          <td style="color:#00c896;font-weight:600;">₹${p.amount ? Number(p.amount).toLocaleString('en-IN') : '-'}</td>
          <td>${p.method || '-'}</td>
          <td style="color:#8fa8c0;">${formatDate(p.createdAt)}</td>
          <td style="display:flex;gap:6px;">${statusBadge(p.status)}
            ${p.status === 'PENDING'
                    ? `<button class="btn-glass" style="font-size:0.7rem;padding:4px 8px;color:#00c896;" onclick="AdminModule.completePayment(${p.id})">Mark Done</button>`
                    : ''}
          </td>
        </tr>`).join('');
        } catch (e) { console.warn('[AdminModule] Could not load payments:', e.message); }
    }

    async function completePayment(id) {
        try {
            await API.payments.updateStatus(id, 'COMPLETED');
            showToast('Payment marked as completed!', 'success');
            globalSyncAfterCRUD();
        } catch (e) { showToast('Error updating payment', 'error'); }
    }

    // ── Notifications ─────────────────────────────────────────
    async function sendNotification(e) {
        e.preventDefault();
        const f = e.target;
        const title = f.querySelector('[name="notifTitle"]')?.value || '';
        const message = f.querySelector('[name="notifMsg"]')?.value || '';
        const type = f.querySelector('[name="notifType"]')?.value || 'INFO';

        if (!title.trim() || !message.trim()) {
            showToast('Please fill in all fields', 'error');
            return;
        }

        try {
            if (!window.API) { showToast('Notification sent! (Demo)', 'success'); f.reset(); return; }
            await API.notifications.send({ title, message, type });
            showToast('Notification sent successfully!', 'success');
            f.reset();
        } catch (e) { showToast('Error sending notification: ' + (e.message || 'Unknown error'), 'error'); }
    }

    async function loadNotifications() {
        try {
            if (!window.API) return;
            const notifs = await API.notifications.getAll();
            if (!notifs || notifs.length === 0) {
                const notifList = document.querySelector('.notif-list');
                if (notifList) {
                    notifList.innerHTML = '<div style="padding:20px;text-align:center;color:#8fa8c0;">No notifications sent yet.</div>';
                }
                return;
            }
        } catch (e) { console.warn('[AdminModule] Could not load notifications:', e.message); }
    }

    // ── Section Loader dispatcher ─────────────────────────────
    function onSectionShown(id) {
        const loaders = {
            'users': loadUsers,
            'ship-orders': loadAllOrders,
            'repairs': loadRepairs,
            'tenders': loadTenders,
            'inventory': loadInventory,
            'stock-exports': loadStockExports,
            'payments': loadPayments,
            'notifications': loadNotifications,
        };
        if (loaders[id]) loaders[id]();
    }

    // ── Helpers ───────────────────────────────────────────────
    function statusBadge(status) {
        const map = {
            'PENDING': 'status-pending',
            'APPROVED': 'status-approved',
            'IN_PROGRESS': 'status-active',
            'COMPLETED': 'status-approved',
            'REJECTED': 'status-rejected',
            'CANCELLED': 'status-rejected',
            'DISPATCHED': 'status-approved',
            'PROCESSING': 'status-active',
            'OPEN': 'status-approved',
            'CLOSING_SOON': 'status-pending',
            'CLOSED': 'status-rejected',
            'AWARDED': 'status-approved',
            'ACTIVE': 'status-approved',
            'INACTIVE': 'status-rejected',
            'AVAILABLE': 'status-approved',
            'LOW_STOCK': 'status-pending',
            'OUT_OF_STOCK': 'status-rejected',
            'FAILED': 'status-rejected',
            'REFUNDED': 'status-pending',
        };
        const cls = map[status?.toUpperCase()] || 'status-pending';
        const label = status?.replace(/_/g, ' ') || 'Unknown';
        return `<span class="status-badge ${cls}">${label}</span>`;
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        return new Date(dateStr).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
    }

    function showToast(msg, type = 'info') {
        const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };
        const container = document.getElementById('toastContainer');
        if (!container) return;
        const t = document.createElement('div');
        t.className = `toast ${type}`;
        t.innerHTML = `<span class="toast-icon">${icons[type]}</span><span class="toast-msg">${msg}</span><button class="toast-close" onclick="this.parentElement.remove()">✕</button>`;
        container.appendChild(t);
        setTimeout(() => t.remove(), 4000);
    }

    return {
        init, loadDashboardStats, loadDashboardSummary, loadUsers, filterUsers, openEditUserModal, saveEditUser, toggleUser, addUser,
        loadAllOrders, approveOrder, rejectOrder,
        loadRepairs, updateRepairStatus,
        loadTenders, createTender, editTender, closeTender,
        loadInventory, addInventoryItem, restockItem, deleteInventoryItem,
        loadStockExports, approveExport, rejectExport, dispatchExport,
        loadPayments, completePayment,
        sendNotification, loadNotifications, onSectionShown, statusBadge, formatDate
    };
})();

// Auto-init on admin dashboard
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', AdminModule.init);
} else {
    AdminModule.init();
}
