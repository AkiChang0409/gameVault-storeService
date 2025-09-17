import { useEffect, useState } from "react";
import {
  List,
  Button,
  InputNumber,
  Select,
  Card,
  message,
  Pagination,
} from "antd";
import api from "../api/api";
import { getUserId } from "../utils/auth";

export default function CartPage() {
  const [cart, setCart] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState("CREDIT_CARD");
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  const loadCart = async () => {
    const userId = getUserId();
    if (!userId) {
      message.warning("请先登录");
      return;
    }

    setLoading(true);
    try {
      const res = await api.get(`/cart/${userId}`);
      setCart(res.data);
    } catch (err) {
      console.error("加载购物车失败:", err);
      message.error("加载购物车失败");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCart();
  }, []);

  if (!cart || !cart.cartItems || cart.cartItems.length === 0) {
    return <Card>购物车是空的</Card>;
  }

  // 分页数据
  const startIndex = (currentPage - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const pagedItems = cart.cartItems.slice(startIndex, endIndex);

  return (
    <Card
      title="购物车"
      style={{
        borderRadius: 12,
        display: "flex",
        flexDirection: "column",
        minHeight: "80vh", // 让卡片有高度，底部悬挂才明显
      }}
      loading={loading}
      bodyStyle={{ flex: 1, display: "flex", flexDirection: "column" }}
    >
      {/* 商品列表 */}
      <div style={{ flex: 1 }}>
        <List
          dataSource={pagedItems}
          renderItem={(item) => (
            <List.Item
              actions={[
                <InputNumber
                  min={1}
                  value={item.quantity}
                  onChange={(val) =>
                    api
                      .post(
                        `/cart/${getUserId()}/items?gameId=${item.game.gameId}&quantity=${val}`
                      )
                      .then(loadCart)
                  }
                />,
                <Button
                  danger
                  onClick={() =>
                    api
                      .delete(
                        `/cart/${getUserId()}/items/${item.game.gameId}`
                      )
                      .then(loadCart)
                  }
                >
                  删除
                </Button>,
              ]}
            >
              <List.Item.Meta
                avatar={
                  <img
                    src={item.game.imageUrl || "https://via.placeholder.com/80x100"}
                    alt={item.game.title}
                    style={{ width: 60, height: 80, borderRadius: 6 }}
                  />
                }
                title={item.game.title}
                description={`单价: ¥${item.unitPrice}`}
              />
              <div>小计: ¥{item.subtotal}</div>
            </List.Item>
          )}
        />
        {/* 分页控件 */}
        <Pagination
          current={currentPage}
          pageSize={pageSize}
          total={cart.cartItems.length}
          onChange={(page) => setCurrentPage(page)}
          style={{ marginTop: 16, textAlign: "right" }}
        />
      </div>

      {/* 结账区域悬挂在底部 */}
      <div
        style={{
          borderTop: "1px solid #f0f0f0",
          paddingTop: 16,
          marginTop: "auto", // ✅ 把结账区推到底部
          textAlign: "right",
        }}
      >
        <h3>总计: ¥{cart.totalPrice}</h3>
        <Select
          value={paymentMethod}
          onChange={(v) => setPaymentMethod(v)}
          style={{ width: 150, marginRight: 10 }}
        >
          <Select.Option value="CREDIT_CARD">信用卡</Select.Option>
          <Select.Option value="PAYPAL">PayPal</Select.Option>
          <Select.Option value="ALIPAY">支付宝</Select.Option>
          <Select.Option value="WECHAT">微信支付</Select.Option>
        </Select>
        <Button
          type="primary"
          onClick={() =>
            api.post(`/orders/checkout?method=${paymentMethod}`).then(() => {
              message.success("下单成功");
              loadCart();
            })
          }
        >
          结账
        </Button>
      </div>
    </Card>
  );
}
