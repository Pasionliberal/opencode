import { useRef, useEffect, useState } from 'react';
import '../styles/Canvas.css';

type Tool = 'pen' | 'eraser' | 'text' | 'line' | 'rectangle' | 'circle';

interface CanvasProps {
  pixels: Map<string, string>;
  onPixelChange: (x: number, y: number, color: string | null) => void;
  tool: Tool;
  color: string;
  isAnimating: boolean;
}

const GRID_WIDTH = 32;
const GRID_HEIGHT = 18;
const PIXEL_SIZE = 30;

export function Canvas({ pixels, onPixelChange, tool, color, isAnimating }: CanvasProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [startX, setStartX] = useState(0);
  const [startY, setStartY] = useState(0);

  const getPixelCoords = (clientX: number, clientY: number) => {
    const canvas = canvasRef.current;
    if (!canvas) return null;

    const rect = canvas.getBoundingClientRect();
    const x = Math.floor((clientX - rect.left) / PIXEL_SIZE);
    const y = Math.floor((clientY - rect.top) / PIXEL_SIZE);

    if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
      return null;
    }

    return { x, y };
  };

  const drawCanvas = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.fillStyle = '#1a1a1a';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    ctx.strokeStyle = '#333';
    ctx.lineWidth = 1;

    for (let i = 0; i <= GRID_WIDTH; i++) {
      ctx.beginPath();
      ctx.moveTo(i * PIXEL_SIZE, 0);
      ctx.lineTo(i * PIXEL_SIZE, GRID_HEIGHT * PIXEL_SIZE);
      ctx.stroke();
    }

    for (let i = 0; i <= GRID_HEIGHT; i++) {
      ctx.beginPath();
      ctx.moveTo(0, i * PIXEL_SIZE);
      ctx.lineTo(GRID_WIDTH * PIXEL_SIZE, i * PIXEL_SIZE);
      ctx.stroke();
    }

    pixels.forEach((pixelColor, key) => {
      const [x, y] = key.split(',').map(Number);
      ctx.fillStyle = pixelColor;
      ctx.fillRect(x * PIXEL_SIZE + 1, y * PIXEL_SIZE + 1, PIXEL_SIZE - 2, PIXEL_SIZE - 2);
    });
  };

  useEffect(() => {
    drawCanvas();
  }, [pixels]);

  const handleMouseDown = (e: React.MouseEvent) => {
    const coords = getPixelCoords(e.clientX, e.clientY);
    if (!coords) return;

    setIsDrawing(true);
    setStartX(coords.x);
    setStartY(coords.y);

    if (tool === 'pen') {
      onPixelChange(coords.x, coords.y, color);
    } else if (tool === 'eraser') {
      onPixelChange(coords.x, coords.y, null);
    }
  };

  const handleMouseMove = (e: React.MouseEvent) => {
    if (!isDrawing) return;

    const coords = getPixelCoords(e.clientX, e.clientY);
    if (!coords) return;

    if (tool === 'pen') {
      onPixelChange(coords.x, coords.y, color);
    } else if (tool === 'eraser') {
      onPixelChange(coords.x, coords.y, null);
    }
  };

  const handleMouseUp = () => {
    setIsDrawing(false);
  };

  return (
    <div className="canvas-container">
      <canvas
        ref={canvasRef}
        width={GRID_WIDTH * PIXEL_SIZE}
        height={GRID_HEIGHT * PIXEL_SIZE}
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseUp}
        className="led-canvas"
      />
    </div>
  );
}
