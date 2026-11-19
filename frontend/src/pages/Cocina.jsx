import { useCallback, useEffect, useMemo, useState } from 'react';
import { api } from '../api.js';
import { useSession } from '../session.jsx';
import { useTopics, usePolling } from '../realtime.js';
import { Empty, ErrorBox, ItemsPedido } from '../components/ui.jsx';
import { ESTADOS, cancelable, fecha, money } from '../format.js';

const COLUMNAS = ['RECIBIDO', 'EN_PREPARACION', 'LISTO', 'ENTREGADO', 'CANCELADO'];

export default function Cocina() {
  const s = useSession();
  const [restaurantes, setRestaurantes] = useState([]);
  const [filtro, setFiltro] = useState('todos');
  const [pedidos, setPedidos] = useState(null);
  const [error, setError] = useState('');
  const [ocupado, setOcupado] = useState(null);

  // El empleado ve los pedidos de todos los clientes: se juntan los de cada restaurante
  const cargar = useCallback(async (lista = restaurantes) => {
    if (!lista.length) return;
    try {
      const res = await Promise.all(lista.map((r) => api.pedidosDeRestaurante(r.id)));
      setPedidos(res.flat());
      setError('');
    } catch (e) { setError(e.message); }
  }, [restaurantes]);

  useEffect(() => {
    api.restaurantes()
      .then((rs) => { setRestaurantes(rs); if (!rs.length) setPedidos([]); return cargar(rs); })
      .catch((e) => setError(e.message));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useTopics(restaurantes.map((r) => `/topic/restaurantes/${r.id}`), (m) => {
    s.toast(`${m.titulo}${m.cuerpo ? ' — ' + m.cuerpo : ''}`, 'ok');
    cargar();
  });
  usePolling(() => cargar(), 10000);

  const nombres = useMemo(() => Object.fromEntries(restaurantes.map((r) => [r.id, r.nombre])), [restaurantes]);
  const visibles = (pedidos || []).filter((p) => filtro === 'todos' || String(p.restauranteId) === filtro);

  async function avanzar(p) {
    setOcupado(p.id);
    try { await api.avanzarPedido(p.id); await cargar(); } catch (e) { setError(e.message); } finally { setOcupado(null); }
  }
  async function cancelar(p) {
    const motivo = window.prompt('Motivo de la cancelación (se avisa al cliente)');
    if (motivo === null) return;
    setOcupado(p.id);
    try { await api.cancelarPedido(p.id, 'RESTAURANTE', motivo); await cargar(); } catch (e) { setError(e.message); } finally { setOcupado(null); }
  }

  return (
    <>
      <h1 className="titulo">Pedidos de todos los clientes</h1>
      <div className="filtros">
        <button className={`chip-btn ${filtro === 'todos' ? 'on' : ''}`} onClick={() => setFiltro('todos')}>Todos los restaurantes</button>
        {restaurantes.map((r) => (
          <button key={r.id} className={`chip-btn ${filtro === String(r.id) ? 'on' : ''}`} onClick={() => setFiltro(String(r.id))}>{r.nombre}</button>
        ))}
      </div>
      <ErrorBox error={error} />
      {!pedidos && !error && <p className="muted">Cargando pedidos…</p>}
      {pedidos && restaurantes.length === 0 && <Empty titulo="No hay restaurantes" texto="Un administrador debe crear uno primero." />}
      {pedidos && restaurantes.length > 0 && (
        <div className="kanban">
          {COLUMNAS.map((est) => {
            const col = visibles.filter((p) => p.estado === est);
            return (
              <section key={est} className={`col c-${est}`}>
                <h2>{ESTADOS[est].label} <span>{col.length}</span></h2>
                {col.length === 0 && <p className="muted vacio">Sin pedidos</p>}
                {col.map((p) => (
                  <article key={p.id} className="tarjeta">
                    <header>
                      <b>#{p.id}</b>
                      <small>{fecha(p.fechaCreacion)}</small>
                    </header>
                    <small className="muted">Cliente #{p.clienteId} · {nombres[p.restauranteId]}</small>
                    <ItemsPedido pedido={p} />
                    <div className="tot">{money(p.total)}</div>
                    {p.motivoCancelacion && <div className="nota">Motivo: {p.motivoCancelacion}</div>}
                    {(ESTADOS[est].siguiente || cancelable(est)) && (
                      <div className="acciones">
                        {ESTADOS[est].siguiente && (
                          <button className="btn primary sm" disabled={ocupado === p.id} onClick={() => avanzar(p)}>{ESTADOS[est].accion}</button>
                        )}
                        {cancelable(est) && (
                          <button className="btn danger sm" disabled={ocupado === p.id} onClick={() => cancelar(p)}>Cancelar</button>
                        )}
                      </div>
                    )}
                  </article>
                ))}
              </section>
            );
          })}
        </div>
      )}
    </>
  );
}
