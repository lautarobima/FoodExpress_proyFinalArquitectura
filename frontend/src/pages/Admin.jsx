import { useCallback, useEffect, useState } from 'react';
import { api } from '../api.js';
import { useSession } from '../session.jsx';
import { Empty, ErrorBox, Field, Modal } from '../components/ui.jsx';
import { DIAS, diaLabel, money } from '../format.js';

/* ───────────── Restaurantes ───────────── */

function FormRestaurante({ inicial, onGuardado, onClose }) {
  const [d, setD] = useState(inicial || { nombre: '', descripcion: '', direccion: '', telefono: '' });
  const [error, setError] = useState('');
  const set = (k) => (e) => setD({ ...d, [k]: e.target.value });
  async function guardar(e) {
    e.preventDefault();
    try {
      const body = { nombre: d.nombre, descripcion: d.descripcion, direccion: d.direccion, telefono: d.telefono };
      const r = inicial ? await api.editarRestaurante(inicial.id, body) : await api.crearRestaurante(body);
      onGuardado(r);
    } catch (err) { setError(err.message); }
  }
  return (
    <Modal titulo={inicial ? 'Editar restaurante' : 'Nuevo restaurante'} onClose={onClose}>
      <form onSubmit={guardar} className="form">
        <Field label="Nombre"><input required maxLength={120} value={d.nombre} onChange={set('nombre')} /></Field>
        <Field label="Descripción"><input maxLength={500} value={d.descripcion || ''} onChange={set('descripcion')} /></Field>
        <Field label="Dirección"><input required maxLength={200} value={d.direccion} onChange={set('direccion')} /></Field>
        <Field label="Teléfono"><input required maxLength={20} value={d.telefono} onChange={set('telefono')} /></Field>
        <ErrorBox error={error} />
        <button className="btn primary">{inicial ? 'Guardar cambios' : 'Crear restaurante'}</button>
      </form>
    </Modal>
  );
}

function Horarios({ restaurante, onClose }) {
  const [lista, setLista] = useState([]);
  const [error, setError] = useState('');
  const [f, setF] = useState({ diaSemana: 'LUNES', apertura: '08:00', cierre: '22:00' });
  const cargar = useCallback(() => api.horarios(restaurante.id).then(setLista).catch((e) => setError(e.message)), [restaurante.id]);
  useEffect(() => { cargar(); }, [cargar]);
  async function agregar(e) {
    e.preventDefault();
    try { await api.crearHorario(restaurante.id, { ...f, apertura: f.apertura + ':00', cierre: f.cierre + ':00' }); setError(''); cargar(); }
    catch (err) { setError(err.message); }
  }
  return (
    <Modal titulo={`Horarios · ${restaurante.nombre}`} onClose={onClose}>
      {lista.length === 0 && <p className="muted">Sin horarios registrados.</p>}
      <ul className="simple">
        {lista.map((h) => (
          <li key={h.id}>
            <span>{diaLabel(h.diaSemana)}: {h.apertura.slice(0, 5)} – {h.cierre.slice(0, 5)}</span>
            <button className="btn ghost sm" onClick={() => api.borrarHorario(restaurante.id, h.id).then(cargar).catch((e) => setError(e.message))}>Quitar</button>
          </li>
        ))}
      </ul>
      <form onSubmit={agregar} className="form inline">
        <Field label="Día">
          <select value={f.diaSemana} onChange={(e) => setF({ ...f, diaSemana: e.target.value })}>
            {DIAS.map((d) => <option key={d} value={d}>{diaLabel(d)}</option>)}
          </select>
        </Field>
        <Field label="Abre"><input type="time" required value={f.apertura} onChange={(e) => setF({ ...f, apertura: e.target.value })} /></Field>
        <Field label="Cierra"><input type="time" required value={f.cierre} onChange={(e) => setF({ ...f, cierre: e.target.value })} /></Field>
        <button className="btn primary">Agregar horario</button>
      </form>
      <ErrorBox error={error} />
    </Modal>
  );
}

