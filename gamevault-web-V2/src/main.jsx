import React from 'react';
import ReactDOM from 'react-dom/client';
import 'antd/dist/reset.css';
import AppRouter from './routes/AppRouter';
import { CartProvider } from './context/CartContext.jsx';  // ✅ 引入 Provider

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <CartProvider>   {/* ✅ 包裹整个应用 */}
      <AppRouter />
    </CartProvider>
  </React.StrictMode>
);
