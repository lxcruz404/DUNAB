function pageCalendario(){

  const evClass = {
    ingreso:'ev-ingreso',
    gasto:'ev-gasto',
    recordatorio:'ev-record'
  };

  const content = `
  
  <div class="page-title">
    Calendario
  </div>

  <div class="page-sub">
    19 al 23 de mayo 2026
  </div>

  <div class="cal-grid">

    ${CAL.map(d => `

      <div class="cal-col">

        <div class="cal-header">

          <div class="dn">
            ${d.day}
          </div>

          <div class="dd">
            ${d.date}
          </div>

        </div>

        ${d.events.map(ev => `

          <div class="cal-event ${evClass[ev.type]}">

            <div class="ev-title">
              ${ev.title}
            </div>

            <div class="ev-sub">
              Tipo:
              ${
                ev.type.charAt(0).toUpperCase()
                + ev.type.slice(1)
              }
            </div>

            ${
              ev.amount !== 0

              ? `

              <div
                class="ev-sub"
                style="
                  font-weight:700;
                  color:${
                    ev.amount > 0
                    ? 'var(--green)'
                    : 'var(--red)'
                  }
                "
              >
                ${ev.amount > 0 ? '+' : ''}
                ${ev.amount} Dunab
              </div>

              `

              : ''
            }

            <div class="ev-sub">
              📌 ${ev.rem}
            </div>

          </div>

        `).join('')}

        <div class="cal-footer">

          <div
            style="
              color:${
                d.total >= 0
                ? 'var(--green)'
                : 'var(--red)'
              }
            "
          >

            Total:
            ${d.total > 0 ? '+' : ''}
            ${d.total} Dunab

          </div>

          <div
            style="
              color:var(--gray-500);
              font-weight:600;
              margin-top:.2rem
            "
          >
            Saldo: ${d.bal} Dunab
          </div>

        </div>

      </div>

    `).join('')}

  </div>

  `;

  return withLayout('calendario', content);
}