function PanelRestaurantes({ restaurantes, recargar }) {
  const s = useSession();
  const [modal, setModal] = useState(null);
  const [error, setError] = useState('');
  const run = (fn) => fn().then(recargar).catch((e) => setError(e.message));

  return (
    <>
      <div className="barra">
        <h2>Restaurantes</h2>
        <button className="btn primary" onClick={() => setModal({ tipo: 'form' })}>Nuevo restaurante</button>
      </div>
      <ErrorBox error={error} />
      {restaurantes.length === 0 && <Empty titulo="Aún no hay restaurantes" texto="Crea el primero para empezar a armar su menú." />}
      <div className="grid">
        {restaurantes.map((r) => {
          const abierto = r.estadoOperativo === 'ABIERTO';
          return (
            <article key={r.id} className="resto estatico">
              <div className="resto-top"><h2>{r.nombre}</h2><span className={`pill ${abierto ? 'ok' : 'off'}`}>{abierto ? 'Abierto' : 'Cerrado'}</span></div>
              <p>{r.descripcion}</p>
              <small>{r.direccion} · {r.telefono}</small>
              <div className="acciones">
                <button className="btn sm" onClick={() => run(() => api.estadoRestaurante(r.id, abierto ? 'CERRADO' : 'ABIERTO'))}>{abierto ? 'Cerrar' : 'Abrir'}</button>
                <button className="btn sm" onClick={() => setModal({ tipo: 'form', r })}>Editar</button>
                <button className="btn sm" onClick={() => setModal({ tipo: 'horarios', r })}>Horarios</button>
                <button className="btn danger sm" onClick={() => window.confirm(`¿Desactivar ${r.nombre}? Dejará de aparecer para los clientes.`) && run(() => api.desactivarRestaurante(r.id))}>Desactivar</button>
              </div>
            </article>
          );
        })}
      </div>
      {modal?.tipo === 'form' && (
        <FormRestaurante inicial={modal.r} onClose={() => setModal(null)}
          onGuardado={(r) => { setModal(null); s.toast(modal.r ? 'Restaurante actualizado' : `Restaurante creado. Está cerrado: ábrelo para recibir pedidos.`, 'ok'); recargar(); }} />
      )}
      {modal?.tipo === 'horarios' && <Horarios restaurante={modal.r} onClose={() => setModal(null)} />}
    </>
  );
}

/* ───────────── Menú: categorías, platos, extras ───────────── */

function FormCategoria({ rid, inicial, onGuardado, onClose }) {
  const [d, setD] = useState(inicial || { nombre: '', descripcion: '' });
  const [error, setError] = useState('');
  async function guardar(e) {
    e.preventDefault();
    try {
      const body = { nombre: d.nombre, descripcion: d.descripcion };
      inicial ? await api.editarCategoria(rid, inicial.id, body) : await api.crearCategoria(rid, body);
      onGuardado();
    } catch (err) { setError(err.message); }
  }
  return (
    <Modal titulo={inicial ? 'Editar categoría' : 'Nueva categoría'} onClose={onClose}>
      <form onSubmit={guardar} className="form">
        <Field label="Nombre" hint="Ej: Entradas, Platos fuertes, Bebidas, Postres">
          <input required maxLength={80} value={d.nombre} onChange={(e) => setD({ ...d, nombre: e.target.value })} />
        </Field>
        <Field label="Descripción"><input maxLength={250} value={d.descripcion || ''} onChange={(e) => setD({ ...d, descripcion: e.target.value })} /></Field>
        <ErrorBox error={error} />
        <button className="btn primary">{inicial ? 'Guardar cambios' : 'Crear categoría'}</button>
      </form>
    </Modal>
  );
}

