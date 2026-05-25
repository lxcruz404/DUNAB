# 🔌 Guía de Integración — Conectar tu frontend existente al backend

Si ya tienes páginas HTML con diseño propio, sigue estos pasos para conectarlas al backend real.

## Paso 1: Agregar api.js a cada página

En el `<head>` o antes de tu script, agrega:
```html
<script src="/api.js"></script>
```

## Paso 2: Proteger páginas que requieren login

Al inicio de cada página privada (dashboard, perfil, etc.):
```javascript
const user = requireAuth(); // Redirige a /login.html si no hay sesión
```

En páginas de login/registro:
```javascript
redirectIfLoggedIn(); // Redirige a /dashboard.html si ya está logueado
```

## Paso 3: Reemplazar el formulario de login

Cambia tu handler de submit por:
```javascript
document.getElementById('tu-form-login').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        const res = await AuthAPI.login(
            document.getElementById('email').value,
            document.getElementById('password').value
        );
        if (res.success) {
            window.location.href = '/dashboard.html'; // o tu página principal
        } else {
            alert(res.message); // o muestra en tu elemento de error
        }
    } catch(err) {
        alert(err.message);
    }
});
```

## Paso 4: Mostrar el saldo real

```javascript
// Al cargar cualquier página
const profileRes = await UserAPI.getProfile();
if (profileRes?.data) {
    const balance = profileRes.data.dunabBalance;
    // Actualiza todos los elementos con clase .dunab-balance
    document.querySelectorAll('.dunab-balance').forEach(el => {
        el.textContent = UI.formatDunab(balance);
    });
    // O actualiza un elemento específico:
    document.getElementById('mi-saldo').textContent = UI.formatDunab(balance);
}
```

## Paso 5: Historial de transacciones real

```javascript
// Reemplaza tu array simulado por:
const res = await TransactionAPI.getRecent();
const transacciones = res.data; // Array de objetos TransactionDTO

// Cada transacción tiene:
// tx.id, tx.amount, tx.type ('INGRESO'|'GASTO'), tx.category, 
// tx.description, tx.transactionDate, tx.balanceAfter
```

## Paso 6: Registrar una transacción real

```javascript
// Reemplaza tu lógica de simulación por:
const res = await TransactionAPI.create(
    150,           // monto
    'ALIMENTACION', // categoría
    'GASTO',       // 'INGRESO' o 'GASTO'
    'Almuerzo en cafetería' // descripción
);
if (res.success) {
    console.log('Nuevo saldo:', res.data.balanceAfter);
}
```

## Paso 7: Encuentros reales

```javascript
// Cargar encuentros
const res = await EncounterAPI.getAll();
const encuentros = res.data;
// Cada encuentro: id, title, type, location, encounterDate, 
// dunabReward, status, registrationCount, userRegistered, dunabCollected

// Inscribirse
await EncounterAPI.register(encuentroId);

// Cobrar DUNAB
await EncounterAPI.collect(encuentroId);
```

## Paso 8: Ranking Top 12

```javascript
const res = await RankingAPI.getTop12();
const top12 = res.data;
// Cada entrada: position, userId, fullName, studentCode, career, dunabBalance
```

## Paso 9: Metas de ahorro

```javascript
// Ver metas
const res = await GoalAPI.getAll();

// Crear meta (máx recomendado: 10.000 DUNAB por el período académico)
await GoalAPI.create('Meta semestral', 'Mi meta 2024', 10000, '2024-11-30');

// Sincronizar progreso con el saldo actual
await GoalAPI.syncProgress(goalId);
```

## Paso 10: Foto de perfil

```javascript
// En el input file
document.getElementById('foto-input').addEventListener('change', async (e) => {
    const file = e.target.files[0];
    const res = await UserAPI.uploadPhoto(file);
    if (res.success) {
        document.getElementById('mi-avatar').src = res.data; // URL de la foto
    }
});
```

## Manejo de errores (patrón recomendado)

```javascript
try {
    const res = await TransactionAPI.create(...);
    if (res.success) {
        UI.toast('¡Éxito!', 'success');
        // actualiza la UI
    } else {
        UI.toast(res.message, 'error');
    }
} catch (err) {
    UI.toast(err.message, 'error'); // Error de red, etc.
}
```

## Funciones de UI disponibles (en api.js)

```javascript
UI.toast('Mensaje', 'success');     // success | error | info | warning
UI.formatDunab(1500.50);            // → "1.500,50 D"
UI.formatDate('2024-03-15T10:30'); // → "15 mar 2024"
UI.formatDateTime('...');           // → "15 mar 2024 10:30"
UI.setButtonLoading(btn, true);     // Deshabilita y pone "Cargando..."
UI.setButtonLoading(btn, false);    // Restaura el botón
```
