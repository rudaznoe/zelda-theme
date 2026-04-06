package com.noe.wildlegend;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.util.Random;

public class WildLegendWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new WildLegendEngine();
    }

    private class WildLegendEngine extends Engine implements Runnable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Random random = new Random();
        private final float[] particleX = new float[28];
        private final float[] particleY = new float[28];
        private final float[] particleSpeed = new float[28];
        private final Runnable drawRunner = this;
        private boolean visible = false;
        private float offset = 0f;
        private float pulse = 0f;

        WildLegendEngine() {
            for (int i = 0; i < particleX.length; i++) {
                particleX[i] = random.nextFloat();
                particleY[i] = random.nextFloat();
                particleSpeed[i] = 0.0008f + random.nextFloat() * 0.0018f;
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                draw();
            } else {
                getHandler().removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
            getHandler().removeCallbacks(drawRunner);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep,
                                     int xPixelOffset, int yPixelOffset) {
            offset = xOffset;
            draw();
        }

        @Override
        public void run() {
            draw();
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();
                if (canvas == null) return;

                int w = canvas.getWidth();
                int h = canvas.getHeight();
                SharedPreferences prefs = getSharedPreferences("wildlegend_prefs", MODE_PRIVATE);
                boolean readable = prefs.getBoolean("readable", true);
                boolean particles = prefs.getBoolean("particles", true);

                canvas.drawColor(Color.rgb(5, 11, 28));

                float skyShift = offset * 90f;
                paint.setShader(new LinearGradient(
                        -skyShift, 0, w + skyShift, h,
                        new int[]{
                                Color.rgb(8, 18, 48),
                                Color.rgb(20, 60, 96),
                                Color.rgb(171, 148, 97)
                        },
                        new float[]{0f, 0.58f, 1f},
                        Shader.TileMode.CLAMP
                ));
                canvas.drawRect(0, 0, w, h, paint);

                paint.setShader(new RadialGradient(
                        w * 0.78f, h * 0.18f, w * 0.42f,
                        new int[]{Color.argb(175, 239, 217, 132), Color.argb(0, 239, 217, 132)},
                        new float[]{0f, 1f},
                        Shader.TileMode.CLAMP
                ));
                canvas.drawRect(0, 0, w, h, paint);

                paint.setShader(null);
                paint.setColor(Color.argb(45, 190, 220, 230));
                canvas.drawRect(0, h * 0.34f, w, h * 0.46f, paint);

                Path mountainBack = new Path();
                mountainBack.moveTo(0, h * 0.54f);
                mountainBack.lineTo(w * 0.24f, h * 0.44f);
                mountainBack.lineTo(w * 0.50f, h * 0.40f);
                mountainBack.lineTo(w * 0.72f, h * 0.47f);
                mountainBack.lineTo(w, h * 0.48f);
                mountainBack.lineTo(w, h);
                mountainBack.lineTo(0, h);
                mountainBack.close();
                paint.setColor(Color.rgb(39, 67, 92));
                canvas.drawPath(mountainBack, paint);

                Path mountainFront = new Path();
                mountainFront.moveTo(0, h * 0.60f);
                mountainFront.lineTo(w * 0.15f, h * 0.56f);
                mountainFront.lineTo(w * 0.35f, h * 0.53f);
                mountainFront.lineTo(w * 0.60f, h * 0.57f);
                mountainFront.lineTo(w * 0.80f, h * 0.55f);
                mountainFront.lineTo(w, h * 0.58f);
                mountainFront.lineTo(w, h);
                mountainFront.lineTo(0, h);
                mountainFront.close();
                paint.setColor(Color.rgb(22, 50, 46));
                canvas.drawPath(mountainFront, paint);

                paint.setColor(Color.rgb(8, 52, 25));
                canvas.drawRect(0, h * 0.60f, w, h, paint);

                paint.setColor(Color.argb(110, 186, 168, 105));
                paint.setStrokeWidth(Math.max(2f, w * 0.0024f));
                for (int i = 0; i < 120; i++) {
                    float x = (i / 119f) * w;
                    float sway = (float) Math.sin((i * 0.45f) + pulse) * (w * 0.004f);
                    canvas.drawLine(x, h, x + sway, h * (0.72f + (i % 7) * 0.015f), paint);
                }

                float cx = w * 0.5f;
                float cy = h * 0.53f;
                float monolithW = w * 0.05f;
                float monolithH = h * 0.22f;

                RectF monolith = new RectF(cx - monolithW / 2f, cy - monolithH / 2f, cx + monolithW / 2f, cy + monolithH / 2f);
                paint.setColor(Color.rgb(12, 17, 30));
                canvas.drawRoundRect(monolith, monolithW * 0.15f, monolithW * 0.15f, paint);

                paint.setColor(Color.argb(100, 116, 198, 199));
                paint.setStrokeWidth(Math.max(2f, w * 0.002f));
                canvas.drawLine(cx, monolith.top + monolithH * 0.08f, cx, monolith.bottom - monolithH * 0.08f, paint);

                pulse += 0.045f;
                float ringRadius = w * 0.08f + (float) Math.sin(pulse) * w * 0.004f;
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(Math.max(3f, w * 0.004f));
                paint.setColor(Color.argb(180, 220, 193, 104));
                canvas.drawOval(new RectF(cx - ringRadius, cy - ringRadius * 0.28f, cx + ringRadius, cy + ringRadius * 0.28f), paint);
                paint.setStyle(Paint.Style.FILL);

                if (particles) {
                    paint.setColor(Color.argb(120, 163, 220, 225));
                    for (int i = 0; i < particleX.length; i++) {
                        float x = particleX[i] * w;
                        float y = particleY[i] * h;
                        canvas.drawCircle(x, y, Math.max(1.8f, w * 0.0024f), paint);
                        particleY[i] -= particleSpeed[i];
                        if (particleY[i] < 0.08f) {
                            particleY[i] = 0.88f;
                            particleX[i] = random.nextFloat();
                        }
                    }
                }

                if (readable) {
                    paint.setShader(new LinearGradient(
                            0, 0, 0, h * 0.22f,
                            new int[]{Color.argb(185, 4, 10, 20), Color.argb(0, 4, 10, 20)},
                            null,
                            Shader.TileMode.CLAMP
                    ));
                    canvas.drawRect(0, 0, w, h * 0.22f, paint);
                    paint.setShader(null);
                }

            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            getHandler().removeCallbacks(drawRunner);
            if (visible) {
                getHandler().postDelayed(drawRunner, 33L);
            }
        }
    }
}
