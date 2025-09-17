import { Card, Avatar, message } from 'antd';
import { useEffect, useState } from 'react';
import api from '../api/axiosInstance';

export default function ProfilePage() {
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    api.get('/profile/me')
      .then(res => setProfile(res.data))
      .catch(err => message.error('获取用户信息失败: ' + err.message));
  }, []);

  if (!profile) return <p>加载中...</p>;

  return (
    <Card>
      <Card.Meta
        avatar={<Avatar src={profile.avatar} />}
        title={profile.username}
        description={profile.email}
      />
    </Card>
  );
}