function FormPlato({ categorias, extras, inicial, categoriaInicial, onGuardado, onClose }) {
  const [d, setD] = useState(inicial
    ? { categoriaId: inicial.categoriaId, nombre: inicial.nombre, descripcion: inicial.descripcion || '', precio: inicial.precio, stock: inicial.stock ?? '' }
    : { categoriaId: categoriaInicial || categorias[0]?.id, nombre: '', descripcion: '', precio: '', stock: '' });
  const [sel, setSel] = useState([]);       // ids de extras asociados
  const [orig, setOrig] = useState([]);
  const [error, setError] = useState('');
  const disponibles = extras.filter((x) => x.disponible);

  useEffect(() => {
    if (inicial) api.extrasDePlato(inicial.id).then((l) => { const ids = l.map((x) => x.extraId); setSel(ids); setOrig(ids); }).catch(() => {});
  }, [inicial]);

  const toggle = (id) => setSel((x) => (x.includes(id) ? x.filter((i) => i !== id) : [...x, id]));

  async function guardar(e) {
    e.preventDefault();
    try {
      const body = {
        categoriaId: Number(d.categoriaId), nombre: d.nombre, descripcion: d.descripcion || null,
        precio: Number(d.precio), stock: d.stock === '' ? null : Number(d.stock),
      };
      const plato = inicial ? await api.editarPlato(inicial.id, body) : await api.crearPlato(body);
      for (const id of sel.filter((i) => !orig.includes(i))) await api.asociarExtra(plato.id, id);
      for (const id of orig.filter((i) => !sel.includes(i))) await api.quitarExtra(plato.id, id);
      onGuardado();
    } catch (err) { setError(err.message); }
  }

  return (
    <Modal titulo={inicial ? 'Editar plato' : 'Nuevo plato'} onClose={onClose}>
      <form onSubmit={guardar} className="form">
        <Field label="Categoría">
          <select value={d.categoriaId} onChange={(e) => setD({ ...d, categoriaId: e.target.value })}>
            {categorias.map((c) => <option key={c.id} value={c.id}>{c.nombre}</option>)}
          </select>
        </Field>
        <Field label="Nombre"><input required maxLength={120} value={d.nombre} onChange={(e) => setD({ ...d, nombre: e.target.value })} /></Field>
        <Field label="Descripción"><input maxLength={500} value={d.descripcion} onChange={(e) => setD({ ...d, descripcion: e.target.value })} /></Field>
        <div className="two">
          <Field label="Precio (COP)"><input type="number" required min="0" step="100" value={d.precio} onChange={(e) => setD({ ...d, precio: e.target.value })} /></Field>
          <Field label="Stock" hint="Vacío = sin control de stock. Con 0 no se puede pedir.">
            <input type="number" min="0" value={d.stock} onChange={(e) => setD({ ...d, stock: e.target.value })} />
          </Field>
        </div>
        <fieldset>
          <legend>Extras que el cliente puede elegir en este plato</legend>
          {disponibles.length === 0 && <p className="muted">Este restaurante no tiene extras disponibles. Créalos en la pestaña Extras.</p>}
          {disponibles.map((x) => (
            <label key={x.id} className="check">
              <input type="checkbox" checked={sel.includes(x.id)} onChange={() => toggle(x.id)} />
              <span>{x.nombre} <small>{x.tipo === 'ADICION' ? 'Adición' : 'Modificación'}</small></span>
              <b>{money(x.precioAdicional)}</b>
            </label>
          ))}
        </fieldset>
        <ErrorBox error={error} />
        <button className="btn primary">{inicial ? 'Guardar cambios' : 'Crear plato'}</button>
      </form>
    </Modal>
  );
}

function FormExtra({ rid, inicial, platos, onGuardado, onClose }) {
  const [d, setD] = useState(inicial
    ? { nombre: inicial.nombre, descripcion: inicial.descripcion || '', tipo: inicial.tipo, precioAdicional: inicial.precioAdicional }
    : { nombre: '', descripcion: '', tipo: 'ADICION', precioAdicional: 0 });
  const [aplicar, setAplicar] = useState(platos.map((p) => p.id)); // por defecto, todos los platos
  const [error, setError] = useState('');
  const toggle = (id) => setAplicar((x) => (x.includes(id) ? x.filter((i) => i !== id) : [...x, id]));

  async function guardar(e) {
    e.preventDefault();
    try {
      const body = { nombre: d.nombre, descripcion: d.descripcion || null, tipo: d.tipo, precioAdicional: Number(d.precioAdicional) };
      if (inicial) { await api.editarExtra(rid, inicial.id, body); }
      else {
        const nuevo = await api.crearExtra(rid, body);
        for (const pid of aplicar) await api.asociarExtra(pid, nuevo.id);
      }
      onGuardado();
    } catch (err) { setError(err.message); }
  }
  return (
    <Modal titulo={inicial ? 'Editar extra' : 'Nuevo extra'} onClose={onClose}>
      <form onSubmit={guardar} className="form">
        <Field label="Nombre"><input required maxLength={100} value={d.nombre} onChange={(e) => setD({ ...d, nombre: e.target.value })} /></Field>
        <Field label="Descripción"><input maxLength={250} value={d.descripcion} onChange={(e) => setD({ ...d, descripcion: e.target.value })} /></Field>
        <div className="two">
          <Field label="Tipo">
            <select value={d.tipo} onChange={(e) => setD({ ...d, tipo: e.target.value })}>
              <option value="ADICION">Adición (ej: queso extra)</option>
              <option value="MODIFICACION">Modificación (ej: sin cebolla)</option>
            </select>
          </Field>
          <Field label="Precio adicional (COP)"><input type="number" required min="0" step="100" value={d.precioAdicional} onChange={(e) => setD({ ...d, precioAdicional: e.target.value })} /></Field>
        </div>
        {!inicial && (
          <fieldset>
            <legend>Platos donde se puede elegir</legend>
            {platos.length === 0 && <p className="muted">Aún no hay platos. Podrás asociarlo al crear o editar un plato.</p>}
            {platos.map((p) => (
              <label key={p.id} className="check">
                <input type="checkbox" checked={aplicar.includes(p.id)} onChange={() => toggle(p.id)} />
                <span>{p.nombre}</span>
              </label>
            ))}
          </fieldset>
        )}
        {inicial && <p className="muted">Para cambiar en qué platos aparece, edita el plato.</p>}
        <ErrorBox error={error} />
        <button className="btn primary">{inicial ? 'Guardar cambios' : 'Crear extra'}</button>
      </form>
    </Modal>
  );
}

