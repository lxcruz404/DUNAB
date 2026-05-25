function withLayout(page, content){

  return `
  
  <div class="app">

    ${sidebar(page)}

    <div class="content">
      ${content}
    </div>

  </div>

  `;
}