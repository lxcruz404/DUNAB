function logo(dark=false){

  return `
  
  <div class="logo${dark ? ' logo-dark' : ''}">

    <div class="logo-box">
      ${icon('home')}
    </div>

    <div>
      <div class="logo-univ">Universidad</div>
      <div class="logo-name">unab</div>
    </div>

  </div>

  `;
}