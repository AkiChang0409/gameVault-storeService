import { Card, Button } from "antd";
import styled, { keyframes } from "styled-components";

const flameGlow = keyframes`
  0% { box-shadow: 0 0 10px rgba(255,69,0,0.5), 0 0 20px rgba(255,140,0,0.6); }
  50% { box-shadow: 0 0 20px rgba(255,215,0,0.8), 0 0 40px rgba(255,69,0,0.9); }
  100% { box-shadow: 0 0 10px rgba(255,69,0,0.5), 0 0 20px rgba(255,140,0,0.6); }
`;

const flamePulse = keyframes`
  0% { transform: scale(1); filter: brightness(1); }
  50% { transform: scale(1.05); filter: brightness(1.2); }
  100% { transform: scale(1); filter: brightness(1); }
`;

const flameClick = keyframes`
  0% { transform: scale(1); }
  50% { transform: scale(0.9); }
  100% { transform: scale(1); }
`;

const FlameCard = styled(Card)`
  border-radius: 12px;
  transition: all 0.3s ease;

  &:hover {
    animation: ${flameGlow} 1.5s infinite alternate;
    border-color: #ff4500;

    button {
      background: linear-gradient(90deg, #ff4500, #ff8c00, #ffd700) !important;
      color: white !important;
    }
  }
`;

const FlameButton = styled(Button)`
  background: #1677ff;
  border: none;
  color: white;
  font-weight: 600;
  transition: all 0.3s ease;

  /* 按钮 hover 时的火焰脉动 */
  &:hover {
    background: linear-gradient(90deg, #ff4500, #ff8c00, #ffd700) !important;
    color: white !important;
    animation: ${flamePulse} 1s infinite;
  }

  /* 按钮点击时的缩放反馈 */
  &:active {
    animation: ${flameClick} 0.2s ease;
  }
`;

// eslint-disable-next-line no-unused-vars
export default function GameCard({ game, onView, onAddToCart }) {
  return (
    <FlameCard
      hoverable
      cover={
        <img
          alt={game.title}
          src={game.imageUrl || "https://via.placeholder.com/240x300"}
          style={{
            borderRadius: "12px 12px 0 0",
            height: 240,
            objectFit: "cover",
          }}
        />
      }
    >
      <Card.Meta
        title={game.title}
        description={`${game.genre} | ${game.platform}`}
      />
      <p style={{ marginTop: 10, fontWeight: "bold", color: "#e74c3c" }}>
        ${game.price}
      </p>
      <FlameButton block onClick={() => onAddToCart(game.gameId)}>
        加入购物车
      </FlameButton>
    </FlameCard>
  );
}
