export function getGameImage(game) {
  // 如果是空字符串或 "null" 或 "undefined"，直接用占位
  if (!game.imageUrl || game.imageUrl === "null" || game.imageUrl === "undefined") {
    return `https://picsum.photos/300/400?random=${game.gameId}`;
  }

  // 如果是假的 example.com 链接，也用占位
  if (game.imageUrl.includes("example.com")) {
    return `https://picsum.photos/300/400?random=${game.gameId}`;
  }

  // 否则返回真实 URL
  return game.imageUrl;
}
