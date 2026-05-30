package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.entity.living.TrilocarisEntity;

public class TrilocarisBucketItem extends BucketableWaterMobItem {
    public TrilocarisBucketItem() {
        super(TrilocarisEntity::new);
    }
}
