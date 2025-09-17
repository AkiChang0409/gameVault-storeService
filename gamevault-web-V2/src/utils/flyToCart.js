// src/utils/flyToCart.js
export function flyToCart(imgSrc, startX, startY) {
  const cartIcon = document.querySelector(".cart-icon");
  if (!cartIcon) return;

  const cartRect = cartIcon.getBoundingClientRect();
  const img = document.createElement("img");
  img.src = imgSrc;
  img.style.position = "fixed";
  img.style.left = `${startX}px`;
  img.style.top = `${startY}px`;
  img.style.width = "120px";
  img.style.height = "160px";
  img.style.objectFit = "cover";
  img.style.borderRadius = "8px";
  img.style.zIndex = 9999;
  img.style.transition = "all 1s ease-in-out";

  document.body.appendChild(img);

  requestAnimationFrame(() => {
    img.style.left = `${cartRect.left + cartRect.width / 2}px`;
    img.style.top = `${cartRect.top + cartRect.height / 2}px`;
    img.style.width = "20px";
    img.style.height = "20px";
    img.style.opacity = "0.3";
  });

  img.addEventListener("transitionend", () => {
    img.remove();
  });
}
