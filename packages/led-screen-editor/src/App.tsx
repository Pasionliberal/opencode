import { useState } from 'react';
import { Canvas } from './components/Canvas';
import { Toolbar } from './components/Toolbar';
import { FreeDrawing } from './components/FreeDrawing';
import './App.css';

type Tool = 'pen' | 'eraser' | 'text' | 'line' | 'rectangle' | 'circle';
type Section = 'editor' | 'free-draw';

export function App() {
  const [currentTool, setCurrentTool] = useState<Tool>('pen');
  const [currentColor, setCurrentColor] = useState('#FF0000');
  const [section, setSection] = useState<Section>('editor');
  const [pixels, setPixels] = useState<Map<string, string>>(new Map());
  const [isAnimating, setIsAnimating] = useState(false);
  const [animationSpeed, setAnimationSpeed] = useState(1);

  const handlePixelChange = (x: number, y: number, color: string | null) => {
    const newPixels = new Map(pixels);
    const key = `${x},${y}`;

    if (color === null) {
      newPixels.delete(key);
    } else {
      newPixels.set(key, color);
    }

    setPixels(newPixels);
  };

  const clearCanvas = () => {
    setPixels(new Map());
  };

  const downloadAsImage = () => {
    const canvas = document.createElement('canvas');
    canvas.width = 1280;
    canvas.height = 720;
    const ctx = canvas.getContext('2d');

    if (ctx) {
      ctx.fillStyle = '#000000';
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      const pixelSize = 40;
      pixels.forEach((color, key) => {
        const [x, y] = key.split(',').map(Number);
        ctx.fillStyle = color;
        ctx.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
      });

      const link = document.createElement('a');
      link.href = canvas.toDataURL('image/png');
      link.download = 'led-screen.png';
      link.click();
    }
  };

  return (
    <div className="app">
      <header className="header">
        <h1>LED Screen Editor</h1>
        <div className="section-tabs">
          <button
            className={`tab ${section === 'editor' ? 'active' : ''}`}
            onClick={() => setSection('editor')}
          >
            Editor
          </button>
          <button
            className={`tab ${section === 'free-draw' ? 'active' : ''}`}
            onClick={() => setSection('free-draw')}
          >
            Dibujo Libre
          </button>
        </div>
      </header>

      <main className="main">
        {section === 'editor' && (
          <>
            <Toolbar
              currentTool={currentTool}
              currentColor={currentColor}
              onToolChange={setCurrentTool}
              onColorChange={setCurrentColor}
              onClear={clearCanvas}
              onDownload={downloadAsImage}
              isAnimating={isAnimating}
              onAnimatingChange={setIsAnimating}
              animationSpeed={animationSpeed}
              onAnimationSpeedChange={setAnimationSpeed}
            />
            <Canvas
              pixels={pixels}
              onPixelChange={handlePixelChange}
              tool={currentTool}
              color={currentColor}
              isAnimating={isAnimating}
            />
          </>
        )}

        {section === 'free-draw' && (
          <FreeDrawing />
        )}
      </main>
    </div>
  );
}
