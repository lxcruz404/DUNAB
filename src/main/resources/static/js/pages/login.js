function pageLogin(){

  return `
  
  <div class="auth-bg">

    <div class="auth-card">

      <div class="auth-form">

        ${logo()}

        <div class="auth-h">
          Bienvenido al Sistema de Estudiantes
        </div>

        <div class="auth-p">
          Ingresa tus credenciales UNAB para acceder a todos los servicios
        </div>

        <div class="field">
          <label>Usuario</label>

          <input
            id="inp-user"
            type="text"
            placeholder="Usuario"
          >
        </div>

        <div class="field">

          <label>Contraseña</label>

          <input
            id="inp-pass"
            type="password"
            placeholder="••••••••"
          >

        </div>

        <button
          class="btn btn-maroon"
          onclick="doLogin()"
        >
          Iniciar Sesión
        </button>

        <div class="link-row">

          ¿Olvidó su contraseña?

          &nbsp;

          <a onclick="goScreen('register')">
            Crear Cuenta
          </a>

        </div>

      </div>

      <div class="auth-img">

        <div class="auth-img-icon">🏛️</div>

        <div class="auth-img-title">UNAB</div>

        <div class="auth-img-sub">
          Universidad Autónoma<br>
          de Bucaramanga
        </div>

      </div>

    </div>

  </div>

  `;
}

async function doLogin(){

  const studentCode = document.getElementById('inp-user').value;

  const password = document.getElementById('inp-pass').value;

  try {

    const response = await fetch('/api/users/login', {

      method: 'POST',

      headers: {
        'Content-Type': 'application/json'
      },

      body: JSON.stringify({
        studentCode,
        password
      })

    });

    const user = await response.json();

    if(user && user.id){

      localStorage.setItem('user', JSON.stringify(user));

      state.screen = 'app';

      state.page = 'dashboard';

      render();

    } else {

      alert('Usuario o contraseña incorrectos');

    }

  } catch(error){

    console.error(error);

    alert('Error de conexión con el servidor');
  }
}