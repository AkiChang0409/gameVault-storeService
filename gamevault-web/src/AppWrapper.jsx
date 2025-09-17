import styled, { keyframes } from 'styled-components';

const gradient = keyframes`
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
`;

export const AppWrapper = styled.div`
  min-height: 100vh;
  background: linear-gradient(-45deg, #0f0c29, #302b63, #24243e, #ff4d4f);
  background-size: 400% 400%;
  animation: ${gradient} 15s ease infinite;
`;
