const STUDENT = {
  name:'Maria Suarez',
  firstName:'Maria',
  code:'U0017890',
  career:'Ing. Sistemas',
  email:'msuarez312@unab.edu.co',
  phone:'3202707294',
  city:'Bucaramanga',
  modality:'Presencial',
  credits:'19 Créditos (4 Asignatura)',
  gpa:'3.0 / 5.0'
};

const DUNAB = {
  total:1000,
  acc:650,
  miss:350,

  tx:[
    {
      date:'2026-05-15',
      desc:'Transferencia de Gonzalo Mejia',
      cat:'Transferencia',
      val:50,
      type:'ingreso'
    },

    {
      date:'2026-05-01',
      desc:'Compra en Cafetería UNAB',
      cat:'Alimentación',
      val:35,
      type:'egreso'
    },

    {
      date:'2026-04-20',
      desc:'Clase Lab Telecomunicaciones',
      cat:'Actividades',
      val:150,
      type:'ingreso'
    },

    {
      date:'2026-04-05',
      desc:'Bus Ruta 1 UNAB',
      cat:'Transporte',
      val:20,
      type:'egreso'
    },

    {
      date:'2026-03-28',
      desc:'Encuentro UNAB Cultura',
      cat:'Actividades',
      val:80,
      type:'ingreso'
    },

    {
      date:'2026-03-15',
      desc:'Material de estudio',
      cat:'Educación',
      val:25,
      type:'egreso'
    },
  ]
};

const GOALS = [
  {
    id:1,
    name:'Compra Play 5',
    target:3500000,
    curr:350000,
    color:'#8B5CF6'
  },

  {
    id:2,
    name:'Ahorro Matrícula',
    target:6500000,
    curr:3250000,
    color:'#1AAE6F'
  },

  {
    id:3,
    name:'Regalo Para Mamá',
    target:300000,
    curr:290000,
    color:'#06B6D4'
  },

  {
    id:4,
    name:'Caja Álbum Del Mundial',
    target:500000,
    curr:22000,
    color:'#F59E0B'
  },
];

const CAL = [
  {
    day:'Lunes',
    date:'19 Mayo',

    events:[
      {
        title:'Transporte',
        type:'gasto',
        amount:-40,
        rem:'7:00 am (semanal)'
      }
    ],

    total:-40,
    bal:960
  },

  {
    day:'Martes',
    date:'20 Mayo',

    events:[
      {
        title:'Encuentro UNAB',
        type:'ingreso',
        amount:5,
        rem:'3:00 pm (semanal)'
      }
    ],

    total:5,
    bal:960
  },

  {
    day:'Miércoles',
    date:'21 Mayo',

    events:[
      {
        title:'Almuerzo',
        type:'gasto',
        amount:-50,
        rem:'12:30 pm (semanal)'
      }
    ],

    total:-50,
    bal:915
  },

  {
    day:'Jueves',
    date:'22 Mayo',

    events:[
      {
        title:'Pagar fotocopias',
        type:'recordatorio',
        amount:0,
        rem:'9:00 am (único)'
      }
    ],

    total:-25,
    bal:890
  },

  {
    day:'Viernes',
    date:'23 Mayo',

    events:[
      {
        title:'Diner casa',
        type:'ingreso',
        amount:100,
        rem:'6:00 pm (semanal)'
      }
    ],

    total:100,
    bal:990
  },
];

const ACHIEV = [
  {
    title:'El Inicio De Todo',
    desc:'Crea tu cuenta DUNAB y consigue tu primer DUNAB.',
    stars:1,
    max:5,
    trophy:'🥉'
  },

  {
    title:'Un Inicio Emocionante, ¿Un Speedrunner?',
    desc:'Consigue 250 DUNAB en primer semestre.',
    stars:3,
    max:5,
    trophy:'🥇'
  },

  {
    title:'Maestro De Mil Artes',
    desc:'Empieza una segunda titulación y consigue tu primer DUNAB en ella.',
    stars:2,
    max:5,
    trophy:'🥈'
  },

  {
    title:'Pasaste Pero, Aún Hay Más...',
    desc:'Ahorra 1000 DUNABs y completa el requisito básico.',
    stars:3,
    max:5,
    trophy:'🥇'
  },
];