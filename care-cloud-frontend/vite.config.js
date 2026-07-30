import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api/app/chat': {
        target: 'http://localhost:8084',
        changeOrigin: true
      },
      '/api/app/assistant': {
        target: 'http://localhost:8084',
        changeOrigin: true
      },
      '/api/payment': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/api/app': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
