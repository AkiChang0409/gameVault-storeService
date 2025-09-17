import { Table, Button, Select, message, Modal, Card } from 'antd';
import { useEffect, useState, useCallback } from 'react';
import { DeleteOutlined } from '@ant-design/icons';
import styled from 'styled-components';
import api from '../api/axiosInstance';
import { getUserId } from '../utils/auth';

// ==================== styled-components 定义 ====================
const CartActions = styled.div`
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-top: 1px solid #eee;
  padding: 10px 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  z-index: 1000;
`;

const GameCover = styled.img`
  width: 50px;
  border-radius: 4px;
`;

// ==================== 页面组件 ====================
export default function CartPage() {
  const [cart, setCart] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState("CREDIT_CARD");
  const [isClearOpen, setIsClearOpen] = useState(false);
  const [isCheckoutOpen, setIsCheckoutOpen] = useState(false);
  const userId = getUserId();

  const fetchCart = useCallback(async () => {
    try {
      const res = await api.get(`/cart/${userId}`);
      setCart(res.data);
    } catch (err) {
      message.error("获取购物车失败: " + err.message);
    }
  }, [userId]);

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  const removeItem = async (gameId) => {
    try {
      await api.delete(`/cart/${userId}/items/${gameId}`);
      message.success('已移除');
      fetchCart();
    } catch (err) {
      message.error("移除失败: " + err.message);
    }
  };

  const clearCart = async () => {
    try {
      await api.delete(`/cart/${userId}`);
      message.success("购物车已清空");
      fetchCart();
      setIsClearOpen(false);
    } catch (err) {
      message.error("清空购物车失败: " + err.message);
    }
  };

  const checkout = async () => {
    try {
      const res = await api.post(`/cart/${userId}/checkout?method=${paymentMethod}`);
      message.success(`订单已创建: ${res.data.orderId}`);
      fetchCart();
      setIsCheckoutOpen(false);
    } catch (err) {
      message.error("结账失败: " + err.message);
    }
  };

  const columns = [
    {
      title: '封面',
      dataIndex: ['game', 'imageUrl'],
      render: (url) => <GameCover src={url} alt="cover" />,
    },
    { title: '游戏', dataIndex: ['game', 'title'] },
    { title: '数量', dataIndex: 'quantity' },
    { title: '单价', dataIndex: 'unitPrice', render: (v) => `¥${v}` },
    { title: '小计', dataIndex: 'subtotal', render: (v) => `¥${v}` },
    {
      title: '操作',
      render: (_, record) => (
        <Button
          danger
          type="text"
          icon={<DeleteOutlined />}
          onClick={() => removeItem(record.game.gameId)}
        />
      )
    }
  ];

  return (
    <>
      <Card title="我的购物车" style={{ marginBottom: 80 }}>
        <Table
          dataSource={cart?.cartItems || []}
          columns={columns}
          rowKey="cartItemId"
          pagination={{
            pageSize: 10,
            showSizeChanger: false,
          }}
        />
      </Card>

      {/* 底部悬浮操作区 */}
      <CartActions>
        <Button
          type="primary"
          size="large"
          onClick={() => setIsCheckoutOpen(true)}
          disabled={!cart || cart.cartItems.length === 0}
        >
          结账
        </Button>
        <Button
          danger
          size="large"
          onClick={() => setIsClearOpen(true)}
          disabled={!cart || cart.cartItems.length === 0}
        >
          清空购物车
        </Button>
      </CartActions>

      {/* 清空确认框 */}
      <Modal
        title={<span style={{ color: 'red' }}>⚠️ 确认清空购物车？</span>}
        open={isClearOpen}
        onOk={clearCart}
        onCancel={() => setIsClearOpen(false)}
        okText="确认"
        cancelText="取消"
      >
        <p>此操作不可撤销，是否继续？</p>
      </Modal>

      {/* 结账确认框 */}
      <Modal
        title="确认结账"
        open={isCheckoutOpen}
        onOk={checkout}
        onCancel={() => setIsCheckoutOpen(false)}
        okText="支付"
        cancelText="取消"
      >
        <p>总金额：<strong>¥{cart?.finalAmount || 0}</strong></p>
        <Select
          value={paymentMethod}
          style={{ width: '100%', marginTop: 10 }}
          onChange={setPaymentMethod}
        >
          <Select.Option value="CREDIT_CARD">信用卡</Select.Option>
          <Select.Option value="PAYPAL">PayPal</Select.Option>
          <Select.Option value="WALLET">钱包</Select.Option>
        </Select>
      </Modal>
    </>
  );
}
