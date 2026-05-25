function pageLogros(){

  const content = `
  
  <div class="page-title">
    Tus Logros y Progreso
  </div>

  <div class="page-sub">
    Revisa tus insignias
  </div>

  ${ACHIEV.map(a => `

    <div class="ach-card">

      <div class="ach-info">

        <div class="ach-title">
          ${a.title}
        </div>

        <div class="ach-desc">
          ${a.desc}
        </div>

        <div class="stars">

          <span
            style="
              font-size:11.5px;
              color:var(--gray-500);
              font-weight:700;
              margin-right:4px
            "
          >
            Dificultad:
          </span>

          ${Array.from({length:a.max}).map((_,i)=>`

            <span
              style="
                color:${
                  i < a.stars
                  ? 'var(--gold)'
                  : 'var(--gray-200)'
                }
              "
            >
              ★
            </span>

          `).join('')}

        </div>

      </div>

      <div style="font-size:42px">
        ${a.trophy}
      </div>

    </div>

  `).join('')}

  `;

  return withLayout('logros', content);
}