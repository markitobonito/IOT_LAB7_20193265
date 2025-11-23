package com.example.appplication;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Módulo de configuración Glide para Firebase Storage
 * Glide cargará imágenes de Firebase Storage automáticamente con URLs
 */
@GlideModule
public class MyAppGlideModule extends AppGlideModule {
    // La configuración básica es suficiente
    // Glide maneja URLs de Firebase Storage automáticamente
}
