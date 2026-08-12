import '../styles/Toolbar.css';

type Tool = 'pen' | 'eraser' | 'text' | 'line' | 'rectangle' | 'circle';

interface ToolbarProps {
  currentTool: Tool;
  currentColor: string;
  onToolChange: (tool: Tool) => void;
  onColorChange: (color: string) => void;
  onClear: () => void;
  onDownload: () => void;
  isAnimating: boolean;
  onAnimatingChange: (animating: boolean) => void;
  animationSpeed: number;
  onAnimationSpeedChange: (speed: number) => void;
}

const COLORS = [
  '#FF0000', '#00FF00', '#0000FF',
  '#FFFF00', '#FF00FF', '#00FFFF',
  '#FFA500', '#800080', '#FFC0CB',
  '#A52A2A', '#808080', '#FFFFFF'
];

export function Toolbar({
  currentTool,
  currentColor,
  onToolChange,
  onColorChange,
  onClear,
  onDownload,
  isAnimating,
  onAnimatingChange,
  animationSpeed,
  onAnimationSpeedChange
}: ToolbarProps) {
  return (
    <div className="toolbar">
      <div className="toolbar-section">
        <h3>Herramientas</h3>
        <div className="tools">
          <button
            className={`tool-btn ${currentTool === 'pen' ? 'active' : ''}`}
            onClick={() => onToolChange('pen')}
            title="Pincel"
          >
            ✏️
          </button>
          <button
            className={`tool-btn ${currentTool === 'eraser' ? 'active' : ''}`}
            onClick={() => onToolChange('eraser')}
            title="Borrador"
          >
            🧹
          </button>
          <button
            className={`tool-btn ${currentTool === 'line' ? 'active' : ''}`}
            onClick={() => onToolChange('line')}
            title="Línea"
          >
            📏
          </button>
          <button
            className={`tool-btn ${currentTool === 'rectangle' ? 'active' : ''}`}
            onClick={() => onToolChange('rectangle')}
            title="Rectángulo"
          >
            ▭
          </button>
          <button
            className={`tool-btn ${currentTool === 'circle' ? 'active' : ''}`}
            onClick={() => onToolChange('circle')}
            title="Círculo"
          >
            ◯
          </button>
        </div>
      </div>

      <div className="toolbar-section">
        <h3>Colores</h3>
        <div className="color-grid">
          {COLORS.map(c => (
            <button
              key={c}
              className={`color-btn ${currentColor === c ? 'active' : ''}`}
              style={{ backgroundColor: c }}
              onClick={() => onColorChange(c)}
              title={c}
            />
          ))}
          <input
            type="color"
            value={currentColor}
            onChange={(e) => onColorChange(e.target.value)}
            className="color-picker"
          />
        </div>
      </div>

      <div className="toolbar-section">
        <h3>Animación</h3>
        <label className="animation-toggle">
          <input
            type="checkbox"
            checked={isAnimating}
            onChange={(e) => onAnimatingChange(e.target.checked)}
          />
          <span>Animar</span>
        </label>
        {isAnimating && (
          <div className="animation-speed">
            <label>Velocidad</label>
            <input
              type="range"
              min="0.5"
              max="3"
              step="0.5"
              value={animationSpeed}
              onChange={(e) => onAnimationSpeedChange(parseFloat(e.target.value))}
            />
            <span>{animationSpeed}x</span>
          </div>
        )}
      </div>

      <div className="toolbar-section">
        <h3>Acciones</h3>
        <button className="action-btn" onClick={onClear}>Limpiar todo</button>
        <button className="action-btn" onClick={onDownload}>Descargar</button>
      </div>
    </div>
  );
}
