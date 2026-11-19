import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';

// Se suscribe a tópicos STOMP del servicio de Notificaciones (/ws).
// topics: lista de rutas, p. ej. ['/topic/clientes/1']. onMessage recibe el mensaje ya parseado.
export function useTopics(topics, onMessage) {
  const handler = useRef(onMessage);
  handler.current = onMessage;
  const key = topics.join('|');

  useEffect(() => {
    if (!topics.length) return undefined;
    const proto = window.location.protocol === 'https:' ? 'wss' : 'ws';
    const client = new Client({
      brokerURL: `${proto}://${window.location.host}/ws`,
      reconnectDelay: 4000,
      onConnect: () => {
        topics.forEach((t) =>
          client.subscribe(t, (frame) => {
            try { handler.current(JSON.parse(frame.body), t); } catch { /* mensaje inválido */ }
          }));
      },
    });
    client.activate();
    return () => { client.deactivate(); };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [key]);
}

// Vuelve a ejecutar fn cada ms milisegundos (respaldo si el WebSocket no está disponible).
export function usePolling(fn, ms = 10000) {
  const ref = useRef(fn);
  ref.current = fn;
  useEffect(() => {
    const id = setInterval(() => ref.current(), ms);
    return () => clearInterval(id);
  }, [ms]);
}
