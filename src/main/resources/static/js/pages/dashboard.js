function pageDashboard(){

  const p = pct(DUNAB.acc, DUNAB.total);

  const user = state.user;

  const firstName = user.fullName.split(' ')[0];

  const initials =
    user.fullName
      .split(' ')
      .map(n => n[0])
      .join('')
      .substring(0,2)
      .toUpperCase();

  const services = [

    [
      'calendario',
      '📅',
      '#EEF2FF',
      'Calendario',
      'Organiza tus eventos y actividades.'
    ],

    [
      'logros',
      '🏆',
      '#FEF9EC',
      'Logros y progreso',
      'Revisa tu avance y resultados.'
    ],

    [
      'metas',
      '🧮',
      '#F0FDF4',
      'Metas de ahorro',
      'Define y sigue tus objetivos de ahorro.'
    ],

    [
      'dunab',
      '💰',
      '#FFF7ED',
      'Seguimiento dinero',
      'Controla tus ingresos y gastos.'
    ],

  ];

  const content = `
  
  <div
    style="
      display:flex;
      align-items:center;
      justify-content:space-between;
      background:white;
      border-radius:16px;
      padding:1.25rem 1.5rem;
      margin-bottom:1.1rem;
      box-shadow:0 2px 10px rgba(123,26,58,.07)
    "
  >

    <div>

      <div
        style="
          font-size:11px;
          color:var(--gray-500);
          font-weight:700;
          text-transform:uppercase;
          letter-spacing:.5px;
          margin-bottom:.15rem
        "
      >
        Sistema de Estudiantes
      </div>

      <div
        style="
          font-size:21px;
          font-weight:900;
          color:var(--gray-900)
        "
      >
        ¡Hola, ${firstName}! 👋
      </div>

      <div
        style="
          font-size:13px;
          color:var(--gray-500);
          margin-top:.15rem
        "
      >
        Dashboard de servicios académicos y DUNAB
      </div>

    </div>

    <div
      class="avatar"
      style="
        width:50px;
        height:50px;
        font-size:18px
      "
    >
      ${initials}
    </div>

  </div>

  <div
    class="grid-2"
    style="margin-bottom:1.1rem"
  >

    <div class="card">

      <div class="card-title">
        Mi Cuenta
      </div>

      <div
        style="
          font-size:14px;
          color:var(--gray-500);
          margin-bottom:.25rem
        "
      >
        Estudiante UNAB
      </div>

      <div
        style="
          font-size:19px;
          font-weight:900;
          color:var(--gold);
          margin-bottom:1rem
        "
      >
        ${user.studentCode}
      </div>

      <button
        class="btn btn-outline btn-sm"
        onclick="goPage('cuenta')"
      >
        Editar perfil
      </button>

    </div>

    <div class="card">

      <div
        style="
          display:flex;
          align-items:center;
          gap:8px;
          margin-bottom:.75rem
        "
      >

        <span
          style="
            color:var(--green);
            font-size:18px
          "
        >
          ✓
        </span>

        <div
          class="card-title gold"
          style="margin:0"
        >
          Progreso de DUNAB
        </div>

      </div>

      <div
        style="
          display:flex;
          gap:2.5rem;
          margin-bottom:1rem
        "
      >

        <div>

          <div
            style="
              font-size:30px;
              font-weight:900
            "
          >
            ${DUNAB.acc}
          </div>

          <div
            style="
              font-size:11px;
              color:var(--gray-500);
              font-weight:700
            "
          >
            Acumulada
          </div>

        </div>

        <div>

          <div
            style="
              font-size:30px;
              font-weight:900
            "
          >
            ${DUNAB.miss}
          </div>

          <div
            style="
              font-size:11px;
              color:var(--gray-500);
              font-weight:700
            "
          >
            Faltantes
          </div>

        </div>

      </div>

      <div
        class="progress"
        style="margin-bottom:.4rem"
      >
        <div
          class="progress-bar"
          style="width:${p}%"
        ></div>
      </div>

      <div
        style="
          font-size:12px;
          color:var(--green);
          font-weight:700;
          margin-bottom:.85rem
        "
      >
        ${p}% del requisito cumplido.
      </div>

      <button
        class="btn btn-gold btn-sm"
        onclick="goPage('dunab')"
      >
        Ver detalles
      </button>

    </div>

  </div>

  <div class="card">

    <div class="card-title">
      Servicios disponibles
    </div>

    <div class="grid-4">

      ${services.map(([id,ic,bg,name,desc]) => `

        <div
          class="service-card"
          onclick="goPage('${id}')"
        >

          <div
            class="service-icon"
            style="background:${bg}"
          >
            ${ic}
          </div>

          <h3>${name}</h3>

          <p>${desc}</p>

        </div>

      `).join('')}

    </div>

  </div>

  `;

  return withLayout('dashboard', content);
}