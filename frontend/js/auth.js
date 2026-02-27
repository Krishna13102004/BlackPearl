/* ============================================================
   BLACK PEARL SHIPYARD – Authentication Module (auth.js)
   Handles login, register, session management, JWT, routing
   ============================================================ */

const Auth = (() => {

    // ── Session helpers ───────────────────────────────────────
    // ── Session helpers ───────────────────────────────────────
    function parseJwt(token) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
        } catch (e) {
            return null;
        }
    }

    function saveSession(data) {
        localStorage.setItem('token', data.token);
        const decoded = parseJwt(data.token);
        if (decoded) {
            localStorage.setItem('role', decoded.role);
            localStorage.setItem('department', decoded.department || '');
            localStorage.setItem('userId', decoded.userId);
            localStorage.setItem('email', decoded.sub);
        } else if (data.user) {
            localStorage.setItem('role', data.user.role);
            localStorage.setItem('department', data.user.department || '');
            localStorage.setItem('userId', data.user.id);
            localStorage.setItem('email', data.user.email);
        }
    }

    function getUser() {
        return {
            id: localStorage.getItem('userId'),
            email: localStorage.getItem('email'),
            role: localStorage.getItem('role'),
            department: localStorage.getItem('department')
        };
    }
    function getToken() { return localStorage.getItem('token'); }
    function getRole() { return localStorage.getItem('role'); }
    function getDepartment() { return localStorage.getItem('department') || ''; }
    function isLoggedIn() { return !!getToken(); }
    function isAdmin() { return getRole() === 'ADMIN'; }

    function logout() {
        localStorage.clear();
        window.location.href = 'login.html';
    }

    // ── Route guard ───────────────────────────────────────────
    function requireAuth(adminOnly = false) {
        if (!isLoggedIn()) { window.location.href = 'login.html'; return false; }
        if (adminOnly && !isAdmin()) { window.location.href = 'user-dashboard.html'; return false; }
        return true;
    }

    // ── Department-based sidebar nav hiding (USER role) ───────
    // Mapping: department → array of section IDs to SHOW
    const DEPT_ACCESS = {
        'ENGINEERING': ['dashboard', 'ship-orders', 'repairs', 'inventory', 'stock-exports'],
        'OPERATIONS': ['dashboard', 'ship-orders', 'repairs', 'stock-exports'],
        'FINANCE': ['dashboard', 'payments'],
        'PROCUREMENT': ['dashboard', 'inventory', 'stock-exports', 'tenders'],
        'ADMINISTRATION': ['dashboard', 'users', 'ship-orders', 'repairs', 'tenders', 'inventory', 'stock-exports', 'payments', 'notifications'],
        'QUALITY_CONTROL': ['dashboard', 'repairs', 'ship-orders'],
        'SAFETY': ['dashboard', 'repairs'],
        'OTHER': ['dashboard'],
    };

    function applyDeptNavFilter() {
        if (isAdmin()) return; // Admin sees everything
        const dept = getDepartment().toUpperCase();
        const allowed = DEPT_ACCESS[dept] || ['dashboard'];
        document.querySelectorAll('.nav-item[data-section]').forEach(item => {
            const section = item.getAttribute('data-section');
            item.style.display = allowed.includes(section) ? '' : 'none';
        });
    }

    // ── Login (called from login.html) ────────────────────────
    async function login(email, password, selectedRole) {
        try {
            const data = await API.auth.login({ email, password });
            if (selectedRole === 'ADMIN' && data.user.role !== 'ADMIN') {
                return { success: false, message: 'Access denied. You are not an admin.' };
            }
            saveSession(data);

            // Role-based redirection
            if (data.user.role === 'ADMIN') {
                window.location.href = 'admin-dashboard.html';
            } else {
                window.location.href = 'user-dashboard.html';
            }

            return { success: true, role: data.user.role };
        } catch (err) {
            return { success: false, message: err.message || 'Invalid email or password' };
        }
    }

    // ── Register ──────────────────────────────────────────────
    async function register(data) {
        try {
            await API.auth.register(data);
            return { success: true };
        } catch (err) {
            return { success: false, message: err.message || 'Registration failed' };
        }
    }

    // ── Demo login fallback (no backend) ─────────────────────
    function demoLogin(email, password, selectedRole) {
        const demoUsers = [
            { email: 'admin@blackpearl.com', password: 'admin123', role: 'ADMIN', name: 'Admin User', department: 'Administration' },
            { email: 'eng@blackpearl.com', password: 'user123', role: 'USER', name: 'Arjun Engineer', department: 'Engineering' },
            { email: 'finance@blackpearl.com', password: 'user123', role: 'USER', name: 'Priya Finance', department: 'Finance' },
            { email: 'user@blackpearl.com', password: 'user123', role: 'USER', name: 'John Doe', department: 'Operations' },
        ];
        const match = demoUsers.find(u => u.email === email && u.password === password);
        if (!match) return { success: false, message: 'Invalid email or password' };
        if (selectedRole === 'ADMIN' && match.role !== 'ADMIN') {
            return { success: false, message: 'Access denied. You are not an admin.' };
        }
        saveSession({
            token: 'demo-token-' + Date.now(),
            user: { id: 1, name: match.name, email: match.email, role: match.role, department: match.department }
        });
        return { success: true, role: match.role };
    }

    // ── Password strength ─────────────────────────────────────
    function checkPasswordStrength(password) {
        let score = 0;
        if (password.length >= 8) score++;
        if (password.length >= 12) score++;
        if (/[A-Z]/.test(password)) score++;
        if (/[0-9]/.test(password)) score++;
        if (/[^A-Za-z0-9]/.test(password)) score++;
        const levels = ['', 'Weak', 'Fair', 'Good', 'Strong', 'Very Strong'];
        const colors = ['', '#ff4d6d', '#ffa500', '#FFD700', '#00c896', '#00c896'];
        return { score, label: levels[score] || 'Very Strong', color: colors[score] || '#00c896' };
    }

    // ── Toast helper ──────────────────────────────────────────
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
        login, register, demoLogin, checkPasswordStrength, showToast,
        logout, requireAuth, isLoggedIn, isAdmin, getUser, getToken,
        getRole, getDepartment, applyDeptNavFilter, saveSession
    };
})();

// Expose showToast globally for inline handlers
function showToast(msg, type) { Auth.showToast(msg, type || 'info'); }
