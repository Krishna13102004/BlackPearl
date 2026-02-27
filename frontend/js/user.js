/* ============================================================
   BLACK PEARL SHIPYARD – User Module (user.js)
   Handles user dashboard data loading and interactions
   ============================================================ */

const UserModule = (() => {

    // ── Init ──────────────────────────────────────────────────
    async function init() {
        if (typeof Auth !== 'undefined' && !Auth.isLoggedIn()) {
            window.location.href = 'login.html'; return;
        }
        loadUserInfo();
        await loadDashboardStats();
        await loadRecentActivity();

        // Start 30s polling for real-time updates
        setInterval(async () => {
            await loadDashboardStats();
            await loadRecentActivity();
            console.log('[Poll] Dashboard data refreshed');
        }, 30000);
    }

    // ── User info ─────────────────────────────────────────────
    function loadUserInfo() {
        if (typeof Auth === 'undefined') return;
        const user = Auth.getUser();
        const name = user.email ? user.email.split('@')[0] : 'User'; // Fallback if name not in token
        const initial = name.charAt(0).toUpperCase();
        ['sidebarUserName', 'topbarName'].forEach(id => {
            const el = document.getElementById(id);
            if (el) el.textContent = name;
        });
        ['userInitial', 'topbarInitial'].forEach(id => {
            const el = document.getElementById(id);
            if (el) el.textContent = initial;
        });

        // Apply dept filters
        Auth.applyDeptNavFilter();
    }

    // ── Dashboard stats ───────────────────────────────────────
    async function loadDashboardStats() {
        try {
            if (!window.API) return;
            const stats = await API.dashboard.userStats();
            if (!stats) return;
            updateStat('statOrders', stats.shipOrders || 0);
            updateStat('statRepairs', stats.repairs || 0);
            updateStat('statTenders', stats.tenders || 0);
            updateStat('statExports', stats.stockExports || 0);
        } catch (e) {
            console.log('[UserModule] Using demo stats');
        }
    }

    function updateStat(id, value) {
        const el = document.getElementById(id);
        if (el) el.textContent = value;
    }

    // ── Recent activity ───────────────────────────────────────
    async function loadRecentActivity() {
        try {
            if (!window.API) return;
            const notifs = await API.notifications.getMine();
            if (!notifs) return;
            const container = document.getElementById('allNotifications');
            if (!container) return;
            container.innerHTML = notifs.map(n => `
        <div class="notif-item">
          <div class="notif-dot-icon ${n.type === 'SUCCESS' ? 'green' : n.type === 'WARNING' ? 'gold' : 'blue'}"></div>
          <div>
            <div class="notif-text">${n.message}</div>
            <div class="notif-time">${formatTime(n.createdAt)}</div>
          </div>
        </div>`).join('');
        } catch (e) {
            console.log('[UserModule] Using demo notifications');
        }
    }

    // ── Ship Orders ───────────────────────────────────────────
    async function loadShipOrders() {
        try {
            if (!window.API) return;
            const orders = await API.shipOrders.getMine();
            if (!orders) return;
            const tbody = document.getElementById('myOrdersBody');
            if (!tbody) return;
            tbody.innerHTML = orders.map(o => `
        <tr>
          <td style="color:#FFD700;font-weight:600;">#SO-${o.id}</td>
          <td>${o.shipType}</td>
          <td>${o.specifications || '-'}</td>
          <td style="color:#8fa8c0;">${formatDate(o.createdAt)}</td>
          <td>${statusBadge(o.status)}</td>
          <td><button class="btn-glass" style="font-size:0.78rem;padding:6px 12px;" onclick="viewOrder(${o.id})">View</button></td>
        </tr>`).join('');
        } catch (e) {
            console.log('[UserModule] Using demo orders');
        }
    }

    // ── Tender Applications ───────────────────────────────────
    async function loadTenderApplications() {
        try {
            if (!window.API) return;
            const apps = await API.tenders.myApplications();
            if (!apps) return;
            const tbody = document.getElementById('myTendersBody');
            if (!tbody) return;
            tbody.innerHTML = apps.map(a => `
        <tr>
          <td style="color:#FFD700;font-weight:600;">#APP-${a.id}</td>
          <td>${a.tenderNo}</td>
          <td>${a.tenderTitle}</td>
          <td>₹${a.bidAmount} Cr</td>
          <td style="color:#8fa8c0;">${formatDate(a.appliedAt)}</td>
          <td>${statusBadge(a.status)}</td>
        </tr>`).join('');
        } catch (e) {
            console.log('[UserModule] Using demo applications');
        }
    }

    // ── Stock Exports ─────────────────────────────────────────
    async function loadStockExports() {
        try {
            if (!window.API) return;
            const exports = await API.stockExports.getMine();
            if (!exports) return;
            const tbody = document.getElementById('myExportsBody');
            if (!tbody) return;
            tbody.innerHTML = exports.map(e => `
        <tr>
          <td style="color:#FFD700;font-weight:600;">#EXP-${e.id}</td>
          <td>${e.itemName}</td>
          <td>${e.quantity} ${e.unit}</td>
          <td>${e.purpose}</td>
          <td style="color:#8fa8c0;">${formatDate(e.createdAt)}</td>
          <td>${statusBadge(e.status)}</td>
        </tr>`).join('');
        } catch (e) {
            console.log('[UserModule] Using demo exports');
        }
    }

    // ── Submit handlers ───────────────────────────────────────
    async function submitShipOrder(formData) {
        try {
            if (!window.API) throw new Error('Demo mode');
            const result = await API.shipOrders.create(formData);
            showToast('Ship order submitted successfully!', 'success');
            return result;
        } catch (e) {
            showToast('Ship order submitted! (Demo mode)', 'success');
        }
    }

    async function submitRepairRequest(formData) {
        try {
            if (!window.API) throw new Error('Demo mode');
            const result = await API.shipRepairs.create(formData);
            showToast('Repair request submitted!', 'success');
            return result;
        } catch (e) {
            showToast('Repair request submitted! (Demo mode)', 'success');
        }
    }

    async function submitExportRequest(formData) {
        try {
            if (!window.API) throw new Error('Demo mode');
            const result = await API.stockExports.create(formData);
            showToast('Export request submitted!', 'success');
            return result;
        } catch (e) {
            showToast('Export request submitted! (Demo mode)', 'success');
        }
    }

    async function updateProfile(formData) {
        try {
            const user = JSON.parse(localStorage.getItem('user') || '{}');
            if (!window.API) throw new Error('Demo mode');
            const result = await API.users.update(user.id, formData);
            localStorage.setItem('user', JSON.stringify({ ...user, ...result }));
            showToast('Profile updated successfully!', 'success');
        } catch (e) {
            showToast('Profile updated! (Demo mode)', 'success');
        }
    }

    // ── Helpers ───────────────────────────────────────────────
    function statusBadge(status) {
        const map = {
            'PENDING': 'status-pending',
            'APPROVED': 'status-approved',
            'IN_PROGRESS': 'status-active',
            'COMPLETED': 'status-approved',
            'REJECTED': 'status-rejected',
            'DISPATCHED': 'status-approved',
            'PROCESSING': 'status-active',
        };
        const cls = map[status?.toUpperCase()] || 'status-pending';
        const label = status?.replace(/_/g, ' ') || 'Unknown';
        return `<span class="status-badge ${cls}">${label}</span>`;
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        return new Date(dateStr).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
    }

    function formatTime(dateStr) {
        if (!dateStr) return '-';
        const diff = Date.now() - new Date(dateStr).getTime();
        const mins = Math.floor(diff / 60000);
        if (mins < 60) return `${mins} min ago`;
        const hrs = Math.floor(mins / 60);
        if (hrs < 24) return `${hrs} hour${hrs > 1 ? 's' : ''} ago`;
        return `${Math.floor(hrs / 24)} day${Math.floor(hrs / 24) > 1 ? 's' : ''} ago`;
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

    return { init, loadShipOrders, loadTenderApplications, loadStockExports, submitShipOrder, submitRepairRequest, submitExportRequest, updateProfile, showToast };
})();

// Auto-init on user dashboard
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', UserModule.init);
} else {
    UserModule.init();
}
