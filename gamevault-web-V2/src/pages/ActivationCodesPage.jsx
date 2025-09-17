import { Table, message } from 'antd';
import { useEffect, useState } from 'react';
import api from '../api/axiosInstance';

export default function ActivationCodesPage() {
  const [codes, setCodes] = useState([]);

  useEffect(() => {
    api.get('/activation-codes/my')
      .then(res => setCodes(res.data))
      .catch(err => message.error('获取激活码失败: ' + err.message));
  }, []);

  const columns = [
    { title: '激活码ID', dataIndex: 'id' },
    { title: '游戏ID', dataIndex: 'gameId' },
    { title: '激活码', dataIndex: 'code' },
  ];

  return <Table dataSource={codes} columns={columns} rowKey="id" />;
}
