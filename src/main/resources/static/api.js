// ============================================================
// DUNAB API Client
// ============================================================
const API_BASE = '/api';
const TOKEN_KEY = 'dunab_token';
const USER_KEY  = 'dunab_user';

// ── Auth helpers ─────────────────────────────────────────────
const Auth = {
  getToken:  () => localStorage.getItem(TOKEN_KEY),
  setToken:  (t) => localStorage.setItem(TOKEN_KEY, t),
  removeToken: () => localStorage.removeItem(TOKEN_KEY),
  getUser:   () => { try { return JSON.parse(localStorage.getItem(USER_KEY)); } catch { return null; } },
  setUser:   (u) => localStorage.setItem(USER_KEY, JSON.stringify(u)),
  removeUser: () => localStorage.removeItem(USER_KEY),
  isLoggedIn: () => !!localStorage.getItem(TOKEN_KEY),
  isAdmin:   () => { const u = Auth.getUser(); return u?.role === 'ADMIN'; },
  logout: () => {
    Auth.removeToken(); Auth.removeUser();
    window.location.href = '/login.html';
  }
};

// ── Core fetch ───────────────────────────────────────────────
async function apiRequest(path, options = {}) {
  const headers = { 'Content-Type': 'application/json' };
  const token = Auth.getToken();
  if (token) headers['Authorization'] = 'Bearer ' + token;
  try {
    const res = await fetch(API_BASE + path, { ...options, headers });
    if (res.status === 401) { Auth.removeToken(); Auth.removeUser(); window.location.href = '/login.html'; return null; }
    return await res.json();
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Error de conexión. Verifica que el servidor esté activo.');
  }
}

// ── Route guards ─────────────────────────────────────────────

/** Si ya está logueado, redirige al lugar correcto */
function redirectIfLoggedIn() {
  if (Auth.isLoggedIn()) {
    window.location.href = Auth.isAdmin() ? '/admin.html' : '/dashboard.html';
  }
}

/** Verifica si el JWT está expirado */
function isTokenExpired() {
  const token = Auth.getToken();
  if (!token) return true;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.exp * 1000 < Date.now();
  } catch(e) { return true; }
}

/** Requiere login. Si es admin en página de estudiante → redirige al panel */
function requireAuth() {
  if (!Auth.isLoggedIn() || isTokenExpired()) {
    Auth.removeToken();
    Auth.removeUser();
    window.location.href = '/login.html';
    return null;
  }
  // Si es admin y está en una página de estudiante → panel admin
  const adminPages = ['/admin.html', '/admin-login.html'];
  const isAdminPage = adminPages.some(p => window.location.pathname.endsWith(p.replace('/','')));
  if (Auth.isAdmin() && !isAdminPage) {
    window.location.href = '/admin.html';
    return null;
  }
  return Auth.getUser();
}

/** Solo para el panel admin */
function requireAdmin() {
  if (!Auth.isLoggedIn()) { window.location.href = '/admin-login.html'; return null; }
  if (!Auth.isAdmin())   { window.location.href = '/login.html'; return null; }
  return Auth.getUser();
}

// ── Auth API ─────────────────────────────────────────────────
const AuthAPI = {
  login: async (email, password) => {
    const res = await apiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
    if (res?.success && res.data?.token) {
      Auth.setToken(res.data.token);
      Auth.setUser(res.data.user);
    }
    return res;
  },
  register: async (fullName, email, studentCode, password, career, semester) => {
    const res = await apiRequest('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ fullName, email, studentCode, password, career, semester })
    });
    if (res?.success && res.data?.token) {
      Auth.setToken(res.data.token);
      Auth.setUser(res.data.user);
    }
    return res;
  }
};

// ── Domain APIs ───────────────────────────────────────────────
const UserAPI = {
  getProfile:      () => apiRequest('/users/me'),
  updateProfile:   (fullName, career, semester) => apiRequest('/users/me', { method:'PUT', body: JSON.stringify({fullName, career, semester}) }),
  uploadPhoto:     async (file) => {
    const form = new FormData(); form.append('file', file);
    const token = Auth.getToken();
    const res = await fetch(API_BASE + '/users/me/photo', { method:'POST', headers:{ Authorization:'Bearer '+token }, body: form });
    return res.json();
  },
  getAchievements: () => apiRequest('/users/me/achievements'),
  calculateGrades: (grades) => apiRequest('/users/calculator', { method:'POST', body: JSON.stringify({grades}) }),
};

const TransactionAPI = {
  create:    (amount, category, type, description) => apiRequest('/transactions', { method:'POST', body: JSON.stringify({amount, category, type, description}) }),
  getAll:    (page=0, size=50) => apiRequest(`/transactions?page=${page}&size=${size}`),
  getRecent: () => apiRequest('/transactions/recent'),
  getStats:  () => apiRequest('/transactions/stats'),
};

