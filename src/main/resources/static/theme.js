const THEME_KEY = 'dunab_theme';

// Aplicar ANTES de pintar la página — sin parpadeo
const _saved = localStorage.getItem(THEME_KEY) || 'light';
document.documentElement.setAttribute('data-theme', _saved);

function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme);
  localStorage.setItem(THEME_KEY, theme);
  const btn = document.getElementById('theme-btn');
  if (btn) btn.textContent = theme === 'dark' ? '☀️ Claro' : '🌙 Oscuro';
}

function toggleTheme() {
  const current = localStorage.getItem(THEME_KEY) || 'light';
  applyTheme(current === 'light' ? 'dark' : 'light');
}

function initTheme() {
  const saved = localStorage.getItem(THEME_KEY) || 'light';
  applyTheme(saved);
}

window.toggleTheme = toggleTheme;
window.initTheme = initTheme;