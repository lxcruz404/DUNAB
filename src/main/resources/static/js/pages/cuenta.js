function pageCuenta(){



  const p = pct(DUNAB.acc, DUNAB.total);

  const user = state.user;

  const initials =
    user.fullName
      .split(' ')
      .map(n => n[0])
      .join('')
      .substring(0,2)
      .toUpperCase();

  const content = `
  
  <div class="page-title">
    Mi Cuenta
  </div>

  <div style="height:.8rem"></div>

  <div
    class="grid-2"
    style="align-items:start"
  >

    <div
      style="
        display:flex;
        flex-direction:column;
        gap:1rem
      "
    >

      <div
        class="card"
        style="text-align:center"
      >

        <div
          class="avatar"
          style="
            width:80px;
            height:80px;
            font-size:28px;
            margin:0 auto 1rem
          "
        >
          ${initials}
        </div>

        <div
          style="
            font-size:18px;
            font-weight:800
          "
        >
          ${user.fullName}
        </div>

        <div
          style="
            font-size:14px;
            color:var(--maroon);
            font-weight:700;
            margin-bottom:.85rem
          "
        >
          Estudiante UNAB
        </div>

        <div
          style="
            display:flex;
            gap:.5rem;
            justify-content:center
          "
        >

          <span class="badge badge-green">
            Activo
          </span>

          <button class="btn btn-gold btn-sm">
            Cambiar foto
          </button>

        </div>

      </div>

      <div class="card">

        <div class="card-title gold">
          Contacto
        </div>

        <div class="info-row">

          <div class="info-icon">✉️</div>

          <div>

            <div class="info-label">
              Correo UNAB
            </div>

            <div class="info-val">
              ${user.email}
            </div>

          </div>

        </div>

        <div class="info-row">

          <div class="info-icon">🪪</div>

          <div>

            <div class="info-label">
              Documento
            </div>

            <div class="info-val">
              ${user.documentId}
            </div>

          </div>

        </div>

      </div>

    </div>

    <div
      style="
        display:flex;
        flex-direction:column;
        gap:1rem
      "
    >

      <div class="card">

        <div
          style="
            display:flex;
            align-items:center;
            gap:8px;
            margin-bottom:.85rem
          "
        >

          <span style="color:var(--green)">
            ✓
          </span>

          <div
            class="card-title gold"
            style="margin:0"
          >
            Seguimiento DUNAB
          </div>

        </div>

        <div
          style="
            font-size:13px;
            font-weight:700;
            margin-bottom:.5rem
          "
        >
          DUNAB Acumulado
        </div>

        <div
          class="progress"
          style="margin-bottom:.6rem"
        >
          <div
            class="progress-bar"
            style="width:${p}%"
          ></div>
        </div>

        <div
          style="
            font-size:12.5px;
            color:var(--gray-500);
            margin-bottom:.85rem
          "
        >
          ${DUNAB.acc} de ${DUNAB.total} DUNAB acumulados (${p}%)
        </div>

        <button
          class="btn btn-gold btn-sm"
          onclick="goPage('dunab')"
        >
          Ver detalles
        </button>

      </div>

      <div class="card">

        <div
          style="
            display:flex;
            align-items:center;
            gap:8px;
            margin-bottom:1rem
          "
        >

          <span style="font-size:18px">
            📚
          </span>

          <div
            class="card-title gold"
            style="margin:0"
          >
            Información académica
          </div>

        </div>

        <div class="grid-2">

          <div>

            <div
              style="
                font-size:11px;
                color:var(--gray-500);
                font-weight:700;
                margin-bottom:.2rem
              "
            >
              Código estudiantil
            </div>

            <div style="font-weight:700">
              ${user.studentCode}
            </div>

          </div>

          <div>

            <div
              style="
                font-size:11px;
                color:var(--gray-500);
                font-weight:700;
                margin-bottom:.2rem
              "
            >
              Estado
            </div>

            <div style="font-weight:700">
              Activo
            </div>

          </div>

        </div>

      </div>

    </div>

  </div>

  `;

  return withLayout('cuenta', content);
}