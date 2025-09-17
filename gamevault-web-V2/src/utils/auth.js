import { jwtDecode } from "jwt-decode";   // ✅ 用命名导入

export function getUserId() {
  const token = sessionStorage.getItem("token");
  if (!token) return null;

  try {
    const decoded = jwtDecode(token);
    return decoded.sub;  // 后端 token 的 userId
  } catch (e) {
    console.error("解析 token 出错:", e);
    return null;
  }
}
