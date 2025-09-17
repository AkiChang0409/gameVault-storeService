import { useState } from "react";
import { Button, Modal } from "antd";

export default function TestPage() {
  const [open, setOpen] = useState(false);

  return (
    <div style={{ padding: 40 }}>
      <Button onClick={() => setOpen(true)}>测试弹窗</Button>

      <Modal
        title="测试弹窗"
        open={open}
        onOk={() => setOpen(false)}
        onCancel={() => setOpen(false)}
      >
        <p>能看到我吗？</p>
      </Modal>
    </div>
  );
}
