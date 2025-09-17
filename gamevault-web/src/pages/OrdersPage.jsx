import { Table, Button, message } from 'antd';
import { useEffect, useState } from 'react';
import api from '../api/axiosInstance';

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);

  const fetchOrders = async () => {
  try {
    const res = await api.get('/orders/orders');
    console.log("订单接口返回:", res.data);
    setOrders(res.data);
  } catch (err) {
    console.error("获取订单失败:", err);
    message.error('获取订单失败: ' + err.message);
  }
};


  useEffect(() => {
    fetchOrders();
  }, []);

  const payOrder = async (orderId) => {
    try {
        
      await api.post(`/orders/${orderId}/pay`);
      message.success(`订单 ${orderId} 支付成功`);
      fetchOrders();
    } catch (err) {
      message.error("支付失败: " + err.message);
    }
  };

  const columns = [
    { title: '订单号', dataIndex: 'orderId' },
    { title: '状态', dataIndex: 'status' },
    { title: '金额', dataIndex: 'finalAmount' },
    {
      title: '操作',
      render: (_, record) =>
        record.status === 'PENDING' ? (
          <Button type="primary" onClick={() => payOrder(record.orderId)}>
            支付
          </Button>
        ) : (
          '—'
        ),
    },
  ];

  return <Table dataSource={orders} columns={columns} rowKey="orderId" />;
}
