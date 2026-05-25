function pageActividad(){

  const successHtml = state.actSuccess

    ? `
    
    <div class="alert-success">
      ✓ Registro simulado exitosamente —
      recibirás confirmación por correo institucional.
    </div>

    `

    : '';

  const content = `
  
  <div class="page-title">
    Registro de Actividad DUNAB
  </div>

  <div style="height:.8rem"></div>

  <div
    class="card"
    style="
      max-width:580px;
      margin:0 auto
    "
  >

    <div
      style="
        text-align:center;
        margin-bottom:1.5rem
      "
    >

      <div
        style="
          font-size:17px;
          font-weight:800;
          margin-bottom:.4rem
        "
      >
        Simulación de Inscripción
      </div>

      <div
        style="
          font-size:13px;
          color:var(--gray-500);
          line-height:1.6
        "
      >
        Completa este formulario para simular tu registro en una actividad. 
        Recuerda que la aprobación de las HL está sujeta a la asistencia.
      </div>

    </div>

    ${successHtml}

    <div class="field">

      <label>
        Seleccione el Lugar/Actividad
      </label>

      <select id="act-lugar">

        <option value="">
          -- Seleccionar --
        </option>

        <option>
          Lab Telecomunicaciones
        </option>

        <option>
          Cafetería UNAB
        </option>

        <option>
          Encuentro UNAB Cultural
        </option>

        <option>
          Biblioteca Central
        </option>

        <option>
          Auditorio Principal
        </option>

      </select>

    </div>

    <div class="field">

      <label>
        Fecha de Participación
      </label>

      <input
        id="act-fecha"
        type="date"
      >

    </div>

    <div class="field">

      <label>
        Tu Código Estudiantil
      </label>

      <input
        id="act-cod"
        type="text"
        value="${STUDENT.code}"
      >

    </div>

    <div class="field">

      <label>
        Correo Institucional
      </label>

      <input
        id="act-mail"
        type="email"
        value="${STUDENT.email}"
      >

    </div>

    <button
      class="btn btn-green"
      onclick="doActividad()"
    >
      Simular Registro
    </button>

  </div>

  `;

  return withLayout('actividad', content);
}