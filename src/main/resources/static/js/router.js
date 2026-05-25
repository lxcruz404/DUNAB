const pageMap = {

  dashboard: pageDashboard,
  cuenta: pageCuenta,
  dunab: pageDunab,
  metas: pageMetas,
  calendario: pageCalendario,
  actividad: pageActividad,
  logros: pageLogros,
  privacidad: pagePrivacidad,

};

function render(){

  const app = $('app');

  if(state.screen === 'login'){
    app.innerHTML = pageLogin();
    return;
  }

  if(state.screen === 'register'){
    app.innerHTML = pageRegister();
    return;
  }

  const fn = pageMap[state.page] || pageDashboard;

  app.innerHTML = fn();
}