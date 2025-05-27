package com.sarahisweird.buzzes_and_crawls.client;

import com.sarahisweird.buzzes_and_crawls.BaCEntities;
import com.sarahisweird.buzzes_and_crawls.client.entity.ant.AntEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class BaCClientJava {
    /**
     * Registers entity renderers of BaC entities.
     * <p>
     * Turns out Kotlin just doesn't have duck typing support, so we have to fall back to Java to do this.
     * :')
     */
    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(BaCEntities.ANT, AntEntityRenderer::new);
    }
}
