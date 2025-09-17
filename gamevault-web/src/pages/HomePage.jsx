import { Typography, Button } from "antd";
import styled, { keyframes } from "styled-components";
import { Link } from "react-router-dom";   // 👈 引入 Link

const { Title, Paragraph } = Typography;

// 动态渐变背景
const gradient = keyframes`
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
`;

const FullPage = styled.div`
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(-45deg, #0f0c29, #302b63, #24243e, #ff4d4f);
  background-size: 400% 400%;
  animation: ${gradient} 15s ease infinite;
  color: white;
  text-align: center;
`;

const Logo = styled.h1`
  font-size: 72px;
  font-weight: 900;
  background: linear-gradient(90deg, #ff4d4f, #f9d423, #24c6dc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 20px rgba(255, 77, 79, 0.7);
`;

const GlassCard = styled.div`
  margin-top: 40px;
  padding: 30px 50px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.37);
`;

export default function HomePage() {
  return (
    <FullPage>
      <Logo>🎮 GameVault</Logo>
      <Paragraph style={{ fontSize: 20, color: "white" }}>
        Unlock Your Next Adventure
      </Paragraph>

      <GlassCard>
        <Title level={3} style={{ color: "white" }}>🔥 热门推荐</Title>
        <Paragraph style={{ color: "white" }}>
          探索最新最热的游戏大作，加入百万玩家的世界。
        </Paragraph>

        <Link to="/store">
          <Button type="primary" size="large" style={{ marginTop: 10 }}>
            立即进入商店
          </Button>
        </Link>
      </GlassCard>
    </FullPage>
  );
}
