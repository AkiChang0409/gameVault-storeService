// src/pages/OrdersPage.jsx
import { useEffect, useState } from "react";
import { List, Card, Tag, Button, message, Select } from "antd";
import api from "../api/api";

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [sortBy, setSortBy] = useState("orderIdDesc");

  // 定义状态优先级
  const statusPriority = {
    PENDING: 1,
    PAID: 2,
    COMPLETED: 3,
    FAILED: 4,
    CANCELLED: 5,
  };

  // 排序函数
  const sortOrders = (data, sortBy) => {
    switch (sortBy) {
      case "orderIdAsc":
        return [...data].sort((a, b) => a.orderId - b.orderId);
      case "orderIdDesc":
        return [...data].sort((a, b) => b.orderId - a.orderId);
      case "dateAsc":
        return [...data].sort(
          (a, b) => new Date(a.orderDate) - new Date(b.orderDate)
        );
      case "dateDesc":
        return [...data].sort(
          (a, b) => new Date(b.orderDate) - new Date(a.orderDate)
        );
      case "status":
        return [...data].sort(
          (a, b) =>
            (statusPriority[a.status] || 99) -
            (statusPriority[b.status] || 99)
        );
      default:
        return data;
    }
  };

  // 加载订单列表
  const loadOrders = async () => {
    setLoading(true);
    try {
      const res = await api.get("/orders"); // ✅ 和后端匹配
      setOrders(sortOrders(res.data, sortBy));
    } catch {
      message.error("加载订单失败");
    } finally {
      setLoading(false);
    }
  };

  // 模拟支付
  const payOrder = async (orderId) => {
    try {
      await api.post(`/orders/${orderId}/pay`);
    message.success("支付成功");
      loadOrders();
    } catch {
      message.error("支付失败");
    }
  };

  useEffect(() => {
    loadOrders();
  }, []);

  // 切换排序规则时重新排序
  useEffect(() => {
    setOrders((prev) => sortOrders(prev, sortBy));
  }, [sortBy]);

  return (
    <Card
      title="我的订单"
      loading={loading}
      extra={
        <Select value={sortBy} onChange={setSortBy} style={{ width: 180 }}>
          <Select.Option value="orderIdDesc">订单号 ↓</Select.Option>
          <Select.Option value="orderIdAsc">订单号 ↑</Select.Option>
          <Select.Option value="dateDesc">时间 ↓</Select.Option>
          <Select.Option value="dateAsc">时间 ↑</Select.Option>
          <Select.Option value="status">状态排序</Select.Option>
        </Select>
      }
    >
      <List
        dataSource={orders}
        renderItem={(order) => (
          <List.Item>
            <Card
              style={{ width: "100%" }}
              title={`订单 #${order.orderId}`}
              extra={
                <Tag
                  color={
                    order.status === "PAID"
                      ? "blue"
                      : order.status === "COMPLETED"
                      ? "green"
                      : order.status === "FAILED"
                      ? "red"
                      : "orange"
                  }
                >
                  {order.status}
                </Tag>
              }
            >
              <p>支付方式: {order.paymentMethod}</p>
              <p>总金额: ¥{order.finalAmount}</p>
              <p>创建时间: {new Date(order.orderDate).toLocaleString()}</p>

              <List
                size="small"
                dataSource={order.orderItems}
                renderItem={(item) => (
                  <List.Item>
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        width: "100%",
                      }}
                    >
                      <img
                        src={item.imageUrl || "https://via.placeholder.com/80x100"}
                        alt={item.gameTitle || "Game"}
                        style={{
                          width: 80,
                          height: 100,
                          objectFit: "cover",
                          borderRadius: 8,
                          marginRight: 16,
                          border: "1px solid #eee",
                        }}
                      />
                      <div style={{ flex: 1 }}>
                        <p>
                          <strong>游戏:</strong> {item.gameTitle || `ID ${item.gameId}`}
                        </p>
                        <p>
                          <strong>单价:</strong> ¥{item.unitPrice}
                        </p>
                        <p>
                          <strong>状态:</strong> {item.orderStatus}
                        </p>
                        {item.activationCode && (
                          <p>
                            <strong>激活码:</strong>{" "}
                            <code
                              style={{
                                background: "#f8f9fa",
                                padding: "2px 6px",
                                borderRadius: 4,
                              }}
                            >
                              {item.activationCode}
                            </code>{" "}
                            <Button
                              type="link"
                              onClick={() => {
                                navigator.clipboard.writeText(item.activationCode);
                                message.success("激活码已复制");
                              }}
                            >
                              复制
                            </Button>
                          </p>
                        )}
                      </div>
                    </div>
                  </List.Item>
                )}
              />

              {order.status === "PENDING" && (
                <Button type="primary" onClick={() => payOrder(order.orderId)}>
                  立即支付
                </Button>
              )}
            </Card>
          </List.Item>
        )}
      />
    </Card>
  );
}
