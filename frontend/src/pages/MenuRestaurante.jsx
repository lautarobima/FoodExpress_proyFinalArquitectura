import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { api } from '../api.js';
import { useSession } from '../session.jsx';
import { Empty, ErrorBox } from '../components/ui.jsx';
import { IMPUESTO, money } from '../format.js';

function Personalizar({ plato, onAgregar, onCancelar }) {
  const [cantidad, setCantidad] = useState(1);
  const [extras, setExtras] = useState([]);
  const [notas, setNotas] = useState('');
  const toggle = (e) =>
    setExtras((xs) => (xs.some((x) => x.id === e.id) ? xs.filter((x) => x.id !== e.id) : [...xs, e]));
  const unit = Number(plato.precio) + extras.reduce((a, e) => a + Number(e.precioAdicional), 0);

  return (
    <div className="personalizar">
      {plato.extras.length > 0 ? (
        <fieldset>
          <legend>Extras y modificaciones</legend>
          {plato.extras.map((e) => (
            <label key={e.id} className="check">
              <input type="checkbox" checked={extras.some((x) => x.id === e.id)} onChange={() => toggle(e)} />
              <span>{e.nombre}{e.descripcion ? <small> {e.descripcion}</small> : null}</span>
              <b>{Number(e.precioAdicional) > 0 ? `+ ${money(e.precioAdicional)}` : 'Gratis'}</b>
            </label>
          ))}
        </fieldset>
      ) : (
        <p className="muted">Este plato no tiene extras.</p>
      )}
      <label className="field">
        <span>Notas para la cocina</span>
        <input maxLength={500} value={notas} onChange={(e) => setNotas(e.target.value)} placeholder="Ej: sin cebolla, término medio" />
      </label>
      <div className="row">
        <div className="qty">
          <button type="button" onClick={() => setCantidad((c) => Math.max(1, c - 1))} aria-label="Menos">−</button>
          <span>{cantidad}</span>
          <button type="button" onClick={() => setCantidad((c) => c + 1)} aria-label="Más">+</button>
        </div>
        <button className="btn primary" onClick={() => onAgregar({
          platoId: plato.id, nombre: plato.nombre, precio: Number(plato.precio), cantidad,
          extras: extras.map((e) => ({ id: e.id, nombre: e.nombre, precio: Number(e.precioAdicional) })),
          notas: notas.trim(),
        })}>
          Agregar · {money(unit * cantidad)}
        </button>
        <button className="btn ghost" onClick={onCancelar}>Cancelar</button>
      </div>
    </div>
  );
}

