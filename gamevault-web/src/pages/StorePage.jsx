import { Card, Row, Col, Input, Button, message } from 'antd';
import { useEffect, useState } from 'react';
import api from '../api/axiosInstance';
import styled, { keyframes } from "styled-components";
import { getUserId } from '../utils/auth';
import { getGameImage } from '../utils/imageHelper';

// 定义红色光晕动画
const glow = keyframes`
  0% {
    box-shadow: 0 0 10px rgba(255, 77, 79, 0.4),
                0 0 20px rgba(200, 0, 0, 0.6);
  }
  50% {
    box-shadow: 0 0 20px rgba(255, 77, 79, 0.8),
                0 0 40px rgba(200, 0, 0, 0.9);
  }
  100% {
    box-shadow: 0 0 10px rgba(255, 77, 79, 0.4),
                0 0 20px rgba(200, 0, 0, 0.6);
  }
`;

// 🔥 火焰流动动画
const fireFlow = keyframes`
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
`;

// 包装 Card
const GameCard = styled(Card)`
  transition: all 0.3s ease;
  border-radius: 10px;

  &:hover {
    animation: ${glow} 1.5s infinite alternate;
    transform: translateY(-4px);
  }

  /* 当卡片 hover 时，内部按钮触发火焰效果 */
  &:hover .add-to-cart-btn {
    background: linear-gradient(270deg, #ff4d4f, #ff7e00, #ff4d4f);
    background-size: 300% 300%;
    animation: ${fireFlow} 3s ease infinite;
    box-shadow: 0 0 15px rgba(255, 126, 0, 0.7),
                0 0 30px rgba(255, 77, 79, 0.6);
    transform: scale(1.05);
  }
`;

const EnergyButton = styled(Button).attrs({
  className: "add-to-cart-btn"
})`
  background: linear-gradient(270deg, #1e90ff, #00c6ff, #1e90ff); /* 默认蓝色 */
  background-size: 300% 300%;
  color: white;
  border: none;
  font-weight: bold;
  transition: all 0.3s ease;
  
  &:hover,
  &:focus,
  .ant-card:hover & {
    background: linear-gradient(270deg, #ff4d4f, #ff7e00, #ff4d4f);
    background-size: 300% 300%;
    animation: ${fireFlow} 3s ease infinite;
    box-shadow: 0 0 15px rgba(255, 126, 0, 0.7),
                0 0 30px rgba(255, 77, 79, 0.6);
    transform: scale(1.05);
  }
`;


export default function StorePage() {
  const [games, setGames] = useState([]);
  const [q, setQ] = useState('');

  useEffect(() => {
    api.get('/games')
      .then(res => setGames(res.data))
      .catch(err => message.error("获取游戏失败: " + err.message));
  }, []);

  const handleSearch = () => {
    api.get(`/games?q=${q}`).then(res => setGames(res.data));
  };

  const addToCart = async (gameId) => {
    const userId = getUserId(); 
    if (!userId) {
      message.error("用户未登录，请先登录");
      return;
    }
    try {
      await api.post(`/cart/${userId}/items?gameId=${gameId}&quantity=1`);
      message.success('已加入购物车');
    } catch (err) {
      message.error("加入购物车失败: " + err.message);
    }
  };

  return (
    <>
      <Input.Search
        placeholder="搜索游戏"
        enterButton="搜索"
        value={q}
        onChange={e => setQ(e.target.value)}
        onSearch={handleSearch}
      />

      <Row gutter={[16, 16]} style={{ marginTop: 20 }}>
        {games.map(game => (
          <Col span={6} key={game.gameId}
           xs={24} sm={12} md={8} lg={6} xl={6}  // 👈 不同屏幕展示数量
          >
            <GameCard
              style={{width: 240}}
              cover={
                <img
                  alt={game.title}
                  src={getGameImage(game)}
                  style={{
                    width: "100%",
                    height: 200,        // 👈 固定高度（可以调整，比如 200px）
                    objectFit: "cover", // 👈 保持比例，裁剪超出部分
                    borderRadius: "8px 8px 0 0"
                  }}
                  onError={(e) => { e.currentTarget.src = "/images/placeholder.jpg"; }}
                />
            }
            actions={[
              <EnergyButton type="primary" onClick={() => addToCart(game.gameId)}>
                加入购物车
              </EnergyButton>
            ]}
          >
            <Card.Meta
              title={game.title}
              description={`价格: ${game.price} / 折扣价: ${game.discountPrice}`}
            />
          </GameCard>

          </Col>
        ))}
      </Row>
    </>
  );
}
