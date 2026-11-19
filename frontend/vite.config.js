import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// En desarrollo (npm run dev) Vite hace el mismo trabajo que nginx en Docker:
// reenvía cada ruta /api/... al microservicio que corresponde.
const menu = 'http://localhost:8081';
const pedidos = 'http://localhost:8082';
const notif = 'http://localhost:8083';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api/restaurantes': menu,
      '/api/platos': menu,
      '/api/pedidos': pedidos,
      '/api/notificaciones': notif,
      '/ws': { target: notif, ws: true },
    },
  },
});