export default function MenuRestaurante() {
  const { id } = useParams();
  const nav = useNavigate();
  const s = useSession();
  const [menu, setMenu] = useState(null);
  const [error, setError] = useState('');
  const [abierto, setAbierto] = useState(null); // plato en personalización
  const [enviando, setEnviando] = useState(false);
  const rid = Number(id);

  useEffect(() => {
    api.menu(rid).then(setMenu).catch((e) => setError(e.message));
  }, [rid]);

  const lineas = s.cart.restauranteId === rid ? s.cart.lineas : [];
  const totales = useMemo(() => {
    const subtotal = lineas.reduce(
      (a, l) => a + (l.precio + l.extras.reduce((x, e) => x + e.precio, 0)) * l.cantidad, 0);
    const imp = Math.round(subtotal * IMPUESTO);
    return { subtotal, imp, total: subtotal + imp };
  }, [lineas]);

  const restaurante = menu?.restaurante;
  const cerrado = restaurante && restaurante.estadoOperativo !== 'ABIERTO';

  async function confirmar() {
    setEnviando(true);
    setError('');
    try {
      const pedido = await api.crearPedido({
        clienteId: s.clienteId,
        restauranteId: rid,
        items: lineas.map((l) => ({
          platoId: l.platoId, cantidad: l.cantidad,
          extrasIds: l.extras.map((e) => e.id), notas: l.notas || undefined,
        })),
      });
      s.vaciar();
      s.toast(`Pedido #${pedido.id} enviado. Total ${money(pedido.total)}`, 'ok');
      nav('/cliente/pedidos');
    } catch (e) {
      setError(e.message);
      // el menú puede haber cambiado (plato agotado): se recarga
      api.menu(rid).then(setMenu).catch(() => {});
    } finally {
      setEnviando(false);
    }
  }

  if (error && !menu) return <><Link to="/cliente" className="back">Volver</Link><ErrorBox error={error} /></>;
  if (!menu) return <p className="muted">Cargando menú…</p>;

  return (
    <>
      <Link to="/cliente" className="back">Volver a restaurantes</Link>
      <div className="split">
        <div>
          <h1 className="titulo">{restaurante.nombre}</h1>
          <p className="muted">{restaurante.descripcion} {restaurante.direccion}</p>
          {cerrado && <div className="aviso">Este restaurante está cerrado ahora. Puedes ver el menú, pero no hacer pedidos.</div>}

          {menu.categorias.length === 0 && (
            <Empty titulo="El menú está vacío" texto="El restaurante aún no tiene platos disponibles." />
          )}
          {menu.categorias.map((c) => (
            <section key={c.id} className="categoria">
              <h2>{c.nombre}</h2>
              {c.platos.length === 0 && <p className="muted">No hay platos disponibles en esta categoría.</p>}
              {c.platos.map((p) => (
                <article key={p.id} className="plato">
                  <div className="plato-head">
                    <div>
                      <h3>{p.nombre}</h3>
                      {p.descripcion && <p>{p.descripcion}</p>}
                      {p.extras.length > 0 && <small className="muted">{p.extras.length} extras disponibles</small>}
                    </div>
                    <div className="plato-precio">
                      <b>{money(p.precio)}</b>
                      <button className="btn sm" disabled={cerrado} onClick={() => setAbierto(abierto === p.id ? null : p.id)}>
                        {abierto === p.id ? 'Cerrar' : 'Personalizar'}
                      </button>
                    </div>
                  </div>
                  {abierto === p.id && (
                    <Personalizar plato={p} onCancelar={() => setAbierto(null)}
                      onAgregar={(l) => { s.agregar(rid, l); setAbierto(null); }} />
                  )}
                </article>
              ))}
            </section>
          ))}
        </div>

        <aside className="ticket">
          <h2>Tu pedido</h2>
          {lineas.length === 0 ? (
            <p className="muted">Agrega platos para empezar.</p>
          ) : (
            <>
              <ul>
                {lineas.map((l) => (
                  <li key={l.key}>
                    <div className="l1">
                      <span>{l.cantidad}× {l.nombre}</span>
                      <b>{money((l.precio + l.extras.reduce((x, e) => x + e.precio, 0)) * l.cantidad)}</b>
                    </div>
                    {l.extras.length > 0 && <div className="l2">+ {l.extras.map((e) => e.nombre).join(', ')}</div>}
                    {l.notas && <div className="l2">“{l.notas}”</div>}
                    <div className="l3">
                      <button onClick={() => s.cambiarCantidad(l.key, Math.max(1, l.cantidad - 1))} aria-label="Menos">−</button>
                      <button onClick={() => s.cambiarCantidad(l.key, l.cantidad + 1)} aria-label="Más">+</button>
                      <button onClick={() => s.quitar(l.key)}>Quitar</button>
                    </div>
                  </li>
                ))}
              </ul>
              <dl>
                <div><dt>Subtotal</dt><dd>{money(totales.subtotal)}</dd></div>
                <div><dt>Impuesto (8 %)</dt><dd>{money(totales.imp)}</dd></div>
                <div className="total"><dt>Total estimado</dt><dd>{money(totales.total)}</dd></div>
              </dl>
              <ErrorBox error={error} />
              <button className="btn primary block" disabled={enviando || cerrado} onClick={confirmar}>
                {enviando ? 'Enviando…' : 'Confirmar pedido'}
              </button>
              <button className="btn ghost block" onClick={s.vaciar}>Vaciar pedido</button>
            </>
          )}
        </aside>
      </div>
    </>
  );
}
