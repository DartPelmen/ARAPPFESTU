/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.festu.ivan.kuznetsov.myapplication.ar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.opengl.GLES30
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import edu.festu.ivan.kuznetsov.myapplication.R
import edu.festu.ivan.kuznetsov.myapplication.ar.render.GLError
import edu.festu.ivan.kuznetsov.myapplication.ar.render.SampleRender
import edu.festu.ivan.kuznetsov.myapplication.ar.render.Texture
import java.lang.ref.WeakReference
import java.nio.ByteBuffer

/** Generates and caches GL textures for label names. */
class TextTextureCache(val context: WeakReference<Context>) {
  companion object {
    private val TAG = TextTextureCache::class.java.simpleName
  }

  private val cacheMap = mutableMapOf<String, Texture>()

  /**
   * Get a texture for a given string. If that string hasn't been used yet, create a texture for it
   * and cache the result.
   */
  fun get(render: SampleRender, string: String): Texture {
    return cacheMap.computeIfAbsent(string) { generateTexture(render, string) }
  }

  /** Generates an OpenGL texture using a bitmap from [generateBitmapFromString]. */
  private fun generateTexture(render: SampleRender, string: String): Texture {
    val texture = Texture(render, Texture.Target.TEXTURE_2D, Texture.WrapMode.CLAMP_TO_EDGE)

    val bitmap = generateBitmapFromString(string)
    val buffer = ByteBuffer.allocateDirect(bitmap.byteCount)
    bitmap.copyPixelsToBuffer(buffer)
    buffer.rewind()

    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture.textureId)
    GLError.maybeThrowGLException("Failed to bind texture", "glBindTexture")
    GLES30.glTexImage2D(
      GLES30.GL_TEXTURE_2D,
      0,
      GLES30.GL_RGBA8,
      bitmap.width,
      bitmap.height,
      0,
      GLES30.GL_RGBA,
      GLES30.GL_UNSIGNED_BYTE,
      buffer
    )
    GLError.maybeThrowGLException("Failed to populate texture data", "glTexImage2D")
    GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
    GLError.maybeThrowGLException("Failed to generate mipmaps", "glGenerateMipmap")

    return texture
  }

  /** Paint used to draw the label's text. */
  private val textPaint =
    Paint().apply {
      textSize = 16f
      setARGB(0xff, 0xff, 0xff, 0xff)
      style = Paint.Style.FILL
      isAntiAlias = true
      textAlign = Paint.Align.CENTER
      typeface = Typeface.DEFAULT_BOLD
      strokeWidth = 2f
    }

  /** Paint used to stroke the label text. */
  private val strokePaint =
    Paint(textPaint).apply {
      setARGB(0xff, 0x00, 0x00, 0x00)
      style = Paint.Style.STROKE
    }

  private fun generateBitmapFromString(string: String): Bitmap {
    val w = 640
    val h = 128
    return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
      eraseColor(0)
      val strings = string.split("\n")

      Canvas(this).apply {
        context.get()?.let{
          drawBitmap(
            ResourcesCompat.getDrawable(it.resources,R.drawable.ic_borders,null)?.toBitmap(this.width,this.height)!!,
          0f,0f,null)
        }
        for (i in strings.indices) {
          drawText(strings[i], w / 2f, h/4+(18f* i), strokePaint)
          drawText(strings[i], w / 2f, h/4+(18f* i), textPaint)
        }
      }
    }
  }
}
