package com.farmtrade.game

import android.content.Context
import android.media.*
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Calendar
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SkyRenderer : GLSurfaceView.Renderer {
    private var prog = 0; private var w = 1f; private var h = 1f
    private val t0 = System.nanoTime()
    private val quad = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer()
        .apply { put(floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)); position(0) }
    private val vs = "attribute vec2 p; void main(){gl_Position=vec4(p,0.0,1.0);}"
    private val fs = """
        precision mediump float; uniform vec2 r; uniform float t; uniform float hr;
        void main(){
          vec2 uv = gl_FragCoord.xy / r;
          float day = clamp(sin((hr-6.0)/24.0*6.2832)*1.5+0.5, 0.0, 1.0);
          vec3 top = mix(vec3(0.02,0.03,0.15), vec3(0.30,0.60,0.95), day);
          vec3 bot = mix(vec3(0.10,0.08,0.20), vec3(0.80,0.92,1.00), day);
          vec3 c = mix(bot, top, uv.y);
          float cl = smoothstep(0.55,0.75, sin(uv.x*6.0+t*0.2)*sin(uv.y*9.0-t*0.1)*0.5+0.5);
          c = mix(c, vec3(1.0), cl*0.25*day);
          gl_FragColor = vec4(c,1.0);
        }""".trimIndent()

    private fun sh(type: Int, src: String): Int {
        val s = GLES20.glCreateShader(type); GLES20.glShaderSource(s, src); GLES20.glCompileShader(s); return s
    }
    override fun onSurfaceCreated(gl: GL10?, c: EGLConfig?) {
        prog = GLES20.glCreateProgram()
        GLES20.glAttachShader(prog, sh(GLES20.GL_VERTEX_SHADER, vs))
        GLES20.glAttachShader(prog, sh(GLES20.GL_FRAGMENT_SHADER, fs))
        GLES20.glLinkProgram(prog)
    }
    override fun onSurfaceChanged(gl: GL10?, W: Int, H: Int) { GLES20.glViewport(0, 0, W, H); w = W.toFloat(); h = H.toFloat() }
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glUseProgram(prog)
        val a = GLES20.glGetAttribLocation(prog, "p")
        GLES20.glEnableVertexAttribArray(a)
        GLES20.glVertexAttribPointer(a, 2, GLES20.GL_FLOAT, false, 0, quad)
        GLES20.glUniform2f(GLES20.glGetUniformLocation(prog, "r"), w, h)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(prog, "t"), (System.nanoTime() - t0) / 1e9f)
        val cal = Calendar.getInstance()
        GLES20.glUniform1f(GLES20.glGetUniformLocation(prog, "hr"), cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60f)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }
}

class Sfx(c: Context) {
    private val sp = SoundPool.Builder().setMaxStreams(4).setAudioAttributes(
        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).build()
    private val ids = HashMap<String, Int>()
    private val tone by lazy { ToneGenerator(AudioManager.STREAM_MUSIC, 60) }
    init {
        listOf("plant", "water", "harvest", "sell", "buy", "alarm").forEach {
            val r = c.resources.getIdentifier("sfx_$it", "raw", c.packageName)
            if (r != 0) ids[it] = sp.load(c, r, 1)
        }
    }
    fun play(n: String) {
        val id = ids[n]
        if (id != null) sp.play(id, 1f, 1f, 1, 0, 1f)
        else try { tone.startTone(ToneGenerator.TONE_PROP_BEEP, 80) } catch (_: Throwable) {}
    }
}