function PanelMenu({ restaurantes }) {
  const s = useSession();
  const [rid, setRid] = useState(restaurantes[0]?.id || '');
  const [tab, setTab] = useState('platos');
  const [categorias, setCategorias] = useState([]);
  const [platos, setPlatos] = useState([]);
  const [extras, setExtras] = useState([]);
  const [modal, setModal] = useState(null);
  const [error, setError] = useState('');

  const cargar = useCallback(async () => {
    if (!rid) return;
    try {
      const cats = (await api.categorias(rid)).filter((c) => c.activa);
      const [ps, ex] = await Promise.all([Promise.all(cats.map((c) => api.platosDeCategoria(c.id))), api.extras(rid)]);
      setCategorias(cats); setPlatos(ps.flat()); setExtras(ex); setError('');
    } catch (e) { setError(e.message); }
  }, [rid]);
  useEffect(() => { cargar(); }, [cargar]);

  const run = (fn, msg) => fn().then(() => { msg && s.toast(msg, 'ok'); cargar(); }).catch((e) => setError(e.message));
  const cerrar = () => setModal(null);
  const guardado = (msg) => () => { cerrar(); s.toast(msg, 'ok'); cargar(); };

  if (restaurantes.length === 0) return <Empty titulo="Primero crea un restaurante" texto="Luego podrás armar su menú aquí." />;

  return (
    <>
      <div className="barra">
        <Field label="Restaurante">
          <select value={rid} onChange={(e) => setRid(Number(e.target.value))}>
            {restaurantes.map((r) => <option key={r.id} value={r.id}>{r.nombre}</option>)}
          </select>
        </Field>
        <div className="tabs">
          {[['platos', 'Platos'], ['categorias', 'Categorías'], ['extras', 'Extras']].map(([k, l]) => (
            <button key={k} className={tab === k ? 'on' : ''} onClick={() => setTab(k)}>{l}</button>
          ))}
        </div>
      </div>
      <ErrorBox error={error} />

      {tab === 'categorias' && (
        <>
          <div className="barra"><h2>Categorías</h2><button className="btn primary" onClick={() => setModal({ t: 'cat' })}>Nueva categoría</button></div>
          {categorias.length === 0 && <Empty titulo="Sin categorías" texto="Crea una (Entradas, Platos fuertes, Bebidas, Postres) para poder agregar platos." />}
          <ul className="simple">
            {categorias.map((c) => (
              <li key={c.id}>
                <span><b>{c.nombre}</b> <small className="muted">{c.descripcion}</small></span>
                <span className="acciones">
                  <button className="btn sm" onClick={() => setModal({ t: 'cat', c })}>Editar</button>
                  <button className="btn danger sm" onClick={() => window.confirm(`¿Desactivar la categoría ${c.nombre}?`) && run(() => api.desactivarCategoria(rid, c.id), 'Categoría desactivada')}>Desactivar</button>
                </span>
              </li>
            ))}
          </ul>
        </>
      )}

      {tab === 'platos' && (
        <>
          <div className="barra"><h2>Platos</h2>
            <button className="btn primary" disabled={!categorias.length} onClick={() => setModal({ t: 'plato' })}>Nuevo plato</button></div>
          {!categorias.length && <Empty titulo="Primero crea una categoría" texto="Los platos se organizan por categoría." />}
          {categorias.map((c) => (
            <section key={c.id} className="categoria">
              <h3>{c.nombre}</h3>
              {platos.filter((p) => p.categoriaId === c.id).length === 0 && <p className="muted">Sin platos.</p>}
              <ul className="simple">
                {platos.filter((p) => p.categoriaId === c.id).map((p) => (
                  <li key={p.id} className={p.disponible ? '' : 'apagado'}>
                    <span><b>{p.nombre}</b> · {money(p.precio)}
                      <small className="muted"> {p.stock == null ? 'sin control de stock' : `stock ${p.stock}`}</small></span>
                    <span className="acciones">
                      <span className={`pill ${p.disponible ? 'ok' : 'off'}`}>{p.disponible ? 'Disponible' : 'No disponible'}</span>
                      <button className="btn sm" onClick={() => run(() => api.disponibilidadPlato(p.id, !p.disponible))}>{p.disponible ? 'Ocultar' : 'Mostrar'}</button>
                      <button className="btn sm" onClick={() => setModal({ t: 'plato', p })}>Editar</button>
                    </span>
                  </li>
                ))}
              </ul>
            </section>
          ))}
        </>
      )}

      {tab === 'extras' && (
        <>
          <div className="barra"><h2>Extras del restaurante</h2><button className="btn primary" onClick={() => setModal({ t: 'extra' })}>Nuevo extra</button></div>
          {extras.length === 0 && <Empty titulo="Sin extras" texto="Crea adiciones (queso extra) o modificaciones (sin cebolla) y asócialas a tus platos." />}
          <ul className="simple">
            {extras.map((x) => (
              <li key={x.id} className={x.disponible ? '' : 'apagado'}>
                <span><b>{x.nombre}</b> · {money(x.precioAdicional)} <small className="muted">{x.tipo === 'ADICION' ? 'Adición' : 'Modificación'}</small></span>
                <span className="acciones">
                  <span className={`pill ${x.disponible ? 'ok' : 'off'}`}>{x.disponible ? 'Disponible' : 'No disponible'}</span>
                  <button className="btn sm" onClick={() => run(() => api.disponibilidadExtra(rid, x.id, !x.disponible))}>{x.disponible ? 'Ocultar' : 'Mostrar'}</button>
                  <button className="btn sm" onClick={() => setModal({ t: 'extra', x })}>Editar</button>
                </span>
              </li>
            ))}
          </ul>
        </>
      )}

      {modal?.t === 'cat' && <FormCategoria rid={rid} inicial={modal.c} onClose={cerrar} onGuardado={guardado('Categoría guardada')} />}
      {modal?.t === 'plato' && <FormPlato categorias={categorias} extras={extras} inicial={modal.p} onClose={cerrar} onGuardado={guardado('Plato guardado')} />}
      {modal?.t === 'extra' && <FormExtra rid={rid} platos={platos} inicial={modal.x} onClose={cerrar} onGuardado={guardado('Extra guardado')} />}
    </>
  );
}

/* ───────────── Página ───────────── */

export default function Admin() {
  const [restaurantes, setRestaurantes] = useState(null);
  const [vista, setVista] = useState('restaurantes');
  const [error, setError] = useState('');
  const recargar = useCallback(() => api.restaurantes().then(setRestaurantes).catch((e) => setError(e.message)), []);
  useEffect(() => { recargar(); }, [recargar]);

  return (
    <>
      <h1 className="titulo">Administración</h1>
      <div className="tabs grande">
        <button className={vista === 'restaurantes' ? 'on' : ''} onClick={() => setVista('restaurantes')}>Restaurantes</button>
        <button className={vista === 'menu' ? 'on' : ''} onClick={() => setVista('menu')}>Menú y extras</button>
      </div>
      <ErrorBox error={error} />
      {!restaurantes && !error && <p className="muted">Cargando…</p>}
      {restaurantes && vista === 'restaurantes' && <PanelRestaurantes restaurantes={restaurantes} recargar={recargar} />}
      {restaurantes && vista === 'menu' && <PanelMenu restaurantes={restaurantes} />}
    </>
  );
}
