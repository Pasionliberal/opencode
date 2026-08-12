import { useRef, useState, useEffect } from 'react';
import '../styles/FreeDrawing.css';

export function FreeDrawing() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [currentColor, setCurrentColor] = useState('#FF0000');
  const [brushSize, setBrushSize] = useState(3);
  const [isPlaying, setIsPlaying] = useState(false);
  const [frames, setFrames] = useState<ImageData[]>([]);
  const [currentFrame, setCurrentFrame] = useState(0);

  const startDrawing = (e: React.MouseEvent) => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    setIsDrawing(true);
    const rect = canvas.getBoundingClientRect();
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.beginPath();
    ctx.moveTo(e.clientX - rect.left, e.clientY - rect.top);
  };

  const draw = (e: React.MouseEvent) => {
    if (!isDrawing) return;

    const canvas = canvasRef.current;
    if (!canvas) return;

    const rect = canvas.getBoundingClientRect();
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.lineWidth = brushSize;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    ctx.strokeStyle = currentColor;

    ctx.lineTo(e.clientX - rect.left, e.clientY - rect.top);
    ctx.stroke();
  };

  const stopDrawing = () => {
    setIsDrawing(false);
  };

  const clearCanvas = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.fillStyle = '#000000';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    setFrames([]);
    setCurrentFrame(0);
  };

  const saveFrame = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    setFrames([...frames, imageData]);
  };

  const playAnimation = () => {
    if (frames.length === 0) return;

    setIsPlaying(true);
    let frameIndex = 0;

    const interval = setInterval(() => {
      const canvas = canvasRef.current;
      if (!canvas) return;

      const ctx = canvas.getContext('2d');
      if (!ctx) return;

      ctx.putImageData(frames[frameIndex], 0, 0);
      frameIndex = (frameIndex + 1) % frames.length;
    }, 200);

    setTimeout(() => {
      clearInterval(interval);
      setIsPlaying(false);
    }, 5000);
  };

  const downloadAnimation = () => {
    if (frames.length === 0) return;

    const data = {
      frames: frames.map(f => {
        const canvas = document.createElement('canvas');
        canvas.width = f.width;
        canvas.height = f.height;
        const ctx = canvas.getContext('2d');
        if (ctx) ctx.putImageData(f, 0, 0);
        return canvas.toDataURL('image/png');
      }),
      frameCount: frames.length,
      timestamp: new Date().toISOString()
    };

    const blob = new Blob([JSON.stringify(data)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'led-animation.json';
    a.click();
    URL.revokeObjectURL(url);
  };

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.fillStyle = '#000000';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
  }, []);

  return (
    <div className="free-drawing">
      <div className="free-drawing-canvas-wrapper">
        <canvas
          ref={canvasRef}
          width={1024}
          height={576}
          onMouseDown={startDrawing}
          onMouseMove={draw}
          onMouseUp={stopDrawing}
          onMouseLeave={stopDrawing}
          className="free-canvas"
        />
      </div>

      <div className="free-drawing-controls">
        <div className="controls-section">
          <h3>Pincel</h3>
          <div className="brush-controls">
            <label>
              Color:
              <input
                type="color"
                value={currentColor}
                onChange={(e) => setCurrentColor(e.target.value)}
              />
            </label>
            <label>
              Tamaño: {brushSize}px
              <input
                type="range"
                min="1"
                max="20"
                value={brushSize}
                onChange={(e) => setBrushSize(parseInt(e.target.value))}
              />
            </label>
          </div>
        </div>

        <div className="controls-section">
          <h3>Fotogramas ({frames.length})</h3>
          <button onClick={saveFrame} className="action-btn">Guardar fotograma</button>
          <button onClick={playAnimation} disabled={frames.length === 0} className="action-btn">
            Reproducir
          </button>
        </div>

        <div className="controls-section">
          <h3>Acciones</h3>
          <button onClick={clearCanvas} className="action-btn danger">Limpiar</button>
          <button onClick={downloadAnimation} disabled={frames.length === 0} className="action-btn">
            Descargar animación
          </button>
        </div>
      </div>
    </div>
  );
}
