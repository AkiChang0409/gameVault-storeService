import { Layout, Menu, Badge } from "antd";
import { BrowserRouter as Router, Routes, Route, Link, useLocation } from "react-router-dom";
import {
  HomeOutlined,
  ShopOutlined,
  ShoppingCartOutlined,
  ProfileOutlined,
  FileTextOutlined,
} from "@ant-design/icons";

import HomePage from "./pages/HomePage";
import GamesPage from "./pages/GamesPage";
import CartPage from "./pages/CartPage";
import OrdersPage from "./pages/OrdersPage";
import ProfilePage from "./pages/ProfilePage";
import { CartProvider, useCart } from "./context/CartContext.jsx";

const { Header, Content, Footer } = Layout;

function NavMenu() {
  const location = useLocation();
  const selectedKey = location.pathname === "/" ? "/home" : location.pathname;
  const { cartCount } = useCart(); // ✅ 在 Provider 包裹下使用

  return (
    <Menu theme="dark" mode="horizontal" selectedKeys={[selectedKey]} style={{ flex: 1 }}>
      <Menu.Item key="/home" icon={<HomeOutlined />}>
        <Link to="/">首页</Link>
      </Menu.Item>
      <Menu.Item key="/game" icon={<ShopOutlined />}>
        <Link to="/game">游戏商店</Link>
      </Menu.Item>
      <Menu.Item
        key="/cart"
        icon={
          <Badge count={cartCount} offset={[5, -2]}>
            <ShoppingCartOutlined className="cart-icon"/>
          </Badge>
        }
      >
        <Link to="/cart">购物车</Link>
      </Menu.Item>
      <Menu.Item key="/orders" icon={<FileTextOutlined />}>
        <Link to="/orders">订单</Link>
      </Menu.Item>
      <Menu.Item key="/profile" icon={<ProfileOutlined />}>
        <Link to="/profile">个人资料</Link>
      </Menu.Item>
    </Menu>
  );
}

function AppContent() {
  return (
    <Layout style={{ minHeight: "100vh" }}>
      {/* 顶部导航 */}
      <Header style={{ display: "flex", alignItems: "center" }}>
        <div style={{ color: "white", fontSize: 20, fontWeight: "bold", marginRight: 20 }}>
          🎮 GameVault
        </div>
        <NavMenu />
      </Header>

      {/* 页面内容 */}
      <Content style={{ padding: "20px 50px" }}>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/game" element={<GamesPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Routes>
      </Content>

      {/* 页脚 */}
      <Footer style={{ textAlign: "center" }}>
        GameVault ©2025 Created by Team
      </Footer>
    </Layout>
  );
}

export default function App() {
  return (
    <Router>
      <CartProvider>  {/* ✅ Provider 包裹整个 App */}
        <AppContent />
      </CartProvider>
    </Router>
  );
}
