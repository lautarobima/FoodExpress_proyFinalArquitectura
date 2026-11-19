import { useCallback, useEffect, useState } from 'react';
import { api } from '../api.js';
import { useSession } from '../session.jsx';
import { useTopics, usePolling } from '../realtime.js';
import { Empty, ErrorBox, EstadoBadge, ItemsPedido, Progreso } from '../components/ui.jsx';
import { cancelable, fecha, money } from '../format.js';

export default function MisPedidos() {
  const s = useSession();
  const [pedidos, setPedidos] = useState(null);
  const [nombres, setNombres] = useState({});
  const [error, setError] = useState('');

  const cargar = useCallback(() => {
    api.pedidosDeCliente(s.clienteId).then(setPedidos).catch((e) => setError(e.message));
  }, [s.clienteId]);

  useEffect(() => {
    cargar();
    api.restaurantes().then((rs) => setNombres(Object.fromEntries(rs.map((r) => [r.id, r.nombre])))).catch(() => {});
  }, [cargar]);

  // Tiempo real: cada notificación al cliente muestra un aviso y recarga la lista
  useTopics([`/topic/clientes/${s.clienteId}`], (m) => {
    s.toast(`${m.titulo}${m.cuerpo ? ' — ' + m.cuerpo : ''}`, 'ok');
    cargar();
  });
  usePolling(cargar, 15000);

  async function cancelar(p) {
    const motivo = window.prompt('¿Por qué cancelas el pedido? (opcional)');
    if (motivo === null) return;
    try {
      await api.cancelarPedido(p.id, 'CLIENTE', motivo);
      s.toast(`Pedido #${p.id} cancelado`);
      cargar();
    } catch (e) { setError(e.message); }
  }

  return (
    <>
      <h1 className="titulo">Mis pedidos</h1>
      <ErrorBox error={error} />
      {!pedidos && !error && <p className="muted">Cargando…</p>}
      {pedidos?.length === 0 && <Empty titulo="Aún no has pedido nada" texto="Cuando hagas un pedido, aparecerá aquí con su estado en vivo." />}
      <div className="lista">
        {pedidos?.map((p) => (
          <article key={p.id} className="pedido">
            <header>
              <div>
                <h2>Pedido #{p.id}</h2>
                <small className="muted">{nombres[p.restauranteId] || `Restaurante ${p.restauranteId}`} · {fecha(p.fechaCreacion)}</small>
              </div>
              <EstadoBadge estado={p.estado} />
            </header>
            <Progreso estado={p.estado} />
            <ItemsPedido pedido={p} />
            {p.motivoCancelacion && <p className="nota">Motivo: {p.motivoCancelacion}</p>}
            <footer>
              <span>Subtotal {money(p.subtotal)} · Impuestos {money(p.impuestos)} · <b>Total {money(p.total)}</b></span>
              {cancelable(p.estado) && <button className="btn danger sm" onClick={() => cancelar(p)}>Cancelar pedido</button>}
            </footer>
          </article>
        ))}
      </div>
    </>
  );
}
