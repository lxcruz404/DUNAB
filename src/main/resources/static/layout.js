// Font Awesome icon helper
const IC = {
  home:    '<i class="fa-solid fa-house"></i>',
  money:   '<i class="fa-solid fa-circle-dollar-to-slot"></i>',
  trophy:  '<i class="fa-solid fa-trophy"></i>',
  target:  '<i class="fa-solid fa-bullseye"></i>',
  notes:   '<i class="fa-solid fa-graduation-cap"></i>',
  ranking: '<i class="fa-solid fa-crown"></i>',
  profile: '<i class="fa-solid fa-user"></i>',
  admin:   '<i class="fa-solid fa-gear"></i>',
  logout:  '<i class="fa-solid fa-right-from-bracket"></i>',
  sun:     '<i class="fa-solid fa-sun"></i>',
  moon:    '<i class="fa-solid fa-moon"></i>',
};

function renderHeader(title, subtitle, user) {
  const avatar = user?.profilePhotoUrl ||
    `https://ui-avatars.com/api/?name=${encodeURIComponent(user?.fullName||'U')}&background=8B1A4A&color=fff&size=80`;
  return `
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <header id="main-header" style="background:var(--header-bg,#fff);padding:14px 36px;display:flex;align-items:center;justify-content:space-between;border-bottom:3px solid #8B1A4A;position:sticky;top:0;z-index:100">
    <div style="display:flex;align-items:center;gap:12px">
      <img src="/logo-unab.png" style="height:48px;object-fit:contain" onerror="this.style.display='none'">
    </div>
    <div style="text-align:center;flex:1;padding:0 20px">
      <div style="font-size:${subtitle?'1rem':'1.3rem'};font-weight:700;color:var(--text-primary,#1a1a1a)">${title||''}</div>
      ${subtitle?`<div style="font-size:0.8rem;color:var(--text-secondary,#666)">${subtitle}</div>`:''}
    </div>
    <div style="display:flex;align-items:center;gap:12px">
      <button onclick="toggleTheme()" id="theme-btn" style="background:var(--input-bg,#f0f0f0);border:1px solid var(--border-color,#ccc);border-radius:20px;padding:6px 14px;cursor:pointer;font-size:0.82rem;color:var(--text-primary,#1a1a1a)">
        ${IC.moon} Oscuro
      </button>
      <img src="${avatar}" style="width:44px;height:44px;border-radius:50%;object-fit:cover;border:2px solid #8B1A4A;cursor:pointer" onclick="window.location='/profile.html'" onerror="this.src='https://ui-avatars.com/api/?name=U&background=8B1A4A&color=fff'">
    </div>
  </header>
  <div style="background:#8B1A4A;height:8px"></div>`;
}

function renderNav(active) {
  const me = Auth.getUser();
  const isAdmin = me?.role === 'ADMIN';
  const pages = [
    {id:'dashboard',    label:'Dashboard',          icon:IC.home,    url:'/dashboard.html'},
    {id:'transactions', label:'Seguimiento Dinero',  icon:IC.money,   url:'/transactions.html'},
    {id:'encounters',   label:'Encuentros',          icon:IC.trophy,  url:'/encounters.html'},
    {id:'goals',        label:'Metas de Ahorro',     icon:IC.target,  url:'/goals.html'},
    {id:'calculator',   label:'Notas',               icon:IC.notes,   url:'/calculator.html'},
    {id:'ranking',      label:'Ranking',             icon:IC.ranking, url:'/ranking.html'},
    {id:'announcements', label:'Anuncios',             icon:'<i class="fa-solid fa-bullhorn"></i>', url:'/announcements.html'},
    {id:'chat',          label:'Soporte <span id="chat-unread-badge" style="display:none;background:#dc3545;color:#fff;border-radius:10px;padding:1px 6px;font-size:0.68rem;font-weight:700;margin-left:2px">0</span>', icon:'<i class="fa-solid fa-headset"></i>', url:'/chat.html'},
    {id:'profile',      label:'Mi Cuenta',           icon:IC.profile, url:'/profile.html'},
  ];
  pages.push({id:'about', label:'Qué es DUNAB', icon:'<i class="fa-solid fa-circle-question"></i>', url:'/about.html'});
  if (isAdmin) pages.push({id:'admin', label:'Admin', icon:IC.admin, url:'/admin.html'});
  return `
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <nav style="background:#8B1A4A;padding:10px 36px;display:flex;gap:4px;flex-wrap:wrap">
    ${pages.map(p=>`<a href="${p.url}" style="padding:7px 14px;border-radius:20px;color:${p.id===active?'#8B1A4A':'rgba(255,255,255,0.85)'};background:${p.id===active?'#fff':'transparent'};text-decoration:none;font-size:0.82rem;font-weight:${p.id===active?'700':'500'};display:flex;align-items:center;gap:6px;white-space:nowrap">${p.icon} ${p.label}</a>`).join('')}
    <a href="#" onclick="Auth.logout()" style="padding:7px 14px;border-radius:20px;color:rgba(255,255,255,0.6);text-decoration:none;font-size:0.82rem;margin-left:auto;display:flex;align-items:center;gap:6px;white-space:nowrap">${IC.logout} Salir</a>
  </nav>`;
}

window.renderHeader = renderHeader;
window.renderNav = renderNav;
