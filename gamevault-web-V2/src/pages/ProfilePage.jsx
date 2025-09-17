import { useEffect, useState } from "react";
import { Card, Avatar, Button, message } from "antd";
import api from "../api/api";

export default function ProfilePage({ setAuthToken }) {
  const [profile, setProfile] = useState(null);

  const loadProfile = async () => {
    try {
      const res = await api.get("/profile/me");
      setProfile(res.data);
    } catch {
      message.error("请先登录");
    }
  };

  useEffect(() => {
    loadProfile();
  }, []);

  if (!profile) {
    return (
      <Card title="个人资料" style={{ textAlign: "center", borderRadius: 12 }}>
        <h3>请先登录</h3>
        <Button
          type="primary"
          onClick={() => {
            const token = prompt("请输入JWT令牌:");
            if (token) {
              localStorage.setItem("authToken", token);
              setAuthToken(token);
              loadProfile();
            }
          }}
        >
          登录
        </Button>
      </Card>
    );
  }

  return (
    <Card title="个人资料" style={{ textAlign: "center", borderRadius: 12 }}>
      <Avatar
        size={100}
        src={profile.imageUrl}
        style={{ backgroundColor: "#667eea", marginBottom: 12 }}
      >
        {profile.username?.charAt(0).toUpperCase()}
      </Avatar>
      <h3>{profile.username}</h3>
      <p>{profile.email}</p>
      <p>用户ID: {profile.uid}</p>
      <Button
        danger
        onClick={() => {
          localStorage.removeItem("authToken");
          setAuthToken(null);
          setProfile(null);
          message.info("已退出登录");
        }}
      >
        退出
      </Button>
    </Card>
  );
}
