const $ = id => document.getElementById(id);

const fmt = n =>
  new Intl.NumberFormat('es-CO',{
    style:'currency',
    currency:'COP',
    maximumFractionDigits:0
  }).format(n);

const pct = (a,b) => Math.round((a/b)*100);