// Capa de acceso al backend. Todas las rutas son relativas: nginx (Docker) o Vite (npm run dev)
// las reenvían al microservicio correcto, así no hay problemas de CORS.

async function request(path, { method = 'GET', body } = {}) {
  let res;
  try {
    res = await fetch(path, {
      method,
      headers: body ? { 'Content-Type': 'application/json' } : undefined,
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new Error('No se pudo conectar con el servidor. Revisa que los servicios estén corriendo.');
  }
  if (res.status === 204) return null;
  const text = await res.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch { /* respuesta sin JSON */ }
  if (!res.ok) {
    let msg = data?.mensaje || data?.message || data?.error;
    if (data?.detalles && typeof data.detalles === 'object') {
      msg = Object.values(data.detalles).join('. ');
    }
    if (res.status === 502 || res.status === 503 || res.status === 504) {
      msg = 'Un servicio no responde todavía. Espera unos segundos e intenta de nuevo.';
    }
    throw new Error(msg || `Error ${res.status}`);
  }
  return data;
}

const q = (obj) => {
  const p = new URLSearchParams();
  Object.entries(obj).forEach(([k, v]) => v !== undefined && v !== null && p.append(k, v));
  return p.toString();
};

export const api = {
  // Restaurantes y menú (8081)
  restaurantes: () => request('/api/restaurantes'),
  crearRestaurante: (d) => request('/api/restaurantes', { method: 'POST', body: d }),
  editarRestaurante: (id, d) => request(`/api/restaurantes/${id}`, { method: 'PUT', body: d }),
  estadoRestaurante: (id, estado) =>
    request(`/api/restaurantes/${id}/estado-operativo?${q({ estado })}`, { method: 'PATCH' }),
  desactivarRestaurante: (id) => request(`/api/restaurantes/${id}/desactivar`, { method: 'PATCH' }),
  menu: (id) => request(`/api/restaurantes/${id}/menu`),

  horarios: (id) => request(`/api/restaurantes/${id}/horarios`),
  crearHorario: (id, d) => request(`/api/restaurantes/${id}/horarios`, { method: 'POST', body: d }),
  borrarHorario: (id, hid) => request(`/api/restaurantes/${id}/horarios/${hid}`, { method: 'DELETE' }),

  categorias: (rid) => request(`/api/restaurantes/${rid}/categorias`),
  crearCategoria: (rid, d) => request(`/api/restaurantes/${rid}/categorias`, { method: 'POST', body: d }),
  editarCategoria: (rid, id, d) => request(`/api/restaurantes/${rid}/categorias/${id}`, { method: 'PUT', body: d }),
  desactivarCategoria: (rid, id) => request(`/api/restaurantes/${rid}/categorias/${id}/desactivar`, { method: 'PATCH' }),

  platosDeCategoria: (cid) => request(`/api/platos/categoria/${cid}`),
  crearPlato: (d) => request('/api/platos', { method: 'POST', body: d }),
  editarPlato: (id, d) => request(`/api/platos/${id}`, { method: 'PUT', body: d }),
  disponibilidadPlato: (id, disponible) =>
    request(`/api/platos/${id}/disponibilidad?${q({ disponible })}`, { method: 'PATCH' }),

  extras: (rid) => request(`/api/restaurantes/${rid}/extras`),
  crearExtra: (rid, d) => request(`/api/restaurantes/${rid}/extras`, { method: 'POST', body: d }),
  editarExtra: (rid, id, d) => request(`/api/restaurantes/${rid}/extras/${id}`, { method: 'PUT', body: d }),
  disponibilidadExtra: (rid, id, disponible) =>
    request(`/api/restaurantes/${rid}/extras/${id}/disponibilidad?${q({ disponible })}`, { method: 'PATCH' }),

  extrasDePlato: (pid) => request(`/api/platos/${pid}/extras`),
  asociarExtra: (pid, eid) => request(`/api/platos/${pid}/extras/${eid}`, { method: 'POST' }),
  quitarExtra: (pid, eid) => request(`/api/platos/${pid}/extras/${eid}`, { method: 'DELETE' }),

  // Pedidos (8082)
  crearPedido: (d) => request('/api/pedidos', { method: 'POST', body: d }),
  pedidosDeCliente: (clienteId) => request(`/api/pedidos?${q({ clienteId })}`),
  pedidosDeRestaurante: (restauranteId) => request(`/api/pedidos?${q({ restauranteId })}`),
  avanzarPedido: (id) => request(`/api/pedidos/${id}/estado`, { method: 'PATCH' }),
  cancelarPedido: (id, canceladoPor, motivo) =>
    request(`/api/pedidos/${id}/cancelar`, { method: 'POST', body: { canceladoPor, motivo: motivo || undefined } }),

  // Notificaciones (8083)
  notificacionesDeCliente: (clienteId) => request(`/api/notificaciones?${q({ clienteId })}`),
};
