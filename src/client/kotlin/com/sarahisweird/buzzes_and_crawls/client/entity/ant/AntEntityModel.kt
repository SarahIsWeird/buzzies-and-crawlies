package com.sarahisweird.buzzes_and_crawls.client.entity.ant

import com.sarahisweird.buzzes_and_crawls.BuzzesAndCrawls.MOD_ID
import com.sarahisweird.buzzes_and_crawls.entities.ant.AntEntity
import net.minecraft.util.Identifier
import software.bernie.geckolib.model.DefaultedEntityGeoModel

class AntEntityModel : DefaultedEntityGeoModel<AntEntity>(
    Identifier.of(MOD_ID, "ant")
)
