import { useEffect, useState } from "react";
import { Row, Col, Input, Select, Button, Spin, Card, message } from "antd";
import GameCard from "../components/GameCard";
import { useCart } from "../context/CartContext.jsx";
import api from "../api/api";
import { getUserId } from "../utils/auth";   // ✅ 从 token 拿 userId

export default function GamesPage() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(false);
  const [genre, setGenre] = useState("");
  const [platform, setPlatform] = useState("");
  const [q, setQ] = useState("");
  const { setCartCount } = useCart(); // ✅ 用来更新导航栏购物车数量

  // 加载游戏列表
  const loadGames = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (genre) params.append("genre", genre);
      if (platform) params.append("platform", platform);
      if (q) params.append("q", q);

      const res = await api.get(`/games?${params.toString()}`);
      setGames(res.data);
    } catch (err) {
      console.error("加载游戏失败:", err);
      message.error("加载游戏失败");
    } finally {
      setLoading(false);
    }
  };

  // 加入购物车逻辑
  const handleAddToCart = async (gameId) => {
    try {
      const userId = getUserId(); // ✅ 从 token 解码
      if (!userId) {
        message.warning("请先登录");
        return;
      }

      await api.post(`/cart/${userId}/items?gameId=${gameId}&quantity=1`);
      message.success("已加入购物车");

      // ✅ 更新全局购物车数量
      setCartCount((prev) => prev + 1);
    } catch (err) {
      console.error("加入购物车失败:", err);
      message.error("加入购物车失败");
    }
  };

  useEffect(() => {
    loadGames();
  }, []);

  return (
    <Card title="游戏商店" style={{ borderRadius: 12 }}>
      {/* 搜索和筛选 */}
      <Row gutter={16} style={{ marginBottom: 20 }}>
        <Col>
          <Input.Search
            placeholder="搜索游戏..."
            enterButton="搜索"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            onSearch={loadGames}
          />
        </Col>
        <Col>
          <Select
            placeholder="选择类型"
            style={{ width: 150 }}
            onChange={(v) => setGenre(v)}
            allowClear
          >
            <Select.Option value="RPG">RPG</Select.Option>
            <Select.Option value="Action">动作</Select.Option>
            <Select.Option value="Strategy">策略</Select.Option>
            <Select.Option value="Adventure">冒险</Select.Option>
            <Select.Option value="Sports">体育</Select.Option>
          </Select>
        </Col>
        <Col>
          <Select
            placeholder="选择平台"
            style={{ width: 150 }}
            onChange={(v) => setPlatform(v)}
            allowClear
          >
            <Select.Option value="PC">PC</Select.Option>
            <Select.Option value="PlayStation">PlayStation</Select.Option>
            <Select.Option value="Xbox">Xbox</Select.Option>
            <Select.Option value="Nintendo">Nintendo</Select.Option>
          </Select>
        </Col>
        <Col>
          <Button type="primary" onClick={loadGames}>
            搜索
          </Button>
        </Col>
      </Row>

      {/* 游戏卡片展示 */}
      {loading ? (
        <Spin tip="加载中..." />
      ) : (
        <Row gutter={[16, 16]}>
          {games.map((game) => (
            <Col xs={24} sm={12} md={8} lg={6} key={game.gameId}>
              <GameCard
                game={game}
                onAddToCart={handleAddToCart}  // ✅ 传回调下去
              />
            </Col>
          ))}
        </Row>
      )}
    </Card>
  );
}
