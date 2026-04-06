package com.noe.wildlegend;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class WildLegendWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new WildLegendEngine();
    }

    private class WildLegendEngine extends Engine {

        private final Handler handler = new Handler(Looper.getMainLooper());

        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };

        private final Paint paint = new Paint();
        private boolean visible = true;
        private float offset = 0f;
        private long startTime = System.currentTimeMillis();

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                drawFrame();
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
            handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onOffsetsChanged(
                float xOffset,
                float yOffset,
                float xOffsetStep,
                float yOffsetStep,
                int xPixelOffset,
                int yPixelOffset) {
            offset = xOffset;
            drawFrame();
        }

        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    render(canvas);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.postDelayed(drawRunner, 33L);
            }
        }

        private void render(Canvas canvas) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            long now = System.currentTimeMillis();
            float t = (now - startTime) / 1000f;

            drawSky(canvas, width, height);
            drawSunGlow(canvas, width, height, t);
            drawMountains(canvas, width, height);
            drawMonolith(canvas, width, height, t);
            drawGround(canvas, width, height);
            drawMist(canvas, width, height, t);
        }

        private void drawSky(Canvas canvas, int width, int height) {
            Paint sky = new Paint();
            Shader shader = new LinearGradient(
                    0, 0, 0, height,
                    new int[]{
                            Color.parseColor("#08111B"),
                            Color.parseColor("#12304A"),
                            Color.parseColor("#D7B46A")
                    },
                    null,
                    Shader.TileMode.CLAMP
            );
            sky.setShader(shader);
            canvas.drawRect(0, 0, width, height, sky);
        }

        private void drawSunGlow(Canvas canvas, int width, int height, float t) {
            Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
            int alpha = 70 + (int)(20 * Math.sin(t * 0.6f));
            glow.setColor(Color.argb(alpha, 255, 220, 150));
            canvas.drawCircle(width * 0.78f, height * 0.22f, width * 0.16f, glow);
        }

        private void drawMountains(Canvas canvas, int width, int height) {
            Paint back = new Paint(Paint.ANTI_ALIAS_FLAG);
            back.setColor(Color.parseColor("#20384A"));

            float[] pts1 = new float[] {
                    0, height * 0.62f,
                    width * 0.18f, height * 0.48f,
                    width * 0.36f, height * 0.58f,
                    width * 0.54f, height * 0.42f,
                    width * 0.72f, height * 0.57f,
                    width, height * 0.46f,
                    width, height,
                    0, height
            };
            drawPolygon(canvas, pts1, back);

            Paint front = new Paint(Paint.ANTI_ALIAS_FLAG);
            front.setColor(Color.parseColor("#162A38"));

            float[] pts2 = new float[] {
                    0, height * 0.72f,
                    width * 0.15f, height * 0.60f,
                    width * 0.30f, height * 0.66f,
                    width * 0.48f, height * 0.54f,
                    width * 0.65f, height * 0.68f,
                    width * 0.82f, height * 0.59f,
                    width, height * 0.64f,
                    width, height,
                    0, height
            };
            drawPolygon(canvas, pts2, front);
        }

        private void drawMonolith(Canvas canvas, int width, int height, float t) {
            float cx = width * (0.5f + (offset - 0.5f) * 0.08f);
            float baseY = height * 0.72f;

            Paint stone = new Paint(Paint.ANTI_ALIAS_FLAG);
            stone.setColor(Color.parseColor("#2F3D46"));
            canvas.drawRect(cx - width * 0.025f, height * 0.34f, cx + width * 0.025f, baseY, stone);

            Paint core = new Paint(Paint.ANTI_ALIAS_FLAG);
            int glow = 120 + (int)(40 * Math.sin(t * 1.2f));
            core.setColor(Color.argb(glow, 110, 200, 255));
            canvas.drawRect(cx - width * 0.006f, height * 0.38f, cx + width * 0.006f, baseY - height * 0.06f, core);

            Paint ring = new Paint(Paint.ANTI_ALIAS_FLAG);
            ring.setStyle(Paint.Style.STROKE);
            ring.setStrokeWidth(width * 0.008f);
            ring.setColor(Color.argb(180, 212, 176, 106));
            Rect oval = new Rect(
                    (int)(cx - width * 0.10f),
                    (int)(height * 0.34f),
                    (int)(cx + width * 0.10f),
                    (int)(height * 0.52f)
            );
            canvas.drawOval(
                    oval.left,
                    oval.top,
                    oval.right,
                    oval.bottom,
                    ring
            );
        }

        private void drawGround(Canvas canvas, int width, int height) {
            Paint ground = new Paint();
            Shader shader = new LinearGradient(
                    0, height * 0.70f, 0, height,
                    new int[]{
                            Color.parseColor("#365B3D"),
                            Color.parseColor("#1C3424")
                    },
                    null,
                    Shader.TileMode.CLAMP
            );
            ground.setShader(shader);
            canvas.drawRect(0, height * 0.70f, width, height, ground);
        }

        private void drawMist(Canvas canvas, int width, int height, float t) {
            Paint mist = new Paint(Paint.ANTI_ALIAS_FLAG);
            int alpha = 35 + (int)(10 * Math.sin(t * 0.5f));
            mist.setColor(Color.argb(alpha, 210, 230, 255));
            canvas.drawRect(0, height * 0.58f, width, height * 0.78f, mist);
        }

        private void drawPolygon(Canvas canvas, float[] pts, Paint paint) {
            android.graphics.Path path = new android.graphics.Path();
            path.moveTo(pts[0], pts[1]);
            for (int i = 2; i < pts.length; i += 2) {
                path.lineTo(pts[i], pts[i + 1]);
            }
            path.close();
            canvas.drawPath(path, paint);
        }
    }
}