const EncounterAPI = {
  getAll:    () => apiRequest('/encounters'),
  register:  (id) => apiRequest(`/encounters/${id}/register`, { method:'POST' }),
  collect:   (id) => apiRequest(`/encounters/${id}/collect`,  { method:'POST' }),
};

const GoalAPI = {
  getAll:       () => apiRequest('/goals'),
  create:       (name, description, targetAmount, targetDate) => apiRequest('/goals', { method:'POST', body: JSON.stringify({name, description, targetAmount, targetDate}) }),
  syncProgress: (id) => apiRequest(`/goals/${id}/sync`, { method:'PUT' }),
  delete:       (id) => apiRequest(`/goals/${id}`, { method:'DELETE' }),
};

const RankingAPI = {
  getTop12:    () => apiRequest('/ranking/top12'),
  getMyPosition: () => apiRequest('/ranking/my-position'),
};

// ── UI Helpers ────────────────────────────────────────────────
const UI = {
  toast: (msg, type='info') => {
    const existing = document.getElementById('dunab-toast');
    if (existing) existing.remove();
    const t = document.createElement('div');
    t.id = 'dunab-toast';
    const colors = { success:'#4CAF50', error:'#dc3545', info:'#2196F3', warning:'#E8A020' };
    t.style.cssText = `position:fixed;bottom:24px;right:24px;padding:12px 20px;background:${colors[type]||colors.info};color:#fff;border-radius:12px;font-size:0.88rem;font-weight:600;z-index:9999;box-shadow:0 4px 20px rgba(0,0,0,0.3);max-width:320px;animation:slideIn 0.3s ease`;
    t.textContent = msg;
    if (!document.getElementById('toast-style')) {
      const s = document.createElement('style');
      s.id = 'toast-style';
      s.textContent = '@keyframes slideIn{from{transform:translateX(100%);opacity:0}to{transform:translateX(0);opacity:1}}';
      document.head.appendChild(s);
    }
    document.body.appendChild(t);
    setTimeout(() => t?.remove(), 3500);
  },
  setButtonLoading: (btn, loading) => {
    if (!btn) return;
    if (loading) { btn._orig = btn.textContent; btn.textContent = 'Cargando...'; btn.disabled = true; }
    else { btn.textContent = btn._orig || btn.textContent; btn.disabled = false; }
  },
  formatDate: (d) => {
    if (!d) return '-';
    return new Date(d).toLocaleDateString('es-CO', { day:'numeric', month:'short', year:'numeric' });
  },
  formatDunab: (n) => {
    if (n == null) return '0 D';
    return (Math.round(n * 10) / 10).toLocaleString('es-CO') + ' D';
  }
};

// ── Push Notifications ─────────────────────────────────────
const Notif = {
  permission: false,
  
  async request() {
    if (!('Notification' in window)) return false;
    if (Notification.permission === 'granted') { this.permission = true; return true; }
    if (Notification.permission !== 'denied') {
      const result = await Notification.requestPermission();
      this.permission = result === 'granted';
      return this.permission;
    }
    return false;
  },

  show(title, body, icon) {
    if (!this.permission || Notification.permission !== 'granted') return;
    const n = new Notification(title, {
      body: body,
      icon: icon || '/logo-unab.png',
      badge: '/logo-unab.png',
      tag: 'dunab-chat'
    });
    n.onclick = () => { window.focus(); n.close(); };
    setTimeout(() => n.close(), 5000);
  }
};

// ── Polling for unread messages ─────────────────────────────
let _lastUnread = 0;
async function startUnreadPolling(isAdmin) {
  await Notif.request();
  setInterval(async () => {
    try {
      const endpoint = isAdmin ? '/chat/unread-count' : '/chat/my-conversations';
      const res = await apiRequest(endpoint);
      let count = 0;
      if (isAdmin) {
        count = res?.data || 0;
      } else {
        count = (res?.data || []).reduce((s, c) => s + (c.unreadByStudent || 0), 0);
      }
      if (count > _lastUnread && _lastUnread >= 0) {
        Notif.show(
          'Nuevo mensaje - DUNAB',
          isAdmin ? `Tienes ${count} mensaje(s) de soporte sin leer` : 'Tienes un nuevo mensaje del equipo de soporte',
          '/logo-unab.png'
        );
        // Update nav badge if exists
        const badge = document.getElementById('chat-unread-badge');
        if (badge) {
          badge.textContent = count;
          badge.style.display = count > 0 ? 'inline' : 'none';
        }
      }
      _lastUnread = count;
    } catch(e) { /* silent */ }
  }, 10000); // Check every 10 seconds
}
