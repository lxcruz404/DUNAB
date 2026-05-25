function goScreen(s){

  state.screen = s;

  render();
}

function goPage(p){

  state.page = p;

  render();
}

async function doLogin() {

  const user = $('inp-user').value;
  const pass = $('inp-pass').value;

  try {

    const response = await fetch(
      'http://localhost:8080/api/auth/login',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email: user,
          password: pass
        })
      }
    );

    if(response.ok){

      const userData = await response.json();

      state.user = userData;

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

function doActividad(){

  const lugar = $('act-lugar');
  const fecha = $('act-fecha');

  if(
    !lugar ||
    !lugar.value ||
    !fecha ||
    !fecha.value
  ){
    alert('Por favor completa todos los campos.');
    return;
  }

  state.actSuccess = true;

  render();

  setTimeout(() => {
    state.actSuccess = false;
  }, 4000);
}

render();