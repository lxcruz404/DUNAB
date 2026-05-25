function pageMetas(){

  const content = `
  
  <div class="page-title">
    Metas personales de ahorro
  </div>

  <div style="height:.8rem"></div>

  <div class="card">

    <div
      style="
        font-size:16px;
        font-weight:800;
        text-align:center;
        margin-bottom:1.5rem
      "
    >
      Mis Metas de Ahorro
    </div>

    <div class="grid-2">

      ${GOALS.map(g => {

        const p = Math.min(
          100,
          pct(g.curr, g.target)
        );

        return `

        <div
          style="
            border:1.5px solid var(--gray-200);
            border-radius:13px;
            padding:1rem
          "
        >

          <div
            style="
              font-weight:800;
              margin-bottom:.25rem
            "
          >
            Meta #${g.id}: ${g.name}
          </div>

          <div
            style="
              font-size:12px;
              color:var(--gray-500);
              margin-bottom:.85rem
            "
          >
            Ahorra ${fmt(g.target)}
          </div>

          <div
            class="progress"
            style="margin-bottom:.75rem"
          >

            <div
              class="progress-bar"
              style="
                width:${p}%;
                background:${g.color}
              "
            ></div>

          </div>

          <div
            style="
              display:flex;
              justify-content:space-between;
              align-items:center
            "
          >

            <div
              style="
                font-size:12px;
                color:var(--gray-500)
              "
            >
              ${fmt(g.curr)} / ${fmt(g.target)}
            </div>

            <button class="btn btn-maroon btn-sm">
              Ver Detalles
            </button>

          </div>

        </div>

        `;
      }).join('')}

    </div>

    <div
      style="
        text-align:right;
        margin-top:1.1rem
      "
    >

      <button class="btn btn-maroon btn-sm">
        Exportar PDF →
      </button>

    </div>

  </div>

  `;

  return withLayout('metas', content);
}