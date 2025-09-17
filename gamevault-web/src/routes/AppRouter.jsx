import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import {
  HomeOutlined,
  ShoppingCartOutlined,
  UserOutlined,
  AppstoreOutlined,
  TeamOutlined,
} from '@ant-design/icons';

import HomePage from '../pages/HomePage.jsx';
import StorePage from '../pages/StorePage.jsx';
import CartPage from '../pages/CartPage.jsx';
import OrdersPage from '../pages/OrdersPage.jsx';
import CommunityPage from '../pages/CommunityPage.jsx';
import { AppWrapper } from '../AppWrapper.jsx';

const { Header, Content, Footer } = Layout;

function NavMenu() {
  const location = useLocation();

  // 根据当前路径匹配 key
  let selectedKey = 'home';
  if (location.pathname.startsWith('/store')) selectedKey = 'store';
  if (location.pathname.startsWith('/cart')) selectedKey = 'cart';
  if (location.pathname.startsWith('/orders')) selectedKey = 'orders';
  if (location.pathname.startsWith('/community')) selectedKey = 'community';

  return (
    <Menu
      theme="dark"
      mode="horizontal"
      selectedKeys={[selectedKey]}   // 👈 绑定当前选中项
      items={[
        { key: 'home', label: <Link to="/">首页</Link>, icon: <HomeOutlined /> },
        { key: 'store', label: <Link to="/store">商店</Link>, icon: <AppstoreOutlined /> },
        { key: 'cart', label: <Link to="/cart">购物车</Link>, icon: <ShoppingCartOutlined /> },
        { key: 'orders', label: <Link to="/orders">订单</Link>, icon: <UserOutlined /> },
        { key: 'community', label: <Link to="/community">社区</Link>, icon: <TeamOutlined /> },
      ]}
    />
  );
}

export default function AppRouter() {
  return (
    <AppWrapper>
        <Router>
        <Layout style={{ minHeight: '100vh', background: 'transparent'  }}>
            <Header
                style={{
                    position: 'fixed',
                    top: 0,
                    width: '100%',
                    zIndex: 1000,
                    display: 'flex',
                    alignItems: 'center',
                }}
                >
                <NavMenu
                    theme="dark"
                    mode="horizontal"
                    defaultSelectedKeys={['home']}
                    items={[
                        { key: 'home', label: <Link to="/">首页</Link> },
                        { key: 'store', label: <Link to="/store">商店</Link> },
                        { key: 'cart', label: <Link to="/cart">购物车</Link> },
                        { key: 'orders', label: <Link to="/orders">订单</Link> },
                        { key: 'community', label: <Link to="/community">社区</Link> },
                    ]}
                />
            </Header>
            <Content style={{ padding: '80px 20px 20px' }}>
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/store" element={<StorePage />} />
                <Route path="/cart" element={<CartPage />} />
                <Route path="/orders" element={<OrdersPage />} />
                <Route path="/community" element={<CommunityPage />} />
            </Routes>
            </Content>

            <Footer style={{ textAlign: 'center' }}>GameVault Web ©2025</Footer>
        </Layout>
        </Router>
    </AppWrapper>
  );
}
