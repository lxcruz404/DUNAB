function navBtn(id, ic, label, active){

  return `
  
  <div
    class="nav-item${active ? ' active' : ''}"
    onclick="goPage('${id}')"
  >

    ${icon(ic)}

    <span>${label}</span>

  </div>

  `;
}

function sidebar(active){

  const items = [

    ['dashboard','home','Dashboard'],
    ['cuenta','user','Mi Cuenta'],
    ['dunab','dollar','DUNAB'],
    ['metas','target','Metas de Ahorro'],
    ['calendario','calendar','Calendario'],
    ['actividad','activity','Actividad DUNAB'],
    ['logros','award','Logros'],
    ['privacidad','shield','Privacidad'],

  ];

  return `
  
  <div class="sidebar">

    <div class="sidebar-top">
      ${logo(true)}
    </div>

    <nav class="sidebar-nav">

      ${items.map(([id,ic,lbl]) =>
        navBtn(id, ic, lbl, active === id)
      ).join('')}

    </nav>

    <div class="sidebar-bottom">

      <div class="user-pill">

        <div class="avatar">MS</div>

        <div>
          <div class="avatar-name">
            ${STUDENT.firstName}
          </div>

          <div class="avatar-role">
            ${STUDENT.career}
          </div>
        </div>

      </div>

      <div
        class="nav-item"
        onclick="goScreen('login')"
        style="margin:.1rem 0 0"
      >

        ${icon('logout')}

        <span>Cerrar sesión</span>

      </div>

    </div>

  </div>

  `;
}