function pageDunab(){

  const p = pct(DUNAB.acc, DUNAB.total);

  const content = `
  
  <div class="page-title">
    Seguimiento de Dinero UNAB (DUNAB)
  </div>

  <div style="height:.8rem"></div>

  <div
    class="card"
    style="margin-bottom:1rem"
  >

    <div
      style="
        font-size:15px;
        font-weight:800;
        text-align:center;
        margin-bottom:1.25rem
      "
    >
      Mi Estado Actual
    </div>

    <div
      class="grid-4"
      style="margin-bottom:1rem"
    >

      <div class="stat">
        <div class="stat-n">1.000</div>
        <div class="stat-l">Requisito Total</div>
      </div>

      <div class="stat">
        <div class="stat-n">${DUNAB.acc}</div>
        <div class="stat-l">Acumuladas</div>
      </div>

      <div class="stat">
        <div class="stat-n">${DUNAB.miss}</div>
        <div class="stat-l">Faltantes</div>
      </div>

      <div class="stat">
        <div class="stat-n">${p}%</div>
        <div class="stat-l">Progreso General</div>
      </div>

    </div>

    <div
      class="progress"
      style="
        height:16px;
        margin-bottom:.5rem
      "
    >

      <div
        class="progress-bar"
        style="width:${p}%"
      ></div>

    </div>

  </div>

  <div class="card">

    <div class="card-title">
      Historial de transacciones
    </div>

    <table>

      <thead>

        <tr>
          <th>Fecha</th>
          <th>Descripción</th>
          <th>Categoría</th>
          <th>DUNAB</th>
          <th>Movimiento</th>
        </tr>

      </thead>

      <tbody>

        ${DUNAB.tx.map(t => `

          <tr>

            <td style="color:var(--gray-500)">
              ${t.date}
            </td>

            <td style="font-weight:600">
              ${t.desc}
            </td>

            <td>
              <span class="badge badge-maroon">
                ${t.cat}
              </span>
            </td>

            <td style="font-weight:700">
              ${t.val}
            </td>

            <td>

              <span
                class="badge ${
                  t.type === 'ingreso'
                  ? 'badge-green'
                  : 'badge-red'
                }"
              >
                ${
                  t.type === 'ingreso'
                  ? 'Ingreso'
                  : 'Egreso'
                }
              </span>

            </td>

          </tr>

        `).join('')}

      </tbody>

    </table>

  </div>

  `;

  return withLayout('dunab', content);
}