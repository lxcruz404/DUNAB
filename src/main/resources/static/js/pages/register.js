function pageRegister(){

  return `
  
  <div class="auth-bg">

    <div class="auth-card">

      <div class="auth-form">

        ${logo()}

        <div class="auth-h">
          Crear cuenta
        </div>

        <div class="auth-p">
          Completa todos tus datos para activar tu acceso
        </div>

        <div class="field">
          <label>Nombre completo</label>

          <input
            id="reg-name"
            type="text"
            placeholder="Nombre completo"
          >
        </div>

        <div class="field">

          <label>Documento de identidad</label>

          <input
            id="reg-document"
            type="text"
            placeholder="1234567890"
          >

        </div>

        <div class="field">

          <label>Código Estudiantil</label>

          <input
            id="reg-code"
            type="text"
            placeholder="U00......"
          >

        </div>

        <div class="field">

          <label>Correo Institucional</label>

          <input
            id="reg-email"
            type="email"
            placeholder="usuario@unab.edu.co"
          >

        </div>

        <div class="field">

          <label>Contraseña</label>

          <input
            id="reg-pass"
            type="password"
            placeholder="••••••••"
          >

        </div>

        <button
          class="btn btn-maroon"
          onclick="registerUser()"
        >
          Crear Cuenta
        </button>

        <div class="link-row">

          <a onclick="goScreen('login')">
            ← Volver al inicio de sesión
          </a>

        </div>

      </div>

      <div class="auth-img">

        <div class="auth-img-icon">🎓</div>

        <div class="auth-img-title">
          ¡Únete a la familia UNAB!
        </div>

        <div class="auth-img-sub">
          Accede a todos los servicios<br>
          académicos y DUNAB
        </div>

      </div>

    </div>

  </div>

  `;
}

async function registerUser(){

  const fullName = document.getElementById('reg-name').value;

  const documentId = document.getElementById('reg-document').value;

  const studentCode = document.getElementById('reg-code').value;

  const email = document.getElementById('reg-email').value;

  const password = document.getElementById('reg-pass').value;

  try {

    const response = await fetch('/api/users/register', {

      method: 'POST',

      headers: {
        'Content-Type': 'application/json'
      },

      body: JSON.stringify({
        fullName,
        documentId,
        studentCode,
        email,
        password
      })

    });

    if(response.ok){

      alert('Usuario registrado correctamente');

      goScreen('login');

    } else {

      alert('Error registrando usuario');
    }

  } catch(error){

    console.error(error);

    alert('Error de conexión');
  }
}