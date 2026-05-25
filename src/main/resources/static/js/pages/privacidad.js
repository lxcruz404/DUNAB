function pagePrivacidad(){

  const content = `
  
  <div class="page-title">
    Políticas de Privacidad
  </div>

  <div class="page-sub">
    Lee los términos y condiciones que trae DUNAB
  </div>

  <div class="card">

    <div
      style="
        font-size:17px;
        font-weight:800;
        color:var(--maroon);
        text-align:center;
        margin-bottom:1.25rem
      "
    >
      Términos Para El Tratamiento De Datos Personales
    </div>

    <div style="margin-bottom:1.1rem">

      <div
        style="
          font-weight:800;
          font-size:14px;
          margin-bottom:.45rem
        "
      >
        La UNAB se compromete a:
      </div>

      <p
        style="
          font-size:13.5px;
          color:var(--gray-700);
          line-height:1.75
        "
      >
        La Universidad Autónoma De Bucaramanga (UNAB)
        se compromete a salvaguardar la privacidad de
        la información personal de sus estudiantes.

        Los datos recopilados
        (Financieros, Académicos, Información Personal)
        serán utilizados exclusivamente para fines
        educativos, administrativos y de comunicación interna,
        según lo estipulado por la Ley 1581 de 2012 de Colombia.
      </p>

    </div>

    <div style="margin-bottom:1.1rem">

      <div
        style="
          font-weight:800;
          font-size:14px;
          margin-bottom:.45rem
        "
      >
        Fines Del Tratamiento De Datos:
      </div>

      <div
        style="
          font-size:13.5px;
          color:var(--gray-700);
          line-height:2.2
        "
      >

        <div>
          — Gestión y seguimiento de tu progreso DUNAB
        </div>

        <div>
          — Emisión y certificado de PDF con tus estadísticas
        </div>

        <div>
          — Comunicación y avisos en cuanto a la aplicación
        </div>

      </div>

    </div>

    <p
      style="
        font-size:13px;
        color:var(--gray-500);
        line-height:1.7;
        margin-bottom:1.1rem
      "
    >
      Al utilizar esta aplicación, aceptas que tus datos
      sean tratados de acuerdo a los términos anteriormente mencionados.

      Puedes leer más detalles acerca de nuestras políticas
      de tratamiento de datos descargando el PDF.
    </p>

    <div style="text-align:right">

      <button class="btn btn-maroon btn-sm">
        Exportar PDF →
      </button>

    </div>

  </div>

  `;

  return withLayout('privacidad', content);
}